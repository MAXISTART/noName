package com.school.store.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @MappedSuperclass用在父类上面。当这个类肯定是父类时，加此标注。如果改成@Entity，则继承后，多个类继承，只会生成�?个表�?
 * 而不是多个继承，生成多个�?
 * @author Administrator
 *
 */
@MappedSuperclass
@Data
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, length = 36)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	protected String id;
	
	//删除标示
	@Column(name = "delFlag")
	protected boolean delFlag;

	// 创建时间，最后修改时间，创建者 和 最后修改者
	@Column(name = "createTime", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date createTime;

	@Column(name = "updateTime")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date updateTime;

	@Column(name = "createAdmin", length = 255, updatable = false)
	private String createAdmin;
	@Column(name = "updateAdmin", length = 255)
	private String updateAdmin;


}
