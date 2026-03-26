package com.orbit.main;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf; // Import the Look & Feel
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.orbit.gui.LoginFormFrame;
import javax.swing.UIManager;           // To handle UI customization
import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.awt.Color;

import javax.swing.JFrame;

public class OrbitLauncherMain {

    public static void setAppIcon(javax.swing.JFrame frame) {
        try {
            FlatSVGIcon svgIcon = new FlatSVGIcon("resources/images/orbit_logo.svg", 16, 16);
            frame.setIconImage(svgIcon.getImage());
            
        } catch (Exception e) {
            System.err.println("Could not load app icon.");
        }
    }
    
    
    
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Component.accentColor", new Color(100, 149, 237)); // Cornflower Blue
            UIManager.put("Button.arc", 15); // Global rounded buttons
            UIManager.put("Component.arc", 15); // Global rounded corners for fields
            
            
            
            
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf. Falling back to default.");
        }

        java.awt.EventQueue.invokeLater(() -> {
            LoginFormFrame login = new LoginFormFrame();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
    
    
}