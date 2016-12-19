/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Dominik
 */
public class Query {
    
    public static final String KLIENT_FORM = "select * from v_klienti where id = ?";  
    public static final String OPRAVY_FORM = "select id,typy_komponent_id,cena,popis_opravy,pocitace_id from v_opravy where id = ?";
    public static final String PERSONAL_FORM = "select * from v_personal where id = ?";
    public static final String POCITACE_FORM = "select * from v_pocitace where id = ?";
    public static final String ZAKAZKY_FORM = "select id,klienti_id,cena_za_opravy,"
            + "datum_prijmuti,datum_priblizne_dokonceni "
            + "from v_zakazky where id = ?";
    
    public static final String USER_INFO = 
            "select id, user_name, jmeno||' '||prijmeni cele_jmeno,"
            + " pozice_id, pozice "
            + "from v_personal where id = ?";
    
    
}
