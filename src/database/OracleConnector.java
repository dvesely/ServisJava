/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Zechmeister Ji��
 */
public class OracleConnector {

    private static String userName;
    private static String password;
    private static String serverName;
    private static int portNumber;
    private static String dbms;
    private static String sid;
    private static Connection conn = null;

    /**
     * Metoda vracejecí aktivní připojení k databazi (v případě že připojení není aktivní, je vrácena výjmka)
     * @return Aktuální aktivní připojení.
     * @throws SQLException 
     */
    public static Connection getConnection() throws SQLException {
        if (OracleConnector.conn == null || !OracleConnector.conn.isValid(0)) {
            //throw new SQLException("Session is not established");
            OracleConnector.setUpConnection(
                    MyDBProperties.SERVER, 
                    MyDBProperties.PORT, 
                    MyDBProperties.SID, 
                    MyDBProperties.MYNAME, 
                    MyDBProperties.MYPWD);                    
        }
        return OracleConnector.conn;        
    }

    /**
     * Provede prvotní nastavení připojení k databázi
     * @param serverName Adresa databázového serveru
     * @param portNumber Číslo portu (pro databáze Oracle typicky 1521)
     * @param sid SID instance databáze, ke které se chceme připojit     
     * @param userName Uživatelské jméno
     * @param password Heslo
     */
    public static void setUpConnection(String serverName, int portNumber, String sid, String userName, String password) throws SQLException {

        OracleConnector.serverName = serverName;
        OracleConnector.portNumber = portNumber;
        OracleConnector.dbms = "oracle:thin";
        OracleConnector.sid = sid;
        OracleConnector.userName = userName;
        OracleConnector.password = password;

        Properties connectionProps = new Properties();
        connectionProps.put("user", OracleConnector.userName);
        connectionProps.put("password", OracleConnector.password);

        OracleConnector.conn = DriverManager.getConnection(
                "jdbc:" + OracleConnector.dbms + ":@"
                + OracleConnector.serverName
                + ":" + OracleConnector.portNumber + ":" + OracleConnector.sid,
                connectionProps);

        OracleConnector.conn.setAutoCommit(false);

        System.out.println("Connected to database");
    }

    /**
     * Connection string aktuálního databázového připojení
     * @return Řetězec reprezentující aktuální connection string
     */
    public static String getConnectionString() {
        return "//jdbc:" + OracleConnector.dbms + ":@" + OracleConnector.serverName + ":" + OracleConnector.portNumber + ":" + OracleConnector.sid;
    }

    /**
     * Uzavření připojení.
     * @param commit
     * @throws SQLException 
     */
    public static void closeConnection(boolean commit) throws SQLException {

        if (conn != null) {

            if (commit) {
                conn.commit();
            }

            conn.close();
            conn = null;
            System.out.println("Connection closed");
        }
    }
    /**
     * 
     * @param date
     * @param format
     * @return 
     */
    public static java.sql.Date parseDate(String date, String format){
        DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        java.sql.Date result = null; 
        try {
            result = new java.sql.Date(df.parse(date).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(OracleConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
