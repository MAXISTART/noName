package com.school.store.admin.boy.entity;


import com.school.store.base.model.BaseEntity;
import com.school.store.admin.girl.entity.Girl;
import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "boys")
@Data
public class Boy extends BaseEntity {



    @Column(name = "cupSize" , length = 255)
    private Integer cupSize;

    @Column(name = "age", length = 255)
    private Integer age;

    @Column(name = "a_id", length = 255)
    private Integer aid;

    @Transient
    private List<Girl> girls;

}
