package com.sf.example.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * For having any object be stored in the database and sync, we will be having a generic object that can store using meta-data.
 */


public class GenericObject extends BaseEntityObject{

    private Map<String,String> dataMap = new HashMap<>();

    public void setData(String key,String value){
        dataMap.put(key,value);
    }

    public String getData(String key){
        return dataMap.get(key);
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }
}
