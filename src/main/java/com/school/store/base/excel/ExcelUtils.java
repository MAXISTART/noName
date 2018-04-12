package com.school.store.base.excel;

import com.school.store.admin.buy.entity.BuyOrder;
import com.school.store.admin.buy.entity.BuyOrderItem;
import com.school.store.admin.department.entity.Department;
import com.school.store.admin.department.service.DepartmentService;
import com.school.store.admin.good.entity.GoodItem;
import com.school.store.admin.good.service.GoodItemService;
import com.school.store.admin.take.entity.TakeOrder;
import com.school.store.admin.take.entity.TakeOrderItem;
import com.school.store.utils.DateUtil;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Component
public class ExcelUtils {

    private String packageName = "com.school.store.base.excel";
    private String packageOutPath = "main.java." + packageName;//指定实体生成所在包的路径


    @Autowired
    private GoodItemService goodItemService;

    @Autowired
    private DepartmentService departmentService;

    Sheet sheet;


    public static void main(String[] args){
        ExcelUtils utils = new ExcelUtils();

/*
        Map<String, Integer> mapper = new HashMap<>();
        mapper.put("totalPrice", 9);
        mapper.put("details.name", 3);
        List<Map> beans = utils.getBeanMaps(mapper, utils.getHeaderSize());*//*

        utils.init(null, "test.xlsx");
        Map<Integer, Block> blocks = utils.separate(2, 0);
        System.out.println(blocks);*/
/*
        utils.init(null, "办公用品信息表_20180411230729.xlsx");
        System.out.println(utils.getGoodItems());

        utils.init(null, "办公用品入库表_20180411233323.xlsx");
        System.out.println(utils.getBuyOrders());*/

        utils.init(null, "办公用品申领表_20180412004119.xlsx");
        System.out.println(utils.getTakeOrders());
    }




    /**
     *  解析 入库表的
     * @return
     */
    public List<BuyOrder> getBuyOrders(){


        if(sheet == null){
            return null;
        }

        List<BuyOrder> buyOrders = new ArrayList<>();

        // 先根据提交时间进行分割
        Map<Integer, Block> blocks = separateByCol(2, 11);

        for(Integer index : blocks.keySet()){
            BuyOrder buyOrder = new BuyOrder();
            int i = 0;
            List<BuyOrderItem> buyOrderItems = new ArrayList<>();
            Row row = null;
            int rowStartIndex = blocks.get(index).getFirstLineNumber();
            for(;i <= blocks.get(index).getSize() - 1;i++){
                row = sheet.getRow(rowStartIndex + i);
                BuyOrderItem buyOrderItem = new BuyOrderItem();
                buyOrderItem.setGoodId(goodItemService.findByName(getCellValue(row.getCell(3))).get(0).getId());
                buyOrderItem.setSpec(getCellValue(row.getCell(5)));
                buyOrderItem.setUnit(getCellValue(row.getCell(6)));
                buyOrderItem.setName(getCellValue(row.getCell(3)));
                String number = getCellValue(row.getCell(7)).trim();
                buyOrderItem.setNumber(number.equals("") ? new BigDecimal(0) : new BigDecimal(number));
                String price = getCellValue(row.getCell(8)).trim();
                buyOrderItem.setPrice(new BigDecimal(price.equals("") ? "0" : price));

                buyOrderItems.add(buyOrderItem);
            }

            buyOrder.setBuyOrderItems(buyOrderItems);
            buyOrder.setApprovalResult(1);
            buyOrders.add(buyOrder);
        }
        return buyOrders;
    }


    /**
     *  解析 申领表的
     * @return
     */
    public List<TakeOrder> getTakeOrders(){


        if(sheet == null){
            return null;
        }

        List<TakeOrder> takeOrders = new ArrayList<>();

        // 先根据提交时间进行分割
        Map<Integer, Block> blocks = separateByDateTime(2, 0, 2);
        for(Integer index : blocks.keySet()){
            TakeOrder takeOrder = new TakeOrder();
            int i = 0;
            List<TakeOrderItem> takeOrderItems = new ArrayList<>();
            Row row = null;
            int rowStartIndex = blocks.get(index).getFirstLineNumber();
            for(;i <= blocks.get(index).getSize() - 1;i++){
                row = sheet.getRow(rowStartIndex + i);
                String name = getCellValue(row.getCell(4));
                if(!name.trim().equals("")){
                    TakeOrderItem takeOrderItem = new TakeOrderItem();
                    System.out.println("name : " + name);
                    takeOrderItem.setGoodId(goodItemService.findByName(name).get(0).getId());
                    takeOrderItem.setUnit(getCellValue(row.getCell(7)));
                    String number = getCellValue(row.getCell(8)).trim();
                    takeOrderItem.setNumber(number.equals("") ? new BigDecimal(0) : new BigDecimal(number));
                    String price = getCellValue(row.getCell(9)).trim();
                    takeOrderItem.setPrice(new BigDecimal(price.equals("") ? "0" : price));
                    takeOrderItem.setName(getCellValue(row.getCell(4)));
                    takeOrderItems.add(takeOrderItem);
                }
            }
            //System.out.println("size : " + takeOrderItems.size());

            takeOrder.setRequestTime(DateUtil.toDateTime(getCellValue(row.getCell(0))));
            takeOrder.setApprovalTime(takeOrder.getRequestTime());
            takeOrder.setApprovalResult(1);
            String departmentName = getCellValue(row.getCell(2));
            List<Department> departments = departmentService.findByName(departmentName);
            if(departments != null && !departments.isEmpty()){
                takeOrder.setDepartmentId(departments.get(0).getId());
            }
            if(!takeOrderItems.isEmpty()){
                takeOrder.setTakeOrderItems(takeOrderItems);
                takeOrders.add(takeOrder);
            }

        }
        return takeOrders;
    }


