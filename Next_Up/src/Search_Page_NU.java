import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Search_Page_NU {
    private String currentUserEmail;

    public Search_Page_NU() {
        this(null);
    }

    public Search_Page_NU(String userEmail) {
        this.currentUserEmail = userEmail;

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
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        searchFrame.add(titleLabel, gbc);

        // 2. Create Search Bar with Placeholder
        JTextField searchBar = new JTextField(30);
        String placeholderText = "Enter your search term here...";
        Color placeholderColor = new Color(0, 0, 0, 100);
        Color textColor = Color.BLACK;

        searchBar.setText(placeholderText);
        searchBar.setForeground(placeholderColor);

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
        resultsArea.setEditable(false);
        resultsArea.setText("search results here...");
        resultsArea.setFont(new Font("Tahoma", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultsArea);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        searchFrame.add(scrollPane, gbc);

        // 5. Add Action Listener to Search Button (Calls NextUpSearchFunction off the UI thread)
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchBar.getText();

                if (query.equals(placeholderText) || query.trim().isEmpty()) {
                    resultsArea.setText("Please enter a valid search term.");
                    return;
                }

                searchButton.setEnabled(false);
                resultsArea.setText("Searching for \"" + query.trim() + "\"...");

                new SwingWorker<List<NextUpSearchReturnsDatabaseManager.SearchResult>, Void>() {
                    @Override
                    protected List<NextUpSearchReturnsDatabaseManager.SearchResult> doInBackground() {
                        List<NextUpSearchReturnsDatabaseManager.SearchResult> rawResults =
                                NextUpSearchFunction.executeSearch(query);

                        List<String> blacklistedDomains = (currentUserEmail != null && !currentUserEmail.trim().isEmpty())
                                ? NextUpUsersDatabaseManager.getUserBlacklistedSites(currentUserEmail)
                                : new ArrayList<>();

                        if (blacklistedDomains.isEmpty()) {
                            return rawResults;
                        }

                        List<NextUpSearchReturnsDatabaseManager.SearchResult> filteredResults = new ArrayList<>();
                        for (NextUpSearchReturnsDatabaseManager.SearchResult r : rawResults) {
                            if (!isUrlBlacklisted(r.url, blacklistedDomains)) {
                                filteredResults.add(r);
                            }
                        }
                        return filteredResults;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<NextUpSearchReturnsDatabaseManager.SearchResult> results = get();
                            if (results.isEmpty()) {
                                resultsArea.setText("No results found for: " + query.trim()
                                        + "\n\nTry clicking 'Refresh Feeds' or adjusting your blacklisted websites in Profile.");
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Found ").append(results.size()).append(" result(s):\n\n");
                                for (NextUpSearchReturnsDatabaseManager.SearchResult r : results) {
                                    sb.append(r.toString()).append("\n\n");
                                }
                                resultsArea.setText(sb.toString());
                                resultsArea.setCaretPosition(0);
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
                new NextUpHome(currentUserEmail);
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

        searchFrame.setSize(800, 500);
        searchFrame.setLocationRelativeTo(null);
        searchFrame.setVisible(true);
    }

    private static boolean isUrlBlacklisted(String url, List<String> blacklistedDomains) {
        if (url == null) {
            return false;
        }
        String lowerUrl = url.toLowerCase();
        for (String domain : blacklistedDomains) {
            if (lowerUrl.contains(domain.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Search_Page_NU());
    }
}
