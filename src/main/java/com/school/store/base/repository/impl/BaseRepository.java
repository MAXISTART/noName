package com.school.store.base.repository.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import com.school.store.annotation.CascadeDelete;
import com.school.store.base.model.SqlParams;
import com.school.store.base.repository.IBaseRepository;
import com.school.store.base.utils.BlankUtil;
import com.school.store.utils.HttpUtil;
import com.school.store.utils.ReflectUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;



public class BaseRepository<T,ID extends Serializable> extends SimpleJpaRepository<T, ID> implements IBaseRepository<T, ID> {

	private final EntityManager entityManager;
	   
    public BaseRepository(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}

    @Transactional(readOnly=false)
	@Override
	public void update(T o) {
    	Session session = entityManager.unwrap(Session.class);
    	session.update(o);
	}
    
	@Transactional(readOnly=false)
	@Override
	public void saveOrUpdate(T o) {
		entityManager.merge(o);
	}

	@Transactional(readOnly=false)
	@Override
	public T get(Class<T> c, Serializable id) {
		return entityManager.find(c, id);
	}

	@Transactional(readOnly=false)
	@Override
	public T get(String sqString) {
		  Query query = entityManager.createQuery(sqString);
		  @SuppressWarnings("unchecked")
		  List<T> list = query.getResultList();
		  if(BlankUtil.isNotBlank(list))
		  {
			  return list.get(0);
		  }
		  else{
			  return null;
		  }
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<T> find(String sqString) {
		Query query = entityManager.createQuery(sqString);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<T> find(String sqString, int page, int rows) {
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session.createQuery(sqString);        
		return query.setFirstResult((page - 1)*rows).setMaxResults(rows).list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<T> find(String sqString, Map<String, Object> params, int page, int rows) {
		    Session session = entityManager.unwrap(Session.class);
	        org.hibernate.Query query = session.createQuery(sqString); 
	        if (params != null && !params.isEmpty()) {
				for (String key : params.keySet()) {
					query.setParameter(key, params.get(key));
				}
			}
			return query.setFirstResult((page - 1)*rows).setMaxResults(rows).list();
	}

	@Transactional(readOnly=false)
	@Override
	public Long count(String sqString) {
		 Session session = entityManager.unwrap(Session.class);
		 org.hibernate.Query query = session.createQuery(sqString); 
		return (Long)query.uniqueResult();
	}

	@Transactional(readOnly=false)
	@Override
	public Long count(String sqString, Map<String, Object> params) {
		Session session = entityManager.unwrap(Session.class);
		org.hibernate.Query query = session.createQuery(sqString);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
			}
		}
		return (Long) query.uniqueResult();
	}

	@Transactional(readOnly=false)
	@Override
	public int executesqString(String sqString) {
		Session session = entityManager.unwrap(Session.class);
		org.hibernate.Query query = session.createQuery(sqString);
		return query.executeUpdate();
	}

	@Transactional(readOnly=false)
	@Override
	public int executesqString(String sqString, Map<String, Object> params) {
		Session session = entityManager.unwrap(Session.class);
		org.hibernate.Query query = session.createQuery(sqString);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				query.setParameter(key, params.get(key));
			}
		}
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<Object[]> findBySql(String sql, Map<String, Object> params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				q.setParameter(key, params.get(key));
			}
		}
		return q.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<Object[]> findBySql(String sql, Map<String, Object> params, int page, int rows) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				q.setParameter(key, params.get(key));
			}
		}
		return q.setFirstResult((page - 1)*rows).setMaxResults(rows).list();
	}

	@Transactional(readOnly=false)
	@Override
	public int executeSql(String sql) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		return q.executeUpdate();
	}

	@Transactional(readOnly=false)
	@Override
	public int executeSql(String sql, Map<String, Object> params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				q.setParameter(key, params.get(key));
			}
		}
		return q.executeUpdate();
	}

	@Transactional(readOnly=false)
	@Override
	public BigInteger countBySql(String sql) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		return (BigInteger) q.uniqueResult();
	}

	@Transactional(readOnly=false)
	@Override
	public BigInteger countBySql(String sql, Map<String, Object> params) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery q = session.createSQLQuery(sql);
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				q.setParameter(key, params.get(key));
			}
		}
		return (BigInteger) q.uniqueResult();
	}

	@Transactional(readOnly=false)
	@Override
	public void batchSave(List<T> entitys) throws SQLException {
		Session session = entityManager.unwrap(Session.class);
		for (int i = 0; i < entitys.size(); i++) {
			session.save(entitys.get(i));
			if (i % 20 == 0) {
				// 20个对象后才清理缓存，写入数据库
				session.flush();
				session.clear();
			}
		}
		// 最后清理一下----防止大于20小于40的不保存
		session.flush();
		session.clear();
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly=false)
	@Override
	public List<T> findByDynamicSqlParams(SqlParams sqlParams, int page, int rows, Class clazz) {
    	Table table = (Table) clazz.getAnnotation(Table.class);
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery("SELECT * FROM " + table.name() + " " + sqlParams.getSql());
		System.out.println("SELECT * FROM " + table.name() + " " + sqlParams.getSql());
		List<SqlParams.SqlParam> Ps = sqlParams.params;
		List<String> values = sqlParams.values;
		if (Ps != null && !Ps.isEmpty()) {
			for(int valueIndex=0; valueIndex < values.size(); valueIndex++){
				query.setParameter(valueIndex, values.get(valueIndex));
			}
		}
		// setResultTransformer 是设置结果集映射到什么地方
		// 注意，数据库中的列名和 实体的属性名 必须一致， 实体类可以比表的列的数量少
		return query.setFirstResult((page - 1)*rows).setMaxResults(rows).setResultTransformer(Transformers.aliasToBean(clazz)).list();
	}



	@Transactional(readOnly=false) //这句话必不可少，不然就会造成事务出错，因为默认事务是readOnly = true
	@Override
	public void dynamicUpdate(T entity) {
		System.out.println("in dynamicUpdate");
		if (entity == null){
			return;
		}
		try {
			// 根据注解来获取entity的表名 和 列名
			String tableName = entity.getClass().getName();

			Field[] fields = ReflectUtil.getAllFields(entity);

			Map<String, Object> params = new HashMap<>();

			for (Field field : fields) {
				field.setAccessible(true);
				String columnName = null;
				Object columnValue = null;
				if(field.isAnnotationPresent(Column.class)){
					// 如果这个属性上面有column的属性，就获取他的属性值
					columnName = field.getName();
					columnValue = field.get(entity);
				}
				if(columnValue == null || columnName.equals("createTime")
						|| columnName.equals("createBy")
						|| columnName.equals("lastmodifiedTime")
						|| columnName.equals("lastmodifiedBy") ){
					continue;
				}else{
					params.put(columnName, columnValue);
				}
			}
			params.put("lastmodifiedTime", new Date());
			params.put("lastmodifiedBy", HttpUtil.getSessionUserId());

			StringBuilder sqlBuidler = new StringBuilder("");
			sqlBuidler.append("UPDATE ").append(tableName).append(" t ").append("SET");

			for(String key : params.keySet()){
				if(key.equals("id")){
					continue;
				}
				sqlBuidler.append(" t.").append(key).append("= :" + key + ",");
			}
			String sql = sqlBuidler.substring(0,sqlBuidler.length()-1) + " WHERE t.id = :id";

			System.out.println(sql);
			System.out.println(params);
			executesqString(sql, params);
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	@Transactional(readOnly=false) //这句话必不可少，不然就会造成事务出错，因为默认事务是readOnly = true
	@Override
	public void cascadeDelete(Object entity){
    	if(entity == null){
    		return;
		}
		// 先删除下级
		deleteCascade(entity);
		// 再删除自己
		Session session = entityManager.unwrap(Session.class);
		org.hibernate.Query query = session.createQuery("DELETE FROM " + entity.getClass().getName() +
						" WHERE id='" + ReflectUtil.getFieldValueByName(entity, "id") + "'");
		query.executeUpdate();
	}


	@Transactional(readOnly=false) //这句话必不可少，不然就会造成事务出错，因为默认事务是readOnly = true

	public void deleteCascade(Object entity) {
    	if(entity == null){
    		return;
		}
		try {

			for(CascadeDelete cascade : entity.getClass().getAnnotationsByType(CascadeDelete.class)){

				String filter = cascade.filter();
				String[] args = cascade.args();

				if(cascade.value().isAnnotationPresent(CascadeDelete.class)){
					System.out.println("cascade.value() : "+cascade.value());
					// 如果删除的下级还有下级，那么就要递归删除了
					// 先找到这些下级对象，对他们进行 级联删除 ，最后再删除自己。
					// 获取下级的表名先
					Table table = (Table) cascade.value().getAnnotation(Table.class);
					Session session = entityManager.unwrap(Session.class);
					SQLQuery query_find = session.createSQLQuery("SELECT * FROM " + table.name() + " WHERE " + filter);
					//System.out.println("query_find : "+query_find);
					int valueIndex = 0;
					for(String argName : args){
						Object argValue = ReflectUtil.getFieldValueByName(entity, argName);
						//System.out.println("argValue is " + argValue);
						query_find.setParameter(valueIndex, argValue);
						valueIndex++;
					}
					//System.out.println("找到的数量为： "+query_find.setResultTransformer(Transformers.aliasToBean(cascade.value())).list().size());

					query_find.setResultTransformer(Transformers.aliasToBean(cascade.value())).list().forEach(P -> {
						//System.out.println(P);
						deleteCascade(P);
					});
				}

				StringBuilder sqlBuidler = new StringBuilder("");
				sqlBuidler.append("DELETE FROM ").append(cascade.value().getName())
						.append(" WHERE ").append(filter);
				// 删除
				Session session = entityManager.unwrap(Session.class);
				org.hibernate.Query query = session.createQuery(sqlBuidler.toString());
				int index = 0;
				for(String argName : args){
					Object argValue = ReflectUtil.getFieldValueByName(entity, argName);
					query.setParameter(index, argValue);
					index++;
				}
				query.executeUpdate();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
