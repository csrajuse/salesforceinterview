package com.sf.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Component class for connecting to the database.
 */

@Component
public class DBSource {

    //Mapped from application.properties
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    /*@PostConstruct
    void init(){
        try {
            dataSource();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }*/

    /**
     *
     * @return returns a DataSource for interaction with the underlying database
     * @throws SQLException
     */
    @Bean
    public DataSource dataSource() throws SQLException {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }

    public Connection getConnection() throws SQLException{
        return dataSource.getConnection();
    }
}
