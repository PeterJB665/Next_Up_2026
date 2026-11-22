import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class NextUp_Profile_Page {
    private JFrame profileFrame;
    private JFrame mainFrame;

    public NextUp_Profile_Page(JFrame mainFrame) {
        this.mainFrame = mainFrame;

        profileFrame = new JFrame("Next Up - User Settings");
        profileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        profileFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Title Label
        JLabel titleLabel = new JLabel("Account Settings", SwingConstants.CENTER);
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));

        // Settings Buttons
        JButton changeUsernameBtn = new JButton("Change Username");
        JButton changeEmailBtn = new JButton("Change Email");
        JButton changePasswordBtn = new JButton("Change Password");
        JButton backBtn = new JButton("Back to NextUp");

        Font buttonFont = new Font("Tahoma", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(250, 45);

        JButton[] buttons = {changeUsernameBtn, changeEmailBtn, changePasswordBtn, backBtn};
        for (JButton btn : buttons) {
            btn.setForeground(Color.BLUE);
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
        }

        backBtn.setForeground(Color.DARK_GRAY);

        // Action Listeners
        changeUsernameBtn.addActionListener(e -> {
            String newUsername = JOptionPane.showInputDialog(profileFrame, "Enter new username:");
            if (newUsername != null && !newUsername.trim().isEmpty()) {
                JOptionPane.showMessageDialog(profileFrame, "Username updated to: " + newUsername);
            }
        });

        changeEmailBtn.addActionListener(e -> {
            String newEmail = JOptionPane.showInputDialog(profileFrame, "Enter new email:");
            if (newEmail != null && !newEmail.trim().isEmpty()) {
                JOptionPane.showMessageDialog(profileFrame, "Email updated to: " + newEmail);
            }
        });

        changePasswordBtn.addActionListener(e -> {
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(
                profileFrame, pf, "Enter new password:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (okCxl == JOptionPane.OK_OPTION) {
                String newPass = new String(pf.getPassword());
                if (!newPass.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(profileFrame, "Password updated successfully.");
                }
            }
        });

        backBtn.addActionListener(e -> {
            profileFrame.dispose();
            if (mainFrame != null) {
                mainFrame.setVisible(true);
            } else {
                System.out.println("Returning to main screen...");
            }
        });

        // Add top margin for the title
        gbc.insets = new Insets(40, 15, 20, 15);
        profileFrame.add(titleLabel, gbc);

        // Reset normal spacing for the settings buttons
        gbc.insets = new Insets(15, 15, 15, 15);
        profileFrame.add(changeUsernameBtn, gbc);
        profileFrame.add(changeEmailBtn, gbc);
        profileFrame.add(changePasswordBtn, gbc);

        // Extra top padding before the Back button
        gbc.insets = new Insets(35, 15, 15, 15);
        profileFrame.add(backBtn, gbc);

        // Invisible filler to leave the bottom half blank
        gbc.weighty = 1.0;
        profileFrame.add(Box.createVerticalGlue(), gbc);

        // Longer window height
        profileFrame.setSize(1000, 850);
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextUp_Profile_Page(null));
    }
}