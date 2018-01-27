package com.school.store.admin.girl.entity;



import com.school.store.base.model.BaseEntity;
import com.school.store.admin.boy.entity.Boy;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


@Entity
@Table(name = "girls")
@Data
public class Girl extends BaseEntity {



    @Column(name = "cupSize" , length = 255)
    private Integer cupSize;

    @Column(name = "age", length = 255)
    private Integer age;

    @Column(name = "a_id", length = 255)
    private Integer aid;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "boy_id")
    private Boy boy;

}
