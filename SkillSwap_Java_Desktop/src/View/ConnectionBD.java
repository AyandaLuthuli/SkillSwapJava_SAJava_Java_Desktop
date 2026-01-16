package View;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionBD {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/skillswap_db";
    private static final String USER = "root";
    private static final String PASSWORD = "0787474599Altie*";

    // Static method that other classes can call
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


}
