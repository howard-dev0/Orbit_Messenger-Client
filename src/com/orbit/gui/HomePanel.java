package com.orbit.gui;

import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import javax.imageio.ImageIO;

public class HomePanel extends JPanel implements NetworkListener {

    private String currentUsername;
    private String currentDisplayName;
    private JPanel feedContainer;
    private JTextArea txtPost;
    
    // 🚀 VARIABLES FOR IMAGE ATTACHMENTS
    private String attachedImageBase64 = null;
    private JPanel attachmentPreviewPanel;

    // Theme Colors
    private final Color MAIN_BG = Color.decode("#18191A");
    private final Color CARD_BG = Color.decode("#242526");

    public HomePanel(String username, String displayName) {
        this.currentUsername = username;
        this.currentDisplayName = (displayName == null || displayName.equalsIgnoreCase("null") || displayName.isEmpty()) ? username : displayName;

        setLayout(new BorderLayout());
        setBackground(MAIN_BG);
        
        initComponents();

        // Register to receive network updates
        NetworkManager.getInstance().addListener(this);
        
        // Fetch the feed from the server immediately upon opening the panel
        NetworkManager.getInstance().send("GET_HOME_FEED|" + currentUsername);
    }

    private void initComponents() {
        // Main wrapper with 20% margins on the sides to center the feed beautifully
        JPanel wrapper = new JPanel(new MigLayout("wrap 1, fillx, insets 20 20% 20 20%", "[fill, grow]"));
        wrapper.setBackground(MAIN_BG);

        // 1. The "Create Post" Box
        wrapper.add(createComposerPanel(), "growx, wrap");

        // 2. The Feed Container
        feedContainer = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill, grow]"));
        feedContainer.setBackground(MAIN_BG);

        wrapper.add(feedContainer, "growx");

        // Scroll Pane
        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

private JPanel createComposerPanel() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 15", "[][grow]", "[][][]"));
        p.setBackground(CARD_BG);
        p.putClientProperty("FlatLaf.style", "arc: 15");

        JLabel avatar = new JLabel("👤");
        avatar.putClientProperty("FlatLaf.style", "font: 250% $defaultFont; foreground: #B0B3B8");
        p.add(avatar, "aligny top");

        txtPost = new JTextArea(3, 20);
        txtPost.setLineWrap(true);
        txtPost.setWrapStyleWord(true);
        txtPost.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        txtPost.putClientProperty("JTextField.placeholderText", "What's on your mind, " + currentDisplayName + "?");
        txtPost.putClientProperty("FlatLaf.style", "background: #3A3B3C; foreground: #E4E6EB");
        txtPost.setBorder(new EmptyBorder(10, 15, 10, 15));

        JScrollPane txtScroll = new JScrollPane(txtPost);
        txtScroll.setBorder(null);
        p.add(txtScroll, "growx, pushx, wrap");
        
        attachmentPreviewPanel = new JPanel(new BorderLayout());
        attachmentPreviewPanel.setOpaque(false);
        attachmentPreviewPanel.setVisible(false); 
        p.add(attachmentPreviewPanel, "skip 1, growx, pushx, wrap");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actions.setOpaque(false);
        
        JButton btnPhoto = createIconButton("🖼️", "Photo/Video");
        btnPhoto.addActionListener(e -> attachPhotoToPost());
        actions.add(btnPhoto);
        
        JButton btnFeeling = createIconButton("😀", "Feeling/Activity");
        btnFeeling.addActionListener(e -> showFeelingPicker(btnFeeling));
        actions.add(btnFeeling);

        JButton btnPost = new JButton("Post");
        btnPost.putClientProperty("FlatLaf.style", "arc: 10; background: #0084FF; foreground: #FFFFFF; font: bold 14; borderWidth: 0; margin: 8, 25, 8, 25");
        btnPost.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnPost.addActionListener(e -> {
            String content = txtPost.getText().trim();
            if (!content.isEmpty() || attachedImageBase64 != null) {
                
                String payload = content;
                
                // 🚀 FIX 1: Use <br> instead of \n to attach the image
                if (attachedImageBase64 != null) {
                    payload += (payload.isEmpty() ? "" : "<br>") + "[IMG]" + attachedImageBase64;
                }
                
                // 🚀 FIX 2: Sanitize all user-typed "Enters/Newlines" into <br> tags so the socket doesn't break
                payload = payload.replace("\n", "<br>");
                
                NetworkManager.getInstance().send("CREATE_POST|" + currentUsername + "|" + payload);
                
                txtPost.setText("");
                attachedImageBase64 = null;
                attachmentPreviewPanel.setVisible(false);
                
                JOptionPane.showMessageDialog(this, "Post created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                NetworkManager.getInstance().send("GET_HOME_FEED|" + currentUsername);
            }
        });

        p.add(actions, "skip 1, growx, split 2");
        p.add(btnPost, "align right");

        return p;
    }

