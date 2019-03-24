package ru.shemplo.fitness.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class Utils {
    
    public static String generateSecret (int length) {
        return "very secret value";
    }
    
    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"),
                                   DATE_FORMAT     = new SimpleDateFormat ("yyyy-MM-dd");;
    
    public static Date parseDate (String date) {
        try {
            return DATETIME_FORMAT.parse (date);
        } catch (ParseException pe) {}
        
        try {
            return DATE_FORMAT.parse (date);
        } catch (ParseException pe) {};
        
        return null;
    }
    
}
