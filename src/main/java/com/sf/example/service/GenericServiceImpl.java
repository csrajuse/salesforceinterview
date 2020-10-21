package com.sf.example.service;

import com.sf.example.entity.GenericObject;
import com.sf.example.repository.AnyObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class GenericServiceImpl implements GenericService{

    @Autowired
    private AnyObjectRepository anyObjectRepository;
    @Override
    public GenericObject find(String id) {
        return null;
    }

    @Override
    public List<GenericObject> findAll() {
        return null;
    }

    @Override
    public void saveObject(String objectName, Map<String, String> columnValues) throws SQLException {
        anyObjectRepository.save(objectName,columnValues);
    }
}
