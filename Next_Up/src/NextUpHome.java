import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.lang.*;

public class NextUpHome {
    JFrame profile_border;
    private JButton dropdownButton;
    private JPopupMenu popupMenu;
    private List<JCheckBoxMenuItem> checkBoxes;
    private JLabel selectionLabel;
    private JPanel buttonspanel; // Made buttonspanel a class-level field

    // Set up frame
    public NextUpHome() {
        profile_border = new JFrame("NextUp - Homepage");
        profile_border.setSize(400, 500);
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
        buttonspanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Initialized field

        // Set up homepage textbox
        JLabel homepage_message_textbox = new JLabel("<html><p> Welcome to your homepage - Select a category to view its corresponding products! <p><html>");
        textboxespanel.add(homepage_message_textbox);
        homepage_message_textbox.setForeground(Color.BLUE);
        homepage_message_textbox.setFont(new Font("Tahoma", Font.BOLD, 18));

        // Add homepage message to the text and feed holder panel
        textandfeedholderpanel.add(textboxespanel, BorderLayout.NORTH);

        // (Static category buttons removed; they will now be populated dynamically)

        // Log-out button
        JButton logout_button = new JButton("Log out");
        logout_button.setForeground(Color.RED);
        logout_button.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Set up settings and logout holder panel
        JPanel settingsandlogout_panel = new JPanel();
        settingsandlogout_panel.setLayout(new BoxLayout(settingsandlogout_panel, BoxLayout.Y_AXIS));

        // String for all the options in the dropdown menu.
        String[] options = {"Video Games", "TV", "Movies", "Music"};
        checkBoxes = new ArrayList<>();
        popupMenu = new JPopupMenu();

        dropdownButton = new JButton("Categories ▾");
        dropdownButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        selectionLabel = new JLabel("Selected: None");
        selectionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        for (String option : options) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(option);

            // Prevent the popup from instantly closing on click
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateSelectionLabel();
                    // STAY OPEN!!
                    popupMenu.setVisible(true);
                }
            });

            checkBoxes.add(item);
            popupMenu.add(item);
        }

        // Action listener to actually show the popup menu under the button
        dropdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(dropdownButton, 0, dropdownButton.getHeight());
            }
        });

        // Add elements to the top panel (Do NOT add the popupMenu directly here)
        settingsandlogout_panel.add(dropdownButton);
        settingsandlogout_panel.add(selectionLabel);
        settingsandlogout_panel.add(logout_button);

        // Set up feed panel
        JPanel feed_panel = new JPanel();
        feed_panel.setLayout(new BorderLayout());
        feed_panel.setBackground(Color.LIGHT_GRAY);
        JLabel feed_goes_here_textbox = new JLabel("Feed will go here");
        feed_panel.add(feed_goes_here_textbox);
        feed_goes_here_textbox.setHorizontalAlignment(JLabel.CENTER);
        textandfeedholderpanel.add(feed_panel, BorderLayout.CENTER);

        // Set up positions for panels on the frame
        profile_border.add(settingsandlogout_panel, BorderLayout.NORTH);
        profile_border.add(textandfeedholderpanel, BorderLayout.CENTER);
        profile_border.add(buttonspanel, BorderLayout.SOUTH);

        profile_border.setVisible(true); // Set whole window page visibility to true
    }

    // Updated method to manage both the text label and bottom panel components dynamically
    private void updateSelectionLabel() {
        List<String> selectedItems = new ArrayList<>();
        for (JCheckBoxMenuItem cb : checkBoxes) {
            if (cb.isSelected()) {
                selectedItems.add(cb.getText());
            }
        }

        if (selectedItems.isEmpty()) {
            selectionLabel.setText("Selected: None");
        } else {
            selectionLabel.setText("Selected: " + String.join(", ", selectedItems));
        }

        // Clear out the bottom panel and add buttons for each selected option
        buttonspanel.removeAll();
        for (String item : selectedItems) {
            JButton categoryButton = new JButton(item);
            buttonspanel.add(categoryButton);
        }

        // Refresh the panel layout so Swing redraws the newly added buttons
        buttonspanel.revalidate();
        buttonspanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextUpHome());
    }
}