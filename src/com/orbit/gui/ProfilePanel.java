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
    private JLabel lblName;
    private JLabel lblUser;
    private JLabel lblPostCount;
    private JLabel lblFriendCount;
    private JButton btnEdit;

    // Theme Colors
    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color CARD_BG = Color.decode("#242526");

    public ProfilePanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        NetworkManager.getInstance().addListener(this);

        initComponents();
        loadUserProfile(currentUsername);
    }

    private void initComponents() {
        JPanel pageWrapper = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]", "[]0[]"));
        pageWrapper.setBackground(MAIN_BG);

        pageWrapper.add(buildProfileHeader(), "growx");

        feedContainer = new JPanel(new MigLayout("wrap 1, fillx, insets 20 150 50 150", "[fill]", "[]20[]"));
        feedContainer.setBackground(MAIN_BG);

        pageWrapper.add(feedContainer, "growx");

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

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 600% $defaultFont; foreground: #B0B3B8");
        headerCard.add(avatar, "span 1 2, aligny top, gapright 20");

        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 5"));
        nameStack.setOpaque(false);
        
        lblName = new JLabel(currentDisplayName);
        lblName.putClientProperty("FlatLaf.style", "font: bold 32; foreground: #E4E6EB");
        
        lblUser = new JLabel("@" + currentUsername);
        lblUser.putClientProperty("FlatLaf.style", "font: 16; foreground: #B0B3B8");
        
        nameStack.add(lblName);
        nameStack.add(lblUser);
        headerCard.add(nameStack, "growx, aligny center");

        btnEdit = new JButton("✏️ Edit Profile");
        btnEdit.putClientProperty("FlatLaf.style", "arc: 10; background: #3A3B3C; foreground: #E4E6EB; font: bold 14; borderWidth: 0; margin: 8, 15, 8, 15");
        headerCard.add(btnEdit, "aligny center, wrap");
        btnEdit.addActionListener(e -> showEditDialog());

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

    public void loadUserProfile(String targetUsername) {
        if (lblName != null) lblName.setText("Loading...");
        feedContainer.removeAll();
        feedContainer.revalidate();
        feedContainer.repaint();

        NetworkManager.getInstance().send("GET_PROFILE_DATA|" + targetUsername + "|" + currentUsername);
    }

    // 🚀 THE FIX: Cleaned up the message handling logic
@Override
    public void onMessageReceived(String incomingMessage) {
        if (incomingMessage.startsWith("PROFILE_DATA|")) {
            System.out.println("🟢 CLIENT RECEIVED PROFILE DATA: " + incomingMessage); // DEBUG LOG
            
            // The -1 ensures that even if the post list is empty, it doesn't drop the last piece of data
            String[] parts = incomingMessage.split("\\|", -1); 
            
            if (parts.length < 6) {
                System.err.println("❌ ERROR: Profile packet was too short! Length: " + parts.length);
                return;
            }

            SwingUtilities.invokeLater(() -> {
                try {
                    lblName.setText(parts[2]);
                    lblUser.setText("@" + parts[1]);
                    lblPostCount.setText(parts[3] + " Posts");
                    lblFriendCount.setText(parts[4] + " Friends");
                    
                    btnEdit.setVisible(parts[5].trim().equals("TRUE"));

                    feedContainer.removeAll();
                    JLabel lblTimeline = new JLabel("Timeline");
                    lblTimeline.putClientProperty("FlatLaf.style", "font: bold 20; foreground: #E4E6EB");
                    feedContainer.add(lblTimeline, "gapbottom 10");

                    // Check if there are posts (parts[6])
                    if (parts.length > 6 && parts[6] != null && !parts[6].trim().isEmpty()) {
                        String[] allPosts = parts[6].split("~");
                        for (String p : allPosts) {
                            if (!p.isEmpty()) {
                                String[] postParts = p.split("\\^");
                                if (postParts.length >= 2) {
                                    feedContainer.add(createPostCard(parts[2], postParts[1], postParts[0], 0, 0), "growx");
                                }
                            }
                        }
                    } else {
                        JLabel noPosts = new JLabel("No posts to show.");
                        noPosts.setForeground(Color.decode("#B0B3B8"));
                        feedContainer.add(noPosts);
                    }

                    feedContainer.revalidate();
                    feedContainer.repaint();
                    System.out.println("✅ CLIENT UI UPDATED SUCCESSFULLY."); // DEBUG LOG
                } catch (Exception e) {
                    System.err.println("❌ UI CRASHED WHILE LOADING PROFILE: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } 
        else if (incomingMessage.startsWith("UPDATE_SUCCESS|")) {
            String newName = incomingMessage.substring(15);
            this.currentDisplayName = newName; 
            SwingUtilities.invokeLater(() -> {
                lblName.setText(newName); 
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            });
        }
    }
    private void showEditDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Profile", true);
        dialog.setLayout(new MigLayout("wrap 1, fillx, insets 20", "[fill]", "[]10[]20[]"));
        dialog.getContentPane().setBackground(CARD_BG);

        JLabel lblTitle = new JLabel("Edit Display Name");
        lblTitle.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        
        JTextField txtName = new JTextField(currentDisplayName);
        txtName.putClientProperty("FlatLaf.style", "arc: 10; background: #3A3B3C; foreground: #E4E6EB; borderWidth: 0");
        txtName.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnSave = new JButton("Save Changes");
        btnSave.putClientProperty("FlatLaf.style", "arc: 10; background: #0084FF; foreground: #FFFFFF; font: bold 14; borderWidth: 0");
        btnSave.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnSave.addActionListener(e -> {
            String newName = txtName.getText().trim();
            if (!newName.isEmpty()) {
                NetworkManager.getInstance().send("UPDATE_PROFILE|" + newName + "|" + currentUsername);
                dialog.dispose();
            }
        });

        dialog.add(lblTitle);
        dialog.add(txtName);
        dialog.add(btnSave);

        dialog.pack();
        dialog.setSize(400, 220);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createPostCard(String authorName, String timeAgo, String content, int likes, int comments) {
        JPanel card = new JPanel(new MigLayout("wrap 1, fillx, insets 15", "[fill]", "[]15[]15[]"));
        card.setBackground(CARD_BG);
        card.putClientProperty("FlatLaf.style", "arc: 15");

        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[][grow][]", ""));
        header.setOpaque(false);
        
        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 200% $defaultFont; foreground: #B0B3B8");
        header.add(avatar);
        
        JPanel nameStack = new JPanel(new MigLayout("wrap 1, insets 0, gap 0"));
        nameStack.setOpaque(false);
        JLabel lblAuthor = new JLabel(authorName);
        lblAuthor.putClientProperty("FlatLaf.style", "font: bold 15; foreground: #E4E6EB");
        JLabel lblTime = new JLabel(timeAgo);
        lblTime.putClientProperty("FlatLaf.style", "font: 12; foreground: #B0B3B8");
        nameStack.add(lblAuthor);
        nameStack.add(lblTime);
        header.add(nameStack, "growx");
        
        JButton btnOptions = new JButton("⋮");
        btnOptions.putClientProperty("FlatLaf.style", "buttonType: borderless; font: 150% $defaultFont; foreground: #B0B3B8");
        header.add(btnOptions);
        
        card.add(header);

        JTextArea txtContent = new JTextArea(content);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setOpaque(false);
        txtContent.putClientProperty("FlatLaf.style", "font: 15; foreground: #E4E6EB");
        card.add(txtContent);

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
}