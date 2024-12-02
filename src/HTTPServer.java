import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HTTPServer {
    public static void main(String[] args) throws IOException {
        // Create an HTTP server that listens on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Set the handler for the "/data" endpoint
        server.createContext("/data", new MyHandler());
        server.createContext("/addReview", new AddReviewHandler());
        server.setExecutor(null); // Use the default executor
        server.start();
        System.out.println("Server started on port 8080");
    }

    // Define a handler for requests to "/data"
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Set CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");

            // Check if it's a GET request
            if ("GET".equals(exchange.getRequestMethod())) {
                // Extract the query parameter from the URL (e.g., "/data?query=productName")
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("query=")) {
                    String productName = query.split("=")[1];
                    List<String> results = performSearchByProductName(productName);

                    // Format the results as JSON
                    String jsonResponse = formatResultsAsJson(results);

                    // Send the response to the client as JSON
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(jsonResponse.getBytes());
                    os.close();
                } else {
                    // Default response if no query
                    String response = "Hello, this is your Java backend!";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }

        // Perform the database search by product name and return the reviews
        private List<String> performSearchByProductName(String productName) {
            List<String> results = new ArrayList<>();
        
            String url = "jdbc:mysql://localhost:3306/project_1";
            String username = "root";
            String password = "YourPasswordHere";
        
            String sql = "SELECT r.review " +
                         "FROM reviews r " +
                         "JOIN product p ON r.ProductId = p.productId " +
                         "WHERE LOWER(p.productName) LIKE LOWER(?)";
        
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
        
                stmt.setString(1, "%" + productName + "%");
                System.out.println("Executing query: " + sql + " with parameter: %" + productName + "%");
        
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String review = rs.getString("review");
                    System.out.println("Fetched review: " + review); // Debugging log
                    results.add(review);
                }
        
            } catch (SQLException e) {
                e.printStackTrace();
            }
        
            System.out.println("Total reviews fetched: " + results.size());
            return results;
        }

        // Format results as a simple JSON array
        private String formatResultsAsJson(List<String> results) {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < results.size(); i++) {
                json.append("\"").append(results.get(i)).append("\"");
                if (i < results.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            return json.toString();
        }
    }

    // Add this inside the HTTPServer class
static class AddReviewHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Set CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            // Parse request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            String[] params = requestBody.split("&");
            String productName = params[0].split("=")[1];
            String reviewContent = params[1].split("=")[1];

            // Placeholder userId for 'guest_user'
            int userId = 1;

            // Ensure product exists
            if (!productExists(productName)) {
                addProduct(productName);
            }

            // Add review
            boolean success = addReview(userId, productName, reviewContent);

            // Send response
            String response = success ? "Review added successfully!" : "Failed to add review.";
            exchange.sendResponseHeaders(success ? 200 : 500, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}

// Check if a product exists in the database
private static void addProduct(String productName) {
    String url = "jdbc:mysql://localhost:3306/project_1";
    String username = "root";
    String password = "Number1guppy1!667"; // Replace with your actual password

    String insertQuery = "INSERT INTO product (productName) VALUES (?)";
    try (Connection conn = DriverManager.getConnection(url, username, password);
         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
        stmt.setString(1, productName);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

private static boolean productExists(String productName) {
    String url = "jdbc:mysql://localhost:3306/project_1";
    String username = "root";
    String password = "Number1guppy1!667"; // Replace with your actual password

    String query = "SELECT COUNT(*) FROM product WHERE productName = ?";
    try (Connection conn = DriverManager.getConnection(url, username, password);
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, productName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Return true if count > 0
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

private static boolean addReview(int userId, String productName, String reviewContent) {
    String url = "jdbc:mysql://localhost:3306/project_1";
    String username = "root";
    String password = "Number1guppy1!667"; // Replace with your actual password

    String insertQuery = "INSERT INTO reviews (userId, productName, review) VALUES (?, ?, ?)";
    try (Connection conn = DriverManager.getConnection(url, username, password);
         PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
        stmt.setInt(1, userId); // Use the placeholder userId for 'guest_user'
        stmt.setString(2, productName);
        stmt.setString(3, reviewContent);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}
