package com.school.store.admin.store.service;

public interface StoreItemCustom {

    public boolean addNumber(String goodId , Integer number);
    public boolean reduceNumber(String goodId , Integer number);
    public boolean setLockNumber(String goodId, Integer lockNumber);
}
