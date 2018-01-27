package com.school.store.base.model;


import java.util.ArrayList;
import java.util.List;

public class SqlParams {


    public List<SqlParam> params;

    public static void main(String[] args){
        SqlParams factory = new SqlParams();
        factory.put("AND","name","=","maxistar1");
        factory.put("OR","id",">","1");
        System.out.println(factory.getSql());
    }

    public void put(String relation, String columnName, String keyWord, String value){
        if(params == null){
            params = new ArrayList<SqlParam>();
        }
        params.add(new SqlParam(relation, columnName, keyWord, value));
    }

    public String getSql(){
        if(params != null && !params.isEmpty()){
            StringBuilder builder = new StringBuilder("WHERE");
            for(int i=0; i<params.size(); i++){
                builder.append(" ");
                if(i != 0){
                    // 保证where后面没有关系词
                    builder.append(params.get(i).getRelation()).append(" ");
                }
                builder.append(params.get(i).getColumnName())
                        .append(" ")
                        .append(params.get(i).getKeyWord())
                        .append(" ")
                        .append("?");
            }
            return builder.toString();
        }else{
            return "";
        }
    }



    // 这是每个Param
    public class SqlParam {     //内部类
        // 是or还是and
        private String relation;
        // 数据库中的字段名
        private String columnName;
        // 关系关键词
        private String keyWord;
        // 字段值
        private Object value;

        public SqlParam(String relation, String columnName, String keyWord, String value) {
            this.columnName = columnName;
            this.keyWord = keyWord;
            this.value = value;
            this.relation = relation;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getKeyWord() {
            return keyWord;
        }

        public void setKeyWord(String keyWord) {
            this.keyWord = keyWord;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }
    }
}
