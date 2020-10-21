package com.sf.example.repository;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

class MetadataRepositoryTest {

    @Test
    void generateMetadataSQL() {
        MetadataRepository repository = new MetadataRepository();
        Set<String> columnNames = new HashSet();
        //Looping through and adding a bunch of column names.
        columnNames.add("Id");
        for(int i =2; i < 77 ; i++){
            columnNames.add("Column-Name"+i);
        }

        String objectName = "TestObject";

        String sql = repository.generateMetadataSQL(objectName,columnNames);

        assertEquals(true,sql.contains(objectName.toLowerCase()));
        columnNames.stream().forEach(columnName->{
            assertEquals(true,sql.contains(columnName));
        });
    }
}