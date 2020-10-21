package com.sf.example.repository;

import com.sf.example.DBSource;
import com.sf.example.entity.BaseEntityObject;
import com.sf.example.repository.util.Cleanup;
import com.sf.example.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An example class if we were to fetch and save objects in the conventional way. It can replaced by AnyObjectRepository
 */

public abstract class BaseRepository<T extends BaseEntityObject> extends Cleanup {

    private static final String DATA_SUFFIX = "data";
    private static final Logger logger = LoggerFactory.getLogger("BaseRepository");
    private final Map<String, String> columnNameToObjParams = new HashMap<>();
    private final Map<String, String> objParamsToColumnName = new HashMap<>();
    //This get the metadata for a given object
    private final String metadataSQL = "select * from metadata where data1=?";
    //This is to get the object, data1 is the publicKey and data2 is the id
    private final String objectDataByIdSQL = "select * from object where data1=? and data2=?";
    //Get ALL the objects by public key
    private final String objectDataBySQL = "select * from object where data1=?";
    @Autowired
    private DBSource dbSource;
    //Determines which object that will be pulled from the database
    //Example if it is "account" then will pull the metadata and data related to it.
    //For the metadata will get the mapping fields from metadata table and data comes from the
    //object table.
    //The value which binds the metadata to data is metadata.data1==object.data1 (which is the public key)
    private String publicKey = "";

    protected void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    protected Map<String, String> insert(Map<String, String> map) {
        Connection connection = null;
        Statement st = null;
        try {
            connection = dbSource.getConnection();
            st = connection.createStatement();
            st.execute(generateInsertSQL(map));

            st.close();
            connection.close();
        } catch (Exception ex) {
            logger.error("Error while inserting" + ex.getMessage());
        } finally {
            closeStatement(st);
            closeConnection(connection);
        }
        return map;
    }

