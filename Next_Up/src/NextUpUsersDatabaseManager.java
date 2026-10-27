import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class NextUpUsersDatabaseManager {
    // Defines the local database file name
    private static final String URL = "jdbc:sqlite:nextup2026users.db";

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Users ("
                + "UserID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "EmailAddress TEXT UNIQUE NOT NULL, "
                + "Username TEXT UNIQUE NOT NULL, "
                + "Password TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createTableSQL);
            System.out.println("Database and Users table initialized successfully.");
            
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // Collects the account information from NextUp_Register_Page.java and stores it in the user database
    public static boolean registerUser(String email, String username, String plainTextPassword) {
        String insertSQL = "INSERT INTO Users(EmailAddress, Username, Password) VALUES(?,?,?)";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
             
            // Scramble the password securely before saving it to the database
            String hashedPassword = BCrypt.withDefaults().hashToString(12, plainTextPassword.toCharArray());
            
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setString(3, hashedPassword);
            
            pstmt.executeUpdate();
            return true; // Registration successful
            
        } catch (SQLException e) {
            System.out.println("Registration error (User may already exist): " + e.getMessage());
            return false;
        }
    }

    // Connects to Next_Up_Login_Page.java
    public static boolean authenticateUser(String email, String plainTextPassword) {
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

    public static void main(String[] args) {
        initializeDatabase();
    }
}