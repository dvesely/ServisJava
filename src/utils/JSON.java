package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.SQLException;


public class JSON {
    
    private static final JsonParser PARSER = new JsonParser();    
    private static JsonObject lastDecodedObject;
    
    public static JsonObject decode(String json) {        
        lastDecodedObject = PARSER.parse(json).getAsJsonObject();
        return lastDecodedObject;
    }
    
    public static JsonElement get(String name) {
        if (lastDecodedObject == null) {
            throw new NullPointerException("Doposud nebyl dekódován žádný řetězec.");
        }
        return lastDecodedObject.get(name);
    }
    
    public static String getAsString(String name) {
        return get(name).getAsString();
    }
    
    public static int getAsInt(String name) {
        return get(name).getAsInt();
    }
    
    /**
     * Vyhodi vyjimku pokud <b>status</b> neni OK
     * @param json String
     * @throws SQLException 
     */
    public static void checkStatus(String json) throws SQLException {
        JsonObject object = decode(json);
        
        if (!object.has("status")) {
            throw new SQLException("Nastala neznamá chyba. Nebyla definována proměnná 'status'.");                    
        }
        if (!object.get("status").getAsString().equals("OK")) {
            throw new SQLException(object.get("message").getAsString());
        }   
    }
}
