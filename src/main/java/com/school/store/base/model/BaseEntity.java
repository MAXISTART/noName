package com.school.store.base.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

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
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", nullable = false, length = 36)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	protected String id;


	/**
	 * 创建时间
	 */
	@Column(name = "createTime", updatable = false)
	@CreatedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date createTime;
	/**
	 * 创建人
	 */
	@Column(name = "createBy", updatable = false)
	@CreatedBy
	private String createBy;
	/**
	 * 修改时间
	 */
	@Column(name = "lastmodifiedTime")
	@LastModifiedDate
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)   //获取数据库时间格式
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")   //Hibernate中@ResponseBody返回的时间格式
	private Date lastmodifiedTime;
	/**
	 * 修改人
	 */
	@Column(name = "lastmodifiedBy")
	@LastModifiedBy
	private String lastmodifiedBy;

}
