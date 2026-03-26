package com.orbit.gui;

import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomePanel extends JPanel implements NetworkListener {

    private String currentUsername;
    private String currentDisplayName;

    // UI Components
    private JPanel feedContainer;
    private JTextField txtPostInput;

    // Theme Colors
    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color CARD_BG = Color.decode("#242526");
    private final Color TEXT_PRIMARY = Color.decode("#E4E6EB");
    private final Color TEXT_MUTED = Color.decode("#B0B3B8");

    public HomePanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        // 1. Register to network
        NetworkManager.getInstance().addListener(this);
        
        // 2. Build UI
        initComponents();
        
        // 3. Ask server for the latest posts
        NetworkManager.getInstance().send("GET_HOME_FEED|" + currentUsername);
    }

    private void initComponents() {
        // Main wrapper that handles the 150px side margins for a centered look
        JPanel contentWrapper = new JPanel(new MigLayout("wrap 1, fillx, insets 20 150 50 150", "[fill]", "[]20[]"));
        contentWrapper.setBackground(MAIN_BG);

        // --- SECTION 1: CREATE POST CARD ---
        contentWrapper.add(buildCreatePostCard(), "growx");

        // --- SECTION 2: THE FEED ---
        feedContainer = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]15[]"));
        feedContainer.setBackground(MAIN_BG);
        contentWrapper.add(feedContainer, "growx");

        // ScrollPane for the entire page
        JScrollPane scroll = new JScrollPane(contentWrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildCreatePostCard() {
        JPanel card = new JPanel(new MigLayout("insets 15, fillx", "[][grow]", "[]15[]"));
        card.setBackground(CARD_BG);
        card.putClientProperty("FlatLaf.style", "arc: 15");

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 250% $defaultFont; foreground: #B0B3B8");
        
        txtPostInput = new JTextField();
        txtPostInput.putClientProperty("JTextField.placeholderText", "What's on your mind, " + currentDisplayName + "?");
        txtPostInput.putClientProperty("FlatLaf.style", "arc: 999; background: #3A3B3C; borderWidth: 0; font: 16; foreground: #E4E6EB");
        txtPostInput.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Submit on Enter
        txtPostInput.addActionListener(e -> submitPost());

        card.add(avatar);
        card.add(txtPostInput, "growx, pushx");
        
        // Visual buttons for decoration
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        actions.setOpaque(false);
        actions.add(createQuickAction("📹 Live Video", "#F3425F"));
        actions.add(createQuickAction("🖼️ Photo/Video", "#45BD62"));
        actions.add(createQuickAction("😊 Feeling", "#F7B928"));
        
        card.add(actions, "span 2, growx, gaptop 10");

        return card;
    }

    private JLabel createQuickAction(String text, String hexColor) {
        JLabel lbl = new JLabel(text);
        lbl.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #B0B3B8");
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return lbl;
    }

    private void submitPost() {
        String content = txtPostInput.getText().trim();
        if (!content.isEmpty()) {
            // Send to server: CREATE_POST | content | authorUsername
            NetworkManager.getInstance().send("CREATE_POST|" + content + "|" + currentUsername);
            
            // Add to UI immediately (Optimistic UI)
            String time = new SimpleDateFormat("h:mm a").format(new Date());
            addPostToFeed(currentDisplayName, time, content, 0, 0, true);
            
            txtPostInput.setText("");
        }
    }

    private void addPostToFeed(String author, String time, String content, int likes, int comments, boolean atTop) {
        JPanel post = createPostCard(author, time, content, likes, comments);
        if (atTop) {
            feedContainer.add(post, "growx", 0);
        } else {
            feedContainer.add(post, "growx");
        }
        feedContainer.revalidate();
        feedContainer.repaint();
    }

    private JPanel createPostCard(String author, String timeAgo, String content, int likes, int comments) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx, insets 15", "[fill]", "[]15[]15[]"));
        card.setBackground(CARD_BG);
        card.putClientProperty("FlatLaf.style", "arc: 15");

        // 1. Post Header
        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[][grow][]", ""));
        header.setOpaque(false);
        
        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 200% $defaultFont; foreground: #B0B3B8");
        
        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0"));
        nameStack.setOpaque(false);
        JLabel lblAuthor = new JLabel(author);
        lblAuthor.putClientProperty("FlatLaf.style", "font: bold 15; foreground: #E4E6EB");
        JLabel lblTime = new JLabel(timeAgo);
        lblTime.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        
        nameStack.add(lblAuthor);
        nameStack.add(lblTime);
        
        header.add(avatar);
        header.add(nameStack, "growx");
        header.add(new JLabel("⋮"), "east");
        card.add(header);

        // 2. Post Content
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.putClientProperty("FlatLaf.style", "font: 15; foreground: #E4E6EB");
        card.add(area);

        // 3. Footer
        JPanel footer = new JPanel(new MigLayout("insets 5 0 0 0, fillx", "[grow][grow]", "[]10[]"));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#393A3B")));
        
        JLabel stats = new JLabel("👍 " + likes + "   💬 " + comments + " Comments");
        stats.setForeground(TEXT_MUTED);
        footer.add(stats, "span 2, wrap");

        JButton btnLike = new JButton("👍 Like");
        btnLike.putClientProperty("FlatLaf.style", "buttonType: borderless; foreground: #B0B3B8; font: bold 14; hoverBackground: #3A3B3C");
        
        JButton btnComment = new JButton("💬 Comment");
        btnComment.putClientProperty("FlatLaf.style", "buttonType: borderless; foreground: #B0B3B8; font: bold 14; hoverBackground: #3A3B3C");
        
        footer.add(btnLike, "growx");
        footer.add(btnComment, "growx");
        card.add(footer);

        return card;
    }

    @Override
    public void onMessageReceived(String message) {
        if (message.startsWith("HOME_FEED|")) {
            String data = message.length() > 10 ? message.substring(10) : "";
            SwingUtilities.invokeLater(() -> {
                feedContainer.removeAll();
                if (data.isEmpty()) {
                    JLabel lbl = new JLabel("No posts yet. Start the conversation!");
                    lbl.setForeground(Color.GRAY);
                    feedContainer.add(lbl);
                } else {
                    // Posts separated by ~, Parts separated by |
                    String[] posts = data.split("~");
                    for (String p : posts) {
                        String[] parts = p.split("\\|");
                        if (parts.length >= 5) {
                            addPostToFeed(parts[0], parts[2], parts[1], 
                                         Integer.parseInt(parts[3]), 
                                         Integer.parseInt(parts[4]), false);
                        }
                    }
                }
                feedContainer.revalidate();
                feedContainer.repaint();
            });
        }
    }
}