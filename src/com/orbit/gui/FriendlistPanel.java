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

public class FriendlistPanel extends JPanel implements NetworkListener {

    // User Data
    private String currentUsername;
    private String currentDisplayName;

    // Layout Components
    private CardLayout cardLayout;
    private JPanel centerCardPanel;

    // Data Containers
    private JPanel allFriendsContainer;
    private JPanel requestsContainer;
    private JPanel suggestionsContainer;
    private JPanel addFriendContainer;

    // Theme Colors
    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color SIDEBAR_BG = Color.decode("#242526");

    public FriendlistPanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        initComponents();

        NetworkManager.getInstance().addListener(this);
        NetworkManager.getInstance().send("GET_SUGGESTED_USERS|" + currentUsername);
    }

    private void initComponents() {
        // --- 1. LEFT SIDEBAR ---
        JPanel leftSidebar = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[fill]", "[]20[]5[]5[]5[]"));
        leftSidebar.setBackground(SIDEBAR_BG);
        leftSidebar.setPreferredSize(new Dimension(280, 0));
        leftSidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#393A3B")));

        JLabel lblTitle = new JLabel("Friends");
        lblTitle.putClientProperty("FlatLaf.style", "font: bold 24; foreground: #E4E6EB");
        leftSidebar.add(lblTitle);

        leftSidebar.add(createSidebarButton("👥", "All Friends", "all_friends"));
        leftSidebar.add(createSidebarButton("🔔", "Friend Requests", "requests"));
        leftSidebar.add(createSidebarButton("✨", "Suggestions", "suggestions"));
        leftSidebar.add(createSidebarButton("🔍", "Add a Friend", "add_friend"));

        add(leftSidebar, BorderLayout.WEST);

        // --- 2. CENTER PANEL ---
        cardLayout = new CardLayout();
        centerCardPanel = new JPanel(cardLayout);
        centerCardPanel.setBackground(MAIN_BG);

        allFriendsContainer = createListContainer();
        requestsContainer = createListContainer();

        // Make Suggestions a 3-Column Grid Layout instead of a list!
        suggestionsContainer = new JPanel(new MigLayout("wrap 3, fillx, gap 20, insets 20", "[fill, grow][fill, grow][fill, grow]", ""));
        suggestionsContainer.setBackground(MAIN_BG);

        JPanel addFriendRoot = new JPanel(new BorderLayout());
        addFriendRoot.setBackground(MAIN_BG);
        addFriendContainer = createListContainer();
        addFriendRoot.add(buildSearchBar(), BorderLayout.NORTH);
        addFriendRoot.add(new JScrollPane(addFriendContainer), BorderLayout.CENTER);

        centerCardPanel.add(createHeaderWrapper("All Friends", allFriendsContainer), "all_friends");
        centerCardPanel.add(createHeaderWrapper("Friend Requests", requestsContainer), "requests");
        centerCardPanel.add(createHeaderWrapper("Suggested for You", suggestionsContainer), "suggestions");
        centerCardPanel.add(addFriendRoot, "add_friend");

        add(centerCardPanel, BorderLayout.CENTER);
    }

    // ==============================================================
    // NETWORK INTERFACE IMPLEMENTATION
    // ==============================================================
    @Override
    public void onMessageReceived(String incomingMessage) {
        //  SUGGESTED USERS HANDLER
        if (incomingMessage.startsWith("SUGGESTED_USERS|")) {
            String data = incomingMessage.length() > 16 ? incomingMessage.substring(16) : "";
            SwingUtilities.invokeLater(() -> {
                suggestionsContainer.removeAll();

                if (data.isEmpty()) {
                    JLabel noResult = new JLabel("No new users to suggest right now!");
                    noResult.setForeground(Color.GRAY);
                    suggestionsContainer.add(noResult, "span 3, align center");
                } else {
                    String[] users = data.split("~");
                    for (String u : users) {
                        if (!u.isEmpty()) {
                            String[] parts = u.split("\\^"); // [0]=username, [1]=fullname, [2]=avatar
                            if (parts.length >= 3) {
                                // Add to the 3-column grid
                                suggestionsContainer.add(createSuggestedUserCard(parts[0], parts[1], parts[2]), "growx, growy");
                            }
                        }
                    }
                }
                suggestionsContainer.revalidate();
                suggestionsContainer.repaint();
            });
        } // 1. HANDLE SEARCH RESULTS
        else if (incomingMessage.startsWith("SEARCH_RESULTS|")) {
            String data = incomingMessage.length() > 15 ? incomingMessage.substring(15) : "";
            SwingUtilities.invokeLater(() -> {
                addFriendContainer.removeAll();
                if (data.isEmpty()) {
                    JLabel noResult = new JLabel("No users found.");
                    noResult.setForeground(Color.GRAY);
                    addFriendContainer.add(noResult);
                } else {
                    String[] users = data.split(",");
                    for (String u : users) {
                        if (!u.isEmpty()) {
                            String[] parts = u.split(":");
                            addFriendContainer.add(createUserRow(parts[1], "@" + parts[0], "ADD"), "growx");
                        }
                    }
                }
                addFriendContainer.revalidate();
                addFriendContainer.repaint();
            });
        } // 2. HANDLE PENDING FRIEND REQUESTS
        else if (incomingMessage.startsWith("FRIEND_REQUESTS|")) {
            String data = incomingMessage.length() > 16 ? incomingMessage.substring(16) : "";
            SwingUtilities.invokeLater(() -> {
                requestsContainer.removeAll();
                if (data.isEmpty()) {
                    JLabel noResult = new JLabel("No pending friend requests.");
                    noResult.setForeground(Color.GRAY);
                    requestsContainer.add(noResult);
                } else {
                    String[] users = data.split(",");
                    for (String u : users) {
                        if (!u.isEmpty()) {
                            String[] parts = u.split(":");
                            requestsContainer.add(createUserRow(parts[1], "@" + parts[0], "REQUEST"), "growx");
                        }
                    }
                }
                requestsContainer.revalidate();
                requestsContainer.repaint();
            });
        } // 3. HANDLE ALL ACCEPTED FRIENDS
        else if (incomingMessage.startsWith("ALL_FRIENDS|")) {
            String data = incomingMessage.length() > 12 ? incomingMessage.substring(12) : "";
            SwingUtilities.invokeLater(() -> {
                allFriendsContainer.removeAll();
                if (data.isEmpty()) {
                    JLabel noResult = new JLabel("You have no friends yet. Go add some!");
                    noResult.setForeground(Color.GRAY);
                    allFriendsContainer.add(noResult);
                } else {
                    String[] users = data.split(",");
                    for (String u : users) {
                        if (!u.isEmpty()) {
                            String[] parts = u.split(":");
                            allFriendsContainer.add(createUserRow(parts[1], "@" + parts[0], "FRIEND"), "growx");
                        }
                    }
                }
                allFriendsContainer.revalidate();
                allFriendsContainer.repaint();
            });
        }
    }

    // ==============================================================
    // COMPONENT BUILDERS
    // ==============================================================
    private JPanel createListContainer() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 20", "[fill]", "[]10[]"));
        panel.setBackground(MAIN_BG);
        return panel;
    }

    private JPanel createHeaderWrapper(String title, JPanel listContainer) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MAIN_BG);

        JLabel lblHeader = new JLabel(title);
        lblHeader.putClientProperty("FlatLaf.style", "font: bold 20; foreground: #E4E6EB");
        lblHeader.setBorder(new EmptyBorder(20, 20, 10, 20));

        wrapper.add(lblHeader, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel buildSearchBar() {
        JPanel searchPanel = new JPanel(new MigLayout("fillx, insets 20", "[fill]", "[]"));
        searchPanel.setBackground(MAIN_BG);

        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Search by username or name...");
        txtSearch.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; borderWidth: 0; font: 16");
        txtSearch.setBorder(new EmptyBorder(12, 20, 12, 20));

        JButton btnSearch = new JButton("Search");
        btnSearch.putClientProperty("FlatLaf.style", "arc: 999; background: #0084FF; foreground: #FFFFFF; font: bold 14; borderWidth: 0");

        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim();
            if (!query.isEmpty()) {
                addFriendContainer.removeAll();
                JLabel loading = new JLabel("Searching database...");
                loading.setForeground(Color.GRAY);
                addFriendContainer.add(loading);
                addFriendContainer.revalidate();
                addFriendContainer.repaint();

                NetworkManager.getInstance().send("SEARCH_USERS|" + query + "|" + currentUsername);
            }
        });

        searchPanel.add(txtSearch, "growx, pushx");
        searchPanel.add(btnSearch, "gapleft 10");
        return searchPanel;
    }

    private JButton createSidebarButton(String icon, String text, String cardName) {
        JButton btn = new JButton(icon + "   " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; font: bold 15; foreground: #E4E6EB; hoverBackground: #3A3B3C; arc: 10");
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));

        btn.addActionListener(e -> {
            cardLayout.show(centerCardPanel, cardName);

            // We must request fresh data based on the tab they clicked!
            if (cardName.equals("requests")) {
                NetworkManager.getInstance().send("GET_FRIEND_REQUESTS|" + currentUsername);
            } else if (cardName.equals("all_friends")) {
                NetworkManager.getInstance().send("GET_ALL_FRIENDS|" + currentUsername);
            } else if (cardName.equals("suggestions")) {
                NetworkManager.getInstance().send("GET_SUGGESTED_USERS|" + currentUsername); // Added this request!
            }
        });

        return btn;
    }

    // ==============================================================
    // DYNAMIC ROW & CARD GENERATORS
    // ==============================================================
    private JPanel createUserRow(String displayName, String username, String actionType) {
        JPanel row = new JPanel(new MigLayout("insets 15, fillx", "[][grow][][]", ""));
        row.setBackground(SIDEBAR_BG);
        row.putClientProperty("FlatLaf.style", "arc: 15");

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 300% $defaultFont; foreground: #B0B3B8");
        row.add(avatar);

        JPanel textStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0"));
        textStack.setOpaque(false);
        JLabel lblName = new JLabel(displayName);
        lblName.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        JLabel lblUser = new JLabel(username);
        lblUser.putClientProperty("FlatLaf.style", "font: 13; foreground: #B0B3B8");
        textStack.add(lblName);
        textStack.add(lblUser);
        row.add(textStack, "growx");

        if ("REQUEST".equals(actionType)) {
            JButton btnConfirm = createActionButton("Confirm", true);
            JButton btnDelete = createActionButton("Delete", false);
            btnConfirm.addActionListener(e -> {
                sendAction("RESPOND_FRIEND_REQUEST|" + username.replace("@", "") + "|ACCEPTED");
                row.setVisible(false);
            });
            btnDelete.addActionListener(e -> {
                sendAction("RESPOND_FRIEND_REQUEST|" + username.replace("@", "") + "|DECLINED");
                row.setVisible(false);
            });
            row.add(btnConfirm);
            row.add(btnDelete);
        } else if ("FRIEND".equals(actionType)) {
            JButton btnMessage = createActionButton("Message", true);
            JButton btnUnfriend = createActionButton("Unfriend", false);
            btnUnfriend.addActionListener(e -> {
                sendAction("REMOVE_FRIEND|" + username.replace("@", ""));
                row.setVisible(false);
            });
            row.add(btnMessage);
            row.add(btnUnfriend);
        } else if ("ADD".equals(actionType)) {
            JButton btnAdd = createActionButton("Add Friend", true);
            btnAdd.addActionListener(e -> {
                sendAction("SEND_FRIEND_REQUEST|" + username.replace("@", ""));
                btnAdd.setText("Request Sent");
                btnAdd.setEnabled(false);
                btnAdd.setBackground(Color.decode("#3A3B3C"));
                btnAdd.setForeground(Color.decode("#B0B3B8"));
            });
            row.add(btnAdd);
        }

        return row;
    }

    // Redesigned the card to look beautiful in a Grid Layout
    private JPanel createSuggestedUserCard(String username, String fullName, String base64Avatar) {
        JPanel card = new JPanel(new MigLayout("wrap 1, insets 15, align center", "[center]", "[]10[]2[]15[]"));
        card.setBackground(Color.decode("#242526")); 
        card.putClientProperty("FlatLaf.style", "arc: 15");

        // 1. Avatar
        JLabel avatar = new JLabel();
        if (base64Avatar == null || base64Avatar.equals("default") || base64Avatar.isEmpty()) {
            avatar.setText("👤");
            avatar.putClientProperty("FlatLaf.style", "font: 400% $defaultFont; foreground: #B0B3B8");
        } else {
            try {
                byte[] bytes = java.util.Base64.getDecoder().decode(base64Avatar);
                java.awt.Image img = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes)).getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
                avatar.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                avatar.setText("👤");
                avatar.putClientProperty("FlatLaf.style", "font: 400% $defaultFont; foreground: #B0B3B8");
            }
        }
        card.add(avatar);

        // 2. Name & Username
        JLabel lblName = new JLabel(fullName);
        lblName.putClientProperty("FlatLaf.style", "font: bold 16; foreground: #E4E6EB");
        JLabel lblUser = new JLabel("@" + username);
        lblUser.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        
        card.add(lblName);
        card.add(lblUser);

        // 3. Add Friend Button
        JButton btnAdd = new JButton("Add Friend");
        btnAdd.putClientProperty("FlatLaf.style", "background: #0084FF; foreground: #FFFFFF; arc: 10; font: bold 13; borderWidth: 0; margin: 8, 25, 8, 25");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAdd.addActionListener(e -> {
            // 🚀 TARGET USERNAME first, then CURRENT USERNAME
            NetworkManager.getInstance().send("SEND_FRIEND_REQUEST|" + username + "|" + currentUsername);
            
            btnAdd.setText("Sent ✓");
            btnAdd.setEnabled(false);
            btnAdd.setBackground(Color.decode("#3A3B3C"));
        });
        
        card.add(btnAdd);

        return card;
    }

    private JButton createActionButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        if (isPrimary) {
            btn.putClientProperty("FlatLaf.style", "arc: 10; background: #0084FF; foreground: #FFFFFF; font: bold 13; borderWidth: 0");
        } else {
            btn.putClientProperty("FlatLaf.style", "arc: 10; background: #3A3B3C; foreground: #E4E6EB; font: bold 13; borderWidth: 0");
        }
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // Helper method to safely format and send actions through NetworkManager
    private void sendAction(String payload) {
        NetworkManager.getInstance().send(payload + "|" + currentUsername);
    }
}