    private void attachPhotoToPost() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);
                
                // Shrink the image so it fits nicely in the feed without crashing the socket
                int max = 400;
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
                attachedImageBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                // Show a preview of the image right inside the composer box
                attachmentPreviewPanel.removeAll();
                
                // Calculate thumbnail size for preview
                int thumbH = 80;
                int thumbW = (80 * w) / h;
                Image thumb = scaled.getScaledInstance(thumbW, thumbH, Image.SCALE_SMOOTH);
                JLabel previewLabel = new JLabel(new ImageIcon(thumb));
                previewLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B"), 1));
                
                JButton btnRemove = new JButton("❌");
                btnRemove.putClientProperty("FlatLaf.style", "buttonType: borderless; foreground: #E0245E; font: 10");
                btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btnRemove.addActionListener(ev -> {
                    attachedImageBase64 = null;
                    attachmentPreviewPanel.setVisible(false);
                });
                
                attachmentPreviewPanel.add(previewLabel, BorderLayout.WEST);
                attachmentPreviewPanel.add(btnRemove, BorderLayout.EAST);
                attachmentPreviewPanel.setVisible(true);
                attachmentPreviewPanel.revalidate();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image.");
            }
        }
    }

    private void showFeelingPicker(JButton invoker) {
        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new GridLayout(0, 1));
        popup.setBackground(CARD_BG);
        popup.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B")));
        
        String[] feelings = {"😀 feeling happy", "🥰 feeling loved", "😎 feeling cool", "🚀 feeling productive", "😴 feeling tired", "🎮 playing games"};
        for (String f : feelings) {
            JMenuItem item = new JMenuItem(f);
            item.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            item.setForeground(Color.WHITE);
            item.setBackground(CARD_BG);
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
            item.addActionListener(e -> {
                String current = txtPost.getText().trim();
                // Append the feeling to the post
                txtPost.setText((current.isEmpty() ? "" : current + "\n\n") + "— " + f);
                txtPost.requestFocus();
            });
            popup.add(item);
        }
        popup.show(invoker, 0, invoker.getHeight());
    }

    private JButton createIconButton(String iconText, String text) {
        JButton btn = new JButton("<html><span style='font-family: Segoe UI Emoji;'>" + iconText + "</span>  " + text + "</html>");
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; hoverBackground: #3A3B3C; font: bold 13; foreground: #B0B3B8; arc: 10; margin: 6, 12, 6, 12");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 🚀 THE DECODER FIX: This properly splits and renders the Image!
    private JPanel createPostCard(String authorName, String content, String timeAgo, int likes, int comments) {
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

        JButton btnMore = new JButton("•••");
        btnMore.putClientProperty("FlatLaf.style", "buttonType: borderless; foreground: #B0B3B8; font: 16");
        header.add(btnMore);

        card.add(header);

        JPanel contentPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 0", "[fill]"));
        contentPanel.setOpaque(false);

        String textContent = content;
        String imageBase64 = null;

        if (content.contains("[IMG]")) {
            int imgIndex = content.indexOf("[IMG]");
            textContent = content.substring(0, imgIndex).trim();
            imageBase64 = content.substring(imgIndex + 5);
        }

        // 🚀 FIX 3: Restore the <br> tags back to actual Newlines (\n) for the UI!
        textContent = textContent.replace("<br>", "\n");

        if (!textContent.isEmpty()) {
            JTextArea txtContent = new JTextArea(textContent);
            txtContent.setEditable(false);
            txtContent.setLineWrap(true);
            txtContent.setWrapStyleWord(true);
            txtContent.setOpaque(false);
            txtContent.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            txtContent.putClientProperty("FlatLaf.style", "foreground: #E4E6EB");
            contentPanel.add(txtContent);
        }

        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] imgBytes = Base64.getDecoder().decode(imageBase64);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
                JLabel imgLabel = new JLabel(new ImageIcon(img));
                imgLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B"), 1, true));
                contentPanel.add(imgLabel, "align left, gaptop 10"); // Left aligned to match your styling
            } catch (Exception e) {
                JLabel errorLbl = new JLabel("[Broken Image Attachment]");
                errorLbl.setForeground(Color.RED);
                contentPanel.add(errorLbl);
            }
        }
        
        card.add(contentPanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.decode("#393A3B"));
        card.add(sep, "growx, gaptop 5, gapbottom 5");

        JPanel actions = new JPanel(new GridLayout(1, 3, 10, 0));
        actions.setOpaque(false);
        
        actions.add(createActionBtn("👍 Like " + (likes > 0 ? "(" + likes + ")" : "")));
        actions.add(createActionBtn("💬 Comment " + (comments > 0 ? "(" + comments + ")" : "")));
        actions.add(createActionBtn("🔁 Share"));
        card.add(actions, "growx");

        return card;
    }

    private JButton createActionBtn(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty("FlatLaf.style", "buttonType: borderless; hoverBackground: #3A3B3C; font: bold 13; foreground: #B0B3B8; arc: 10; margin: 8, 0, 8, 0");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public void onMessageReceived(String incomingMessage) {
        if (incomingMessage.startsWith("HOME_FEED|")) {
            String data = incomingMessage.length() > 10 ? incomingMessage.substring(10) : "";
            
            SwingUtilities.invokeLater(() -> {
                feedContainer.removeAll();
                
                if (data.isEmpty()) {
                    JLabel noPosts = new JLabel("No posts to show right now.");
                    noPosts.setForeground(Color.GRAY);
                    noPosts.setHorizontalAlignment(SwingConstants.CENTER);
                    feedContainer.add(noPosts, "align center, gaptop 20");
                } else {
                    String[] posts = data.split("~");
                    for (String p : posts) {
                        if (!p.isEmpty()) {
                            String[] parts = p.split("\\|");
                            if (parts.length >= 5) {
                                feedContainer.add(createPostCard(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), Integer.parseInt(parts[4])), "growx, gaptop 15");
                            }
                        }
                    }
                }
                feedContainer.revalidate();
                feedContainer.repaint();
            });
        }
    }
    
    public void refreshFeed() {
        NetworkManager.getInstance().send("GET_HOME_FEED|" + currentUsername);
    }
}