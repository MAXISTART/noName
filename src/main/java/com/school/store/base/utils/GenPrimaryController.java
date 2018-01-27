package com.school.store.base.utils;

import java.io.*;

public class GenPrimaryController {

    private String packageName = "com.school.store.base.utils";
    private String packageOutPath = "main.java." + packageName;//指定实体生成所在包的路径


    public static void main(String[] args) {


        GenPrimaryController gen = new GenPrimaryController();
        gen.doReplace("storeItem");

    }


    public void doReplace(String entityName){
        try {
            File directory = new File("");
            String inputPath = directory.getAbsolutePath() + "/src/" + this.packageOutPath.replace(".", "/") + "/genFiles/" + "muban.txt";
            String outputPath = directory.getAbsolutePath() + "/src/" + this.packageOutPath.replace(".", "/") + "/genFiles/" + "gen.txt";

            String path = inputPath;
            String path1 = outputPath;

            File file = new File(path);
            File file1 = new File(path1);

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(file1);
            String str = null;
            while ((str = br.readLine()) != null) {
                pw.print(str.replaceAll("Xxx", initcap(entityName)).replaceAll("xxx", entityName));
                pw.print("\r\n");
            }
            pw.flush();
            pw.close();
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 功能：将输入字符串的首字母改成大写
     *
     * @param str
     * @return
     */
    private String initcap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }

}
