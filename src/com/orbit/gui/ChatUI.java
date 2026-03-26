package com.orbit.gui;

import com.orbit.network.NetworkManager;
import java.awt.*;
import javax.swing.*;

public class ChatUI extends JFrame {

    private String currentUsername;
    private String currentDisplayName;

    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color SIDEBAR_BG = Color.decode("#242526");
    private ProfilePanel profilePanel;
    
    
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

    public ProfilePanel getProfilePanel() {
    return this.profilePanel;
}
    
    private void initComponents() {
        setLayout(new BorderLayout());

        profilePanel = new ProfilePanel(currentUsername, currentDisplayName);
        
        // TOP NAVIGATION BAR
        JPanel topNavBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 8));
        topNavBar.setBackground(SIDEBAR_BG);
        topNavBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#393A3B")));

        topNavBar.add(createNavButton("💬", "messages_card", "Messages"));
        topNavBar.add(createNavButton("🏠", "home_card", "Home"));
        topNavBar.add(createNavButton("👥", "friends_card", "Friends"));
        topNavBar.add(createNavButton("👤", "profile_card", "Profile"));
        topNavBar.add(createNavButton("⚙️", "settings_card", "Settings"));

        add(topNavBar, BorderLayout.NORTH);

        // MAIN CARD LAYOUT
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(MAIN_BG);

        // --- PLUG IN THE MODULAR PANELS HERE ---
        mainCardPanel.add(new MessagesPanel(currentUsername, currentDisplayName), "messages_card");
        mainCardPanel.add(new FriendlistPanel(currentUsername, currentDisplayName), "friends_card");
        mainCardPanel.add(new ProfilePanel(currentUsername, currentDisplayName), "profile_card");
        // Placeholders for team members to build later:
        mainCardPanel.add(buildPlaceholderPanel("Home Feed"), "home_card");

        
        mainCardPanel.add(buildPlaceholderPanel("Settings"), "settings_card");

        add(mainCardPanel, BorderLayout.CENTER);
      
    }

    private JButton createNavButton(String iconText, String cardName, String tooltip) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip);
        btn.putClientProperty("FlatLaf.style", "borderWidth: 0; focusWidth: 0; arc: 15; margin: 8, 20, 8, 20; hoverBackground: #3A3B3C; font: 180% $defaultFont; foreground: #B0B3B8");
        btn.addActionListener(e -> cardLayout.show(mainCardPanel, cardName));
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
        // Ensure cardLayout and mainCardPanel are class-level variables!
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
