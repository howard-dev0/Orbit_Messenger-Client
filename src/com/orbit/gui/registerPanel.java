/* @author eduho */
package com.orbit.gui;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.security.MessageDigest;

public class registerPanel extends javax.swing.JPanel {

    public registerPanel() {
        initComponents();
        initFormListeners();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        nameResponseLabel = new javax.swing.JLabel();
        emailResponseLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        usernameResponseLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();
        confirmpassField = new javax.swing.JPasswordField();
        passwordResponseLabel = new javax.swing.JLabel();
        confirmpassResponseLabel = new javax.swing.JLabel();
        createaccountBtn = new javax.swing.JButton();
        toregisterBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(57, 58, 65));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Create an account");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Email");

        emailField.setBackground(new java.awt.Color(53, 53, 60));
        emailField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        emailField.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Display Name");

        nameField.setBackground(new java.awt.Color(53, 53, 60));
        nameField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        nameField.setForeground(new java.awt.Color(255, 255, 255));

        nameResponseLabel.setForeground(new java.awt.Color(255, 255, 255));
        nameResponseLabel.setText("How others see you. You can use special characters and emoji.");

        emailResponseLabel.setBackground(new java.awt.Color(0, 0, 0));
        emailResponseLabel.setForeground(new java.awt.Color(255, 160, 155));
        emailResponseLabel.setText(" ");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Username");

        usernameField.setBackground(new java.awt.Color(53, 53, 60));
        usernameField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N

        usernameResponseLabel.setForeground(new java.awt.Color(255, 255, 255));
        usernameResponseLabel.setText("Please only use numbers, letters, underscores _, or periods.");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Password");

        passwordField.setBackground(new java.awt.Color(53, 53, 60));
        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N
        passwordField.addActionListener(this::passwordFieldActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Confirm Password");

        confirmpassField.setBackground(new java.awt.Color(53, 53, 60));
        confirmpassField.setFont(new java.awt.Font("Segoe UI", 0, 22)); // NOI18N

        passwordResponseLabel.setForeground(new java.awt.Color(255, 160, 155));
        passwordResponseLabel.setText(" ");

        confirmpassResponseLabel.setForeground(new java.awt.Color(255, 160, 155));
        confirmpassResponseLabel.setText(" ");

        createaccountBtn.setBackground(new java.awt.Color(88, 101, 242));
        createaccountBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        createaccountBtn.setForeground(new java.awt.Color(255, 255, 255));
        createaccountBtn.setText("Create Account");

        toregisterBtn.setForeground(new java.awt.Color(148, 168, 255));
        toregisterBtn.setText("Already have an account? Login");
        toregisterBtn.setBorderPainted(false);
        toregisterBtn.setContentAreaFilled(false);
        toregisterBtn.addActionListener(this::toregisterBtnActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(128, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(128, 128, 128))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(emailField)
                            .addComponent(nameField)
                            .addComponent(nameResponseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                            .addComponent(emailResponseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usernameField)
                            .addComponent(usernameResponseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(passwordField)
                            .addComponent(confirmpassField)
                            .addComponent(passwordResponseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(confirmpassResponseLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(createaccountBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(toregisterBtn)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(emailResponseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(nameResponseLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(usernameResponseLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(passwordResponseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(confirmpassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(confirmpassResponseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(createaccountBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(toregisterBtn)
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void initFormListeners() {
        Color errColor = Color.decode("#ffa09b");

        // 2. NAME FIELD: Restrict to 32 characters maximum using a DocumentFilter
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (fb.getDocument().getLength() + text.length() - length <= 32) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= 32) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        });

        DocumentListener activeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateFields();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateFields();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        };

        emailField.getDocument().addDocumentListener(activeListener);
        usernameField.getDocument().addDocumentListener(activeListener);
        passwordField.getDocument().addDocumentListener(activeListener);
        confirmpassField.getDocument().addDocumentListener(activeListener);
        nameField.getDocument().addDocumentListener(activeListener);
        
        // 4. CREATE ACCOUNT BUTTON
        createaccountBtn.addActionListener(e -> {
            boolean isValid = true;

            if (emailField.getText().trim().isEmpty()) {
                emailResponseLabel.setText("!Required");
                emailResponseLabel.setForeground(errColor);
                isValid = false;
            }
            if (nameField.getText().trim().isEmpty()) {
                nameResponseLabel.setText("!Required");
                nameResponseLabel.setForeground(errColor);
                isValid = false;
            }
            if (usernameField.getText().trim().isEmpty()) {
                usernameResponseLabel.setText("!Required");
                usernameResponseLabel.setForeground(errColor);
                isValid = false;
            }
            if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                passwordResponseLabel.setText("!Required");
                passwordResponseLabel.setForeground(errColor);
                isValid = false;
            }
            if (String.valueOf(confirmpassField.getPassword()).isEmpty()) {
                confirmpassResponseLabel.setText("!Required");
                confirmpassResponseLabel.setForeground(errColor);
                isValid = false;
            }

            // If everything is perfectly valid, hash and register!
            if (isValid && emailResponseLabel.getText().equals("")
                    && usernameResponseLabel.getText().equals("Username is available. Nice!")
                    && confirmpassResponseLabel.getText().equals("")) {

                // 1. Get the raw password
                String rawPassword = String.valueOf(passwordField.getPassword());

                // 2. HASH IT! 
                String securePassword = hashPassword(rawPassword);

                // 3. Send the HASHED password to the server
                boolean success = registerUserToServer(
                        nameField.getText().trim(),
                        emailField.getText().trim(),
                        usernameField.getText().trim(),
                        securePassword 
                );

                if (success) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    toregisterBtn.doClick(); // Go to login
                } else {
                    JOptionPane.showMessageDialog(this, "Server Error. Could not register.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // 5. The "Brain" of the active validation
    private void validateFields() {
        Color errColor = Color.decode("#ffa09b");
        Color succColor = Color.decode("#6fab82");

        // --- EMAIL VALIDATION ---
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailResponseLabel.setText("");
        } else if (!email.matches("^[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            emailResponseLabel.setText("Invalid email");
            emailResponseLabel.setForeground(errColor);
        } else if (checkEmailInDB(email)) {
            emailResponseLabel.setText("Email is already registered.");
            emailResponseLabel.setForeground(errColor);
        } else {
            emailResponseLabel.setText("");
        }
        
        String namef = nameField.getText().trim();
        if (!namef.isEmpty()){
            nameResponseLabel.setText("How others see you. You can use special characters and emoji.");
            nameResponseLabel.setForeground(Color.WHITE);
        }
        
        String passwordf = String.valueOf(passwordField.getPassword()).trim();
        if (!passwordf.isEmpty()){
            passwordResponseLabel.setText("");
            passwordResponseLabel.setForeground(Color.WHITE);
        }

        // --- USERNAME VALIDATION ---
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            usernameResponseLabel.setText("Please only use numbers, letters, underscores _, or periods.");
            usernameResponseLabel.setForeground(Color.WHITE);
        } else if (checkUsernameInDB(username)) {
            usernameResponseLabel.setText("Username is already taken.");
            usernameResponseLabel.setForeground(errColor);
        } else if (!username.matches("^[a-zA-Z0-9_.]+$")) {
            usernameResponseLabel.setText("Please only use numbers, letters, underscores _, or periods.");
            usernameResponseLabel.setForeground(errColor);
        } else {
            usernameResponseLabel.setText("Username is available. Nice!");
            usernameResponseLabel.setForeground(succColor);
        }

        // --- PASSWORD MATCHING ---
        String pass = String.valueOf(passwordField.getPassword());
        String confirm = String.valueOf(confirmpassField.getPassword());

        if (confirm.isEmpty()) {
            confirmpassResponseLabel.setText("");
        } else if (!pass.equals(confirm)) {
            confirmpassResponseLabel.setText("Passwords do not match");
            confirmpassResponseLabel.setForeground(errColor);
        } else {
            confirmpassResponseLabel.setText("");
        }
    }

    // ==================================================
    // NETWORK METHODS (Client asks the Server)
    // ==================================================
    private boolean checkEmailInDB(String email) {
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 8080);
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            out.println("CHECK_EMAIL|" + email);
            String response = in.readLine();
            socket.close();

            return "EXISTS".equals(response);
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
            return false;
        }
    }

    private boolean checkUsernameInDB(String username) {
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 8080);
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            out.println("CHECK_USERNAME|" + username);
            String response = in.readLine();
            socket.close();

            return "EXISTS".equals(response);
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
            return false;
        }
    }

    private boolean registerUserToServer(String name, String email, String username, String password) {
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 8080);
            java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

            // Send the registration payload
            out.println("REGISTER|" + name + "|" + email + "|" + username + "|" + password);
            String response = in.readLine();
            socket.close();

            return "SUCCESS".equals(response);
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
            return false;
        }
    }

    // ==================================================
    // UTILITY & ACTIONS
    // ==================================================
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

    private void clearFields(){
        emailField.setText("");
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmpassField.setText("");
        
        emailResponseLabel.setText("");
        nameResponseLabel.setText("How others see you. You can use special characters and emoji.");
        nameResponseLabel.setForeground(Color.WHITE);
        usernameResponseLabel.setText("Please only use numbers, letters, underscores _, or periods.");
        usernameResponseLabel.setForeground(Color.WHITE);
        passwordResponseLabel.setText("");
        confirmpassResponseLabel.setText("");
    }
    
    private void toregisterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toregisterBtnActionPerformed
        LoginFormFrame parentFrame = (LoginFormFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.switchAndAnimate(parentFrame.getLoginPanel());
            clearFields();
        }
    }//GEN-LAST:event_toregisterBtnActionPerformed

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_passwordFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField confirmpassField;
    private javax.swing.JLabel confirmpassResponseLabel;
    private javax.swing.JButton createaccountBtn;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailResponseLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameResponseLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordResponseLabel;
    private javax.swing.JButton toregisterBtn;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameResponseLabel;
    // End of variables declaration//GEN-END:variables
}