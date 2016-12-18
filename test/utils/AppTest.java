/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.ParseException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import org.junit.Test;

/**
 *
 * @author Dominik
 */
public class AppTest {
    
    public AppTest() {
    }

    @Test
    public void testDatum() throws ParseException {
        Format formatter = new SimpleDateFormat("dd. MM. YYYY");
        System.out.println(formatter.format(Calendar.getInstance().getTime()));
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
