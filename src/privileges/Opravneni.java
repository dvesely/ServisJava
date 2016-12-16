/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privileges;

/**
 *
 * @author Dominik
 */
public class Opravneni {
    
    public static boolean pristupPouze(Pozice vlastni, Pozice... pozice) {
        for (Pozice p : pozice) {
            if (vlastni == p) return true;
        }
        return false;
    }
    
    public static boolean maPristup(Pozice vlastniPozice, Pozice nejnizsiPoziceSPristupem) {        
        switch (vlastniPozice) {
            case GENERALNI_MANAGER:
                return true;
            case MANAGER_OBCHODNIKU:
                
                
        }
        return false;
    }
    
}
