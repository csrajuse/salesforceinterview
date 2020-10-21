package com.sf.example.repository;

import com.sf.example.DBSource;
import com.sf.example.repository.util.Cleanup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class SalesforceSyncfromRepository extends Cleanup {

    @Autowired
    private DBSource dbSource;

    private static final Logger logger = LoggerFactory.getLogger("SalesForceSyncRespository");

    private static final String INSERTSQL = "INSERT INTO syncfromsalesforce (lastmodified,objectname) VALUES (?,?)";

    private static final String UPDATESQL = "UPDATE syncfromsalesforce SET lastmodified=? WHERE objectname=? ";

    public void save(String objectName,String lastModifiedDate) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try{
            connection = dbSource.getConnection();
            preparedStatement = connection.prepareStatement(UPDATESQL);
            preparedStatement.setString(1,objectName);
            preparedStatement.setString(2,lastModifiedDate);
            preparedStatement.execute();

        }catch(Exception ex){
            logger.error(ex.getMessage());
        }finally{
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    public String fetchLatestSyncTime(String objectName){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String retString = null;
        try{

            connection = dbSource.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT lastmodified from syncfromsalesforce where objectname='"+objectName+"'");
            if(rs.next()){
                retString = rs.getString(1);
            }else{
                //insert it for the first time.
                 preparedStatement = connection.prepareStatement(INSERTSQL);
                 String formatedDateStr = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss'Z'").format(new Date());

                 preparedStatement.setString(1,formatedDateStr);
                 preparedStatement.setString(2,objectName);

                 preparedStatement.execute();
                 preparedStatement.close();

                 retString = formatedDateStr;
            }

        }catch(Exception ex){
            logger.error(ex.getMessage());
        }finally{
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return retString;
    }
}
