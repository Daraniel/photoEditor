import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class MsAccessDatabaseConnection {

    static List<String> query(String query) {
        return query(query, false);
    }

    static List<String> query(String query, boolean update) {

        // variables
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> result = new ArrayList();

        // Step 1: Loading or registering Oracle JDBC driver class
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException cnfex) {

            System.out.println("Problem in loading or "
                    + "registering MS Access JDBC driver");
            cnfex.printStackTrace();
        }

        // Step 2: Opening database connection
        try {
            //URL path = demo.class.getResource("Sepanta.dll");
            //String msAccDB = path.getPath();
            String msAccDB = "Sepanta.dll";
            String dbURL = "jdbc:ucanaccess://" + msAccDB;

            // Step 2.A: Create and get connection using DriverManager class
            connection = DriverManager.getConnection(dbURL);

            // Step 2.B: Creating JDBC Statement 
            statement = connection.createStatement();

            if (update) {
                statement.executeUpdate(query);
            } else {
                // query = "SELECT * FROM Users0";
                // Step 2.C: Executing SQL & retrieve data into ResultSet
                resultSet = statement.executeQuery(query);

                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int columnCount = resultSetMetaData.getColumnCount();

                // processing returned data and printing into console
                while (resultSet.next()) {
                    for (int i = 1; i < columnCount + 1; i++) {
                        result.add(resultSet.getString(i));
                    }
                }
            }

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            if (update) {
                result.add("error");
            }
        } finally {

            // Step 3: Closing database connection
            try {
                if (null != connection) {

                    // cleanup resources, once after processing
                    if (!update) {
                        resultSet.close();
                    }
                    statement.close();

                    // and then finally close connection
                    connection.close();
                }
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
            }
        }
        return result;
    }
}