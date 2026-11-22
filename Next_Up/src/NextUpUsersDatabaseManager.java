import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class NextUpUsersDatabaseManager {
    // Defines the local database file name
    private static final String URL = "jdbc:sqlite:nextup2026users.db";

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
                + "UserID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "EmailAddress TEXT UNIQUE NOT NULL, "
                + "Username TEXT UNIQUE NOT NULL, "
                + "Password TEXT NOT NULL, "
                + "SelectedCategories TEXT DEFAULT ''"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSQL);
            ensureSelectedCategoriesColumn(conn);
            ensureBlacklistedSitesColumn(conn);
            System.out.println("Database and Users table initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // Safely adds SelectedCategories to existing Users tables if missing
    private static void ensureSelectedCategoriesColumn(Connection conn) throws SQLException {
        boolean hasColumn = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(Users)")) {
            while (rs.next()) {
                if ("SelectedCategories".equalsIgnoreCase(rs.getString("name"))) {
                    hasColumn = true;
                    break;
                }
            }
        }

        if (!hasColumn) {
            try (Statement alterStmt = conn.createStatement()) {
                alterStmt.execute("ALTER TABLE Users ADD COLUMN SelectedCategories TEXT DEFAULT ''");
            }
        }
    }

    // Collects the account information from NextUp_Register_Page.java and stores it in the user database
    public static boolean registerUser(String email, String username, String plainTextPassword) {
        initializeDatabase();
        String insertSQL = "INSERT INTO Users(EmailAddress, Username, Password, SelectedCategories) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Scramble the password securely before saving it to the database
            String hashedPassword = BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());

            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, "");

            pstmt.executeUpdate();
            return true; // Registration successful

        } catch (SQLException e) {
            System.out.println("Registration error (User may already exist): " + e.getMessage());
            return false;
        }
    }

    // Connects to Next_Up_Login_Page.java
    public static boolean authenticateUser(String email, String plainTextPassword) {
        initializeDatabase();
        String querySQL = "SELECT Password FROM Users WHERE EmailAddress = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");
                // Compare the plain text attempt against the stored hash
                BCrypt.Result result = BCrypt.verifyer().verify(plainTextPassword.toCharArray(), storedHashedPassword);
                return result.verified;
            }
            return false; // Email not found

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    // Saves selected categories for a specific user account (identified by EmailAddress)
    public static boolean saveUserSelectedCategories(String email, List<String> categories) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        initializeDatabase();

        String serialized = (categories == null || categories.isEmpty())
                ? ""
                : String.join("|", categories);

        String updateSQL = "UPDATE Users SET SelectedCategories = ? WHERE EmailAddress = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, serialized);
            pstmt.setString(2, email.trim());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving selected categories: " + e.getMessage());
            return false;
        }
    }

    // Loads selected categories for a specific user account (identified by EmailAddress)
    public static List<String> getUserSelectedCategories(String email) {
        List<String> resultCategories = new ArrayList<>();
        if (email == null || email.trim().isEmpty()) {
            return resultCategories;
        }
        initializeDatabase();

        String querySQL = "SELECT SelectedCategories FROM Users WHERE EmailAddress = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setString(1, email.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String raw = rs.getString("SelectedCategories");
                    if (raw != null && !raw.trim().isEmpty()) {
                        for (String part : raw.split("\\|")) {
                            String trimmed = part.trim();
                            if (!trimmed.isEmpty()) {
                                resultCategories.add(trimmed);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading selected categories: " + e.getMessage());
        }
        return resultCategories;
    }

    // connect to "forgot password" dialog
    // check if email address and username both match sql User table
    public static boolean verify_user_email_username(String email, String username) {
        initializeDatabase();
        String querySQL = "SELECT 1 FROM Users WHERE EmailAddress = ? AND Username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setString(1, email);
            pstmt.setString(2, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // True only if both match the same account

        } catch (SQLException e) {
            System.out.println("Verification error: " + e.getMessage());
            return false;
        }
    }

    // Update the database with user's new password and bcrypt hashing
    public static boolean resetPasswordWithUsername(String email, String username, String newPlainTextPassword) {
        initializeDatabase();
        String updateSQL = "UPDATE Users SET Password = ? WHERE EmailAddress = ? AND Username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            String new_HashedPassword = BCrypt.withDefaults().hashToString(12, newPlainTextPassword.toCharArray());

            pstmt.setString(1, new_HashedPassword);
            pstmt.setString(2, email);
            pstmt.setString(3, username);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Password reset error: " + e.getMessage());
            return false;
        }
    }

    private static void ensureBlacklistedSitesColumn(Connection conn) throws SQLException {
        boolean hasColumn = false;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(Users)")) {
            while (rs.next()) {
                if ("BlacklistedSites".equalsIgnoreCase(rs.getString("name"))) {
                    hasColumn = true;
                    break;
                }
            }
        }

        if (!hasColumn) {
            try (Statement alterStmt = conn.createStatement()) {
                alterStmt.execute("ALTER TABLE Users ADD COLUMN BlacklistedSites TEXT DEFAULT ''");
            }
        }
    }

    // Saves blacklisted domains for a specific user account
    public static boolean saveUserBlacklistedSites(String email, List<String> domains) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        initializeDatabase();

        String serialized = (domains == null || domains.isEmpty())
                ? ""
                : String.join("|", domains);

        String updateSQL = "UPDATE Users SET BlacklistedSites = ? WHERE EmailAddress = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {

            pstmt.setString(1, serialized);
            pstmt.setString(2, email.trim());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving blacklisted sites: " + e.getMessage());
            return false;
        }
    }

    // Loads blacklisted domains for a specific user account
    public static List<String> getUserBlacklistedSites(String email) {
        List<String> resultDomains = new ArrayList<>();
        if (email == null || email.trim().isEmpty()) {
            return resultDomains;
        }
        initializeDatabase();

        String querySQL = "SELECT BlacklistedSites FROM Users WHERE EmailAddress = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {

            pstmt.setString(1, email.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String raw = rs.getString("BlacklistedSites");
                    if (raw != null && !raw.trim().isEmpty()) {
                        for (String part : raw.split("\\|")) {
                            String trimmed = part.trim();
                            if (!trimmed.isEmpty()) {
                                resultDomains.add(trimmed);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading blacklisted sites: " + e.getMessage());
        }
        return resultDomains;
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
