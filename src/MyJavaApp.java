import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyJavaApp {
    // Database connection details
    static final String DB_URL = "jdbc:mysql://localhost:3306/<MySQL80";
    static final String USER = "root";
    static final String PASS = "Number1guppy1!667";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            // 1. Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Connect to the database
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 3. Create a SQL query
            String sql = "SELECT * FROM your_table";
            stmt = conn.prepareStatement(sql);

            // 4. Execute the query and get the results
            ResultSet rs = stmt.executeQuery();

            // 5. Process the result set
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("------");
            }

            // 6. Close the connection
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
