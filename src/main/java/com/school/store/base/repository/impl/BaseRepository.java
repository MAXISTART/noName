package com.school.store.base.repository.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.school.store.base.model.SqlParams;
import com.school.store.base.repository.IBaseRepository;
import com.school.store.base.utils.BlankUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
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
	public List<T> findByDynamicSqlParams(String tableName, SqlParams sqlParams, int page, int rows, Class clazz) {
		Session session = entityManager.unwrap(Session.class);
		SQLQuery query = session.createSQLQuery("SELECT * FROM " + tableName + " " + sqlParams.getSql());
		List<SqlParams.SqlParam> Ps = sqlParams.params;
		if (Ps != null && !Ps.isEmpty()) {
			for(int paramIndex=0; paramIndex < Ps.size(); paramIndex++){
				query.setParameter(paramIndex, Ps.get(paramIndex).getValue());
			}
		}
		// setResultTransformer 是设置结果集映射到什么地方
		return query.setFirstResult((page - 1)*rows).setMaxResults(rows).setResultTransformer(Transformers.aliasToBean(clazz)).list();
	}

}
