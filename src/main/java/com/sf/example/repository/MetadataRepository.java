package com.sf.example.repository;

import com.sf.example.DBSource;
import com.sf.example.cache.MetadataObject;
import com.sf.example.repository.util.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * saves metadata for a given object. The loading of metadata is done in BaseRepository for any given publicKey.
 */
@Service
@CacheConfig(cacheNames = "metadata")
public class MetadataRepository extends Cleanup {

    @Autowired
    private DBSource dbSource;

    private static final String DATA_SUFFIX = "data";

    //This get the metadata for a given object
    private final String metadataSQL = "select * from metadata where data1=?";

    private static final Logger logger = LoggerFactory.getLogger("MetadataRepository");

    @CacheEvict(value = "metadata",allEntries = true)
    public void generateMetadata(String objectName,Set<String> namesOfTheFields){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dbSource.getConnection();

            String metadataInsertSQL = generateMetadataSQL(objectName, namesOfTheFields);

            Statement stst = connection.createStatement();
            stst.execute(metadataInsertSQL);

        }catch(Exception sqle){
            logger.error(sqle.getMessage());
        }finally{
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    String generateMetadataSQL(String objectName,Set<String> namesOfTheFields){
        //data1 is the name of the object and data2 is the id of the object. So these 2 fields need to be there.
        StringBuffer metadataInsertSQL = new StringBuffer("INSERT INTO metadata (data1,data2,");
        //Size of the fields cannot exceed 80, it is a restriction for now in the table design.
        List<String> listOfColumnNames = new ArrayList<>();
        int size = namesOfTheFields.size()>78 ? 78 : namesOfTheFields.size();
        for(int i=3;i<=size+1;i++) {
            listOfColumnNames.add("data"+i);
        }
        metadataInsertSQL.append(listOfColumnNames.stream().map(String::valueOf).collect(Collectors.joining(",")));
        //Remove the name "Id" from the set as we are manually assing it. This needs to be present.
        namesOfTheFields.remove("Id");
        metadataInsertSQL.append(") VALUES ('")
                .append(objectName.toLowerCase()) //Making sure we check every object name with lowercase so that it is not case sensitive.
                .append("',")
                .append("'Id'")
                .append(",");
        metadataInsertSQL.append(namesOfTheFields.stream().map(String::valueOf).collect(Collectors.joining("','","'","'")));
        metadataInsertSQL.append(")");

        return metadataInsertSQL.toString();
    }

    @Cacheable(cacheNames="metadata")
    public MetadataObject returnMetaData(String objectName){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {

            connection = dbSource.getConnection();
            Map<String,String> columnNameToObjParams =  new HashMap<>();
            Map<String,String> objParamsToColumnName =  new HashMap<>();
            if(objectName == null || objectName.length() == 0){
                return null;
            }

            preparedStatement = connection.prepareStatement(metadataSQL);
            preparedStatement.setString(1, objectName);

            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                //We know the 1st column is not part of the data, it is the public key
                for (int i = 2; i <= 80; i++) {
                    String columnName = DATA_SUFFIX + i;
                    String columnMetaData = rs.getString(columnName);
                    if (columnMetaData != null) {
                        columnNameToObjParams.put(columnName, columnMetaData);
                        objParamsToColumnName.put(columnMetaData, columnName);
                    }
                }
            }else{
                return null;
            }
            rs.close();
            return new MetadataObject(objectName,objParamsToColumnName,columnNameToObjParams);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }finally{
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return null;
    }
}
