package com.orbit.gui;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.orbit.network.NetworkManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VideoCallUI extends JFrame {
    private Webcam webcam;
    private String remoteUser;
    private String myUsername;

    public VideoCallUI(String remoteUser, String myUsername, boolean isRequester) {
        this.remoteUser = remoteUser;
        this.myUsername = myUsername;

        setTitle("Orbit Video - " + remoteUser);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.decode("#18191A"));
        setLayout(new BorderLayout());

        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                endCall();
            }
        });
    }

    private void initComponents() {
        // Main Video Feed (Center)
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(new Dimension(640, 480));
            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setFillArea(true);
            add(panel, BorderLayout.CENTER);
        } else {
            add(new JLabel("No Webcam Detected", SwingConstants.CENTER), BorderLayout.CENTER);
        }

        // Bottom Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controls.setOpaque(false);

        JButton btnEnd = new JButton("📞 End Call");
        btnEnd.putClientProperty("FlatLaf.style", "background: #E0245E; foreground: #FFF; arc: 999; font: bold 14; margin: 10, 20, 10, 20");
        btnEnd.addActionListener(e -> endCall());

        controls.add(btnEnd);
        add(controls, BorderLayout.SOUTH);
    }

    private void endCall() {
        if (webcam != null) webcam.close();
        this.dispose();
    }
}