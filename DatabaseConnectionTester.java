import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionTester {
    public static void main(String[] args) {
        String message;

        try (Connection conn = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1")) {

            StringBuilder sb = new StringBuilder();
            if (rs.next()) {
                sb.append("Database connection OK.");
            } else {
                sb.append("Connected, but no response.");
            }

            DatabaseMetaData meta = conn.getMetaData();
            boolean hasFlightsTable;
            try (ResultSet tables = meta.getTables(null, null, "flights", new String[] { "TABLE" })) {
                hasFlightsTable = tables.next();
            }

            if (hasFlightsTable) {
                sb.append(" Table 'flights' found.");
            } else {
                sb.append(" Table 'flights' not found.");
            }

            message = sb.toString();
        } catch (SQLException e) {
            message = "Database connection failed: " + e.getMessage();
        }

        System.out.println(message);
    }
}
