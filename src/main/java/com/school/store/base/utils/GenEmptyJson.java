package com.school.store.base.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.school.store.enums.ResultEnum;
import sun.applet.Main;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GenEmptyJson {


    private String packageName = "com.school.store.base.utils";
    private String packageOutPath = "main.java." + packageName;//指定实体生成所在包的路径

    // 存储所有entity的class文件的物理路径
    private List<String> classPaths = new ArrayList<>();

    // 存储所有entity的包路径
    private List<String> packagePaths = new ArrayList<>();

    public static void main(String[] args){
        GenEmptyJson gen = new GenEmptyJson();
        try {
            // 先搜索所有的entity包路径
            gen.searchClass();
            // 然后print
            gen.doPrint();

        }catch (Exception e){e.printStackTrace();}
    }



    public String bean2Json2(Object entity){
        String jsonString = JSON.toJSONString(entity,
                SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        return jsonString;
    }



    public void doPrint(){
        try {
            File directory = new File("");
            String outputPath = directory.getAbsolutePath() + "/src/" + this.packageOutPath.replace(".", "/") + "/genFiles/" + "jsgen.txt";

            String path1 = outputPath;

            File file1 = new File(path1);

            PrintWriter pw = new PrintWriter(file1);
            String str = null;
            for(String entityPath : packagePaths){
                pw.print("//" + getEntityName(entityPath));
                Class cls = Class.forName(entityPath);
                pw.print("\r\n");
                pw.print(bean2Json2(cls.newInstance()));
                pw.print("\r\n");
                pw.print("\r\n");
                pw.print("\r\n");
            }
            for (ResultEnum resultEnum : getAllResultEnum()){
                pw.print(bean2Json2(resultEnum).replaceAll("\"", "") + ":" + resultEnum.toJson());
                pw.print(",");
                pw.print("\r\n");
            }
            pw.print("\r\n");
            pw.print("\r\n");
            pw.print("\r\n");
            pw.flush();
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<ResultEnum> getAllResultEnum(){
        List<ResultEnum> resultEnums = new ArrayList<>();
        for(ResultEnum resultEnum : ResultEnum.values()){
            resultEnums.add(resultEnum);
        }
        return resultEnums;
    }


    public String getEntityName(String fullName){
        String[] parts = fullName.split("\\.");
        return parts[parts.length - 1];
    }


    public void searchClass() throws ClassNotFoundException {
        //包名
        String basePack = "com.school.store";
        //先把包名转换为路径,首先得到项目的classpath
        String classpath = Main.class.getResource("/").getPath();
        //然后把我们的包名basPach转换为路径名
        basePack = basePack.replace(".", File.separator);
        //然后把classpath和basePack合并
        String searchPath = classpath + basePack;
        doPath(new File(searchPath));
        for (String s : classPaths) {
            //把 D:\work\code\20170401\search-class\target\classes\com\baibin\search\a\A.class 这样的绝对路径转换为全类名com.baibin.search.a.A
            s = s.replace(classpath.replace("/","\\").replaceFirst("\\\\",""),"").replace("\\",".").replace(".class","");
            String[] parts = s.split("\\.");
            if(parts[parts.length - 2].equals("entity")){
                packagePaths.add(s);
            }
        }
    }


    /**
     * 该方法会得到所有的类，将类的绝对路径写入到classPaths中
     * @param file
     */
    private void doPath(File file) {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                doPath(f1);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件而且路径是包含entity的字眼的我们就放入我们的集合中。
                if(file.getParentFile().getName().equals("entity")){
                    classPaths.add(file.getPath());
                }
            }
        }
    }


}