    /**
     *  解析 商品的
     * @return
     */
    public List<GoodItem> getGoodItems(){


        if(sheet == null){
            return null;
        }

        List<GoodItem> goodItems = new ArrayList<>();

        int rowIndex = 1;
        int count = sheet.getLastRowNum()+1;//总行数
        for(; rowIndex < count; rowIndex++){
            Row row = sheet.getRow(rowIndex);
            GoodItem goodItem = new GoodItem();

            goodItem.setName(getCellValue(row.getCell(0)));
            goodItem.setUnit(getCellValue(row.getCell(1)));
            goodItem.setSpec(getCellValue(row.getCell(2)));
            String price = getCellValue(row.getCell(3)).trim();
            goodItem.setPrice(new BigDecimal(price.equals("") ? "0" : price));
            goodItem.setNumber(new BigDecimal(0));
            goodItem.setSort(null);
            goodItems.add(goodItem);
        }
        return goodItems;
    }



    public List<Map> getBeanMaps(Map<String, Integer> mapper, Map<Integer, Block> blocks){

        if(sheet == null){
            return null;
        }

        // 获取头部最后一行

        int count = sheet.getLastRowNum()+1;//总行数
        //int count = 4;//总行数

        List<Map> list = new ArrayList<>();

        for(Integer startRowIndex : blocks.keySet()){

            int rowIndex = 0;
            for(; rowIndex <=blocks.get(startRowIndex).getSize(); rowIndex++ ){
                Map bean = new HashMap();
                Row row = sheet.getRow(rowIndex);
                for(String keyName : mapper.keySet()){
                    // 获取所在列
                    int columnIndex = mapper.get(keyName);
                    String keyValue = getCellValue(row.getCell(columnIndex));
                    resolve(keyName, keyValue, bean);
                }
                list.add(bean);
            }

        }

        return list;
    }


    // 解析键值名用的
    public void resolve(String keyName, String keyValue, Map bean){
        String[] parts = keyName.split("\\.");

        int size = parts.length;

        if(size == 1){
            bean.put(parts[0], keyValue);
        }

        if(size == 2){
            Map bean_2 = (Map)bean.get(parts[0]);
            if(bean_2 == null){
                bean_2 = new HashMap();
            }
            bean_2.put(parts[1], keyValue);
            bean.put(parts[0], bean_2);
        }

    }


    // 根据多少个合并单元格判断header是多大
    // 这种方法不太好，因为可能下面的具体表格就是合并单元格
    public Integer getHeaderSize(){

        int count = sheet.getLastRowNum()+1;//总行数

        int size = 1;

        while(size < count){
            if(isMergedRegion(sheet,size-1,0)){
                size++;
            }else {
                break;
            }
        }
        return size-1;
    }


    /**
     *  根据时间来分割行，Map里面存储的是行号, columnIndex用来标识不同的block
     */
    public Map<Integer,Block> separateByDateTime(int headerSize, int dateColumnIndex, int departColumnIndex){
        Map<Integer,Block> blocks = new HashMap<>();

        int listIndex = 0;

        int count = sheet.getLastRowNum()+1;//总行数

        for(int rowIndex = headerSize;rowIndex < count;rowIndex++){
            Row row = sheet.getRow(rowIndex);
            String firstCellValue = getCellValue(row.getCell(dateColumnIndex));

            String secondCellValue = getCellValue(row.getCell(departColumnIndex));
            if(blocks.get(listIndex) == null){
                addBlock(blocks, listIndex, rowIndex, firstCellValue, secondCellValue);
            }else{
                // 如果属于同一段时间，比如都是14点期间，而且是同一个部门，那么就判定为一个总单
                if(getHour(firstCellValue).equals(getHour(blocks.get(listIndex).getFirstCellValue())) && secondCellValue.equals(blocks.get(listIndex).getSecondCellValue())){
                    // 属于上一个block的
                    blocks.get(listIndex).increaseSize();
                }else{
                    // 属于新的block
                    listIndex++;
                    addBlock(blocks,listIndex, rowIndex, firstCellValue, secondCellValue);
                }
            }
        }
        return blocks;
    }