    protected Map<String, String> update(Map<String, String> map) {
        Connection connection = null;
        Statement st = null;
        try {
            StringBuffer updateSQLBuffer = new StringBuffer("UPDATE object SET ");
            List<String> metadataList = new ArrayList<>(map.keySet());
            updateSQLBuffer.append("data1 = '");
            updateSQLBuffer.append(publicKey);
            updateSQLBuffer.append("' , ");
            Collections.sort(metadataList);
            updateSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(metadataItem ->
                    objParamsToColumnName.get(metadataItem) + "='" + map.get(metadataItem) + "'").collect(Collectors.joining(",")));
            updateSQLBuffer.append(" WHERE ");
            updateSQLBuffer.append(" data2 ='");
            updateSQLBuffer.append(map.get(Constants.ID));
            updateSQLBuffer.append("'");

            connection = dbSource.getConnection();
            st = connection.createStatement();
            st.execute(updateSQLBuffer.toString());

        } catch (Exception ex) {
            logger.error("Error occured while updating the record " + ex.getMessage());
        } finally {
            closeStatement(st);
            closeConnection(connection);
        }
        return map;
    }


    /**
     * For generating the SQL statement that is to be used for inserting the object
     *
     * @param map
     * @return
     */
    String generateInsertSQL(Map<String, String> map) {
        StringBuffer insertSQLBuffer = new StringBuffer("INSERT INTO object ");

        insertSQLBuffer.append("(");
        List<String> metadataList = new ArrayList<>(map.keySet());
        insertSQLBuffer.append("data1,");
        Collections.sort(metadataList);
        insertSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(metadataItem -> objParamsToColumnName.get(metadataItem)).collect(Collectors.joining(",")));
        insertSQLBuffer.append(") VALUES (");
        insertSQLBuffer.append("'");
        insertSQLBuffer.append(publicKey);
        insertSQLBuffer.append("',");
        insertSQLBuffer.append(metadataList.stream().filter(metadata -> map.get(metadata) != null).map(map::get).collect(Collectors.joining("','", "'", "'")));
        insertSQLBuffer.append(")");

        return insertSQLBuffer.toString();
    }

    protected Map<String, String> getObject(String id) {
        try {
            Connection connection = dbSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(objectDataByIdSQL);
            preparedStatement.setString(1, publicKey);
            preparedStatement.setString(2, id);
            ResultSet rs = preparedStatement.executeQuery();
            Map<String, String> objectDataMap = new HashMap<>();
            if (rs.next()) {
                objectDataMap = fetchObjectData(rs);
            }
            rs.close();
            connection.close();

            return objectDataMap;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected List<Map<String, String>> getAllObjects() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Map<String, String>> listOfMap = new ArrayList<>();
        try {

            connection = dbSource.getConnection();
            preparedStatement = connection.prepareStatement(objectDataBySQL);
            preparedStatement.setString(1, publicKey);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                listOfMap.add(fetchObjectData(rs));
            }

            return listOfMap;
        } catch (Exception ex) {
            logger.error("Error while getting all objects");
        }finally {
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return listOfMap;
    }


    //this is assuming that the result set has been already in the loop
    Map<String, String> fetchObjectData(ResultSet rs) throws SQLException {

        Map<String, String> dataMap = new HashMap<>();

        for (int i = 2; i < 80; i++) {
            String columnName = DATA_SUFFIX + i;
            dataMap.put(columnNameToObjParams.get(columnName), rs.getString(columnName));

        }

        return dataMap;
    }

    protected boolean mapTheColumns() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        boolean retBool = false;
        try {

            if (publicKey == null || publicKey.length() == 0) {
                return retBool;
            }
            connection = dbSource.getConnection();

            preparedStatement = connection.prepareStatement(metadataSQL);
            preparedStatement.setString(1, publicKey);

            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                retBool = true;
                //We know the 1st column is not part of the data, it is the public key
                for (int i = 2; i <= 80; i++) {
                    String columnName = DATA_SUFFIX + i;
                    String columnMetaData = rs.getString(columnName);
                    if (columnMetaData != null) {
                        columnNameToObjParams.put(columnName, columnMetaData);
                        objParamsToColumnName.put(columnMetaData, columnName);
                    }
                }
            }
            return retBool;
        } catch (Exception ex) {
            logger.error("Error occured in fetching Mapping data");
        }finally{
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }

        return retBool;
    }

    Map<String, String> transferObjectToMap(T obj) {
        Map<String, String> objectMap = new HashMap<>();
        objectMap.put(Constants.ID, obj.getId());
        objectMap.put(Constants.NAME, obj.getName());
        objectMap.put(Constants.CREATEDDATE, obj.getCreatedDate());
        objectMap.put(Constants.CREATEDBYID, obj.getCreatedById());
        objectMap.put(Constants.LASTMODIFIEDBYID, obj.getLastModifiedById());
        objectMap.put(Constants.LASTACTIVITYDATE, obj.getLastModifiedDate());
        objectMap.put(Constants.SYSTEMMODSTAMP, obj.getSystemModstamp());
        objectMap.put(Constants.LASTVIEWEDDATE, obj.getLastViewedDate());
        objectMap.put(Constants.LASTREFERENCEDDATE, obj.getLastReferencedDate());

        return objectMap;
    }

    void transferMapToObject(Map<String, String> map, T obj) {
        obj.setId(map.get(Constants.ID));
        obj.setName(map.get(Constants.NAME));
        obj.setCreatedDate(map.get(Constants.CREATEDDATE));
        obj.setCreatedById(map.get(Constants.CREATEDBYID));
        obj.setLastModifiedById(Constants.LASTMODIFIEDBYID);
        obj.setLastModifiedDate(map.get(Constants.LASTACTIVITYDATE));
        obj.setSystemModstamp(map.get(Constants.SYSTEMMODSTAMP));
        obj.setLastViewedDate(map.get(Constants.LASTVIEWEDDATE));
        obj.setLastReferencedDate(map.get(Constants.LASTREFERENCEDDATE));
    }

    protected String generateId() {
        return UUID.randomUUID().toString();
    }

    protected abstract T find(String id);

    protected abstract List<T> findAll();

}
