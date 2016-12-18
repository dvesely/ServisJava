package tridy;

public class Pocitac {
    
    private static Pocitac pocitac;
    
    /**
     * Vrati posledni ulozeny pocitac a smaze ho ze tridy
     * @return 
     */
    public static Pocitac removePocitac() {
        Pocitac p = pocitac;
        pocitac = null;
        return p;
    }
    
    public static void setPocitac(Pocitac pocitac) {
        Pocitac.pocitac = pocitac;
    }
    
    private int id;
    private String popis;
    private int cena;
    //private LinkedList<Oprava> opravy;
    
    
    public Pocitac(String popis, int cena) {
       this(-1, popis, cena);
    }
    
    public Pocitac(int id, String popis, int cena) {
        this.id = id;
        this.popis = popis;
        this.cena = cena;        
    }

    public int getId() {
        return id;
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
        if (id < 0) {
            return String.format("#Nový (%d Kč)", cena);
        }
        return String.format("#%d (%d Kč)", id, cena);
    }
}
