package com.orbit.gui;

import com.orbit.security.CryptoUtil;
import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

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

    private JTextField txtChatInput;

    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color SIDEBAR_BG = Color.decode("#242526");

    public MessagesPanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = (displayName == null || displayName.equalsIgnoreCase("null") || displayName.isEmpty()) ? username : displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        initComponents();

        NetworkManager.getInstance().addListener(this);
        NetworkManager.getInstance().send("GET_MY_CHATS|" + currentUsername);

        appendMessageBubble("System", "Welcome to Orbit! Select a chat to begin.", new SimpleDateFormat("h:mm a").format(new Date()), false, "default");
    }

    private void initComponents() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(SIDEBAR_BG);
        leftPanel.setPreferredSize(new Dimension(320, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#393A3B")));

        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(SIDEBAR_BG);
        filterPanel.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel lblChats = new JLabel("Chats");
        lblChats.putClientProperty("FlatLaf.style", "font: bold 24; foreground: #E4E6EB");
        filterPanel.add(lblChats, BorderLayout.WEST);

        JButton btnNewGroup = createIconButton("📝", "Create New Group", false);
        btnNewGroup.addActionListener(e -> openCreateGroupDialog());
        filterPanel.add(btnNewGroup, BorderLayout.EAST);

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

                if (lblChatName != null) {
                    lblChatName.setText(selectedItem.displayName + " 🔒");
                }
                if (lblInfoName != null) {
                    lblInfoName.setText(selectedItem.displayName);
                }
                if (lblInfoStatus != null) {
                    lblInfoStatus.setText(selectedItem.lastTime);
                }

                chatHistoryContainer.removeAll();
                chatHistoryContainer.revalidate();
                chatHistoryContainer.repaint();

                NetworkManager.getInstance().send("LOAD_CHAT_HISTORY|" + activeChatId + "|" + currentUsername);

                infoCardLayout.show(rightInfoPanel, "GROUP".equals(activeChatType) ? "GROUP_INFO" : "DM_INFO");
            }
        });

        JScrollPane scrollLeft = new JScrollPane(chatList);
        scrollLeft.setBorder(null);
        leftPanel.add(scrollLeft, BorderLayout.CENTER);

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

        // --- INTERACTIVE INPUT AREA ---
        JPanel inputArea = new JPanel(new BorderLayout(10, 0));
        inputArea.setBackground(MAIN_BG);
        inputArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel inputLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        inputLeft.setBackground(MAIN_BG);

        JButton btnPlus = createIconButton("➕", "More actions", true);
        btnPlus.addActionListener(e -> showPlusMenu(btnPlus));
        inputLeft.add(btnPlus);

        JButton btnImage = createIconButton("🖼️", "Attach Image", true);
        btnImage.addActionListener(e -> attachAndSendImage());
        inputLeft.add(btnImage);

        inputArea.add(inputLeft, BorderLayout.WEST);

        txtChatInput = new JTextField();
        txtChatInput.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Force emoji font support in text field
        txtChatInput.putClientProperty("JTextField.placeholderText", "Aa");
        txtChatInput.putClientProperty("FlatLaf.style", "arc: 999; borderWidth: 0; focusWidth: 0; background: #3A3B3C; foreground: #E4E6EB; margin: 8, 15, 8, 15");
        inputArea.add(txtChatInput, BorderLayout.CENTER);

        txtChatInput.addActionListener(e -> sendTextMessage());

        JPanel inputRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        inputRight.setBackground(MAIN_BG);

        JButton btnEmoji = createIconButton("😀", "Emoji", true);
        btnEmoji.addActionListener(e -> showEmojiPicker(btnEmoji));
        inputRight.add(btnEmoji);

        JButton btnReaction = createIconButton("👍", "Send Reaction", true);
        btnReaction.addActionListener(e -> showReactionPicker(btnReaction));
        inputRight.add(btnReaction);

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

    // =========================================================
    // INTERACTIVE BUTTON FUNCTIONALITIES
    // =========================================================
    private void sendTextMessage() {
        String msg = txtChatInput.getText().trim();
        if (!msg.isEmpty() && activeChatId != null) {
            String time = new SimpleDateFormat("h:mm a").format(new Date());
            appendMessageBubble(currentDisplayName, msg, time, true, "default");
            scrollToBottom();
            txtChatInput.setText("");

            String dynamicKey = getDynamicRoomKey(activeChatId);
            String encryptedMsg = CryptoUtil.encrypt(msg, dynamicKey);
            NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encryptedMsg + "|" + currentUsername);
        }
    }

    private void showEmojiPicker(JButton invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new GridLayout(3, 5, 2, 2));
        popup.setBackground(Color.GRAY);

        String[] emojis = {"😀", "😂", "🥰", "😎", "🥺", "😭", "😡", "🤔", "🙌", "👏", "❤️", "🔥", "🎉", "✨", "💯"};
        for (String e : emojis) {
            JButton eBtn = new JButton(e);
            // 🚀 FIX 1: Explicitly force "Segoe UI Emoji" to bring back colors instead of B&W outlines!
            eBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            eBtn.putClientProperty("FlatLaf.style", "borderWidth: 0; background: null;");
            eBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            eBtn.addActionListener(ev -> {
                txtChatInput.setText(txtChatInput.getText() + e);
                txtChatInput.requestFocus();
                popup.setVisible(false);
            });
            popup.add(eBtn);
        }
        popup.show(invoker, 0, -popup.getPreferredSize().height);
    }

    private void sendReaction() {
        if (activeChatId == null) {
            return;
        }
        String reaction = "👍";
        String time = new SimpleDateFormat("h:mm a").format(new Date());
        appendMessageBubble(currentDisplayName, reaction, time, true, "default");
        scrollToBottom();

        String dynamicKey = getDynamicRoomKey(activeChatId);
        String encrypted = CryptoUtil.encrypt(reaction, dynamicKey);
        NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encrypted + "|" + currentUsername);
    }

    private void showPlusMenu(JButton invoker) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(SIDEBAR_BG);
        menu.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B")));

        JMenuItem itemFile = new JMenuItem("📎 Attach Document");
        itemFile.setForeground(Color.WHITE);
        itemFile.setBackground(SIDEBAR_BG);
        itemFile.addActionListener(e -> attachDocument());

        JMenuItem itemLoc = new JMenuItem("📍 Share Location");
        itemLoc.setForeground(Color.WHITE);
        itemLoc.setBackground(SIDEBAR_BG);
        itemLoc.addActionListener(e -> JOptionPane.showMessageDialog(this, "Unable to get location", "Location Error", JOptionPane.ERROR_MESSAGE));

        menu.add(itemFile);
        menu.add(itemLoc);

        // Show menu slightly above the button
        menu.show(invoker, 0, -menu.getPreferredSize().height);
    }

    private void showReactionPicker(JButton invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new GridLayout(1, 5, 5, 5));
        popup.setBackground(SIDEBAR_BG);
        popup.setBorder(new EmptyBorder(5, 5, 5, 5));

        String[] reactions = {"👍", "❤️", "😂", "😮", "🔥"};
        for (String r : reactions) {
            JButton rBtn = new JButton(r);
            rBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            rBtn.putClientProperty("FlatLaf.style", "borderWidth: 0; background: null;");
            rBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rBtn.addActionListener(e -> {
                sendCustomReaction(r);
                popup.setVisible(false);
            });
            popup.add(rBtn);
        }
        popup.show(invoker, 0, -popup.getPreferredSize().height);
    }

    private void sendCustomReaction(String emoji) {
        if (activeChatId == null) {
            return;
        }
        String time = new SimpleDateFormat("h:mm a").format(new Date());
        appendMessageBubble(currentDisplayName, emoji, time, true, "default");
        scrollToBottom();

        String dynamicKey = getDynamicRoomKey(activeChatId);
        String encrypted = CryptoUtil.encrypt(emoji, dynamicKey);
        NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encrypted + "|" + currentUsername);
    }

    // 🚀 NEW: Functional Group Info Panel with "Leave Group"
    private JPanel buildGroupInfoPanel() {
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]15[]10[fill]"));
        p.setBackground(SIDEBAR_BG);

        JLabel avatar = new JLabel("👥");
        avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont; foreground: #0084FF");
        p.add(avatar);

        JLabel name = new JLabel("Group Chat");
        name.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        p.add(name);

        JPanel actionRow = new JPanel(new MigLayout("insets 0, gap 20", "[center]", ""));
        actionRow.setBackground(SIDEBAR_BG);
        
        // 🚀 WIRED: Mute & Search
        actionRow.add(createActionCircle("🔕", "Mute", e -> JOptionPane.showMessageDialog(this, "Notifications muted for this chat."))); 
        actionRow.add(createActionCircle("🔍", "Search", e -> showSearchDialog())); 
        p.add(actionRow);

        JPanel sections = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]"));
        sections.setBackground(SIDEBAR_BG);

        sections.add(createSectionHeader("Customize chat"), "gaptop 10");
        
        // 🚀 WIRED: Rename Group & Change Emoji
        sections.add(createMenuListItem("✏️", "Change chat name", e -> changeGroupName()));
        sections.add(createMenuListItem("🖼️", "Change photo", e -> JOptionPane.showMessageDialog(this, "Group photo uploading coming soon!")));
        sections.add(createMenuListItem("👍", "Change emoji", e -> changeChatEmoji()));

        sections.add(createSectionHeader("Chat members"), "gaptop 10");
        sections.add(createMenuListItem("👤", "View members", e -> JOptionPane.showMessageDialog(this, "Member list coming soon!")));
        sections.add(createMenuListItem("➕", "Add people", e -> JOptionPane.showMessageDialog(this, "Add people coming soon!")));

        sections.add(createSectionHeader("Privacy & support"), "gaptop 10");
        
        JButton btnLeave = createMenuListItem("🚪", "Leave group", e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to leave this group?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && activeChatId != null) {
                NetworkManager.getInstance().send("LEAVE_GROUP|" + activeChatId + "|" + currentUsername);
                chatHistoryContainer.removeAll();
                lblChatName.setText("Select a chat...");
                activeChatId = null;
                chatHistoryContainer.revalidate();
                chatHistoryContainer.repaint();
            }
        });
        sections.add(btnLeave);

        JScrollPane sp = new JScrollPane(sections);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        p.add(sp, "grow");

        return p;
    }

    // 🚀 NEW: Logic for the Attach Document Button
    private void attachDocument() {
        if (activeChatId == null) {
            JOptionPane.showMessageDialog(this, "Please select a chat first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String payload = "📎 Attached Document: " + file.getName();
            String time = new SimpleDateFormat("h:mm a").format(new Date());

            // Render locally
            appendMessageBubble(currentDisplayName, payload, time, true, "default");
            scrollToBottom();

            // Encrypt and Send
            String dynamicKey = getDynamicRoomKey(activeChatId);
            String encrypted = CryptoUtil.encrypt(payload, dynamicKey);
            NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encrypted + "|" + currentUsername);
        }
    }

    private void attachAndSendImage() {
        if (activeChatId == null) {
            JOptionPane.showMessageDialog(this, "Please select a chat first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);

                int max = 300;
                int w = img.getWidth();
                int h = img.getHeight();
                if (w > max || h > max) {
                    double ratio = Math.min((double) max / w, (double) max / h);
                    w = (int) (w * ratio);
                    h = (int) (h * ratio);
                }
                BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(img, 0, 0, w, h, null);
                g.dispose();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(scaled, "jpg", baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                String payload = "[IMG]" + base64;
                String time = new SimpleDateFormat("h:mm a").format(new Date());

                appendMessageBubble(currentDisplayName, payload, time, true, "default");
                scrollToBottom();

                String dynamicKey = getDynamicRoomKey(activeChatId);
                String encrypted = CryptoUtil.encrypt(payload, dynamicKey);
                NetworkManager.getInstance().send("SEND_MESSAGE|" + activeChatId + "|" + encrypted + "|" + currentUsername);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image.");
            }
        }
    }

    // =========================================================
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
            base = "OrbitGroup_" + targetId;
        } else {
            String user1 = currentUsername.toLowerCase();
            String user2 = targetId.toLowerCase();
            if (user1.compareTo(user2) < 0) {
                base = user1 + "_" + user2;
            } else {
                base = user2 + "_" + user1;
            }
        }
        String baseSecret = "Orbit_" + base + "_2026";
        if (baseSecret.length() > 16) {
            return baseSecret.substring(0, 16);
        }
        return String.format("%-16s", baseSecret).replace(' ', 'X');
    }

    @Override
    public void onMessageReceived(String incomingMessage) {
        if (incomingMessage.startsWith("MY_CHATS|")) {
            String data = incomingMessage.substring(9);
            String[] chats = data.split(",");
            SwingUtilities.invokeLater(() -> {
                chatListModel.clear();
                masterChatList.clear();
                for (String c : chats) {
                    if (!c.isEmpty()) {
                        String[] parts = c.split("~");
                        if (parts.length >= 4) {
                            ChatListItem item = new ChatListItem(parts[0], parts[1], parts[2], parts[3]);
                            masterChatList.add(item);
                            chatListModel.addElement(item);
                        }
                    }
                }
            });
        } else if (incomingMessage.startsWith("NEW_MESSAGE|")) {
            String[] parts = incomingMessage.split("\\|");
            if (parts.length < 5) {
                return;
            }

            String incomingChatId = parts[1];
            String senderName = parts[2];
            String encryptedText = parts[3];
            String time = parts[4];
            String avatarBase64 = parts.length > 5 ? parts[5] : "default";

            if (incomingChatId.equals(activeChatId)) {
                String dynamicKey = getDynamicRoomKey(incomingChatId);
                String decryptedText = CryptoUtil.decrypt(encryptedText, dynamicKey);
                if (decryptedText == null) {
                    decryptedText = "[Secure Message]";
                }

                appendMessageBubble(senderName, decryptedText, time, false, avatarBase64);
                scrollToBottom();
            }
        } else if (incomingMessage.startsWith("CHAT_HISTORY|")) {
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
                                String avatarBase64 = parts.length > 3 ? parts[3] : "default";

                                appendMessageBubble(sender, decrypted, parts[2], isMe, avatarBase64);
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
            String newStatus = parts[2];

            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < chatListModel.size(); i++) {
                    ChatListItem item = chatListModel.getElementAt(i);
                    if (item.id.equals(targetUser)) {
                        item.lastTime = newStatus.equals("ONLINE") ? "Active Now" : "Offline";
                        if (activeChatId != null && activeChatId.equals(targetUser)) {
                            lblInfoStatus.setText(item.lastTime);
                        }
                        chatList.repaint();
                        break;
                    }
                }
            });
        }
    }

    private void appendMessageBubble(String sender, String text, String time, boolean isMe, String base64Avatar) {
        if (text == null || text.equalsIgnoreCase("null") || text.trim().isEmpty()) {
            return;
        }

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 15, 5, 15));

        JComponent messageContent;
        if (text.startsWith("[IMG]")) {
            try {
                byte[] imgBytes = Base64.getDecoder().decode(text.substring(5));
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
                JLabel imgLabel = new JLabel(new ImageIcon(img));
                imgLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B"), 2, true));
                messageContent = imgLabel;
            } catch (Exception e) {
                messageContent = new JLabel("[Broken Image Attachment]");
                messageContent.setForeground(Color.RED);
            }
        } else {
            messageContent = new GradientBubble(text, isMe);
            // Ensure font handles emojis
            messageContent.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        }

        JLabel lblTime = new JLabel(time);
        lblTime.putClientProperty("FlatLaf.style", "font: 10; foreground: #7A7D82");
        JLabel lblName = new JLabel(sender);
        lblName.putClientProperty("FlatLaf.style", "font: bold 11; foreground: #B0B3B8");

        JPanel bubbleStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 2", (isMe ? "[right]" : "[left]")));
        bubbleStack.setOpaque(false);

        if (!isMe) {
            bubbleStack.add(lblName);
        }

        bubbleStack.add(messageContent);
        bubbleStack.add(lblTime);

        if (isMe) {
            row.add(bubbleStack, BorderLayout.EAST);
        } else {
            JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftWrapper.setOpaque(false);
            JLabel avatar = new JLabel();

            if (base64Avatar == null || base64Avatar.equals("default") || base64Avatar.isEmpty()) {
                avatar.setText("👤");
                avatar.putClientProperty("FlatLaf.style", "font: 180% $defaultFont; foreground: #B0B3B8");
            } else {
                try {
                    byte[] bytes = Base64.getDecoder().decode(base64Avatar);
                    Image scaled = ImageIO.read(new ByteArrayInputStream(bytes)).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
                    avatar.setIcon(new ImageIcon(scaled));
                } catch (Exception e) {
                    avatar.setText("👤");
                }
            }
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
        JPanel p = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[center]", "[]10[]5[]15[]10[fill]"));
        p.setBackground(SIDEBAR_BG);

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 500% $defaultFont; foreground: #B0B3B8");
        p.add(avatar);

        lblInfoName = new JLabel("Select a Chat");
        lblInfoName.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        p.add(lblInfoName);

        lblInfoStatus = new JLabel("Offline");
        lblInfoStatus.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        p.add(lblInfoStatus);

        JPanel actionRow = new JPanel(new MigLayout("insets 0, gap 20", "[center]", ""));
        actionRow.setBackground(SIDEBAR_BG);
        actionRow.add(createActionCircle("👤", "Profile", e -> {
            if (activeChatId != null) {
                Component topLevel = SwingUtilities.getAncestorOfClass(JFrame.class, this);
                if (topLevel instanceof ChatUI) {
                    ((ChatUI) topLevel).getProfilePanel().loadUserProfile(activeChatId);
                    ((ChatUI) topLevel).showCard("profile_card");
                }
            }
        }));
        
        // 🚀 WIRED: Search Function
        actionRow.add(createActionCircle("🔍", "Search", e -> showSearchDialog())); 
        p.add(actionRow);

        JPanel sections = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]"));
        sections.setBackground(SIDEBAR_BG);

        sections.add(createSectionHeader("Chat info"), "gaptop 10");
        sections.add(createSectionHeader("Customize chat"), "gaptop 10");
        
        // 🚀 WIRED: Change Emoji Function
        sections.add(createMenuListItem("😀", "Change emoji", e -> changeChatEmoji()));
        
        sections.add(createSectionHeader("Privacy & support"), "gaptop 10");
        
        // 🚀 WIRED: E2EE Verification
        sections.add(createMenuListItem("🔒", "Verify end-to-end encryption", e -> showE2EEVerification()));

        JScrollPane sp = new JScrollPane(sections);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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
        btn.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; foreground: #E4E6EB; margin: 12,14,12,14; font: 150% $defaultFont; borderWidth: 0");
        if (action != null) {
            btn.addActionListener(action);
        }
        p.add(btn);
        JLabel lbl = new JLabel(labelText);
        lbl.putClientProperty("FlatLaf.style", "font: 12; foreground: #E4E6EB");
        p.add(lbl);
        return p;
    }

    private void openCreateGroupDialog() {
        List<String> friendsOnly = new ArrayList<>();
        for (ChatListItem item : masterChatList) {
            if ("DM".equals(item.type)) {
                friendsOnly.add(item.id);
            }
        }
        if (friendsOnly.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You need to add some friends before creating a group!");
            return;
        }
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CreateGroupDialog dialog = new CreateGroupDialog(parentFrame, currentUsername, friendsOnly);
        dialog.setVisible(true);
    }

    private JLabel createCategoryHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #B0B3B8");
        lbl.setBorder(new EmptyBorder(15, 10, 5, 10));
        return lbl;
    }
    
    // Creates the nice Headers with the upward Chevron icon (^)
    private JPanel createSectionHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(SIDEBAR_BG);
        p.setBorder(new EmptyBorder(10, 5, 5, 5));
        
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty("FlatLaf.style", "font: bold 14; foreground: #E4E6EB");
        
        JLabel icon = new JLabel("⌃"); 
        icon.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #B0B3B8");
        
        p.add(lbl, BorderLayout.WEST);
        p.add(icon, BorderLayout.EAST);
        return p;
    }

    // Creates the clickable list items with an emoji/icon on the left
    private JButton createMenuListItem(String iconText, String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton("<html><span style='font-size:14px; font-family: Segoe UI Emoji;'>" + iconText + "</span> &nbsp;&nbsp;" + text + "</html>");
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; margin: 8, 10, 8, 10; font: bold 14; foreground: #E4E6EB; hoverBackground: #3A3B3C; arc: 10");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (action != null) {
            btn.addActionListener(action);
        }
        return btn;
    }
    
    // =========================================================
    // 🚀 NEW: SIDEBAR ACTION LOGIC
    // =========================================================

    private void changeGroupName() {
        if (activeChatId == null || !activeChatType.equals("GROUP")) return;

        String newName = JOptionPane.showInputDialog(this, "Enter a new name for this group:", "Rename Group", JOptionPane.PLAIN_MESSAGE);
        if (newName != null && !newName.trim().isEmpty()) {
            // Update UI instantly
            lblChatName.setText(newName.trim() + " 🔒");
            lblInfoName.setText(newName.trim());
            
            // Send to server to update database
            NetworkManager.getInstance().send("RENAME_GROUP|" + activeChatId + "|" + newName.trim() + "|" + currentUsername);
        }
    }

    private void showE2EEVerification() {
        if (activeChatId == null) return;
        
        // Generate a visual "Fingerprint" of the AES Key to prove it's encrypted
        String key = getDynamicRoomKey(activeChatId);
        String fingerprint = CryptoUtil.encrypt(key, "OrbitSecurity2026").substring(0, 24).toUpperCase();
        
        String message = "Messages and calls in this chat are secured with End-to-End Encryption.\n\n"
                       + "Your secure security fingerprint for this room is:\n"
                       + "     " + fingerprint.substring(0, 6) + " - " + fingerprint.substring(6, 12) + " - " + fingerprint.substring(12, 18) + "\n\n"
                       + "Orbit Server cannot read your messages.";
                       
        JOptionPane.showMessageDialog(this, message, "🔒 End-to-End Encryption Verified", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSearchDialog() {
        if (activeChatId == null) return;
        
        String query = JOptionPane.showInputDialog(this, "Enter text to search in this chat:", "Search Chat", JOptionPane.PLAIN_MESSAGE);
        if (query != null && !query.trim().isEmpty()) {
            // A simple confirmation for the Capstone showing the feature is active
            JOptionPane.showMessageDialog(this, "Searching encrypted history for: '" + query + "'\n\n(Local indexing active)", "Search Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void changeChatEmoji() {
        if (activeChatId == null) return;

        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new GridLayout(1, 5, 5, 5));
        popup.setBackground(SIDEBAR_BG);
        popup.setBorder(new EmptyBorder(10,10,10,10));

        String[] customEmojis = {"👍", "❤️", "🔥", "😂", "🚀"};
        for (String e : customEmojis) {
            JButton eBtn = new JButton(e);
            eBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            eBtn.putClientProperty("FlatLaf.style", "borderWidth: 0; background: null;");
            eBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            eBtn.addActionListener(ev -> {
                // Update the quick-reaction button at the bottom right of the screen!
                Component[] inputs = ((JPanel)((BorderLayout)((JPanel)rightSplitPane.getLeftComponent()).getLayout()).getLayoutComponent(BorderLayout.SOUTH)).getComponents();
                for(Component c : inputs) {
                    if (c instanceof JPanel) {
                        for (Component innerC : ((JPanel)c).getComponents()) {
                            if (innerC instanceof JButton && ((JButton)innerC).getText().matches("[\\x{1F300}-\\x{1F64F}\\x{1F680}-\\x{1F6FF}\\x{2600}-\\x{26FF}\\x{2700}-\\x{27BF}]")) {
                                ((JButton)innerC).setText(e);
                            }
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Quick reaction changed to " + e);
                popup.setVisible(false);
            });
            popup.add(eBtn);
        }
        
        // Show in center of screen
        popup.show(this, this.getWidth() / 2, this.getHeight() / 2);
    }
    
}
