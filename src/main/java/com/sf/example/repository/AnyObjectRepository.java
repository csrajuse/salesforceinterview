package com.sf.example.repository;

import com.sf.example.DBSource;
import com.sf.example.cache.MetadataObject;
import com.sf.example.repository.util.Cleanup;
import com.sf.example.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnyObjectRepository extends Cleanup {

    private static final String DATA_SUFFIX = "data";
    private static final String dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
    private static final String toDateFormat = "YYYY-MM-dd'T'hh:mm:ss'Z'";
    private static final Logger logger = LoggerFactory.getLogger("AnyObjectRepository");
    //This is to get the object, data1 is the publicKey and data2 is the id
    private final String objectDataByIdSQL = "select * from object where data1=? and data2=?";
    //Get ALL the objects by public key
    private final String objectDataBySQL = "select * from object where data1=?";
    @Autowired
    private DBSource dbSource;
    @Autowired
    private MetadataRepository metadataRepository;

    /**
     * Saves the object into the database. This also takes care of Update. If there is an ID it will update the object.
     *
     * @param objectName
     * @param objectData
     */
    public void save(String objectName, Map<String, String> objectData) {
        //Make Sure there is that object present, if it is not present in the metadata we will be creating the object.

        Connection connection = null;
        Statement st = null;
        try {
            connection = dbSource.getConnection();
            //Updating the lastmodifieddate.
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String gmtTime = simpleDateFormat.format(new Date());
            objectData.put("LastModifiedDate", gmtTime);
            if (objectData.get("Id") == null || fetchObject(objectName, objectData.get("Id")).isEmpty()) {
                //we need to insert the Object as it does not exist in the database.
                if (objectData.get("Id") == null) {
                    //Assign a new ID.
                    objectData.put("Id", UUID.randomUUID().toString());
                }
                String sql = generateInsertSQL(objectName, objectData);
                st = connection.createStatement();
                st.execute(sql);
            } else {
                update(connection, objectName, objectData);
            }

        } catch (Exception ex) {
            logger.error("Error has occured when saving the object:", ex);
        } finally {
            closeStatement(st);
            closeConnection(connection);
        }
    }

    /**
     * Saving multiple objects.
     *
     * @param objectName
     * @param objectsList
     */
    public void saveAll(String objectName, List<Map<String, String>> objectsList) {
        objectsList.forEach(map -> save(objectName, map));
    }

    /**
     * Fetch an object with objectName and ID, for example Account with ID 100
     *
     * @param objectName
     * @param id
     * @return
     */
    public Map<String, String> fetchObject(String objectName, String id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        Map<String, String> objectDataMap = new HashMap<>();
        try {
            connection = dbSource.getConnection();
            MetadataObject metaData = metadataRepository.returnMetaData(objectName);
            Map<String, String> columnNameToObjParams = metaData.getObjectsToColumnNames();
            preparedStatement = connection.prepareStatement(objectDataByIdSQL);
            preparedStatement.setString(1, objectName);
            preparedStatement.setString(2, id);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                objectDataMap = fetchObjectData(columnNameToObjParams, rs);
            }

            return objectDataMap;
        } catch (Exception ex) {
            logger.error("Error fetching an object", ex);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return objectDataMap;
    }

    /**
     * Fetches all objects from the database
     *
     * @param objectName
     * @return
     */
    public List<Map<String, String>> fetchAllObjects(String objectName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Map<String, String>> listOfMap = new ArrayList<>();
        try {
            connection = dbSource.getConnection();

            MetadataObject metaData = metadataRepository.returnMetaData(objectName);
            Map<String, String> columnNameToObjParams = metaData.getColumnNamesToObject();
            preparedStatement = connection.prepareStatement(objectDataBySQL);
            preparedStatement.setString(1, objectName);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                listOfMap.add(fetchObjectData(columnNameToObjParams, rs));
            }

        } catch (Exception ex) {
            logger.error("Error fetching all objects", ex);
        } finally {
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return listOfMap;
    }

    private Map<String, String> update(Connection connection, String objectName, Map<String, String> map) {
        Statement st = null;
        try {
            MetadataObject metaData = metadataRepository.returnMetaData(objectName);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            String gmtTime = simpleDateFormat.format(new Date());

            map.put("LastModifiedDate", gmtTime);

            Map<String, String> objParamsToColumnName = metaData.getObjectsToColumnNames();
            StringBuffer updateSQLBuffer = new StringBuffer("UPDATE object SET ");
            List<String> metadataList = new ArrayList<>(map.keySet());
            updateSQLBuffer.append("data1 = '");
            updateSQLBuffer.append(objectName);
            updateSQLBuffer.append("' , ");
            Collections.sort(metadataList);
            updateSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(metadataItem ->
                    objParamsToColumnName.get(metadataItem) + "='" + map.get(metadataItem) + "'").collect(Collectors.joining(",")));
            updateSQLBuffer.append(" WHERE ");
            updateSQLBuffer.append(" data2 ='");
            updateSQLBuffer.append(map.get(Constants.ID));
            updateSQLBuffer.append("'");

            st = connection.createStatement();
            st.execute(updateSQLBuffer.toString());

        } catch (Exception ex) {
            logger.error("Error updating the object", ex);
        } finally {
            closeStatement(st);
        }
        return map;
    }

    /**
     * For generating the SQL statement that is to be used for inserting the object
     *
     * @param map1
     * @return
     */
    String generateInsertSQL(String objectName, Map<String, String> map1) {
        MetadataObject metadataObject = metadataRepository.returnMetaData(objectName);
        if (metadataObject == null) {
            logger.info("Meta data is not created, creating metadata");
            metadataRepository.generateMetadata(objectName, map1.keySet());
            metadataObject = metadataRepository.returnMetaData(objectName);
        }
        Map<String, String> map = new HashMap<>();
        map1.forEach((key, value) -> {
            if (value != null) {
                map.put(key, value);
            }
        });

        Map<String, String> objParamsToColumnName = metadataObject.getObjectsToColumnNames();
        StringBuffer insertSQLBuffer = new StringBuffer("INSERT INTO object ");
        //Add the data2 as Id as we reference it as the ID of the object.
        insertSQLBuffer.append("(");
        List<String> metadataList = new ArrayList<>(map.keySet());
        metadataList.remove("Id");
        //metadataList.remove("objectName");
        insertSQLBuffer.append("data1,data2,");
        Collections.sort(metadataList);
        insertSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(metadataItem -> objParamsToColumnName.get(metadataItem)).collect(Collectors.joining(",")));
        insertSQLBuffer.append(") VALUES (");
        insertSQLBuffer.append("'");
        insertSQLBuffer.append(objectName);
        insertSQLBuffer.append("','");

        insertSQLBuffer.append(map.get("Id"));
        insertSQLBuffer.append("',");
        insertSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(metadataItem -> map.get(metadataItem)).collect(Collectors.joining("','", "'", "'")));
        insertSQLBuffer.append(")");

        return insertSQLBuffer.toString();
    }


    Map<String, String> fetchObjectData(Map<String, String> columnNameToObjParams, ResultSet rs) {
        Map<String, String> dataMap = new HashMap<>();
        try {

            for (int i = 2; i < 80; i++) {
                String columnName = DATA_SUFFIX + i;
                if (columnNameToObjParams.get(columnName) != null) {
                    dataMap.put(columnNameToObjParams.get(columnName), rs.getString(columnName));
                }
            }

            return dataMap;
        } catch (Exception ex) {
            logger.error("Error occured while fetchingObjectData:", ex);
        }
        return dataMap;
    }
}
