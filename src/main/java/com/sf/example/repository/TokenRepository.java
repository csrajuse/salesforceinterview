package com.sf.example.repository;

import com.sf.example.DBSource;
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

@Service
@CacheConfig(cacheNames = "token")
public class TokenRepository extends Cleanup {

    @Autowired
    private DBSource dbSource;

    private static final String insertTokenSQL = "insert into tokenstore (token,type) values (?,?)";
    private static final String deleteTokenSQL = "delete from tokenstore where type = ?";
    private static final String getTokenSQL = "select token from tokenstore where type=?";

    private static final Logger logger = LoggerFactory.getLogger("TokenRepository");

    @CacheEvict(value = "token",allEntries=true)
    public void storeToken(String token,String forWhichService){
        Connection connection = null;
        PreparedStatement deletePreparedStatement = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = dbSource.getConnection();
            deletePreparedStatement = connection.prepareStatement(deleteTokenSQL);
            deletePreparedStatement.setString(1,forWhichService);
            deletePreparedStatement.execute();
            preparedStatement = connection.prepareStatement(insertTokenSQL);
            preparedStatement.setString(1,token);
            preparedStatement.setString(2,forWhichService);
            preparedStatement.execute();


        }catch(Exception ex){
            logger.error("Error saving the token"+ex.getMessage());
        }finally{
            closePreparedStatement(deletePreparedStatement);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Cacheable(cacheNames="token")
    public String getStoredToken(String forWhichService){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String token = null;
        try{

            connection = dbSource.getConnection();
            preparedStatement = connection.prepareStatement(getTokenSQL);
            preparedStatement.setString(1,forWhichService);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                token = rs.getString("token");
            }
            return token;
        }catch(Exception ex){
            logger.error("Error while storing token");
        }finally{
            closeResultSet(rs);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return token;
    }
}