    public String getHour(String time){
        return time.split(" ")[1].split(":")[0];
    }

    /**
     *  根据某列来分割行，Map里面存储的是行号, columnIndex用来标识不同的block
     */
    public Map<Integer,Block> separateByCol(int headerSize, int columnIndex){
        Map<Integer,Block> blocks = new HashMap<>();

        int listIndex = 0;

        int count = sheet.getLastRowNum()+1;//总行数

        for(int rowIndex = headerSize;rowIndex < count;rowIndex++){
            Row row = sheet.getRow(rowIndex);
            String firstCellValue = getCellValue(row.getCell(columnIndex));
            if(blocks.get(listIndex) == null){
                addBlock(blocks, listIndex, rowIndex, firstCellValue);
            }else{
                if(firstCellValue.equals(blocks.get(listIndex).getFirstCellValue())){
                    // 属于上一个block的
                    blocks.get(listIndex).increaseSize();
                }else{
                    // 属于新的block
                    listIndex++;
                    addBlock(blocks,listIndex, rowIndex, firstCellValue);
                }
            }
        }
        return blocks;
    }




    /**
     *  根据合并单元格来分割行，Map里面存储的是行号, columnIndex用来标识不同的block
     */
    public Map<Integer,Block> separate(int headerSize, int columnIndex){
        Map<Integer,Block> blocks = new HashMap<>();

        int listIndex = 0;

        int count = sheet.getLastRowNum()+1;//总行数

        for(int rowIndex = headerSize;rowIndex < count;rowIndex++){
            Row row = sheet.getRow(rowIndex);
            String firstCellValue = getCellValue(row.getCell(columnIndex));
            System.out.println(firstCellValue);
            if(isMergedRegion(sheet,rowIndex,0)){

                if(blocks.get(listIndex) == null){
                    addBlock(blocks, listIndex, rowIndex, firstCellValue);
                }else{
                    if(firstCellValue.equals(blocks.get(listIndex).getFirstCellValue()) || firstCellValue.trim().equals("")){
                        // 属于上一个block的
                        blocks.get(listIndex).increaseSize();
                    }else{
                        // 属于新的block
                        listIndex++;
                        addBlock(blocks,listIndex, rowIndex, firstCellValue);
                    }
                }
            }else {
                // 如果不是合并单元格，就表明只是占一行的block
                listIndex++;
                addBlock(blocks, listIndex, rowIndex, firstCellValue);
            }
        }
        return blocks;
    }



    public void addBlock(Map<Integer,Block> blocks, Integer listIndex, int rowIndex, String firstCellValue){
        Block block = new Block();
        block.setFirstLineNumber(rowIndex);
        block.increaseSize();
        block.setFirstCellValue(firstCellValue);
        blocks.put(listIndex, block);
    }


    public void addBlock(Map<Integer,Block> blocks, Integer listIndex, int rowIndex, String firstCellValue, String secondCellValue){
        Block block = new Block();
        block.setFirstLineNumber(rowIndex);
        block.increaseSize();
        block.setFirstCellValue(firstCellValue);
        block.setSecondCellValue(secondCellValue);
        blocks.put(listIndex, block);
    }


