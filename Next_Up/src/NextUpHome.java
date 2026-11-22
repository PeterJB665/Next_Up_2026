import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class NextUpHome {
    JFrame profile_border;
    private JPanel buttonspanel;
    private List<String> selectedCategories = new ArrayList<>();
    private String currentUserEmail;

    public NextUpHome() {
        this(null);
    }

    public NextUpHome(String userEmail) {
        this.currentUserEmail = userEmail;

        profile_border = new JFrame("NextUp - Homepage");
        profile_border.setSize(800, 800);
        profile_border.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        profile_border.setLocationRelativeTo(null);

        // Set up JPanel to hold the homepage textbox and the feed area
        JPanel textandfeedholderpanel = new JPanel(new BorderLayout());
        textandfeedholderpanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set up JPanel for textboxes
        JPanel textboxespanel = new JPanel();
        textboxespanel.setLayout(new BoxLayout(textboxespanel, BoxLayout.Y_AXIS));
        textboxespanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Set up JPanel belonging to category buttons
        buttonspanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Set up homepage textbox
        JLabel homepage_message_textbox = new JLabel("<html><p> Welcome to your homepage - Select a category to view its corresponding products! <p><html>");
        textboxespanel.add(homepage_message_textbox);
        homepage_message_textbox.setForeground(Color.BLUE);
        homepage_message_textbox.setFont(new Font("Tahoma", Font.BOLD, 18));

        // Add homepage message to the text and feed holder panel
        textandfeedholderpanel.add(textboxespanel, BorderLayout.NORTH);

        // Log-out button
        JButton logout_button = new JButton("Log out");
        logout_button.setForeground(Color.RED);
        logout_button.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(logout_button, "You have been logged out successfully!");
                new NextUp();
                profile_border.dispose();
            }
        });

        // Set up settings and logout holder panel (Left side of header)
        JPanel settingsandlogout_panel = new JPanel();
        settingsandlogout_panel.setLayout(new BoxLayout(settingsandlogout_panel, BoxLayout.Y_AXIS));

        // Add search button to homepage
        JButton search_button = new JButton("Search");
        search_button.setAlignmentX(Component.LEFT_ALIGNMENT);
        search_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Search_Page_NU(currentUserEmail);
                profile_border.dispose();
            }
        });

        // Add elements to the left side of the top panel
        settingsandlogout_panel.add(search_button);
        settingsandlogout_panel.add(logout_button);

        // Profile Button in Top-Right Corner
        JButton profile_button = new JButton("Profile");
        profile_button.setForeground(Color.BLUE);
        profile_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                profile_border.setVisible(false);
                new NextUp_Profile_Page(NextUpHome.this);
            }
        });

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topRightPanel.add(profile_button);

        // Combine left controls and top-right Profile button into a single header panel
        JPanel topHeaderPanel = new JPanel(new BorderLayout());
        topHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        topHeaderPanel.add(settingsandlogout_panel, BorderLayout.WEST);
        topHeaderPanel.add(topRightPanel, BorderLayout.EAST);

        // Set up feed panel
        JPanel feed_panel = new JPanel();
        feed_panel.setLayout(new BorderLayout());
        feed_panel.setBackground(Color.LIGHT_GRAY);
        JLabel feed_goes_here_textbox = new JLabel("Feed will go here");
        feed_panel.add(feed_goes_here_textbox);
        feed_goes_here_textbox.setHorizontalAlignment(JLabel.CENTER);
        textandfeedholderpanel.add(feed_panel, BorderLayout.CENTER);

        // Set up positions for panels on the frame
        profile_border.add(topHeaderPanel, BorderLayout.NORTH);
        profile_border.add(textandfeedholderpanel, BorderLayout.CENTER);
        profile_border.add(buttonspanel, BorderLayout.SOUTH);

        // Load saved categories for the logged-in user
        if (currentUserEmail != null && !currentUserEmail.trim().isEmpty()) {
            List<String> saved = NextUpUsersDatabaseManager.getUserSelectedCategories(currentUserEmail);
            renderCategoryButtons(saved);
        }

        profile_border.setVisible(true);
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    // Returns a copy of currently selected categories so the Profile page can sync checkboxes
    public List<String> getSelectedCategories() {
        return new ArrayList<>(selectedCategories);
    }

    // Updates bottom category buttons and persists selections to the current user's account
    public void updateSelectedCategories(List<String> newSelections) {
        renderCategoryButtons(newSelections);

        if (currentUserEmail != null && !currentUserEmail.trim().isEmpty()) {
            NextUpUsersDatabaseManager.saveUserSelectedCategories(currentUserEmail, this.selectedCategories);
        }
    }

    private void renderCategoryButtons(List<String> selections) {
        this.selectedCategories = (selections != null)
                ? new ArrayList<>(selections)
                : new ArrayList<>();

        buttonspanel.removeAll();
        for (String item : this.selectedCategories) {
            JButton categoryButton = new JButton(item);
            buttonspanel.add(categoryButton);
        }

        buttonspanel.revalidate();
        buttonspanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextUpHome());
    }
}
