import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class NextUp_Profile_Page {
    private JFrame profileFrame;
    private JFrame mainFrame;
    private NextUpHome homePage;
    private String currentUserEmail;

    private JButton categoriesDropdownBtn;
    private JPopupMenu categoryPopupMenu;
    private List<JCheckBoxMenuItem> categoryCheckBoxes;
    private JLabel selectedCategoriesLabel;

    private JButton blacklistDropdownBtn;
    private JPopupMenu blacklistPopupMenu;
    private List<JCheckBoxMenuItem> blacklistCheckBoxes;
    private JLabel blacklistedSitesLabel;

    public NextUp_Profile_Page(NextUpHome homePage) {
        this.homePage = homePage;
        this.mainFrame = (homePage != null) ? homePage.profile_border : null;
        this.currentUserEmail = (homePage != null) ? homePage.getCurrentUserEmail() : null;

        profileFrame = new JFrame("Next Up - User Settings");
        profileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        profileFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);

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

        // --- Categories Dropdown Setup ---
        String[] options = {"Video Games", "TV", "Movies", "Music", "eSports", "Table Top"};
        List<String> initialSelections = (homePage != null)
                ? homePage.getSelectedCategories()
                : new ArrayList<>();

        categoryCheckBoxes = new ArrayList<>();
        categoryPopupMenu = new JPopupMenu();

        categoriesDropdownBtn = new JButton("Categories ▾");
        categoriesDropdownBtn.setForeground(Color.BLUE);
        categoriesDropdownBtn.setFont(buttonFont);
        categoriesDropdownBtn.setPreferredSize(buttonSize);

        selectedCategoriesLabel = new JLabel("Selected: None", SwingConstants.CENTER);
        selectedCategoriesLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        for (String option : options) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(option);
            if (initialSelections.contains(option)) {
                item.setSelected(true);
            }

            item.addActionListener(e -> {
                syncCategorySelections();
                categoryPopupMenu.setVisible(true);
            });

            categoryCheckBoxes.add(item);
            categoryPopupMenu.add(item);
        }

        categoriesDropdownBtn.addActionListener(e ->
                categoryPopupMenu.show(categoriesDropdownBtn, 0, categoriesDropdownBtn.getHeight())
        );

        syncCategorySelections();

        // --- Blacklist Dropdown Setup ---
        String[] websiteOptions = {
                "tvline.com",
                "variety.com",
                "rottentomatoes.com",
                "polygon.com",
                "gamespot.com",
                "rollingstone.com",
                "pitchfork.com",
                "dotesports.com",
                "esportsinsider.com",
                "dicebreaker.com",
                "belloflostsouls.net"
        };

        List<String> initialBlacklisted = (currentUserEmail != null && !currentUserEmail.trim().isEmpty())
                ? NextUpUsersDatabaseManager.getUserBlacklistedSites(currentUserEmail)
                : new ArrayList<>();

        blacklistCheckBoxes = new ArrayList<>();
        blacklistPopupMenu = new JPopupMenu();

        blacklistDropdownBtn = new JButton("Blacklist ▾");
        blacklistDropdownBtn.setForeground(Color.RED);
        blacklistDropdownBtn.setFont(buttonFont);
        blacklistDropdownBtn.setPreferredSize(buttonSize);

        blacklistedSitesLabel = new JLabel("Blacklisted: None", SwingConstants.CENTER);
        blacklistedSitesLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        for (String site : websiteOptions) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(site);
            if (initialBlacklisted.contains(site)) {
                item.setSelected(true);
            }

            item.addActionListener(e -> {
                syncBlacklistedSites(true);
                blacklistPopupMenu.setVisible(true);
            });

            blacklistCheckBoxes.add(item);
            blacklistPopupMenu.add(item);
        }

        blacklistDropdownBtn.addActionListener(e ->
                blacklistPopupMenu.show(blacklistDropdownBtn, 0, blacklistDropdownBtn.getHeight())
        );

        syncBlacklistedSites(false);

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
        gbc.insets = new Insets(30, 15, 15, 15);
        profileFrame.add(titleLabel, gbc);

        // Settings buttons
        gbc.insets = new Insets(10, 15, 10, 15);
        profileFrame.add(changeUsernameBtn, gbc);
        profileFrame.add(changeEmailBtn, gbc);
        profileFrame.add(changePasswordBtn, gbc);

        // Categories dropdown + label
        profileFrame.add(categoriesDropdownBtn, gbc);
        gbc.insets = new Insets(0, 15, 10, 15);
        profileFrame.add(selectedCategoriesLabel, gbc);

        // Blacklist dropdown + label
        gbc.insets = new Insets(10, 15, 10, 15);
        profileFrame.add(blacklistDropdownBtn, gbc);
        gbc.insets = new Insets(0, 15, 10, 15);
        profileFrame.add(blacklistedSitesLabel, gbc);

        // Back button
        gbc.insets = new Insets(25, 15, 15, 15);
        profileFrame.add(backBtn, gbc);

        // Invisible filler
        gbc.weighty = 1.0;
        profileFrame.add(Box.createVerticalGlue(), gbc);

        profileFrame.setSize(1000, 850);
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

    public NextUp_Profile_Page(JFrame mainFrame) {
        this((NextUpHome) null);
        this.mainFrame = mainFrame;
    }

    private void syncCategorySelections() {
        List<String> selectedItems = new ArrayList<>();
        for (JCheckBoxMenuItem cb : categoryCheckBoxes) {
            if (cb.isSelected()) {
                selectedItems.add(cb.getText());
            }
        }

        if (selectedItems.isEmpty()) {
            selectedCategoriesLabel.setText("Selected: None");
        } else {
            selectedCategoriesLabel.setText("Selected: " + String.join(", ", selectedItems));
        }

        if (homePage != null) {
            homePage.updateSelectedCategories(selectedItems);
        }
    }

    private void syncBlacklistedSites(boolean saveToDb) {
        List<String> blacklistedItems = new ArrayList<>();
        for (JCheckBoxMenuItem cb : blacklistCheckBoxes) {
            if (cb.isSelected()) {
                blacklistedItems.add(cb.getText());
            }
        }

        if (blacklistedItems.isEmpty()) {
            blacklistedSitesLabel.setText("Blacklisted: None");
        } else {
            blacklistedSitesLabel.setText("Blacklisted: " + String.join(", ", blacklistedItems));
        }

        if (saveToDb && currentUserEmail != null && !currentUserEmail.trim().isEmpty()) {
            NextUpUsersDatabaseManager.saveUserBlacklistedSites(currentUserEmail, blacklistedItems);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextUp_Profile_Page((NextUpHome) null));
    }
}
