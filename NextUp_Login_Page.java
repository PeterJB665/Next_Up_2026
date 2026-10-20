import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NextUp_Login_Page {
    public static void main(String[] args){
        //Create profile page
        //Put a display for the land in page
        //Have button to go to log in
        //boxes that let you enter text to log in
        //match text for email box and password box with database to check if the login is real
            //if real: login, if not real: say error try again.

        SwingUtilities.invokeLater(() -> {
            //create a frame login
            JFrame login = new JFrame("Next Up - Login");
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //make elements stack vertically with gbc
            login.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.insets = new Insets(5,5,5,5);

            //Create the email text field
            JTextField email = new JTextField(20);
            //Create the password text field
            JTextField password = new JTextField(20);

            //Placeholder settings
            String placeholderEmail = "Enter your Email Address...";
            String placeholderPassword = "Enter your password...";
            Color placeholderColor = new Color(0,0,0,100);
            Color textColor = Color.BLACK;

            // Initialize the email field with placeholder styling
            email.setText(placeholderEmail);
            email.setForeground(placeholderColor);
            // Initialize the password field with placeholder styling
            password.setText(placeholderPassword);
            password.setForeground(placeholderColor);


            // Add FocusListener for email to handle selection and disappearance
            email.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    // When selected if it still shows the placeholder, clear it
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

            // Add FocusListener for password to handle selection and disappearance
            password.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    // When selected if it still shows the placeholder, clear it
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
            //create a label that welcomes the user
            JLabel welcome_login = new JLabel("Welcome to Next Up! Please log in.");
            welcome_login.setForeground(Color.BLUE);
            welcome_login.setFont(new Font("Tahoma", Font.BOLD, 18));

            //create a button to login, it doesn't do anything yet, but it will
            JButton login_button = new JButton("See what's up!");
            login_button.setForeground(Color.BLUE);
            login_button.setFont(new Font("Tahoma", Font.BOLD, 18));

            //action listener to see when the button is pressed
            login_button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //here, we swap to the main app
                    //login.setVisible(false);
                    //next_up.setVisible(true);

                }
            });

            //add everything to login
            login.add(welcome_login,gbc);
            login.add(email,gbc);
            login.add(password,gbc);
            login.add(login_button,gbc);
            login.setSize(500,500);
            login.setLocationRelativeTo(null);
            login.setVisible(true);



        });
    }
}
