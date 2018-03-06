package com.school.store.base.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class SqlParams {


    public List<SqlParam> params;

    public List<String> values;

    public static void main(String[] args){
        SqlParams factory = new SqlParams();
        factory.put("AND","name","=");
        factory.put("OR","id",">");
        System.out.println(factory.getSql());
    }

    public void put(String relation, String columnName, String keyWord){
        if(params == null){
            params = new ArrayList<SqlParam>();
        }
        params.add(new SqlParam(relation, columnName, keyWord));
    }

    public void putValue(String... values_input){
        if(values == null){
            values = new ArrayList<>();
        }
        for(String value : values_input){
            values.add(value);
        }
    }

    public String getSql(){
        if(params != null && !params.isEmpty()){
            StringBuilder builder = new StringBuilder("WHERE 1=1");
            for(int i=0; i<params.size(); i++){
                builder.append(" ");
                // 保证where后面没有关系词
                String relation = params.get(i).getRelation();
                builder.append(relation).append(" ");
                if(relation.equals("ORDER BY")){
                    // 如果是排序，那么就不需要加参数问号了
                    builder.append(params.get(i).getColumnName()).append(" ");
                    builder.append(params.get(i).getKeyWord()).append(" ");
                    continue;
                }
                builder.append(params.get(i).getColumnName()).append(" ");
                if(params.get(i).getKeyWord().equals("BETWEEN")){
                    builder.append(params.get(i).getKeyWord())
                            .append(" ")
                            .append("?")
                            .append(" ")
                            .append("AND")
                            .append(" ")
                            .append("?");
                }else{
                    builder.append(params.get(i).getKeyWord())
                            .append(" ")
                            .append("?");
                }

            }
            return builder.toString();
        }else{
            return "";
        }
    }



    // 这是每个Param
    @Data
    public class SqlParam {     //内部类
        // 是or还是and
        private String relation;
        // 数据库中的字段名
        private String columnName;
        // 关系关键词
        private String keyWord;


        public SqlParam(String relation, String columnName, String keyWord) {
            this.columnName = columnName;
            this.keyWord = keyWord;
            this.relation = relation;
        }
    }
}
