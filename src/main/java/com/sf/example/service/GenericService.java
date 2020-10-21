package com.sf.example.service;

import com.sf.example.entity.GenericObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface GenericService {
    GenericObject find(String id);
    List<GenericObject> findAll();
    void saveObject(String objectName,Map<String,String> columnValues) throws SQLException;
}
