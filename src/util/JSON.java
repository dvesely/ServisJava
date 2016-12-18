package util;

import app.App;
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
        if (json == null) return;
        JsonObject object = decode(json);        
        if (!object.has("status")) {
            return;                    
        }
        if (!object.get("status").getAsString().equals("OK")) {
            String message;
            FormWindow form = App.getActiveForm();
            
            if (object.has("message")) {
                message = object.get("message").getAsString();
                if (object.get("code").getAsInt() < -20000) {//uzivateslka vyjimka
                    message = extractMessage(message);
                }                          
            }else {                
               message = "Nastala chyba na serveru. (chybí info o chybě)";
            }
            if (form != null) {
                form.rollback();
            }
            throw new SQLException(message);
        }   
    }
    
    /**
     * Extrahuje zpravu z uzivatelske vyjimky v databazi
     * @param oraMessage
     * @return 
     */
    private static String extractMessage(String oraMessage) {
        int endLine = oraMessage.indexOf("\n");
        if (endLine > 0) {//bylo nalezeno odradkovani
            oraMessage = oraMessage.substring(0, endLine);//odriznuti po odradkovani
        }
        return oraMessage.replaceFirst("(ORA-[0-9]+:\\s)", "");//odstraneni prefixu chyby s cislem
    }
}
