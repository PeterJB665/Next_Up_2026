import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
public class Search_Page_NU {
    public Search_Page_NU() {
        //public static void main(String[] args) {
        //SwingUtilities.invokeLater(() -> {
        // Create the main frame
        JFrame searchFrame = new JFrame("Next Up - Search sources");
        searchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up GridBagLayout
        searchFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 1. Create Title Label
        JLabel titleLabel = new JLabel("Search Next Up");
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Spans across both the search bar and button
        gbc.anchor = GridBagConstraints.CENTER;
        searchFrame.add(titleLabel, gbc);

        // 2. Create Search Bar with Placeholder
        JTextField searchBar = new JTextField(30);
        String placeholderText = "Enter your search term here...";
        Color placeholderColor = new Color(0, 0, 0, 100);
        Color textColor = Color.BLACK;

        searchBar.setText(placeholderText);
        searchBar.setForeground(placeholderColor);

        // Add FocusListener for the search bar placeholder
        searchBar.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchBar.getText().equals(placeholderText)) {
                    searchBar.setText("");
                    searchBar.setForeground(textColor);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchBar.getText().isEmpty()) {
                    searchBar.setText(placeholderText);
                    searchBar.setForeground(placeholderColor);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchFrame.add(searchBar, gbc);

        // 3. Create Search Button
        JButton searchButton = new JButton("Search");
        searchButton.setForeground(Color.BLUE);
        searchButton.setFont(new Font("Tahoma", Font.BOLD, 14));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        searchFrame.add(searchButton, gbc);

        // 4. Create Results Display Area
        JTextArea resultsArea = new JTextArea(15, 40);
        resultsArea.setEditable(false); // Prevents users from typing in the results box
        resultsArea.setText("search results here...");
        resultsArea.setFont(new Font("Tahoma", Font.PLAIN, 14));

        // Wrap the text area in a scroll pane in case there are many results
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Tells the layout to give extra vertical space to the results area
        searchFrame.add(scrollPane, gbc);

        // 5. Add Action Listener to Button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchBar.getText();

                // Basic validation to make sure they didn't just search the placeholder
                if (query.equals(placeholderText) || query.trim().isEmpty()) {
                    resultsArea.setText("Please enter a valid search term.");
                } else {
                    // This is where you will eventually query your NextUp SQLite database
                    // This is where TC will eventually query NextUp SQLite database
                    resultsArea.setText("Searching database for: " + query + "\n\n(Database connection needed to display real results...)");
                }
            }
        });

        // 5. Add Action Listener to Search Button (Calls NextUpSearchFunction off the UI thread)
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchBar.getText();

                // Basic validation to make sure they didn't just search the placeholder
                if (query.equals(placeholderText) || query.trim().isEmpty()) {
                    resultsArea.setText("Please enter a valid search term.");
                    return;
                }

                // Disable button and show feedback while background thread runs
                searchButton.setEnabled(false);
                resultsArea.setText("Searching for \"" + query.trim() + "\"...");

                new SwingWorker<List<NextUpSearchReturnsDatabaseManager.SearchResult>, Void>() {
                    @Override
                    protected List<NextUpSearchReturnsDatabaseManager.SearchResult> doInBackground() {
                        return NextUpSearchFunction.executeSearch(query);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<NextUpSearchReturnsDatabaseManager.SearchResult> results = get();
                            if (results.isEmpty()) {
                                resultsArea.setText("No results found for: " + query.trim()
                                        + "\n\nTry clicking 'Refresh Feeds' to pull the latest articles.");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Found ").append(results.size()).append(" result(s):\n\n");
                                for (NextUpSearchReturnsDatabaseManager.SearchResult r : results) {
                                    sb.append(r.toString()).append("\n\n");
                                }
                                resultsArea.setText(sb.toString());
                                resultsArea.setCaretPosition(0); // Scroll back to top
                            }
                        } catch (Exception ex) {
                            resultsArea.setText("Error retrieving search results: " + ex.getMessage());
                        } finally {
                            searchButton.setEnabled(true);
                        }
                    }
                }.execute();
            }
        });

        // 6. Add "Back" button and "Refresh Feeds" button
        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.RED);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NextUpHome();
                searchFrame.dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        searchFrame.add(backButton, gbc);

        JButton refreshButton = new JButton("Refresh Feeds");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshButton.setEnabled(false);
                searchButton.setEnabled(false);
                resultsArea.setText("Crawling RSS feeds and updating database... Please wait.");

                new SwingWorker<Integer, Void>() {
                    @Override
                    protected Integer doInBackground() {
                        return NextUpSearchFunction.refreshAllFeeds();
                    }

                    @Override
                    protected void done() {
                        try {
                            int added = get();
                            resultsArea.setText("Feed refresh complete. " + added
                                    + " new article(s) added to the database.\nEnter a search term above to query them.");
                        } catch (Exception ex) {
                            resultsArea.setText("Error refreshing feeds: " + ex.getMessage());
                        } finally {
                            refreshButton.setEnabled(true);
                            searchButton.setEnabled(true);
                        }
                    }
                }.execute();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        searchFrame.add(refreshButton, gbc);

        // Set final window properties to match the rest of the NextUp project
        searchFrame.setSize(800, 500);
        searchFrame.setLocationRelativeTo(null);
        searchFrame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Search_Page_NU());

    }
}
