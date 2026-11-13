import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NextUpSearchFunction {

    // Stores the mapping of the 6 verticals to their respective RSS/Atom feed URLs
    private static final Map<String, List<String>> CATEGORY_FEEDS = new HashMap<>();

    // Stores blacklisted domains that the crawler will skip (ready for future UI integration)
    private static final Set<String> BLACKLISTED_DOMAINS = new HashSet<>();

    static {
        // Initialize the 6 core verticals with default RSS feeds
        CATEGORY_FEEDS.put("TV Shows", new ArrayList<>(Arrays.asList(
                "https://tvline.com/feed/",
                "https://variety.com/v/tv/feed/"
        )));

        CATEGORY_FEEDS.put("Movies", new ArrayList<>(Arrays.asList(
                "https://www.rottentomatoes.com/syndication/rss/top_movies.xml",
                "https://variety.com/v/film/feed/"
        )));

        CATEGORY_FEEDS.put("Video Games", new ArrayList<>(Arrays.asList(
                "https://www.polygon.com/rss/index.xml",
                "https://www.gamespot.com/feeds/news/"
        )));

        CATEGORY_FEEDS.put("Music", new ArrayList<>(Arrays.asList(
                "https://www.rollingstone.com/music/feed/",
                "https://pitchfork.com/feed/feed-news/rss"
        )));

        CATEGORY_FEEDS.put("eSports", new ArrayList<>(Arrays.asList(
                "https://dotesports.com/feed",
                "https://esportsinsider.com/feed"
        )));

        CATEGORY_FEEDS.put("Tabletop Gaming", new ArrayList<>(Arrays.asList(
                "https://www.dicebreaker.com/feed",
                "https://www.belloflostsouls.net/feed"
        )));
    }

    // Hooks for future expansion: adding custom user feeds and blacklisting domains
    public static void addCustomFeed(String category, String feedUrl) {
        CATEGORY_FEEDS.computeIfAbsent(category, k -> new ArrayList<>()).add(feedUrl);
    }

    public static void addBlacklistedDomain(String domain) {
        BLACKLISTED_DOMAINS.add(domain.toLowerCase().trim());
    }

    private static boolean isBlacklisted(String url) {
        String lowerUrl = url.toLowerCase();
        for (String domain : BLACKLISTED_DOMAINS) {
            if (lowerUrl.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    // Primary method to crawl all selected categories and populate the SQLite database
    public static int searchAndPopulate(List<String> selectedCategories) {
        NextUpSearchReturnsDatabaseManager.initializeDatabase();
        int newEntriesCount = 0;

        for (String category : selectedCategories) {
            List<String> feeds = CATEGORY_FEEDS.get(category);
            if (feeds == null) {
                System.out.println("No feeds configured for category: " + category);
                continue;
            }

            for (String feedUrl : feeds) {
                if (isBlacklisted(feedUrl)) {
                    System.out.println("Skipping blacklisted feed: " + feedUrl);
                    continue;
                }
                newEntriesCount += fetchAndParseFeed(category, feedUrl);
            }
        }
        return newEntriesCount;
    }

    // Connects to a single RSS/Atom feed, parses the XML, and sends records to the Database Manager
    private static int fetchAndParseFeed(String category, String feedUrl) {
        int addedCount = 0;
        HttpURLConnection conn = null;

        try {
            URL url = URI.create(feedUrl).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Set a standard User-Agent so strict web servers don't reject the Java connection
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) NextUpApp/1.0");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            try (InputStream inputStream = conn.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // Disable external DTDs for faster, safer XML parsing
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);
                doc.getDocumentElement().normalize();

                // Check standard RSS 2.0 <item> nodes. An RSS 2.0 <item> node is the specific XML 
                // container block that wraps a single news story, video, or podcast episode inside 
                // an RSS feed.
                NodeList itemNodes = doc.getElementsByTagName("item");
                if (itemNodes.getLength() > 0) {
                    for (int i = 0; i < itemNodes.getLength(); i++) {
                        Element item = (Element) itemNodes.item(i);
                        String title = getTagValue("title", item);
                        String link = getTagValue("link", item);
                        String pubDate = getTagValue("pubDate", item);
                        if (pubDate.isEmpty()) {
                            pubDate = getTagValue("dc:date", item);
                        }

                        if (!link.isEmpty() && !title.isEmpty() && !isBlacklisted(link)) {
                            boolean inserted = NextUpSearchReturnsDatabaseManager.insertSearchReturn(
                                    category, link, title, pubDate.isEmpty() ? "Unknown Date" : pubDate
                            );
                            if (inserted) {
                                addedCount++;
                            }
                        }
                    }
                } else {
                    // Fallback to Atom <entry> nodes (used by sites like Polygon / Dicebreaker)
                    // This is a newer, alternative XML syndication standard to RSS 2.0
                    NodeList entryNodes = doc.getElementsByTagName("entry");
                    for (int i = 0; i < entryNodes.getLength(); i++) {
                        Element entry = (Element) entryNodes.item(i);
                        String title = getTagValue("title", entry);
                        String link = getAtomLink(entry);
                        String pubDate = getTagValue("published", entry);
                        if (pubDate.isEmpty()) {
                            pubDate = getTagValue("updated", entry);
                        }

                        if (!link.isEmpty() && !title.isEmpty() && !isBlacklisted(link)) {
                            boolean inserted = NextUpSearchReturnsDatabaseManager.insertSearchReturn(
                                    category, link, title, pubDate.isEmpty() ? "Unknown Date" : pubDate
                            );
                            if (inserted) {
                                addedCount++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error crawling feed [" + feedUrl + "]: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return addedCount;
    }

    // Helper to extract text inside standard XML tags like <title>...</title>
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            String content = nodeList.item(0).getTextContent();
            if (content != null) {
                return content.trim();
            }
        }
        return "";
    }

    // Helper to extract the href attribute from Atom <link href="..."/> tags
    private static String getAtomLink(Element entry) {
        NodeList links = entry.getElementsByTagName("link");
        for (int i = 0; i < links.getLength(); i++) {
            Element linkEl = (Element) links.item(i);
            String rel = linkEl.getAttribute("rel");
            String href = linkEl.getAttribute("href");
            if ((rel.isEmpty() || rel.equals("alternate")) && !href.isEmpty()) {
                return href.trim();
            }
        }
        return getTagValue("link", entry);
    }

    // Standalone test harness to verify crawling and database insertion
    public static void main(String[] args) {
        List<String> testCategories = Arrays.asList(
                "TV Shows", "Movies", "Video Games", "Music", "eSports", "Tabletop Gaming"
        );

        System.out.println("Running test crawl across all 6 verticals...");
        int added = searchAndPopulate(testCategories);
        System.out.println("Crawl complete. Newly added rows: " + added);

        List<NextUpSearchReturnsDatabaseManager.SearchResult> storedResults =
                NextUpSearchReturnsDatabaseManager.getResultsByTopics(testCategories);
        System.out.println("Total matching rows currently in database: " + storedResults.size());

        // Print the top 5 most recent entries to verify formatting
        for (int i = 0; i < Math.min(5, storedResults.size()); i++) {
            System.out.println(storedResults.get(i));
        }
    }
}