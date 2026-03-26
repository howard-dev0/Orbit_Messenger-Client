package com.orbit.gui;

import com.orbit.security.CryptoUtil;
import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class MessagesPanel extends JPanel implements NetworkListener {

    private final String ROOM_SECRET_KEY = "OrbitCapstone2026!";

    private class ChatListItem {

        String id, displayName, type, lastTime;

        public ChatListItem(String id, String displayName, String type, String lastTime) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
            this.lastTime = lastTime;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private DefaultListModel<ChatListItem> chatListModel;
    private JList<ChatListItem> chatList;
    private List<ChatListItem> masterChatList = new ArrayList<>();

    private String currentUsername;
    private String currentDisplayName;
    private String activeChatId = null;
    private String activeChatType = "DM";

    private JPanel chatHistoryContainer;
    private JPanel rightInfoPanel;
    private CardLayout infoCardLayout;
    private JSplitPane rightSplitPane;
    private boolean isInfoPanelVisible = true;
    private JLabel lblChatName;

    private JLabel lblInfoName;
    private JLabel lblInfoStatus;

    private java.util.HashMap<String, String> chatTypeMap = new java.util.HashMap<>();
    private java.util.HashMap<String, String> chatIdMap = new java.util.HashMap<>();

    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color SIDEBAR_BG = Color.decode("#242526");

    public MessagesPanel(String username, String displayName) {
        // Scrub "null" from the local session immediately
        this.currentUsername = username;
        this.currentDisplayName = (displayName == null || displayName.equalsIgnoreCase("null") || displayName.isEmpty()) ? username : displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        initComponents();

        NetworkManager.getInstance().addListener(this);
        NetworkManager.getInstance().send("GET_MY_CHATS|" + currentUsername);

        // 🚀 UPDATED: Time format to 12:30 PM
        appendMessageBubble("System", "Welcome to Orbit! Select a chat to begin.", new SimpleDateFormat("h:mm a").format(new Date()), false);
    }

    private void initComponents() {
        // --- LEFT PANEL ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(SIDEBAR_BG);
        leftPanel.setPreferredSize(new Dimension(320, 0));
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

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterChats();
            }

            public void removeUpdate(DocumentEvent e) {
                filterChats();
            }

            public void changedUpdate(DocumentEvent e) {
                filterChats();
            }

            private void filterChats() {
                String query = txtSearch.getText().toLowerCase();
                chatListModel.clear();
                for (ChatListItem item : masterChatList) {
                    if (item.displayName.toLowerCase().contains(query)) {
                        chatListModel.addElement(item);
                    }
                }
            }
        });

        leftHeaderWrapper.add(txtSearch, BorderLayout.SOUTH);
        leftPanel.add(leftHeaderWrapper, BorderLayout.NORTH);

        chatListModel = new DefaultListModel<>();
        chatList = new JList<>(chatListModel);
        chatList.setBackground(SIDEBAR_BG);
        chatList.setCellRenderer(new ChatListCellRenderer());
        chatList.setFixedCellHeight(65);

        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && chatList.getSelectedValue() != null) {
                ChatListItem selectedItem = chatList.getSelectedValue();

                activeChatId = selectedItem.id;
                activeChatType = selectedItem.type;

                // Update Top Center Header
                if (lblChatName != null) {
                    lblChatName.setText(selectedItem.displayName + " 🔒");
                }

                // 🚀 THE FIX: Update the Right Info Panel labels!
                if (lblInfoName != null) {
                    lblInfoName.setText(selectedItem.displayName);
                }
                if (lblInfoStatus != null) {
                    lblInfoStatus.setText(selectedItem.lastTime); // e.g., "Active Now"
                }
                chatHistoryContainer.removeAll();
                chatHistoryContainer.revalidate();
                chatHistoryContainer.repaint();

                NetworkManager.getInstance().send("LOAD_CHAT_HISTORY|" + activeChatId + "|" + currentUsername);

                if ("GROUP".equals(activeChatType)) {
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
        lblChatName = new JLabel("Select a chat...");
        lblChatName.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        JLabel lblActive = new JLabel("E2E Encrypted Session");
        lblActive.putClientProperty("FlatLaf.style", "font: 11; foreground: #0084FF");
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

        txtChatInput.addActionListener(e -> {
            String msg = txtChatInput.getText().trim();
            if (!msg.isEmpty() && activeChatId != null) {
                // 🚀 UPDATED: Format to h:mm a
                String time = new SimpleDateFormat("h:mm a").format(new Date());
                appendMessageBubble(currentDisplayName, msg, time, true);
                scrollToBottom();
                txtChatInput.setText("");
                String dynamicKey = getDynamicRoomKey(activeChatId);
                String encryptedMsg = CryptoUtil.encrypt(msg, dynamicKey);
                NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encryptedMsg + "|" + currentUsername);
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

        infoCardLayout = new CardLayout();
        rightInfoPanel = new JPanel(infoCardLayout);
        rightInfoPanel.setPreferredSize(new Dimension(320, 0));
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

    private class ChatListCellRenderer extends JPanel implements ListCellRenderer<ChatListItem> {

        private JLabel lblAvatar, lblName, lblTime;

        public ChatListCellRenderer() {
            setLayout(new MigLayout("insets 5 15 5 15, fillx", "[][grow][]", "[]"));
            setOpaque(true);
            lblAvatar = new JLabel("👤");
            lblAvatar.putClientProperty("FlatLaf.style", "font: 250% $defaultFont; foreground: #B0B3B8");
            lblName = new JLabel();
            lblName.putClientProperty("FlatLaf.style", "font: bold 14; foreground: #E4E6EB");
            lblTime = new JLabel();
            lblTime.putClientProperty("FlatLaf.style", "font: 11; foreground: #B0B3B8");
            add(lblAvatar, "gapright 10");
            add(lblName, "growx");
            add(lblTime);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ChatListItem> list, ChatListItem value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                // Scrub "null" from left list
                String nameToDraw = (value.displayName == null || value.displayName.equalsIgnoreCase("null")) ? value.id : value.displayName;
                lblName.setText(nameToDraw);
                lblAvatar.setText("GROUP".equals(value.type) ? "👥" : "👤");
                lblTime.setText(value.lastTime);
            }
            setBackground(isSelected ? Color.decode("#3A3B3C") : Color.decode("#242526"));
            lblName.setForeground(isSelected ? Color.WHITE : Color.decode("#E4E6EB"));
            return this;
        }
    }

    private String getDynamicRoomKey(String targetId) {
        String base;

        if (activeChatType.equals("GROUP")) {
            // Groups use the Group ID as the key base
            base = "OrbitGroup_" + targetId;
        } else {
            // DMs: Sort usernames alphabetically so both users generate the EXACT same key
            // Example: "howard" and "angely" both become "angely_howard"
            String user1 = currentUsername.toLowerCase();
            String user2 = targetId.toLowerCase();

            if (user1.compareTo(user2) < 0) {
                base = user1 + "_" + user2;
            } else {
                base = user2 + "_" + user1;
            }
        }

        // Hash it slightly to make it 16 bytes for AES
        String baseSecret = "Orbit_" + base + "_2026";
        if (baseSecret.length() > 16) {
            return baseSecret.substring(0, 16);
        }
        return String.format("%-16s", baseSecret).replace(' ', 'X');
    }

    @Override
    public void onMessageReceived(String incomingMessage) {
        // 1. POPULATE CHATS
        if (incomingMessage.startsWith("MY_CHATS|")) {
            String data = incomingMessage.substring(9);
            String[] chats = data.split(",");
            SwingUtilities.invokeLater(() -> {
                chatListModel.clear();
                masterChatList.clear();
                for (String c : chats) {
                    if (!c.isEmpty()) {
                        String[] parts = c.split("~");
                        if (parts.length >= 4) { // Now expecting 4 parts: ID, Name, Type, Status
                            String id = parts[0];
                            String name = parts[1];
                            String type = parts[2];
                            String status = parts[3]; // "Active Now" or "Offline"

                            ChatListItem item = new ChatListItem(id, name, type, status);
                            masterChatList.add(item);
                            chatListModel.addElement(item);
                        }
                    }
                }
            });
        } // 2. RECEIVE LIVE MESSAGE
        else if (incomingMessage.startsWith("NEW_MESSAGE|")) {
            String[] parts = incomingMessage.split("\\|");
            if (parts.length < 5) {
                return;
            }

            String incomingChatId = parts[1]; // This is the sender's username
            String senderName = parts[2];
            String encryptedText = parts[3];
            String time = parts[4];

            // Use our new sorted key logic to decrypt
            if (incomingChatId.equals(activeChatId)) {
                String dynamicKey = getDynamicRoomKey(incomingChatId);
                String decryptedText = CryptoUtil.decrypt(encryptedText, dynamicKey);

                // Final protection against decryption failure
                if (decryptedText == null) {
                    decryptedText = "[Secure Message]";
                }

                appendMessageBubble(senderName, decryptedText, time, false);
                scrollToBottom();
            }
        } // 3. RECEIVE HISTORY
        else if (incomingMessage.startsWith("CHAT_HISTORY|")) {
            String data = incomingMessage.length() > 13 ? incomingMessage.substring(13) : "";
            SwingUtilities.invokeLater(() -> {
                chatHistoryContainer.removeAll();
                if (!data.isEmpty()) {
                    String[] messages = data.split(",");
                    String dynamicKey = getDynamicRoomKey(activeChatId);
                    for (String m : messages) {
                        if (!m.isEmpty()) {
                            String[] parts = m.split("~");
                            if (parts.length >= 3) {
                                String sender = parts[0];
                                String decrypted = CryptoUtil.decrypt(parts[1], dynamicKey);

                                if (decrypted == null || decrypted.equalsIgnoreCase("null")) {
                                    decrypted = "[Encrypted]";
                                }

                                boolean isMe = sender.equals(currentDisplayName) || sender.equals(currentUsername);
                                appendMessageBubble(sender, decrypted, parts[2], isMe);
                            }
                        }
                    }
                    scrollToBottom();
                }
                chatHistoryContainer.revalidate();
                chatHistoryContainer.repaint();
            });
        } else if (incomingMessage.startsWith("UPDATE_STATUS|")) {
            String[] parts = incomingMessage.split("\\|");
            if (parts.length < 3) {
                return;
            }

            String targetUser = parts[1];
            String newStatus = parts[2]; // "ONLINE" or "OFFLINE"

            SwingUtilities.invokeLater(() -> {
                // Loop through the sidebar list to find the friend who changed status
                for (int i = 0; i < chatListModel.size(); i++) {
                    ChatListItem item = chatListModel.getElementAt(i);

                    // We check if the ID matches the friend's username
                    if (item.id.equals(targetUser)) {
                        // 1. Update the underlying data for the left sidebar subtext
                        item.lastTime = newStatus.equals("ONLINE") ? "Active Now" : "Offline";

                        // 2. If we are currently looking at THIS user's profile on the right...
                        if (activeChatId != null && activeChatId.equals(targetUser)) {
                            // ...Update the Right Info Panel status label immediately
                            lblInfoStatus.setText(item.lastTime);

                            // Optional: Update the "Active now" text under the center header name
                            // nameStack.getComponent(1).setText(item.lastTime);
                        }

                        // 3. Refresh the UI so the changes appear on screen
                        chatList.repaint();
                        break; // Exit the loop since we found our target
                    }
                }
            });
        }

    }

    private void appendMessageBubble(String sender, String text, String time, boolean isMe) {
        // 1. Scrub "null" or empty messages immediately
        if (text == null || text.equalsIgnoreCase("null") || text.trim().isEmpty()) {
            return;
        }

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 15, 5, 15));

        GradientBubble bubble = new GradientBubble(text, isMe);

        // 2. Ensure Time is always 12-hour format
        JLabel lblTime = new JLabel(time);
        lblTime.putClientProperty("FlatLaf.style", "font: 10; foreground: #7A7D82");

        JPanel bubbleStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 2", (isMe ? "[right]" : "[left]")));
        bubbleStack.setOpaque(false);
        bubbleStack.add(bubble);
        bubbleStack.add(lblTime);

        if (isMe) {
            row.add(bubbleStack, BorderLayout.EAST);
        } else {
            JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftWrapper.setOpaque(false);
            JLabel avatar = new JLabel("👤");
            avatar.putClientProperty("FlatLaf.style", "font: 180% $defaultFont; foreground: #B0B3B8");
            leftWrapper.add(avatar);
            leftWrapper.add(bubbleStack);
            row.add(leftWrapper, BorderLayout.WEST);
        }

        chatHistoryContainer.add(row);
        chatHistoryContainer.revalidate();
        chatHistoryContainer.repaint();
    }

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

    private JPanel buildDMInfoPanel() {
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]20[]20[fill]"));
        p.setBackground(SIDEBAR_BG);

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont; foreground: #B0B3B8");
        p.add(avatar);

        // 🚀 THE FIX: Use the class-level variable instead of a local JLabel
        lblInfoName = new JLabel("Select a Chat");
        lblInfoName.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        p.add(lblInfoName);

        // 🚀 THE FIX: Use the class-level variable for status
        lblInfoStatus = new JLabel("Offline");
        lblInfoStatus.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        p.add(lblInfoStatus);

JPanel actionRow = new JPanel(new MigLayout("insets 0, gap 20", "[][][]", ""));
        actionRow.setBackground(SIDEBAR_BG);

        // 1. Profile Button (Uses the lambda action)
        actionRow.add(createActionCircle("👤", "Profile", e -> {
            if (activeChatId != null) {
                Component topLevel = SwingUtilities.getAncestorOfClass(JFrame.class, this);
                if (topLevel instanceof ChatUI) {
                    ChatUI ui = (ChatUI) topLevel;
                    ui.getProfilePanel().loadUserProfile(activeChatId);
                    ui.showCard("profile_card");
                }
            }
        }));

        // 2. Mute Button (Passes 'null' for now since we haven't built Mute logic)
        actionRow.add(createActionCircle("🔕", "Mute", null));

        // 3. Search Button (Passes 'null')
        actionRow.add(createActionCircle("🔍", "Search", null));

        p.add(actionRow);
        JPanel sections = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]"));
        sections.setBackground(SIDEBAR_BG);
        sections.add(createCategoryHeader("Privacy & support"));
        sections.add(createMenuButton("🔒 End-to-end encrypted"));

        JScrollPane sp = new JScrollPane(sections);
        sp.setBorder(null);
        p.add(sp, "grow");

        return p;
    }

    private JPanel buildGroupInfoPanel() {
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]20[]20[fill]"));
        p.setBackground(SIDEBAR_BG);
        JLabel avatar = new JLabel("👥");
        avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont");
        p.add(avatar);
        JLabel name = new JLabel("Group Settings");
        name.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        p.add(name);
        JScrollPane sp = new JScrollPane(new JPanel());
        sp.setBorder(null);
        p.add(sp, "grow");
        return p;
    }

    private JButton createIconButton(String iconText, String tooltip, boolean isBlueAccent) {
        JButton btn = new JButton(iconText);
        btn.setToolTipText(tooltip);
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; hoverBackground: #3A3B3C; arc: 999; margin: 5, 8, 5, 8; font: 140% $defaultFont; " + (isBlueAccent ? "foreground: #0084FF;" : "foreground: #B0B3B8;"));
        return btn;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; margin: 0, 0, 0, 0; font: 14; foreground: #E4E6EB; hoverBackground: #3A3B3C; arc: 10");
        btn.setBorder(new EmptyBorder(8, 10, 8, 10));
        return btn;
    }

private JPanel createActionCircle(String icon, String labelText, java.awt.event.ActionListener action) {
        JPanel p = new JPanel(new MigLayout("wrap 1, insets 0, align center", "[center]", "[]5[]"));
        p.setOpaque(false);
        
        JButton btn = new JButton(icon);
        // Styling the circle button
        btn.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; foreground: #E4E6EB; margin: 12,14,12,14; font: 150% $defaultFont; borderWidth: 0");
        
        // 🚀 THE FIX: Attach the action to the button!
        if (action != null) {
            btn.addActionListener(action);
        }
        
        p.add(btn);
        
        JLabel lbl = new JLabel(labelText);
        lbl.putClientProperty("FlatLaf.style", "font: 12; foreground: #E4E6EB");
        p.add(lbl);
        
        return p;
    }

    private JLabel createCategoryHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #B0B3B8");
        lbl.setBorder(new EmptyBorder(15, 10, 5, 10));
        return lbl;
    }
}
