/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import privileges.Pozice;

public class User {
    private static int id;
    private static Pozice pozice;

    private User() {};

    public static void set(int id, Pozice pozice) {
        User.id = id;
        User.pozice = pozice;
    }
    
    public static int getId() {
        return id;
    }

    public static Pozice getPozice() {
        return pozice;
    }
}
