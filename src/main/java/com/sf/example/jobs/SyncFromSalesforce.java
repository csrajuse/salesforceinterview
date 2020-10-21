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
public class SyncFromSalesforce {

    private static final Logger logger = LoggerFactory.getLogger("SyncFromSalesforce");

    @Autowired
    private SyncService syncService;

    @Autowired
    private SalesforceSyncfromRepository salesforceSyncfromRepository;

    @Value("${objects_to_sync}")
    private String objectsToSync;

    @Scheduled(fixedRate= 10*60*1000)
    public void syncObjects(){
        try {
            List<String> objectNames = Arrays.asList(objectsToSync.split(","));

            for(int i =0;i<objectNames.size();i++) {
                logger.info("Syncing objects from salesforce:"+objectNames.get(i));
                syncService.syncFromSalesForce(objectNames.get(i),salesforceSyncfromRepository.fetchLatestSyncTime(objectNames.get(i)));
            }
        }catch(Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
    }
}
