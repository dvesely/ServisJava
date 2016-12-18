/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import privileges.Pozice;

public class User {
    
    private static int id;
    private static String username;
    private static String celeJmeno;
    private static Pozice pozice;
    private static String poziceString;//pozice z DB
    

    private User() {};

    public static void set(int id, String username, String celeJmeno, Pozice pozice, String poziceString) {
        User.id = id;
        User.username = username;
        User.celeJmeno = celeJmeno;
        User.pozice = pozice;
        User.poziceString = poziceString;
    }
    
    public static int getId() {
        return id;
    }
    
    public static String getUsername() {
        return username;
    }

    public static String getCeleJmeno() {
        return celeJmeno;
    }
    
    public static String getPoziceString() {
        return poziceString;
    }
    
    public static Pozice getPozice() {
        return pozice;
    }
}
