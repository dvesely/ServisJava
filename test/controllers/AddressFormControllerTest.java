/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.OracleConnector;
import java.sql.CallableStatement;
import oracle.jdbc.OracleTypes;
import org.junit.Test;
import static org.junit.Assert.*;
import app.App;
import util.JSON;

/**
 *
 * @author Dominik
 */
public class AddressFormControllerTest {
    
    public AddressFormControllerTest() {
    }

    @Test
    public void testInitialize() {
    }

    @Test
    public void testConfirm() throws Exception {
        CallableStatement cStmt = OracleConnector.getConnection()
                .prepareCall("{call pck_adresy.uprav_adresu(?, ?, ?, ?, ?, ?, ?)}");
        
        cStmt.setNull("p_id", OracleTypes.NUMBER);
        cStmt.setString("p_ulice", "Street");
        cStmt.setInt("p_cislo_popisne", 20);
        cStmt.setString("p_mesto", "HK");
        cStmt.setInt("p_psc", 12333);                
        cStmt.setString("p_zeme", "CR");
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        
        cStmt.execute();        
        JSON.checkStatus(cStmt.getString("p_result"));        
        cStmt.close();                    
    }

    @Test
    public void testCancel() throws Exception {
    }

    @Test
    public void testSetData() {
    }
    
}
