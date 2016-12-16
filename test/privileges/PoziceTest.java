/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privileges;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Dominik
 */
public class PoziceTest {
    
    public PoziceTest() {
    }

    @Test
    public void testSomeMethod() {
        System.out.println(Pozice.MANAGER_OBCHODNIKU.jeNizsiNez(Pozice.GENERALNI_MANAGER));
        System.out.println(Pozice.MANAGER_OBCHODNIKU.jeNizsiNez(Pozice.MANAGER_TECHNIKU));
        System.out.println(Pozice.MANAGER_OBCHODNIKU.jeNizsiNez(Pozice.HW_TECHNIK));
        System.out.println(Pozice.MANAGER_OBCHODNIKU.jeNizsiNez(Pozice.OBCHODNIK));
        System.out.println(Pozice.MANAGER_OBCHODNIKU.jeNizsiNez(Pozice.MANAGER_OBCHODNIKU));
        System.out.println("------------------");
        System.out.println(Pozice.HW_TECHNIK.jeNizsiNez(Pozice.GENERALNI_MANAGER));
        System.out.println(Pozice.HW_TECHNIK.jeNizsiNez(Pozice.MANAGER_TECHNIKU));
        System.out.println(Pozice.HW_TECHNIK.jeNizsiNez(Pozice.MANAGER_OBCHODNIKU));
        System.out.println(Pozice.HW_TECHNIK.jeNizsiNez(Pozice.HW_TECHNIK));
        System.out.println(Pozice.HW_TECHNIK.jeNizsiNez(Pozice.OBCHODNIK));        
    }
    
}
