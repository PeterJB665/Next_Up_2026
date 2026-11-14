import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NextUp_Register_Page {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //create a frame register
            JFrame register = new JFrame("Register here for more information");
            register.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //make elements stack vertically  
                      register.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.insets = new Insets(17,17,17,17);

            //Create text fields
            JTextField username = new JTextField(20);
            JTextField email = new JTextField(20);
            JTextField password = new JTextField(20);
            JTextField confirmPassword = new JTextField(20);

            //Placeholder settings
            String placeholderUsername = "Please choose a username...";
            String placeholderEmail = "enter your email...";
            String placeholderPassword = "Choose a password...";
            String placeholderConfirm = "Confirm your password...";
            Color placeholderColor = new Color(0,0,0,100);
            Color textColor = Color.BLACK;

            // Initialize fields with placeholder styling
            username.setText(placeholderUsername);
            username.setForeground(placeholderColor);
            
            email.setText(placeholderEmail);
            email.setForeground(placeholderColor);
            
            password.setText(placeholderPassword);
            password.setForeground(placeholderColor);
            
            confirmPassword.setText(placeholderConfirm);
            confirmPassword.setForeground(placeholderColor);

            // Add FocusListener-username
            username.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (username.getText().equals(placeholderUsername)) {
                        username.setText("");
                        username.setForeground(textColor);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (username.getText().isEmpty()) {
                        username.setText(placeholderUsername);
                        username.setForeground(placeholderColor);
                    }
                }
            });

            // Add FocusListener - email
            email.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (email.getText().equals(placeholderEmail)) {
                        email.setText("");
                        email.setForeground(textColor);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (email.getText().isEmpty()) {
                        email.setText(placeholderEmail);
                        email.setForeground(placeholderColor);
                    }
                }
            });

            // Add FocusListener - password
            password.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (password.getText().equals(placeholderPassword)) {
                        password.setText("");
                        password.setForeground(textColor);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (password.getText().isEmpty()) {
                        password.setText(placeholderPassword);
                        password.setForeground(placeholderColor);
                    }
                }
            });

            // Add FocusListener -confirm password
            confirmPassword.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (confirmPassword.getText().equals(placeholderConfirm)) {
                        confirmPassword.setText("");
                        confirmPassword.setForeground(textColor);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (confirmPassword.getText().isEmpty()) {
                        confirmPassword.setText(placeholderConfirm);
                        confirmPassword.setForeground(placeholderColor);
                    }
                }
            });

            //create welcome label
            JLabel welcome_register = new JLabel("Join Next up!");
            welcome_register.setForeground(Color.BLUE);
            welcome_register.setFont(new Font("Tahoma", Font.BOLD, 18));

            //create reg button
            JButton register_button = new JButton("Create Account");
            register_button.setForeground(Color.BLUE);
            register_button.setFont(new Font("Tahoma", Font.BOLD, 18));

            //action listener to see when buttons pressed
            register_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //process registration and swap to login or main app
                    //register.setVisible(false);
                    //login.setVisible(true);
                }
            });

            //add everything to reg
            register.add(welcome_register,gbc);
            register.add(username,gbc);
            register.add(email,gbc);
            register.add(password,gbc);
            register.add(confirmPassword,gbc);
            register.add(register_button,gbc);
            
            register.setSize(500,500);
            register.setLocationRelativeTo(null);
            register.setVisible(true);
        });
    }
}
