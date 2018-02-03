package com.school.store.base.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private String packageName = "com.school.store.base.excel";
    private String packageOutPath = "main.java." + packageName;//指定实体生成所在包的路径

    Sheet sheet;

    /**
     *  要做的两个功能
     *  重点说下，下面的配置文件用  config.java 或者 constance.java  来编写更好（尽量用到生成器去生成代码），然后注入到 BaseController 中
     *
     *  1. 实现数据库资料的转移，需要写个配置文件，配置简道云上不同表单对应的不同实体名称以及对应的mapper的生成
     *  2. 实现自身数据库的选择字段导出模板以及导入文件，实现一个别名接口（数据库和实体都是保存的是字段英文名，
     *      需要匹配到中文名，其实本质也是mapper）
     *
     */

    public static void main(String[] args){
        ExcelUtils utils = new ExcelUtils();
        utils.init(null, "test.xlsx");
        Map<String, Integer> mapper = new HashMap<>();
        mapper.put("totalPrice", 9);
        mapper.put("details.name", 3);
        List<Map> beans = utils.getBeanMaps(mapper, utils.getHeaderSize());
        System.out.println(beans);

    }


    public List<Map> getBeanMaps(Map<String, Integer> mapper, int headerSize){

        if(sheet == null){
            return null;
        }

        // 获取头部最后一行
        Row headerLastRow = sheet.getRow(headerSize - 1);

        int count = sheet.getLastRowNum()+1;//总行数
        //int count = 4;//总行数

        List<Map> list = new ArrayList<>();

        for(int rowIndex = headerSize; rowIndex < count; rowIndex++){
            Row row = sheet.getRow(rowIndex);

            Map bean = new HashMap();
            for(String keyName : mapper.keySet()){
                // 获取所在列
                int columnIndex = mapper.get(keyName);
                String keyValue = getCellValue(row.getCell(columnIndex));
                resolve(keyName, keyValue, bean);
            }
            list.add(bean);
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
