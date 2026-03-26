package com.orbit.gui;

import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProfilePanel extends JPanel implements NetworkListener {

    // User Data
    private String currentUsername;
    private String currentDisplayName;

    // UI Components
    private JPanel feedContainer;
    private JLabel lblPostCount;
    private JLabel lblFriendCount;

    // Theme Colors
    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color CARD_BG = Color.decode("#242526");
    private final Color ACCENT_BLUE = Color.decode("#0084FF");
    private final Color TEXT_PRIMARY = Color.decode("#E4E6EB");
    private final Color TEXT_MUTED = Color.decode("#B0B3B8");

    public ProfilePanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // Register to listen for profile data from the server later
        NetworkManager.getInstance().addListener(this);

        initComponents();
        loadDummyProfileData();
    }

    private void initComponents() {
        // Wrapper for the entire scrollable page
        JPanel pageWrapper = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]"));
        pageWrapper.setBackground(MAIN_BG);

        // 1. TOP SECTION: Profile Header Card
        pageWrapper.add(buildProfileHeader(), "growx");

        // 2. BOTTOM SECTION: The Post Feed
        feedContainer = new JPanel(new MigLayout("wrap 1, fillx, insets 20 150 50 150", "[fill]", "[]20[]")); // Margins keep the feed centered
        feedContainer.setBackground(MAIN_BG);
        
        JLabel lblTimeline = new JLabel("Timeline");
        lblTimeline.putClientProperty("FlatLaf.style", "font: bold 20; foreground: #E4E6EB");
        feedContainer.add(lblTimeline, "gapbottom 10");

        pageWrapper.add(feedContainer, "growx");

        // Put the whole page in a ScrollPane
        JScrollPane mainScroll = new JScrollPane(pageWrapper);
        mainScroll.setBorder(null);
        mainScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel buildProfileHeader() {
        JPanel headerCard = new JPanel(new MigLayout("insets 30 150 30 150, fillx", "[][grow][]", "[][]"));
        headerCard.setBackground(CARD_BG);
        headerCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#393A3B")));

        // Avatar
        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 600% $defaultFont; foreground: #B0B3B8");
        headerCard.add(avatar, "span 1 2, aligny top, gapright 20");

        // Name & Username
        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 5"));
        nameStack.setOpaque(false);
        
        JLabel lblName = new JLabel(currentDisplayName);
        lblName.putClientProperty("FlatLaf.style", "font: bold 32; foreground: #E4E6EB");
        
        JLabel lblUser = new JLabel("@" + currentUsername);
        lblUser.putClientProperty("FlatLaf.style", "font: 16; foreground: #B0B3B8");
        
        nameStack.add(lblName);
        nameStack.add(lblUser);
        headerCard.add(nameStack, "growx, aligny center");

        // Edit Button
        JButton btnEdit = new JButton("✏️ Edit Profile");
        btnEdit.putClientProperty("FlatLaf.style", "arc: 10; background: #3A3B3C; foreground: #E4E6EB; font: bold 14; borderWidth: 0; margin: 8, 15, 8, 15");
        headerCard.add(btnEdit, "aligny center, wrap");

        // Stats Row
        JPanel statsRow = new JPanel(new MigLayout("insets 0, gap 30"));
        statsRow.setOpaque(false);
        
        lblPostCount = new JLabel("0 Posts");
        lblPostCount.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        
        lblFriendCount = new JLabel("0 Friends");
        lblFriendCount.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        
        statsRow.add(lblPostCount);
        statsRow.add(lblFriendCount);
        headerCard.add(statsRow, "skip 1, span 2, growx, gaptop 10");

        return headerCard;
    }

    // ==============================================================
    // TIMELINE POST GENERATOR
    // ==============================================================

    private JPanel createPostCard(String timeAgo, String content, int likes, int comments) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx, insets 15", "[fill]", "[]15[]15[]"));
        card.setBackground(CARD_BG);
        card.putClientProperty("FlatLaf.style", "arc: 15");

        // 1. Post Header (Avatar, Name, Time)
        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[][grow][]", ""));
        header.setOpaque(false);
        
        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 200% $defaultFont; foreground: #B0B3B8");
        header.add(avatar);
        
        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0"));
        nameStack.setOpaque(false);
        JLabel lblName = new JLabel(currentDisplayName);
        lblName.putClientProperty("FlatLaf.style", "font: bold 15; foreground: #E4E6EB");
        JLabel lblTime = new JLabel(timeAgo);
        lblTime.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        nameStack.add(lblName);
        nameStack.add(lblTime);
        header.add(nameStack, "growx");
        
        JButton btnOptions = new JButton("⋮");
        btnOptions.putClientProperty("FlatLaf.style", "buttonType: borderless; font: 150% $defaultFont; foreground: #B0B3B8");
        header.add(btnOptions);
        
        card.add(header);

        // 2. Post Body
        JTextArea txtContent = new JTextArea(content);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setOpaque(false);
        txtContent.putClientProperty("FlatLaf.style", "font: 15; foreground: #E4E6EB");
        card.add(txtContent);

        // 3. Stats & Interactions
        JPanel footer = new JPanel(new MigLayout("insets 5 0 0 0, fillx", "[grow][grow]", "[]10[]"));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#393A3B")));
        
        JLabel lblStats = new JLabel("👍 " + likes + "   💬 " + comments + " Comments");
        lblStats.putClientProperty("FlatLaf.style", "font: 13; foreground: #B0B3B8");
        footer.add(lblStats, "span 2, wrap");

        JButton btnLike = new JButton("👍 Like");
        btnLike.putClientProperty("FlatLaf.style", "buttonType: borderless; font: bold 14; foreground: #B0B3B8; hoverBackground: #3A3B3C; arc: 10");
        
        JButton btnComment = new JButton("💬 Comment");
        btnComment.putClientProperty("FlatLaf.style", "buttonType: borderless; font: bold 14; foreground: #B0B3B8; hoverBackground: #3A3B3C; arc: 10");
        
        footer.add(btnLike, "growx");
        footer.add(btnComment, "growx");
        
        card.add(footer);

        return card;
    }

    // ==============================================================
    // NETWORK INTERFACE
    // ==============================================================

    @Override
    public void onMessageReceived(String incomingMessage) {
        // We will wire this up later to fetch profile data!
        // e.g. if (incomingMessage.startsWith("PROFILE_DATA|")) { update UI }
    }

    // ==============================================================
    // DUMMY DATA FOR TESTING
    // ==============================================================

    private void loadDummyProfileData() {
        lblPostCount.setText("3 Posts");
        lblFriendCount.setText("128 Friends");

        feedContainer.add(createPostCard("Just now", "Finally finishing up the Orbit Messenger capstone architecture! The Java Swing UI is coming together perfectly. Next up: polishing the MySQL database integration.", 5, 2));
        
        feedContainer.add(createPostCard("2 days ago", "Restocking the Scent 22 inventory this weekend. Let me know if you need any rebranded perfumes before we run out! Choco-mallows coming back next season too. 🍫✨", 24, 8));
        
        feedContainer.add(createPostCard("Last week", "Midterms at Bestlink College are brutal this semester. The BSIT network administration modules are no joke. Time to review those subnets.", 12, 4));
    }
}