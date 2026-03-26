package com.orbit.gui;

import com.orbit.network.NetworkManager;
import java.awt.*;
import javax.swing.*;

public class ChatUI extends JFrame {

    private String currentUsername;
    private String currentDisplayName;

    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    // 🚀 NEW: Store references to ALL panels so we can refresh them
    private HomePanel homePanel;
    private MessagesPanel messagesPanel;
    private FriendlistPanel friendlistPanel;
    private ProfilePanel profilePanel;

    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color SIDEBAR_BG = Color.decode("#242526");
    
    public ChatUI(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;

        applyOrbitStyles();
        setupFrame();

        // 🚀 THE FIX: Turn on the internet FIRST!
        // Make sure this IP matches your XAMPP PC's IPv4 address
        com.orbit.network.NetworkManager.getInstance().setServerAddress("192.168.100.32");
        com.orbit.network.NetworkManager.getInstance().connect(currentUsername);

        // NOW build the panels (so they can successfully talk to the server)
        initComponents();

        System.out.println("Orbit ChatUI Initialized: " + displayName);
    }

    private void setupFrame() {
        setTitle("Orbit Secure Messenger - " + currentDisplayName);
        setSize(1280, 800);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getRootPane().putClientProperty("flatlaf.titleBarShowTitle", false);
        getContentPane().setBackground(MAIN_BG);
    }

    // Getters so panels can talk to each other
    public ProfilePanel getProfilePanel() {
        return this.profilePanel;
    }
    
    public HomePanel getHomePanel() {
        return this.homePanel;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());

        // 1. 🚀 INITIALIZE ALL PANELS FIRST
        homePanel = new HomePanel(currentUsername, currentDisplayName);
        messagesPanel = new MessagesPanel(currentUsername, currentDisplayName);
        friendlistPanel = new FriendlistPanel(currentUsername, currentDisplayName);
        profilePanel = new ProfilePanel(currentUsername, currentDisplayName);
        
        // 2. TOP NAVIGATION BAR
        JPanel topNavBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 8));
        topNavBar.setBackground(SIDEBAR_BG);
        topNavBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#393A3B")));

        // 3. CREATE BUTTONS AND ADD REFRESH LOGIC
        JButton btnMessages = createNavButton("💬", "Messages");
        btnMessages.addActionListener(e -> showCard("messages_card"));

        JButton btnHome = createNavButton("🏠", "Home");
        btnHome.addActionListener(e -> {
            showCard("home_card");
            homePanel.refreshFeed(); // Force feed to refresh when clicked
        });

        JButton btnFriends = createNavButton("👥", "Friends");
        btnFriends.addActionListener(e -> {
            showCard("friends_card");
            NetworkManager.getInstance().send("GET_FRIEND_REQUESTS|" + currentUsername); // Refresh friends list
        });

        JButton btnProfile = createNavButton("👤", "Profile");
        btnProfile.addActionListener(e -> {
            showCard("profile_card");
            profilePanel.loadUserProfile(currentUsername); // Load your profile data
        });

        JButton btnSettings = createNavButton("⚙️", "Settings");
        btnSettings.addActionListener(e -> showCard("settings_card"));

        topNavBar.add(btnMessages);
        topNavBar.add(btnHome);
        topNavBar.add(btnFriends);
        topNavBar.add(btnProfile);
        topNavBar.add(btnSettings);

        add(topNavBar, BorderLayout.NORTH);

        // 4. MAIN CARD LAYOUT
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(MAIN_BG);

        // 5. --- PLUG IN THE MODULAR PANELS HERE ---
        // Notice we pass in the variables we initialized in Step 1!
        mainCardPanel.add(messagesPanel, "messages_card");
        mainCardPanel.add(homePanel, "home_card");
        mainCardPanel.add(friendlistPanel, "friends_card");
        mainCardPanel.add(profilePanel, "profile_card");
        mainCardPanel.add(buildPlaceholderPanel("Settings"), "settings_card");

        add(mainCardPanel, BorderLayout.CENTER);
    }

    // Notice we removed 'cardName' parameter so we can add custom ActionListeners above
    private JButton createNavButton(String iconText, String tooltip) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip);
        btn.putClientProperty("FlatLaf.style", "borderWidth: 0; focusWidth: 0; arc: 15; margin: 8, 20, 8, 20; hoverBackground: #3A3B3C; font: 180% $defaultFont; foreground: #B0B3B8");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void applyOrbitStyles() {
        UIManager.put("SplitPane.dividerSize", 0);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumb", Color.decode("#555555"));
    }
    
    public void showCard(String cardName) {
        if (cardLayout != null && mainCardPanel != null) {
            cardLayout.show(mainCardPanel, cardName);
        } else {
            System.err.println("Error: CardLayout or MainPanel not initialized in ChatUI.");
        }
    }

    private JPanel buildPlaceholderPanel(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(MAIN_BG);
        JLabel l = new JLabel(text + " (Under Construction)");
        l.putClientProperty("FlatLaf.styleClass", "h1");
        l.setForeground(Color.decode("#E4E6EB"));
        p.add(l);
        return p;
    }
}