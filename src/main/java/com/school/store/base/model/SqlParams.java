package com.school.store.base.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class SqlParams {


    public StringBuilder sqlBuilder = new StringBuilder("WHERE 1=1");

    public List<String> values = new ArrayList<>();

    public static void main(String[] args){

    }

    public void put(String sql){
        sqlBuilder.append(sql);
    }

    public void putValue(String... values_input){
        for(String value : values_input){
            values.add(value);
        }
    }

    public String getSql(){
        return sqlBuilder.toString();
    }
}
