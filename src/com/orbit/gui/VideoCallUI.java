package com.orbit.gui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.orbit.network.NetworkListener;
import com.orbit.network.NetworkManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.*;

public class VideoCallUI extends JFrame implements NetworkListener {
    private Webcam webcam;
    private String remoteUser;
    private String myUsername;
    
    private JLabel lblRemoteVideo; // Where the other person appears
    private Timer frameSender;      // Sends your frames every 100ms

    public VideoCallUI(String remoteUser, String myUsername, boolean isRequester) {
        this.remoteUser = remoteUser;
        this.myUsername = myUsername;

        setTitle("Orbit Video: " + remoteUser);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        NetworkManager.getInstance().addListener(this);
        initComponents();

        // 🚀 THE MAGIC: Start sending your face to the other person!
        startStreaming();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                endCall();
            }
        });
    }

    private void initComponents() {
        JLayeredPane layeredPane = new JLayeredPane();
        add(layeredPane, BorderLayout.CENTER);

        // 1. REMOTE VIDEO (The Big Background)
        lblRemoteVideo = new JLabel("Waiting for " + remoteUser + "...", SwingConstants.CENTER);
        lblRemoteVideo.setForeground(Color.WHITE);
        lblRemoteVideo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblRemoteVideo.setBounds(0, 0, 1000, 630);
        layeredPane.add(lblRemoteVideo, JLayeredPane.DEFAULT_LAYER);

        // 2. LOCAL VIDEO (The small Picture-in-Picture)
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(new Dimension(640, 480));
            WebcamPanel localPanel = new WebcamPanel(webcam);
            localPanel.setFPSDisplayed(false);
            localPanel.setMirrored(true);
            // Put it in the bottom right corner
            localPanel.setBounds(760, 450, 200, 150);
            localPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            layeredPane.add(localPanel, JLayeredPane.PALETTE_LAYER);
        }

        // 3. CONTROLS
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controls.setOpaque(false);
        JButton btnEnd = new JButton("📞 End Call");
        btnEnd.setBackground(Color.RED);
        btnEnd.setForeground(Color.WHITE);
        btnEnd.addActionListener(e -> endCall());
        controls.add(btnEnd);
        add(controls, BorderLayout.SOUTH);
    }

    private void startStreaming() {
        // Capture and send a frame every 150ms (approx 7 FPS for stability)
        frameSender = new Timer(150, e -> {
            if (webcam != null && webcam.isOpen()) {
                try {
                    BufferedImage img = webcam.getImage();
                    // Shrink it for the network (Bandwidth is key!)
                    Image scaled = img.getScaledInstance(320, 240, Image.SCALE_FAST);
                    BufferedImage bufferedScaled = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                    bufferedScaled.getGraphics().drawImage(scaled, 0, 0, null);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedScaled, "jpg", baos);
                    String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());

                    // Send to server: VIDEO_FRAME | target | sender | base64
                    NetworkManager.getInstance().send("VIDEO_FRAME|" + remoteUser + "|" + myUsername + "|" + base64);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        frameSender.start();
    }

    @Override
    public void onMessageReceived(String incomingMessage) {
        // Listen for frames coming from the remote user
        if (incomingMessage.startsWith("VF|")) {
            String[] parts = incomingMessage.split("\\|");
            if (parts.length >= 3 && parts[1].equals(remoteUser)) {
                updateRemoteView(parts[2]);
            }
        }
    }

    private void updateRemoteView(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            
            // Scale it back up to fit the window
            ImageIcon icon = new ImageIcon(img.getScaledInstance(lblRemoteVideo.getWidth(), lblRemoteVideo.getHeight(), Image.SCALE_SMOOTH));
            
            SwingUtilities.invokeLater(() -> {
                lblRemoteVideo.setText("");
                lblRemoteVideo.setIcon(icon);
            });
        } catch (Exception e) {
            System.err.println("Frame decode error");
        }
    }

    private void endCall() {
    if (frameSender != null) frameSender.stop();
    if (webcam != null) webcam.close();
    
    // 🚀 This is the line that uses your new method!
    NetworkManager.getInstance().removeListener(this); 
    
    this.dispose();
}
}