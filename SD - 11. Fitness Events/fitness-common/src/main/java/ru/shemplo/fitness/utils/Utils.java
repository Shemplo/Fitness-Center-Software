package ru.shemplo.fitness.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ru.shemplo.fitness.db.DBObjectUnwrapper;
import ru.shemplo.fitness.entities.Completable;
import ru.shemplo.fitness.entities.FitnessEvent;
import ru.shemplo.fitness.entities.Identifiable;
import ru.shemplo.fitness.entities.Updatable;

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
