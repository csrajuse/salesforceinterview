package com.sf.example.repository;

import com.sf.example.cache.MetadataObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class AnyObjectRepositoryTest {

    @Mock
    MetadataRepository metadataRepository ;

    @InjectMocks
    private AnyObjectRepository anyObjectRepository = new AnyObjectRepository();

    @Test
    void generateInsertSQL_Unit() {

        initMocks(this);

        Map<String,String> objParamsToColumnNames = new HashMap<>();

        objParamsToColumnNames.put("Id","data2");
        objParamsToColumnNames.put("objectName","data1");
        objParamsToColumnNames.put("Name","date3");
        objParamsToColumnNames.put("Phone","date4");

        MetadataObject metadata = Mockito.mock(MetadataObject.class);
        when(metadataRepository.returnMetaData(any(String.class))).thenReturn(metadata);
        when(metadata.getObjectsToColumnNames()).thenReturn(objParamsToColumnNames);

        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("Id","ID1");
        dataMap.put("objectName","Account");
        dataMap.put("Name","something1");
        dataMap.put("Phone","something2");

        String generatedSQL = anyObjectRepository.generateInsertSQL("Account",dataMap);
        assertEquals(true,generatedSQL.contains("Account"));
    }
}