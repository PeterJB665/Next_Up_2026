import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NextUpSearchReturnsDatabaseManager {
    //define the local database file name
    private static final String URL = "jdbc:sqlite:nextup2026searchreturns.db";

    // Data carrier class to cleanly pass search records between the database and UI
    public static class SearchResult {
        public final String topic;
        public final String url;
        public final String title;
        public final String datePublished;

        public SearchResult(String topic, String url, String title, String datePublished) {
            this.topic = topic;
            this.url = url;
            this.title = title;
            this.datePublished = datePublished;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (%s) - %s", topic, title, datePublished, url);
        }
    }


// Creates the SearchReturns table if it does not already exist
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS SearchReturns ("
                + "ReturnID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "Topic TEXT NOT NULL, "
                + "URL TEXT UNIQUE NOT NULL, "
                + "Title TEXT NOT NULL, "
                + "DatePublished TEXT"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
            System.out.println("SearchReturns database and table initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error initializing SearchReturns database: " + e.getMessage());
        }
    }

    // Inserts a single search return; silently ignores duplicates based on the UNIQUE URL constraint
    public static boolean insertSearchReturn(String topic, String articleUrl, String title, String datePublished) {
        String insertSQL = "INSERT OR IGNORE INTO SearchReturns(Topic, URL, Title, DatePublished) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, topic);
            pstmt.setString(2, articleUrl);
            pstmt.setString(3, title);
            pstmt.setString(4, datePublished);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Returns true if a new row was added, false if ignored as a duplicate

        } catch (SQLException e) {
            System.out.println("Error inserting search return: " + e.getMessage());
            return false;
        }
    }

    // Retrieves stored search returns matching the user's selected verticals
    public static List<SearchResult> getResultsByTopics(List<String> topics) {
        List<SearchResult> results = new ArrayList<>();
        if (topics == null || topics.isEmpty()) {
            return results;
        }

        // Dynamically build the (?, ?, ...) placeholders for the SQL IN clause
        String placeholders = String.join(",", Collections.nCopies(topics.size(), "?"));
        String querySQL = "SELECT Topic, URL, Title, DatePublished FROM SearchReturns "
                + "WHERE Topic IN (" + placeholders + ") ORDER BY ReturnID DESC";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            for (int i = 0; i < topics.size(); i++) {
                pstmt.setString(i + 1, topics.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new SearchResult(
                            rs.getString("Topic"),
                            rs.getString("URL"),
                            rs.getString("Title"),
                            rs.getString("DatePublished")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error querying search returns: " + e.getMessage());
        }
        return results;
    }

    // Retrieves all stored search returns regardless of topic
    public static List<SearchResult> getAllResults() {
        List<SearchResult> results = new ArrayList<>();
        String querySQL = "SELECT Topic, URL, Title, DatePublished FROM SearchReturns ORDER BY ReturnID DESC";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            while (rs.next()) {
                results.add(new SearchResult(
                        rs.getString("Topic"),
                        rs.getString("URL"),
                        rs.getString("Title"),
                        rs.getString("DatePublished")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error querying all search returns: " + e.getMessage());
        }
        return results;
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}