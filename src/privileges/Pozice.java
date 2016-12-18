
package privileges;

import exceptions.NotFoundException;

public enum Pozice {
    MANAGER,     
    OBCHODNIK, TECHNIK;//, SW_TECHNIK(2);
    
    public static Pozice valueOf(int poradi) throws NotFoundException {
        for (Pozice p : values()) {            
            if (poradi == p.ordinal()+1) return p;
        }
        throw new NotFoundException("Pozice s pořadím '"+poradi+"' nebylo nalezeno.");
    }
}
