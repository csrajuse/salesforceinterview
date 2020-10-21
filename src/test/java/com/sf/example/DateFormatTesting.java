package com.sf.example;

import org.junit.Test;

import java.text.SimpleDateFormat;

public class DateFormatTesting {

    @Test
    public void DateFormatterTest(){
        String dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
        String toDateFormat = "YYYY-MM-dd'T'hh:mm:ss'Z'";

        String dateString = "2020-10-20T06:10:35.000+0000";

        try {
            SimpleDateFormat dateFormatIn = new SimpleDateFormat(dateFormat);
            dateFormatIn.parse(dateString);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
