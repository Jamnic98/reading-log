package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

<<<<<<< HEAD
    private static final Logger logger = Logger.getLogger(DBConnector.class.getName());
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_CONNECTION = "jdbc:mysql://localhost/userdb?serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
=======
    // C:\Users\Jamie\Documents\Programming\Reading Log
>>>>>>> 972838c (initial commit)

    public static Connection getDBConnection() {
        Connection connection;

        try {
            // db parameters
            String sqlDBLocation = "src\\sample\\my_books.db";
            String url = "jdbc:sqlite:" + sqlDBLocation;
            // create a connection to the database
            connection = DriverManager.getConnection(url);
            return connection;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}