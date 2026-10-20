import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class NextUp {
    JFrame window_border; //Introduce the frame
    public NextUp() { //Constructor for the frame
        window_border = new JFrame("NextUp - Welcome Page"); //Frame Title
        window_border.setSize(800,750); //Frame size
        window_border.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Frame exit function
        window_border.setLocationRelativeTo(null); //Frame position centered in screen
        //window_border.setVisible(true); //Set frame visibility to true

        JPanel panel = new JPanel(); //Introduce the panel
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20)); //Create margins so text boxes don't touch window border
        window_border.add(panel); //Add panel to the window border frame

        JLabel app_title_textbox = new JLabel("NextUp"); //Introduce the title textbox
        JLabel welcome_message_textbox = new JLabel("Welcome to NextUp! Here, you can get updated when all of your favorite artists and media are coming out with new releases!"); //Introduce the welcome textbox
        panel.add(app_title_textbox); //Add title textbox to panel
        panel.add(welcome_message_textbox); //Add welcome textbox to panel

        JButton login_button = new JButton("Login");
        login_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(login_button);
        JButton register_button = new JButton("Register");
        register_button.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(register_button);

        login_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
                    window_border.setVisible(false);
                    login.setVisible(true);

                });
            }
        });

        register_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
                    window_border.setVisible(false);
                    register.setVisible(true);
                });
            }
        });


        window_border.setVisible(true); //Set whole window page visibility to true
        }

        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextUp()); //Invoke every characteristic for the frame at once


        }
    }
