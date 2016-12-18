/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package privileges;

import user.User;
import static privileges.Pozice.MANAGER;

/**
 *
 * @author Dominik
 */
public class Opravneni {
    
    public static boolean pristupPouze(Pozice... pozice) {
        Pozice userPozice = User.getPozice();
        if (userPozice == null)
            throw new NullPointerException("Uživatel nemá nastavenou pozici.");
        for (Pozice p : pozice) {
            if (userPozice == p) return true;
        }
        return false;
    }    
}
