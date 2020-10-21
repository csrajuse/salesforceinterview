package com.sf.example.service;

import com.sf.example.repository.AnyObjectRepository;
import com.sf.example.repository.SalesforceSyncfromRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Purpose of this service class is to pull and push data based on the last saved date.
 */

@Service
public class SyncService extends BaseSyncService{

    @Autowired
    private AnyObjectRepository anyObjectRepository;

    @Autowired
    private SalesforceSyncfromRepository salesforceSyncfromRepository;

    private static final String dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String toDateFormat = "YYYY-MM-dd'T'hh:mm:ss'Z'";

    /**
     * Date String in the format YYYY-MM-DDThh:mm:ssZ. The timezone does not matter at this point as we will be always comparing
     * what is stored in the database with the same column. Assuming it is always stored in the same timezone this should be
     * sufficient.
     *
     * @param datetimeStr
     */
    public void syncFromSalesForce(String objectName,String datetimeStr){
        try {
            List<Map<String, String>> objectsToSaveList = this.syncObjectFromSalesForce(objectName, datetimeStr);
            SimpleDateFormat revievedDateFormat = new SimpleDateFormat(dateFormat);
            List<Date> lastModifiedDatesList = objectsToSaveList.stream()
                    .map(map->map.get("LastModifiedDate"))
                    .map(dateStr-> {
                        try {
                            return revievedDateFormat.parse(dateStr);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());

            if(lastModifiedDatesList.size()>0) {
                Date maxDate = Collections.max(lastModifiedDatesList);
                SimpleDateFormat toDateFormtter = new SimpleDateFormat(toDateFormat);
                String lastModifiedToStore = toDateFormtter.format(maxDate);

                salesforceSyncfromRepository.save(objectName, lastModifiedToStore);
            }

            anyObjectRepository.saveAll(objectName, objectsToSaveList);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void syncToSalesForce(String objectName,String datetimeStr){

    }
}
