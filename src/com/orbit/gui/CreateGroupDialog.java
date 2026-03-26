package com.orbit.gui;

import com.orbit.network.NetworkManager;
import com.orbit.security.CryptoUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CreateGroupDialog extends JDialog {

    private String currentUsername;
    private List<String> selectedFriends = new ArrayList<>();
    
    // We pass in the list of friends so the user can check boxes next to them
    public CreateGroupDialog(Frame parent, String currentUsername, List<String> allFriends) {
        super(parent, "Create New Group", true);
        this.currentUsername = currentUsername;

        setLayout(new MigLayout("wrap 1, fillx, insets 20", "[fill]", "[]15[]10[grow, fill]15[]"));
        getContentPane().setBackground(Color.decode("#242526"));

        // 1. Header
        JLabel lblTitle = new JLabel("Create Encrypted Group");
        lblTitle.putClientProperty("FlatLaf.style", "font: bold 18; foreground: #E4E6EB");
        add(lblTitle);

        // 2. Group Name Input
        JTextField txtGroupName = new JTextField();
        txtGroupName.putClientProperty("JTextField.placeholderText", "Enter Group Name...");
        txtGroupName.putClientProperty("FlatLaf.style", "arc: 10; background: #3A3B3C; foreground: #E4E6EB; borderWidth: 0; font: 14");
        txtGroupName.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(txtGroupName);

        // 3. Friends List (Scrollable Checkboxes)
        JPanel friendsPanel = new JPanel(new MigLayout("wrap 1, fillx", "[fill]"));
        friendsPanel.setBackground(Color.decode("#18191A"));
        
        for (String friendUsername : allFriends) {
            JCheckBox chk = new JCheckBox(friendUsername);
            chk.putClientProperty("FlatLaf.style", "foreground: #E4E6EB; font: 14");
            chk.setBackground(Color.decode("#18191A"));
            chk.addActionListener(e -> {
                if (chk.isSelected()) selectedFriends.add(friendUsername);
                else selectedFriends.remove(friendUsername);
            });
            friendsPanel.add(chk);
        }

        JScrollPane scroll = new JScrollPane(friendsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#393A3B")));
        scroll.setPreferredSize(new Dimension(300, 200));
        add(scroll);

        // 4. Create Button
        JButton btnCreate = new JButton("Create & Generate Keys");
        btnCreate.putClientProperty("FlatLaf.style", "arc: 10; background: #0084FF; foreground: #FFFFFF; font: bold 14; borderWidth: 0");
        btnCreate.setBorder(new EmptyBorder(10, 0, 10, 0));
        
btnCreate.addActionListener(e -> {
            String groupName = txtGroupName.getText().trim();
            if (groupName.isEmpty() || selectedFriends.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name and select at least one friend.");
                return;
            }

            // 1. Generate the master AES key for this group
            String groupAESKey = CryptoUtil.generateRandomAESKey();
            
            // 2. ADD THE CREATOR to the list so they get an encrypted key too!
            selectedFriends.add(currentUsername); 

            StringBuilder friendsStr = new StringBuilder();
            StringBuilder keysStr = new StringBuilder();

            // 3. Encrypt the key individually for EVERY member (including the creator)
            for (int i = 0; i < selectedFriends.size(); i++) {
                String member = selectedFriends.get(i);
                friendsStr.append(member).append(i < selectedFriends.size() - 1 ? "," : "");

                // Use the deterministic DM key to lock the Group Key
                String dmKey = getDMKey(currentUsername, member);
                String encryptedGroupKey = CryptoUtil.encrypt(groupAESKey, dmKey);
                keysStr.append(encryptedGroupKey).append(i < selectedFriends.size() - 1 ? "," : "");
            }

            // 🚀 SECURE SEND: Notice we are NO LONGER sending the plain groupAESKey!
            NetworkManager.getInstance().send("CREATE_GROUP|" + groupName + "|" + currentUsername + "|" + friendsStr.toString() + "|" + keysStr.toString());
            
            dispose();
        });
        
        add(btnCreate);

        pack();
        setLocationRelativeTo(parent);
    }

    // Helper to generate the DM key
    private String getDMKey(String user1, String user2) {
        String u1 = user1.toLowerCase();
        String u2 = user2.toLowerCase();
        String base = (u1.compareTo(u2) < 0) ? u1 + "_" + u2 : u2 + "_" + u1;
        String baseSecret = "Orbit_" + base + "_2026";
        return baseSecret.length() > 16 ? baseSecret.substring(0, 16) : String.format("%-16s", baseSecret).replace(' ', 'X');
    }
}