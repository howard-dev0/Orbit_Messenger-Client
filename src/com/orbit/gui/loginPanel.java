/* @author eduho */
package com.orbit.gui;

import java.awt.Color;
import java.security.MessageDigest;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class loginPanel extends javax.swing.JPanel {

    public loginPanel() {
        initComponents();
        initFormListeners();
    }

    private void initFormListeners() {
        Color errColor = Color.decode("#ffa09b");

        // 1. REMEMBER ME LOAD: Check if we saved a username from last time
        Preferences prefs = Preferences.userNodeForPackage(loginPanel.class);
        String savedUser = prefs.get("orbit_username", "");
        if (!savedUser.isEmpty()) {
            usernameField.setText(savedUser);
            rememberBtn.setSelected(true); // Assuming rememberBtn is a JRadioButton or JCheckBox
        }

        // 2. ACTIVE TYPING LISTENER: Clear errors when user types
        DocumentListener typingListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearErrors();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearErrors();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };

        usernameField.getDocument().addDocumentListener(typingListener);
        passwordField.getDocument().addDocumentListener(typingListener);

        // 3. LOGIN BUTTON LOGIC
        loginBtn.addActionListener(e -> {
            boolean isValid = true;
            String username = usernameField.getText().trim();
            String password = String.valueOf(passwordField.getPassword());

            // Check for empty fields
            if (username.isEmpty()) {
                fieldResponse1.setText("Please fill out this field");
                fieldResponse1.setForeground(errColor);
                isValid = false;
            }
            if (password.isEmpty()) {
                fieldResponse2.setText("Please fill out this field");
                fieldResponse2.setForeground(errColor);
                isValid = false;
            }

            // If filled out, talk to the Orbit Server
            if (isValid) {
                String securePassword = hashPassword(password);
                String serverResponse = authenticateToServer(username, securePassword);

                if (serverResponse.startsWith("SUCCESS")) {
                    // Extract the Display Name sent back from the server
                    String[] parts = serverResponse.split("\\|");
                    String displayName = parts[1];

                    // Handle "Remember Me"
                    if (rememberBtn.isSelected()) {
                        prefs.put("orbit_username", username);
                    } else {
                        prefs.remove("orbit_username");
                    }

                    System.out.println("Login Success! Welcome " + displayName);

                    ChatUI chat = new ChatUI(username, displayName);
                    chat.setVisible(true);
                    SwingUtilities.getWindowAncestor(this).dispose();

                } else {
                    // Server rejected the login
                    fieldResponse1.setText("Login or password is invalid.");
                    fieldResponse1.setForeground(errColor);
                    fieldResponse2.setText("Login or password is invalid.");
                    fieldResponse2.setForeground(errColor);
                }
            }
        });
    }

    private void clearErrors() {
        fieldResponse1.setText(" ");
        fieldResponse2.setText(" ");
    }

    // ==================================================
    // NETWORK & UTILITY
    // ==================================================
    private String authenticateToServer(String username, String hashedPassword) {
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 8080);
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            // Ask server to verify credentials
            out.println("LOGIN|" + username + "|" + hashedPassword);
            String response = in.readLine();
            socket.close();

            return response; // Expected: "SUCCESS|FullName" or "FAILED"
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
            return "ERROR";
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        rememberBtn = new javax.swing.JCheckBox();
        loginBtn = new javax.swing.JButton();
        registerBtn = new javax.swing.JButton();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        fieldResponse1 = new javax.swing.JLabel();
        fieldResponse2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(57, 58, 65));
        setMaximumSize(new java.awt.Dimension(410, 450));
        setMinimumSize(new java.awt.Dimension(410, 450));
        setPreferredSize(new java.awt.Dimension(410, 450));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Welcome back!");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("We're so excited to see you again!");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Username");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Password");

        rememberBtn.setForeground(new java.awt.Color(255, 255, 255));
        rememberBtn.setText("Remember me");
        rememberBtn.addActionListener(this::rememberBtnActionPerformed);

        loginBtn.setBackground(new java.awt.Color(88, 101, 242));
        loginBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        loginBtn.setForeground(new java.awt.Color(255, 255, 255));
        loginBtn.setText("Log In");
        loginBtn.addActionListener(this::loginBtnActionPerformed);

        registerBtn.setForeground(new java.awt.Color(148, 168, 255));
        registerBtn.setText("Need an account? Register");
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        registerBtn.addActionListener(this::registerBtnActionPerformed);

        usernameField.setBackground(new java.awt.Color(53, 53, 60));
        usernameField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        usernameField.setForeground(new java.awt.Color(255, 255, 255));

        passwordField.setBackground(new java.awt.Color(53, 53, 60));
        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        passwordField.setForeground(new java.awt.Color(255, 255, 255));

        fieldResponse1.setForeground(new java.awt.Color(255, 160, 155));
        fieldResponse1.setText(" ");

        fieldResponse2.setForeground(new java.awt.Color(255, 160, 155));
        fieldResponse2.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(139, 139, 139))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel3))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(registerBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(rememberBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(fieldResponse2, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(usernameField, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                                .addComponent(fieldResponse1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(28, 28, 28))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(fieldResponse1)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(fieldResponse2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rememberBtn)
                .addGap(30, 30, 30)
                .addComponent(loginBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(registerBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void rememberBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rememberBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rememberBtnActionPerformed

    private void loginBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loginBtnActionPerformed

    private void registerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerBtnActionPerformed
        LoginFormFrame parentFrame = (LoginFormFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
        parentFrame.switchAndAnimate(parentFrame.getRegisterPanel());
    }//GEN-LAST:event_registerBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fieldResponse1;
    private javax.swing.JLabel fieldResponse2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton loginBtn;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton registerBtn;
    private javax.swing.JCheckBox rememberBtn;
    private javax.swing.JTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
