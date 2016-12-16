package tridy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Pocitac {
    
    private static Pocitac pocitac;
    
    /**
     * Vrati posledni ulozeny pocitac a smaze ho ze tridy
     * @return 
     */
    public static Pocitac getPocitac() {
        Pocitac p = pocitac;
        pocitac = null;
        return p;
    }
    
    public static void setPocitac(Pocitac pocitac) {
        Pocitac.pocitac = pocitac;
    }
    
    private String popis;
    private int cena;
    //private LinkedList<Oprava> opravy;
    
    
    public Pocitac(String popis, int cena) {
       
        this.popis = popis;
        this.cena = cena;        
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }
    
    public String getPopis() {
        return popis;
    }
    
    public int getCena() {
        return cena;
    }
    
    public void setCena(int cena) {
        this.cena = cena;
    }
    /*
    public void pridejOpravu(Oprava oprava) {
        opravy.add(oprava);
    }
    
    public Oprava odeberOpravu(int index) {        
        return opravy.remove(index);                                    
    }
*/
    @Override
    public String toString() {
        return popis + " ("+cena+" Kč)";
    }
}
