package com.sf.example.repository.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Methods for cleaning up database resources.
 */
public class Cleanup {

    private static final Logger logger = LoggerFactory.getLogger("Cleanup");

    protected void closeConnection(Connection connection){
        try{
            if(connection!=null) {
                connection.close();
            }
        }catch(SQLException sqle){
            logger.error("Error closing the connection");
        }
    }

    protected void closeResultSet(ResultSet resultSet){
        try{
            if(resultSet!=null) {
                resultSet.close();
            }
        }catch(SQLException sqle){
            logger.error("Error closing the ResultSet");
        }
    }

    protected void closeStatement(Statement statement){
        try{
            if(statement!=null) {
                statement.close();
            }
        }catch(SQLException sqle){
            logger.error("Error closing the Statement");
        }
    }

    protected void closePreparedStatement(PreparedStatement preparedStatement){
        try{
            if(preparedStatement!=null) {
                preparedStatement.close();
            }
        }catch(SQLException sqle){
            logger.error("Error closing the Prepared Statement");
        }
    }
}
