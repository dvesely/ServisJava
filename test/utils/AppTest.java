/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import database.OracleConnector;
import java.awt.image.BufferedImage;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import oracle.jdbc.OracleTypes;
import org.junit.Test;

/**
 *
 * @author Dominik
 */
public class AppTest {
    
    public AppTest() {
    }

    @Test
    public void testDatum() throws ParseException{
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());
        
            System.out.println(df.parse("1995-03-19 00:00:00.0"));
        
    }
    
    @Test
    public void testSetStage() {
        /* try {
            CallableStatement call= OracleConnector.getConnection().prepareCall
        ("{call pck_adresy.uprav_adresu(:id,:ulice,:cislo,:mesto,:psc,:zeme,:status)}");
            
            call.setNull(":id", OracleTypes.NUMBER);
            call.setString(":ulice", "jo ulice");
            call.setInt(":cislo", 100);
            call.setString(":mesto", "mesto");
            call.setInt(":psc", 46554);
            call.setString(":zeme", "zeme");
            call.registerOutParameter(":status", OracleTypes.CLOB);
            call.execute();
            String status = call.getString(":status");                          
            JSON.checkStatus(status);
        }catch (SQLException e) {
            System.out.println(e.getMessage());                        
        };*/
    }
    
}
