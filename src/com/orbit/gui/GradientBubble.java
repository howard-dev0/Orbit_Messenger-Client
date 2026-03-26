package com.orbit.gui;

import java.awt.*;
import javax.swing.*;

public class GradientBubble extends JPanel {
    private String text;
    private boolean isMe;

    public GradientBubble(String text, boolean isMe) {
        this.text = text;
        this.isMe = isMe;
        setLayout(new BorderLayout());
        setOpaque(false);
        
        String color = isMe ? "white" : "#E4E6EB";
        JLabel label = new JLabel("<html><div style='max-width: 250px; padding: 10px 14px; color: " + color + "; font-family: Segoe UI, sans-serif; font-size: 14px;'>" + text + "</div></html>");
        add(label, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (isMe) {
            GradientPaint gp = new GradientPaint(0, 0, Color.decode("#00B2FF"), getWidth(), getHeight(), Color.decode("#006AFF"));
            g2.setPaint(gp);
        } else {
            g2.setColor(Color.decode("#3E4042"));
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35); 
        g2.dispose();
    }
}