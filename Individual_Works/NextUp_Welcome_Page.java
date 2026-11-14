import java.awt.*;
import javax.swing.*;


    public class welcome_page {
        JFrame window_border; //Introduce the frame
        public welcome_page() { //Constructor for the frame
        window_border = new JFrame("NextUp - Welcome Page"); //Frame Title
        window_border.setSize(400,500); //Frame size
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








        window_border.setVisible(true); //Set whole window page visibility to true
        }

        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new welcome_page()); //Invoke every characteristic for the frame at once

        }
    }
