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
    public static final String OPRAVY_FORM = "select * from v_opravy where id = ?";
    public static final String PERSONAL_FORM = "select * from v_personal where id = ?";
    public static final String POCITACE_FORM = "select * from v_pocitace where id = ?";
    public static final String ZAKAZKY_FORM = "select * from zakazky where id = ?";
    
    
}
