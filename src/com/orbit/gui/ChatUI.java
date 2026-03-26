package com.orbit.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatUI extends JFrame {

    // Session Data
    private String currentUsername;
    private String currentDisplayName;

    // Main Layout Components
    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    
    // The 3-Panel Message Components
    private JPanel rightInfoPanel;
    private boolean isInfoPanelVisible = true;
    private JSplitPane rightSplitPane;
    
    // Chat History Container
    private JPanel chatHistoryContainer;

    public ChatUI(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;
        
        setupFrame();
        initComponents();
        applyOrbitStyles();
        
        // Populate with some fake messages to show the new UI
        loadDummyMessages();
        
        System.out.println("ChatUI Initialized for: " + displayName + " (@" + username + ")");
    }

    private void setupFrame() {
        setTitle("Orbit Secure Messenger - " + currentDisplayName);
        setSize(1280, 800); 
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getRootPane().putClientProperty("flatlaf.titleBarShowTitle", false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        
        // 1. TOP NAVIGATION BAR (Using Styled Icons)
        JPanel topNavBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        topNavBar.putClientProperty("FlatLaf.style", "background: $Panel.background");
        topNavBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")));

        JButton btnMessages = createNavButton("💬", "messages_card", "Messages");
        JButton btnHome = createNavButton("🏠", "home_card", "Home");
        JButton btnFriends = createNavButton("👥", "friends_card", "Friends");
        JButton btnProfile = createNavButton("👤", "profile_card", "Profile");
        JButton btnSettings = createNavButton("⚙️", "settings_card", "Settings");

        topNavBar.add(btnMessages);
        topNavBar.add(btnHome);
        topNavBar.add(btnFriends);
        topNavBar.add(btnProfile);
        topNavBar.add(btnSettings);
        
        add(topNavBar, BorderLayout.NORTH);

        // 2. MAIN CARD LAYOUT
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        mainCardPanel.add(buildMessagesPanel(), "messages_card");
        mainCardPanel.add(buildHomePanel(), "home_card");
        mainCardPanel.add(buildFriendsPanel(), "friends_card");
        mainCardPanel.add(buildProfilePanel(), "profile_card");
        mainCardPanel.add(buildSettingsPanel(), "settings_card");

        add(mainCardPanel, BorderLayout.CENTER);
    }

    // ==============================================================
    // PAGE 1: MESSAGES (The Facebook Messenger Clone)
    // ==============================================================
    private JPanel buildMessagesPanel() {
        // --- LEFT PANEL: Chat List ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setMinimumSize(new Dimension(250, 0));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(createPillButton("All"));
        filterPanel.add(createPillButton("Unread"));
        filterPanel.add(createPillButton("Groups"));
        leftPanel.add(filterPanel, BorderLayout.NORTH);
        
        JList<String> chatList = new JList<>(new String[]{"ITE 2 - GROUP 4", "Angely", "Zandro Zoleta"});
        chatList.putClientProperty("FlatLaf.style", 
            "background: $Panel.background; " + 
            "selectionBackground: $Button.background; " + 
            "selectionArc: 15; " + 
            "selectionInsets: 0, 5, 0, 5; " +
            "cellHeight: 50; " +
            "font: $h3.font"
        );
        leftPanel.add(new JScrollPane(chatList), BorderLayout.CENTER);

        // --- CENTER PANEL: Main Chat Area ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Chat Header
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header Avatar & Title
        JPanel headerTitleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel headerAvatar = new JLabel("👥");
        headerAvatar.putClientProperty("FlatLaf.style", "font: 200% $defaultFont");
        JLabel lblChatName = new JLabel("ITE 2 - GROUP 4");
        lblChatName.putClientProperty("FlatLaf.styleClass", "h2");
        headerTitleWrap.add(headerAvatar);
        headerTitleWrap.add(lblChatName);
        
        // Header Actions
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnVideoCall = createIconButton("📞", "Video Call"); 
        JButton btnInfo = createIconButton("ℹ️", "Chat Info"); 
        btnInfo.addActionListener(e -> toggleInfoPanel());
        headerActions.add(btnVideoCall);
        headerActions.add(btnInfo);
        
        chatHeader.add(headerTitleWrap, BorderLayout.WEST);
        chatHeader.add(headerActions, BorderLayout.EAST);
        
        // Chat Messages Area (Dynamic Bubbles)
        chatHistoryContainer = new JPanel();
        chatHistoryContainer.setLayout(new BoxLayout(chatHistoryContainer, BoxLayout.Y_AXIS));
        chatHistoryContainer.putClientProperty("FlatLaf.style", "background: $Panel.background");
        
        JScrollPane scrollChat = new JScrollPane(chatHistoryContainer);
        scrollChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Chat Input Area
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        inputArea.add(createIconButton("📎", "Attach File"), BorderLayout.WEST); 
        
        JTextField txtChatInput = new JTextField();
        txtChatInput.putClientProperty("JTextField.placeholderText", "Aa");
        txtChatInput.putClientProperty("FlatLaf.style", 
            "arc: 999; " +           
            "borderWidth: 0; " +     
            "focusWidth: 0; " +      
            "background: $Button.background; " + // Adapts to light/dark
            "margin: 10, 15, 10, 15;" 
        );
        inputArea.add(txtChatInput, BorderLayout.CENTER);
        
        JPanel inputRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        inputRight.add(createIconButton("😀", "Emoji")); 
        inputRight.add(createIconButton("👍", "Send Reaction")); 
        inputArea.add(inputRight, BorderLayout.EAST);

        centerPanel.add(chatHeader, BorderLayout.NORTH);
        centerPanel.add(scrollChat, BorderLayout.CENTER);
        centerPanel.add(inputArea, BorderLayout.SOUTH);

        // --- RIGHT PANEL: Chat Info ---
        rightInfoPanel = new JPanel(new CardLayout()); 
        rightInfoPanel.setPreferredSize(new Dimension(300, 0));
        rightInfoPanel.setMinimumSize(new Dimension(250, 0));
        
        JPanel groupInfo = new JPanel();
        groupInfo.setLayout(new BoxLayout(groupInfo, BoxLayout.Y_AXIS));
        groupInfo.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JLabel bigAvatar = new JLabel("👥");
        bigAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        bigAvatar.putClientProperty("FlatLaf.style", "font: 400% $defaultFont");
        
        JLabel infoTitle = new JLabel("ITE 2 - GROUP 4");
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoTitle.putClientProperty("FlatLaf.styleClass", "h2");
        
        groupInfo.add(bigAvatar);
        groupInfo.add(Box.createVerticalStrut(10));
        groupInfo.add(infoTitle);
        groupInfo.add(Box.createVerticalStrut(20));
        groupInfo.add(new JSeparator());
        groupInfo.add(createMenuButton("📌 View pinned messages"));
        groupInfo.add(createMenuButton("🎨 Customize chat"));
        groupInfo.add(createMenuButton("👥 Chat members"));
        groupInfo.add(createMenuButton("📁 Media, files and links"));
        
        JButton btnLeave = createMenuButton("🚪 Leave group");
        btnLeave.setForeground(Color.RED);
        groupInfo.add(btnLeave);
        
        rightInfoPanel.add(groupInfo, "GROUP_INFO");

        // Assemble Panels
        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, rightInfoPanel);
        rightSplitPane.setResizeWeight(1.0); 
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(300);

        JPanel messagesRoot = new JPanel(new BorderLayout());
        messagesRoot.add(mainSplitPane, BorderLayout.CENTER);
        return messagesRoot;
    }

    private void toggleInfoPanel() {
        isInfoPanelVisible = !isInfoPanelVisible;
        rightInfoPanel.setVisible(isInfoPanelVisible);
        rightSplitPane.setDividerLocation(isInfoPanelVisible ? rightSplitPane.getWidth() - 300 : rightSplitPane.getWidth());
    }

    // ==============================================================
    // CHAT BUBBLE ENGINE
    // ==============================================================
    
    // Adds a visual chat bubble to the screen
    private void appendMessageBubble(String sender, String text, String time, boolean isMe) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 10, 5, 10));

        // The Bubble itself
        JLabel bubble = new JLabel("<html><div style='max-width: 300px; padding: 5px;'>" + text + "</div></html>");
        bubble.setOpaque(true);
        // Hover feature!
        bubble.setToolTipText("Sent by " + sender + " at " + time); 
        
        if (isMe) {
            // Messenger Blue for user
            bubble.putClientProperty("FlatLaf.style", 
                "arc: 20; background: #0084FF; foreground: #FFFFFF; margin: 8,12,8,12;");
            row.add(bubble, BorderLayout.EAST);
        } else {
            // Adapts to Light/Dark mode automatically
            bubble.putClientProperty("FlatLaf.style", 
                "arc: 20; background: $Button.background; foreground: $Label.foreground; margin: 8,12,8,12;");
            
            // Wrapper to hold Avatar + Bubble
            JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftWrapper.setOpaque(false);
            
            JLabel avatar = new JLabel("👤");
            avatar.setToolTipText(sender); // Hover avatar to see name
            avatar.putClientProperty("FlatLaf.style", "font: 200% $defaultFont");
            
            leftWrapper.add(avatar);
            leftWrapper.add(bubble);
            row.add(leftWrapper, BorderLayout.WEST);
        }
        
        chatHistoryContainer.add(row);
        chatHistoryContainer.revalidate();
        chatHistoryContainer.repaint();
    }

    private void loadDummyMessages() {
        appendMessageBubble("Angely", "pano yan", "5:00 PM", false);
        appendMessageBubble(currentDisplayName, "design palang to boss", "5:02 PM", true);
        appendMessageBubble("Zandro Zoleta", "help daw sabi ni brinio", "5:10 PM", false);
        appendMessageBubble("Angely", "leader 🫡", "5:11 PM", false);
    }

    // ==============================================================
    // OTHER PAGES (Home, Friends, Profile, Settings)
    // ==============================================================
    
    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel createPostArea = new JPanel(new BorderLayout());
        createPostArea.setBorder(new EmptyBorder(20, 50, 20, 50));
        createPostArea.add(new JLabel("What's on your mind, " + currentDisplayName + "?"), BorderLayout.NORTH);
        createPostArea.add(new JTextField(), BorderLayout.CENTER);
        panel.add(createPostArea, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildFriendsPanel() {
        return new JPanel(); // Simplified for length
    }

    private JPanel buildProfilePanel() {
        return new JPanel(); // Simplified for length
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JLabel lblTitle = new JLabel("Settings");
        lblTitle.putClientProperty("FlatLaf.styleClass", "h1");
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(20));

        // THEME TOGGLE (Light/Dark instantly adapts everything)
        JButton btnToggleTheme = new JButton("🌓 Switch Theme");
        btnToggleTheme.addActionListener(e -> {
            boolean isDark = UIManager.getLookAndFeel() instanceof FlatDarkLaf;
            try {
                if (isDark) {
                    UIManager.setLookAndFeel(new FlatLightLaf());
                } else {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                }
                SwingUtilities.updateComponentTreeUI(this);
                // Re-apply specific overrides
                applyOrbitStyles();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        panel.add(btnToggleTheme);
        return panel;
    }

    // ==============================================================
    // UTILITIES & FLATLAF STYLING
    // ==============================================================
    
    // Top Navigation Icon Buttons
    private JButton createNavButton(String iconText, String cardName, String tooltip) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip);
        btn.putClientProperty("FlatLaf.style", 
            "borderWidth: 0; focusWidth: 0; arc: 15; " +
            "margin: 5, 20, 5, 20; " +
            "hoverBackground: $Button.hoverBackground; " + 
            "font: 200% $defaultFont" // Makes the Unicode huge like an image icon
        );
        btn.addActionListener(e -> cardLayout.show(mainCardPanel, cardName));
        return btn;
    }
    
    // Action Icon Buttons (Call, Info, Attach)
    private JButton createIconButton(String iconText, String tooltip) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip);
        btn.putClientProperty("FlatLaf.style", 
            "buttonType: borderless; " +
            "hoverBackground: $Button.hoverBackground; " +
            "arc: 999; margin: 5, 10, 5, 10; font: 150% $defaultFont"
        );
        return btn;
    }
    
    // Pill filters (All, Unread)
    private JButton createPillButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("FlatLaf.style", "arc: 999; margin: 5, 15, 5, 15;");
        return btn;
    }
    
    // Transparent list-style buttons for the right menu
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; margin: 5, 10, 5, 10;");
        return btn;
    }

    private void applyOrbitStyles() {
        // Automatically adapts because we use UIManager Keys!
        UIManager.put("SplitPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("SplitPaneDivider.border", BorderFactory.createEmptyBorder());
        UIManager.put("SplitPane.dividerSize", 1); 
        
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumbArc", 999); 
    }
}