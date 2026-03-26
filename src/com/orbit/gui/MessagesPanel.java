package com.orbit.gui;

import com.orbit.security.CryptoUtil;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessagesPanel extends JPanel {

    // Network Variables
    private java.net.Socket chatSocket;
    private java.io.PrintWriter out;
    private java.io.BufferedReader in;
    private final String ROOM_SECRET_KEY = "OrbitCapstone2026!";

    // User Data
    private String currentUsername;
    private String currentDisplayName;

    // UI Components
    private JPanel chatHistoryContainer;
    private JPanel rightInfoPanel;
    private CardLayout infoCardLayout;
    private JSplitPane rightSplitPane;
    private boolean isInfoPanelVisible = true;

    // Colors
    private final Color MAIN_BG = Color.decode("#18191A"); 
    private final Color SIDEBAR_BG = Color.decode("#242526"); 

    public MessagesPanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;
        
        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        initComponents();
        connectToChatServer();
        
        // Initial Dummy Data (Remove later)
        appendMessageBubble("Angely", "pano yan", "5:00 PM", false);
        appendMessageBubble(currentDisplayName, "Modular architecture engaged!", "5:02 PM", true);
    }

    private void initComponents() {
        // --- LEFT PANEL ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(SIDEBAR_BG);
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setMinimumSize(new Dimension(280, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#393A3B")));
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        filterPanel.setBackground(SIDEBAR_BG);
        JLabel lblChats = new JLabel("Chats");
        lblChats.putClientProperty("FlatLaf.style", "font: bold 24; foreground: #E4E6EB");
        filterPanel.add(lblChats);
        
        JPanel leftHeaderWrapper = new JPanel(new BorderLayout());
        leftHeaderWrapper.setBackground(SIDEBAR_BG);
        leftHeaderWrapper.add(filterPanel, BorderLayout.NORTH);
        
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "🔍 Search Messenger");
        txtSearch.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; borderWidth: 0; margin: 0,10,10,10");
        txtSearch.setBorder(new EmptyBorder(8, 15, 8, 15)); 
        leftHeaderWrapper.add(txtSearch, BorderLayout.SOUTH);
        leftPanel.add(leftHeaderWrapper, BorderLayout.NORTH);
        
        JList<String> chatList = new JList<>(new String[]{"ITE 2 - GROUP 4", "Angely", "Zandro Zoleta"});
        chatList.putClientProperty("FlatLaf.style", "background: #242526; selectionBackground: #3A3B3C; selectionForeground: #E4E6EB; selectionArc: 15; selectionInsets: 2, 10, 2, 10; font: bold 14");
        chatList.setFixedCellHeight(60); 
        
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && chatList.getSelectedValue() != null) {
                if (chatList.getSelectedValue().contains("GROUP")) {
                    infoCardLayout.show(rightInfoPanel, "GROUP_INFO");
                } else {
                    infoCardLayout.show(rightInfoPanel, "DM_INFO");
                }
            }
        });
        
        JScrollPane scrollLeft = new JScrollPane(chatList);
        scrollLeft.setBorder(null);
        leftPanel.add(scrollLeft, BorderLayout.CENTER);

        // --- CENTER PANEL ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(MAIN_BG);
        
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(MAIN_BG);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#393A3B")), new EmptyBorder(10, 15, 10, 15)));
        
        JPanel headerTitleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerTitleWrap.setBackground(MAIN_BG);
        JLabel headerAvatar = new JLabel("👥");
        headerAvatar.putClientProperty("FlatLaf.style", "font: 250% $defaultFont");
        
        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0"));
        nameStack.setBackground(MAIN_BG);
        JLabel lblChatName = new JLabel("ITE 2 - GROUP 4");
        lblChatName.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        JLabel lblActive = new JLabel("Active now");
        lblActive.putClientProperty("FlatLaf.style", "font: 11; foreground: #B0B3B8");
        nameStack.add(lblChatName);
        nameStack.add(lblActive);
        
        headerTitleWrap.add(headerAvatar);
        headerTitleWrap.add(nameStack);
        
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.setBackground(MAIN_BG);
        headerActions.add(createIconButton("📞", "Video Call", true)); 
        JButton btnInfo = createIconButton("ℹ️", "Chat Info", true); 
        btnInfo.addActionListener(e -> toggleInfoPanel());
        headerActions.add(btnInfo);
        
        chatHeader.add(headerTitleWrap, BorderLayout.WEST);
        chatHeader.add(headerActions, BorderLayout.EAST);
        
        chatHistoryContainer = new JPanel();
        chatHistoryContainer.setLayout(new BoxLayout(chatHistoryContainer, BoxLayout.Y_AXIS));
        chatHistoryContainer.setBackground(MAIN_BG);
        chatHistoryContainer.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JScrollPane scrollChat = new JScrollPane(chatHistoryContainer);
        scrollChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollChat.setBorder(null);
        scrollChat.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setBackground(MAIN_BG);
        inputArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel inputLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        inputLeft.setBackground(MAIN_BG);
        inputLeft.add(createIconButton("➕", "More actions", true));
        inputLeft.add(createIconButton("🖼️", "Attach Image", true)); 
        inputArea.add(inputLeft, BorderLayout.WEST); 
        
        JTextField txtChatInput = new JTextField();
        txtChatInput.putClientProperty("JTextField.placeholderText", "Aa");
        txtChatInput.putClientProperty("FlatLaf.style", "arc: 999; borderWidth: 0; focusWidth: 0; background: #3A3B3C; foreground: #E4E6EB; margin: 8, 15, 8, 15; font: 14");
        inputArea.add(txtChatInput, BorderLayout.CENTER);
        
        // ENTER KEY LOGIC
        txtChatInput.addActionListener(e -> {
            String msg = txtChatInput.getText().trim();
            if (!msg.isEmpty()) {
                String time = new SimpleDateFormat("h:mm a").format(new Date());
                appendMessageBubble(currentDisplayName, msg, time, true);
                scrollToBottom();
                txtChatInput.setText("");
                
                String encryptedMsg = CryptoUtil.encrypt(msg, ROOM_SECRET_KEY);
                if (out != null) {
                    out.println("MSG|" + currentDisplayName + "|" + encryptedMsg);
                }
            }
        });
        
        JPanel inputRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        inputRight.setBackground(MAIN_BG);
        inputRight.add(createIconButton("😀", "Emoji", true)); 
        inputRight.add(createIconButton("👍", "Send Reaction", true)); 
        inputArea.add(inputRight, BorderLayout.EAST);

        centerPanel.add(chatHeader, BorderLayout.NORTH);
        centerPanel.add(scrollChat, BorderLayout.CENTER);
        centerPanel.add(inputArea, BorderLayout.SOUTH);

        // --- RIGHT PANEL ---
        infoCardLayout = new CardLayout();
        rightInfoPanel = new JPanel(infoCardLayout); 
        rightInfoPanel.setPreferredSize(new Dimension(320, 0));
        rightInfoPanel.setMinimumSize(new Dimension(280, 0));
        rightInfoPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.decode("#393A3B")));
        
        rightInfoPanel.add(buildDMInfoPanel(), "DM_INFO");
        rightInfoPanel.add(buildGroupInfoPanel(), "GROUP_INFO");

        rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, rightInfoPanel);
        rightSplitPane.setResizeWeight(1.0); 
        rightSplitPane.setBorder(null);
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightSplitPane);
        mainSplitPane.setDividerLocation(320);
        mainSplitPane.setBorder(null);

        add(mainSplitPane, BorderLayout.CENTER);
    }

    // --- Networking ---
    private void connectToChatServer() {
        try {
            chatSocket = new java.net.Socket("localhost", 8080);
            out = new java.io.PrintWriter(chatSocket.getOutputStream(), true);
            in = new java.io.BufferedReader(new java.io.InputStreamReader(chatSocket.getInputStream()));

            out.println("JOIN|" + currentDisplayName);

            new Thread(() -> {
                try {
                    String incomingMessage;
                    while ((incomingMessage = in.readLine()) != null) {
                        if (incomingMessage.startsWith("MSG|")) {
                            String[] parts = incomingMessage.split("\\|");
                            String sender = parts[1];
                            String encryptedText = parts[2];
                            String decryptedText = CryptoUtil.decrypt(encryptedText, ROOM_SECRET_KEY);
                            String time = new SimpleDateFormat("h:mm a").format(new Date());
                            
                            if (!sender.equals(currentDisplayName)) {
                                appendMessageBubble(sender, decryptedText, time, false);
                                scrollToBottom();
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();
        } catch (Exception e) {
            System.err.println("Network Error: Could not connect to Chat Server.");
        }
    }

    // --- UI Logic & Helpers ---
    private void toggleInfoPanel() {
        isInfoPanelVisible = !isInfoPanelVisible;
        rightInfoPanel.setVisible(isInfoPanelVisible);
        rightSplitPane.setDividerLocation(isInfoPanelVisible ? rightSplitPane.getWidth() - 320 : rightSplitPane.getWidth());
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollPane scrollPane = (JScrollPane) chatHistoryContainer.getParent().getParent();
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void appendMessageBubble(String sender, String text, String time, boolean isMe) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(2, 15, 2, 15)); 

        GradientBubble bubble = new GradientBubble(text, isMe);
        bubble.setToolTipText("Sent by " + sender + " at " + time); 
        
        if (isMe) {
            row.add(bubble, BorderLayout.EAST);
        } else {
            JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftWrapper.setOpaque(false);
            JLabel avatar = new JLabel("👤");
            avatar.setToolTipText(sender);
            avatar.putClientProperty("FlatLaf.style", "font: 180% $defaultFont; foreground: #B0B3B8");
            leftWrapper.add(avatar);
            leftWrapper.add(bubble);
            row.add(leftWrapper, BorderLayout.WEST);
        }
        
        chatHistoryContainer.add(row);
        chatHistoryContainer.revalidate();
        chatHistoryContainer.repaint();
    }

    private JPanel buildDMInfoPanel() {
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]20[]20[fill]"));
        p.setBackground(SIDEBAR_BG);
        JLabel avatar = new JLabel("👤"); avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont"); p.add(avatar);
        JLabel name = new JLabel("Angely"); name.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB"); p.add(name);
        JLabel status = new JLabel("Active now"); status.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8"); p.add(status);
        
        JPanel actionRow = new JPanel(new MigLayout("insets 0, gap 20", "[][][]", "")); actionRow.setBackground(SIDEBAR_BG);
        actionRow.add(createActionCircle("👤", "Profile")); actionRow.add(createActionCircle("🔕", "Mute")); actionRow.add(createActionCircle("🔍", "Search"));
        p.add(actionRow);
        
        JPanel sections = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]")); sections.setBackground(SIDEBAR_BG);
        sections.add(createCategoryHeader("Chat info")); sections.add(createMenuButton("📌 View pinned messages"));
        sections.add(createCategoryHeader("Customize chat")); sections.add(createMenuButton("😀 Change emoji")); sections.add(createMenuButton("✏️ Edit nicknames"));
        sections.add(createCategoryHeader("Media, files and links")); sections.add(createMenuButton("🖼️ Media")); sections.add(createMenuButton("📁 Files"));
        sections.add(createCategoryHeader("Privacy & support")); sections.add(createMenuButton("🔒 End-to-end encrypted"));
        JButton btnBlock = createMenuButton("⛔ Block"); btnBlock.setForeground(Color.decode("#FA383E")); sections.add(btnBlock);
        
        JScrollPane sp = new JScrollPane(sections); sp.setBorder(null); p.add(sp, "grow");
        return p;
    }

    private JPanel buildGroupInfoPanel() {
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]20[]20[fill]"));
        p.setBackground(SIDEBAR_BG);
        JLabel avatar = new JLabel("👥"); avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont"); p.add(avatar);
        JLabel name = new JLabel("ITE 2 - GROUP 4"); name.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB"); p.add(name);
        
        JPanel actionRow = new JPanel(new MigLayout("insets 0, gap 20", "[][]", "")); actionRow.setBackground(SIDEBAR_BG);
        actionRow.add(createActionCircle("🔕", "Mute")); actionRow.add(createActionCircle("🔍", "Search"));
        p.add(actionRow);
        
        JPanel sections = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]")); sections.setBackground(SIDEBAR_BG);
        sections.add(createCategoryHeader("Customize chat")); sections.add(createMenuButton("📝 Change group name")); sections.add(createMenuButton("🖼️ Change photo")); sections.add(createMenuButton("😀 Change emoji"));
        sections.add(createCategoryHeader("Chat members")); sections.add(createMenuButton("➕ Add people")); sections.add(createMemberRow("Howard Brinio", "Added by Angely")); sections.add(createMemberRow("Angely", "Group Creator"));
        sections.add(createCategoryHeader("Privacy & support"));
        JButton btnLeave = createMenuButton("🚪 Leave group"); btnLeave.setForeground(Color.decode("#FA383E")); sections.add(btnLeave);
        
        JScrollPane sp = new JScrollPane(sections); sp.setBorder(null); p.add(sp, "grow");
        return p;
    }

    private JButton createIconButton(String iconText, String tooltip, boolean isBlueAccent) {
        JButton btn = new JButton(iconText); btn.setToolTipText(tooltip);
        String color = isBlueAccent ? "foreground: #0084FF;" : "foreground: #B0B3B8;";
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; hoverBackground: #3A3B3C; arc: 999; margin: 5, 8, 5, 8; font: 140% $defaultFont; " + color);
        return btn;
    }

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("FlatLaf.style", "arc: 999; margin: 6, 16, 6, 16; background: #3A3B3C; foreground: #E4E6EB; borderWidth: 0; font: bold 13");
        return btn;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text); btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; margin: 0, 0, 0, 0; font: 14; foreground: #E4E6EB; hoverBackground: #3A3B3C; arc: 10");
        btn.setBorder(new EmptyBorder(8, 10, 8, 10)); 
        return btn;
    }

    private JPanel createActionCircle(String icon, String labelText) {
        JPanel p = new JPanel(new MigLayout("wrap 1, insets 0, align center", "[center]", "[]5[]")); p.setOpaque(false);
        JButton btn = new JButton(icon); btn.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; foreground: #E4E6EB; margin: 12,14,12,14; font: 150% $defaultFont; borderWidth: 0");
        p.add(btn); JLabel lbl = new JLabel(labelText); lbl.putClientProperty("FlatLaf.style", "font: 12; foreground: #E4E6EB"); p.add(lbl);
        return p;
    }

    private JLabel createCategoryHeader(String text) {
        JLabel lbl = new JLabel(text); lbl.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #B0B3B8"); lbl.setBorder(new EmptyBorder(15, 10, 5, 10));
        return lbl;
    }

    private JPanel createMemberRow(String name, String subtext) {
        JPanel row = new JPanel(new MigLayout("insets 5 10 5 10, fillx", "[][grow][]", "")); row.setOpaque(false);
        JLabel avatar = new JLabel("👤"); avatar.putClientProperty("FlatLaf.style", "font: 180% $defaultFont; foreground: #B0B3B8"); row.add(avatar);
        JPanel textStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0")); textStack.setOpaque(false);
        JLabel lblName = new JLabel(name); lblName.putClientProperty("FlatLaf.style", "font: 14; foreground: #E4E6EB"); textStack.add(lblName);
        JLabel sub = new JLabel(subtext); sub.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8"); textStack.add(sub);
        row.add(textStack, "growx"); row.add(createIconButton("⋮", "Options", false));
        return row;
    }
}