    public void init(String filePath, String fileName){
        // 默认先不传上面两个参数
        File directory = new File("");
        String outputPath = directory.getAbsolutePath() + "/src/" + this.packageOutPath.replace(".", "/") + "/" + fileName;
        try {
            FileInputStream inputStream = new FileInputStream(new File(outputPath));
            boolean isE2007 = false;    //判断是否是excel2007格式
            if(fileName.endsWith("xlsx")){
                isE2007 = true;
            }

            int rowIndex = 0;
            int columnIndex = 0;

            InputStream input = inputStream;  //建立输入流
            Workbook wb  = null;
            //根据文件格式(2003或者2007)来初始化
            if(isE2007){
                wb = new XSSFWorkbook(input);
            }else{
                wb = new HSSFWorkbook(input);
            }
            sheet = wb.getSheetAt(0);    //获得第一个表单

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     *  从test可以看出实际上 如test.xlsx的一二行，合并的单元格一二行都是一样的值，合并但是分行的单元格是不一样的值
     */
    public void test(){
        File directory = new File("");
        String outputPath = directory.getAbsolutePath() + "/src/" + this.packageOutPath.replace(".", "/") + "/test.xlsx";
        try {
            FileInputStream inputStream = new FileInputStream(new File(outputPath));
            testRead(inputStream, "test.xlsx");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public String testRead( InputStream inputStream, String fileName)
            throws Exception{
        String message = "Import success";

        boolean isE2007 = false;    //判断是否是excel2007格式
        if(fileName.endsWith("xlsx")){
            isE2007 = true;
        }

        int rowIndex = 0;
        int columnIndex = 0;
        try {
            InputStream input = inputStream;  //建立输入流
            Workbook wb  = null;
            //根据文件格式(2003或者2007)来初始化
            if(isE2007){
                wb = new XSSFWorkbook(input);
            }else{
                wb = new HSSFWorkbook(input);
            }
            Sheet sheet = wb.getSheetAt(0);    //获得第一个表单

            //System.out.println("总行数:"+sheet.getLastRowNum());

            List<CellRangeAddress> cras = getCombineCell(sheet);
            //isMergedRegion(Sheet sheet,int row ,int column);判断是不是合并单元格\
            int count = sheet.getLastRowNum()+1;//总行数
            System.out.println(" 总行数 是  " + count);

            for(int i = 0; i < 2;i++){
                rowIndex = i;
                Row row = sheet.getRow(i);

                for(int j = 0; j < 13;j++){

                    System.out.println("是否合并单元格: " + isMergedRegion(sheet,i,j));
                    if(isMergedRegion(sheet,i,j)){
                        System.out.println("单元格的值是: " + getMergedRegionValue(sheet,i,j));

                    }
                    System.out.println("第" + i + "行" + "第" + j + "列" + "的值是： "  + getCellValue(row.getCell(j)));
                }
                System.out.println("================");
            }

            System.out.println(" 第一行第一个单元 是  " + count);


        } catch (Exception ex) {
            //xr.setMessage("Import failed, please check the data in "+rowIndex+" rows "+columnIndex+" columns ");
            message =  "Import failed, please check the data in "+rowIndex+" rows ";
        }
        return message;
    }




    /**
     * 获取单元格的值
     * @param cell
     * @return
     */
    public String getCellValue(Cell cell){
        if(cell == null) return "";
        if(cell.getCellType() == Cell.CELL_TYPE_STRING){
            return cell.getStringCellValue();
        }else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
            return String.valueOf(cell.getBooleanCellValue());
        }else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){
            return cell.getCellFormula() ;
        }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
            return String.valueOf(cell.getNumericCellValue());
        }
        return "";
    }



    /**
     * 合并单元格处理,获取合并行
     * @param sheet
     * @return List<CellRangeAddress>
     */
    public List<CellRangeAddress> getCombineCell(Sheet sheet)
    {
        List<CellRangeAddress> list = new ArrayList<CellRangeAddress>();
        //获得一个 sheet 中合并单元格的数量
        int sheetmergerCount = sheet.getNumMergedRegions();
        //遍历所有的合并单元格
        for(int i = 0; i<sheetmergerCount;i++)
        {
            //获得合并单元格保存进list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }

    private int getRowNum(List<CellRangeAddress> listCombineCell,Cell cell,Sheet sheet){
        int xr = 0;
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        for(CellRangeAddress ca:listCombineCell)
        {
            //获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if(cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR)
            {
                if(cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC)
                {
                    xr = lastR;
                }
            }

        }
        return xr;

    }
    /**
     * 判断单元格是否为合并单元格，是的话则将单元格的值返回
     * @param listCombineCell 存放合并单元格的list
     * @param cell 需要判断的单元格
     * @param sheet sheet
     * @return
     */
    public String isCombineCell(List<CellRangeAddress> listCombineCell,Cell cell,Sheet sheet)
            throws Exception{
        int firstC = 0;
        int lastC = 0;
        int firstR = 0;
        int lastR = 0;
        String cellValue = null;
        for(CellRangeAddress ca:listCombineCell)
        {
            //获得合并单元格的起始行, 结束行, 起始列, 结束列
            firstC = ca.getFirstColumn();
            lastC = ca.getLastColumn();
            firstR = ca.getFirstRow();
            lastR = ca.getLastRow();
            if(cell.getRowIndex() >= firstR && cell.getRowIndex() <= lastR)
            {
                if(cell.getColumnIndex() >= firstC && cell.getColumnIndex() <= lastC)
                {
                    Row fRow = sheet.getRow(firstR);
                    Cell fCell = fRow.getCell(firstC);
                    cellValue = getCellValue(fCell);
                    break;
                }
            }
            else
            {
                cellValue = "";
            }
        }
        return cellValue;
    }

    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row
     * @param column
     * @return
     */
    public String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();

        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }

        return null ;
    }


    /**
     * 判断指定的单元格是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    private boolean isMergedRegion(Sheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }





}

@Data
class Block{

    private Integer firstLineNumber;

    private Integer size = 0;

    private String firstCellValue;

    private String secondCellValue;

    public void increaseSize(){
        size++;
    }
}
