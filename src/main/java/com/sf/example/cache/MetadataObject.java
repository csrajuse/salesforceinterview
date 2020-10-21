package com.sf.example.cache;

import java.util.Map;

/**
 * Object that is cacheable for avoiding frequent visits to the database for loading the metadata.
 */
public class MetadataObject {

    private  String objectName;

    private Map<String,String> objectsToColumnNames;

    private Map<String,String> columnNamesToObject;

    public MetadataObject(String objectName,Map<String,String> objectsToColumnNames, Map<String,String> columnNamesToObject){
        this.objectName = objectName;
        this.objectsToColumnNames = objectsToColumnNames;
        this.columnNamesToObject = columnNamesToObject;
    }

    public String getObjectName() {
        return objectName;
    }

    public Map<String, String> getObjectsToColumnNames() {
        return objectsToColumnNames;
    }

    public Map<String, String> getColumnNamesToObject() {
        return columnNamesToObject;
    }
}
