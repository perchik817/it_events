//package whz.it_events.it_eventsdbapp.config;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DbConnection {
//    private static final String URL =
//            "jdbc:sqlserver://LILEVIL:1433;" +
//                    "databaseName=itEventsDB;" +
//                    "encrypt=true;" +
//                    "trustServerCertificate=true;";
//
//    private static final String USERNAME = "it_events_admin";
//    private static final String PASSWORD = "superDB";
//
//    private Connection connection;
//
//    public DbConnection() {
//        try {
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//            System.out.println("✓ Connected to database successfully!");
//        } catch (SQLException e) {
//            System.err.println("✗ Database connection failed!");
//            System.err.println("Error: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Connection getConnection() {
//        return connection;
//    }
//
//    public void close() {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                connection.close();
//                System.out.println("Connection closed.");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error closing connection: " + e.getMessage());
//        }
//    }
//}
