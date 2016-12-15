/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dominik
 */
public class JSONTest {
    
    public JSONTest() {
    }

    @Test
    public void testSomeMethod() {
        String oraMessage = "ORA-20001: Špatně zadané jméno.\n" +
                "ORA-06512: na 'ST46659.S_VALID_KLIENT', line 14\n" +
                "ORA-04088: chyba během provádění triggeru 'ST46659.S_VALID_KLIENT'";        
        oraMessage = oraMessage.substring(0, oraMessage.indexOf("\n")).replaceFirst("(ORA-[0-9]+:\\s)", "");
        System.out.println(oraMessage);
    }
    
}
