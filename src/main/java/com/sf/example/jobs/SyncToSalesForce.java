package com.sf.example.jobs;

import com.sf.example.repository.SalesforceSyncfromRepository;
import com.sf.example.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SyncToSalesForce {

    @Autowired
    private SyncService syncService;

    @Autowired
    private SalesforceSyncfromRepository salesforceSyncfromRepository;

    private static final Logger logger = LoggerFactory.getLogger("SyncToSalesForce");

    @Value("${objects_to_sync}")
    private String objectsToSync;

    @Scheduled(fixedRate= 10*60*1000)
    public void syncToSalesforce(){
        try {
            List<String> objectNames = Arrays.asList(objectsToSync.split(","));

            for(int i =0;i<objectNames.size();i++) {
                logger.info("Syncing objects to salesforce:"+objectNames.get(i));
                syncService.syncToSalesForce(objectNames.get(i),salesforceSyncfromRepository.fetchLatestSyncTime(objectNames.get(i)));
            }
        }catch(Exception ex){
            logger.error(ex.getMessage());
        }
    }
}
