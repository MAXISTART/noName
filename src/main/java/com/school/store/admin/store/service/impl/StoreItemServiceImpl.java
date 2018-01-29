package com.school.store.admin.store.service.impl;

import com.school.store.admin.store.entity.StoreItem;
import com.school.store.admin.store.service.StoreItemCustom;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.Map;

public class StoreItemServiceImpl implements StoreItemCustom{


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly=false)
    public int executesqString(String sqString, Map<Integer, Object> params) {
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session.createQuery(sqString);
        if (params != null && !params.isEmpty()) {
            for (int key : params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        return query.executeUpdate();
    }


    // 实现find
    public StoreItem findByGoodId(String goodId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<StoreItem> query = builder.createQuery(StoreItem.class);
        Root<StoreItem> root = query.from(StoreItem.class);
        query.where(builder.equal(root.get("goodId"), goodId));
        return entityManager.createQuery(query.select(root)).getResultList().get(0);
    }

    @Override
    public boolean addNumber(String goodId, Integer number) {
        String sql = "UPDATE StoreItem SET number = number + ? WHERE goodId = ?";
        Map<Integer, Object> map = new HashMap<>();
        map.put(0, number);
        map.put(1, goodId);
        if(executesqString(sql, map) > 0){
            // 受影响的行数大于 1
            return true;
        }
        return false;
    }

    @Override
    public boolean reduceNumber(String goodId, Integer number) {
        StoreItem storeItem = findByGoodId(goodId);
        if(storeItem.getNumber() < number){
            // 如果库存所含数量小于要减少的数量
            return false;
        }
        String sql = "UPDATE StoreItem SET number = number - ? WHERE goodId = ?";
        Map<Integer, Object> map = new HashMap<>();
        map.put(0, number);
        map.put(1, goodId);
        if(executesqString(sql, map) > 0){
            // 受影响的行数大于 1
            return true;
        }
        return true;
    }

    @Override
    public boolean setLockNumber(String goodId, Integer lockNumber){
        String sql = "UPDATE StoreItem SET lockNumber = ? WHERE goodId = ?";
        Map<Integer, Object> map = new HashMap<>();
        map.put(0, lockNumber);
        map.put(1, goodId);
        return true;
    }
}
