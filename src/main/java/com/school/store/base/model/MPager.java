package com.school.store.base.model;

import lombok.Data;

import java.util.List;

@Data
public class MPager<T> {

    // 获取得到的数据总数
    private Integer total;

    // 当前是第几页
    private Integer page;

    // 每页多少数据
    private Integer pageSize;

    // 数据
    private List<T> data;

}
