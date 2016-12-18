/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Dominik
 */
public class StringDate {
    
    public static final String FORMAT = "d.M.YYYY";
    
    public static boolean isDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}.*");
    }
    
    public static Date LocalDateToSQLDate(LocalDate date) {
        return new Date(Timestamp.valueOf(date.atStartOfDay()).getTime());
    }
    
    public static LocalDate StringToLocalDate(String date) {        
        return LocalDate.parse(date.replaceFirst(" \\d{2}:\\d{2}:\\d{2}.\\d", ""),//odstrani HH:mm:ss.S 
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    public static String formatDate(String date) {
        return formatDate(date, "yyyy-MM-dd");
    }
    
    public static String formatDate(String date, String format) {        
        LocalDateTime datetime = LocalDateTime.parse(date, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        return datetime.format(DateTimeFormatter.ofPattern(format));
    }
    
}
