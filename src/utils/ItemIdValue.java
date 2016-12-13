/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Objects;

/**
 *
 * @author Dominik
 */
public class ItemIdValue {
    
    private int id;
    private String vale;
    
    public ItemIdValue(String id) {
        int intId;
        try {
            intId = Integer.parseInt(id);
        }catch (NumberFormatException ex){
            intId = -1;
        };
        this.id = intId;
    }
    
    public ItemIdValue(int id ){
        this(id, null);
    }
    
    public ItemIdValue(int id, String vale) {
        this.id = id;
        this.vale = vale;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return vale;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemIdValue other = (ItemIdValue) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    
}
