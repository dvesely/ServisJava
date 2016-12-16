
package privileges;

import exceptions.NotFoundException;

public enum Pozice {
    GENERALNI_MANAGER(0), 
    MANAGER_OBCHODNIKU(1), MANAGER_TECHNIKU(1),
    OBCHODNIK(2), HW_TECHNIK(2), SW_TECHNIK(2);

    private int priorita;
    
    Pozice(int priorita) {
        this.priorita = priorita;        
    }
    
    public boolean jeNizsiNez(Pozice hledana) {        
        return hledana.priorita < this.priorita;
    }
    
    public static Pozice valueOf(int poradi) throws NotFoundException {
        for (Pozice p : values()) {            
            if (poradi == p.ordinal()+1) return p;
        }
        throw new NotFoundException("Pozice s pořadím '"+poradi+"' nebylo nalezeno.");
    }
}
