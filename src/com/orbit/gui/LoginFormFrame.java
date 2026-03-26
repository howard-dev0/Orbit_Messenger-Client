/* @author eduho */
package com.orbit.gui;

import com.formdev.flatlaf.FlatLightLaf;
import com.orbit.main.OrbitLauncherMain;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import javax.swing.Timer;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.UIManager;

public class LoginFormFrame extends javax.swing.JFrame {
    private boolean isAnimating = false;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFormFrame.class.getName());

    public LoginFormFrame() {
        initComponents();
        // 2. Set the Title Bar text
        this.setTitle("Orbit Messenger");

        // 3. Set the SVG Application Icon (Requires FlatLaf Extras)
        OrbitLauncherMain.setAppIcon(this);

        // 4. ADD the login panel to your wrapper (This stops the shrinking!)
        // GridBagLayout will automatically center it.
        wrapperPanel.add(loginPanel1);

        // 5. Force the window size so it doesn't default to "tiny"
        this.setSize(1200, 800);

        // 6. Center the window on your screen
        this.setLocationRelativeTo(null);

    }

    public JPanel getLoginPanel() {
        return loginPanel1; // This is the name Matisse gave the dragged component
    }

    public JPanel getRegisterPanel() {
        return registerPanel1; // This is the name Matisse gave the dragged component
    }

    
    
    // meow tools
    public void switchAndAnimate(JPanel targetPanel) {
    // 1. Prevent overlapping animations
    if (isAnimating || wrapperPanel.getComponentCount() == 0) return;

    JPanel currentPanel = (JPanel) wrapperPanel.getComponent(0);
    if (currentPanel == targetPanel) return;

    isAnimating = true;

    // 2. Take a "screenshot" of the Current Panel
    int currentWidth = Math.max(1, currentPanel.getWidth());
    int currentHeight = Math.max(1, currentPanel.getHeight());
    BufferedImage currentImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2dCurrent = currentImage.createGraphics();
    currentPanel.paint(g2dCurrent);
    g2dCurrent.dispose();

    // 3. Prepare the Target Panel and take its "screenshot"
    int targetWidth = Math.max(1, targetPanel.getPreferredSize().width);
    int targetHeight = Math.max(1, targetPanel.getPreferredSize().height);
    targetPanel.setSize(targetWidth, targetHeight);
    targetPanel.doLayout(); // Forces the fields/buttons into their correct positions

    BufferedImage targetImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2dTarget = targetImage.createGraphics();
    targetPanel.paint(g2dTarget);
    g2dTarget.dispose();

    // 4. Create an array to hold our alpha value (so the Timer can update it)
    final float[] alphaHolder = {0.0f};

    // 5. Create a temporary Fading Panel to draw the images
    JPanel fadePanel = new JPanel() {
        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2d = (Graphics2D) g.create();

            // Turn on anti-aliasing for smooth image scaling
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            float alpha = alphaHolder[0];

            // Calculate exact center coordinates for the images
            int curX = (getWidth() - currentWidth) / 2;
            int curY = (getHeight() - currentHeight) / 2;
            int tarX = (getWidth() - targetWidth) / 2;
            int tarY = (getHeight() - targetHeight) / 2;

            // Draw the OLD panel fading OUT (1.0 to 0.0)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - alpha));
            g2d.drawImage(currentImage, curX, curY, null);

            // Draw the NEW panel fading IN (0.0 to 1.0)
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(targetImage, tarX, tarY, null);

            g2d.dispose();
        }
    };
    
    // CRITICAL: Make the fading panel transparent so the SVG background shows through!
    fadePanel.setOpaque(false); 

    // 6. Swap the real panels out for the temporary fade panel
    wrapperPanel.removeAll();
    wrapperPanel.setLayout(new java.awt.BorderLayout());
    wrapperPanel.add(fadePanel, java.awt.BorderLayout.CENTER);
    wrapperPanel.revalidate();
    wrapperPanel.repaint();

    // 7. Start the Crossfade Animation
    Timer timer = new Timer(15, new ActionListener() {
        float progress = 0.0f;

        @Override
        public void actionPerformed(ActionEvent e) {
            progress += 0.85f; // Adjust this to make the fade faster/slower

            if (progress >= 1.0f) {
                ((Timer) e.getSource()).stop();
                
                // Animation finished: Put the real, clickable target panel back
                wrapperPanel.removeAll();
                wrapperPanel.setLayout(new java.awt.GridBagLayout());
                wrapperPanel.add(targetPanel);
                
                wrapperPanel.revalidate();
                wrapperPanel.repaint();
                
                isAnimating = false; // Unlock buttons
            } else {
                // Smooth Sine Easing (Starts slow, accelerates, slows down at the end)
                float ease = (float) (0.5 * (1 - Math.cos(Math.PI * progress)));
                
                // Lock value between 0.0 and 1.0 to prevent AlphaComposite crashes
                alphaHolder[0] = Math.max(0.0f, Math.min(1.0f, ease));
                
                // Trigger a visual redraw of the fading images
                fadePanel.repaint(); 
            }
        }
    });
    timer.start();
}
    
    
    
    
    
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginPanel1 = new com.orbit.gui.loginPanel();
        registerPanel1 = new com.orbit.gui.registerPanel();
        bgPanel = new SVGBackgroundPanel("resources/images/orbit_bg.svg");
        wrapperPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bgPanel.setLayout(new java.awt.BorderLayout());

        wrapperPanel.setOpaque(false);
        wrapperPanel.setLayout(new java.awt.GridBagLayout());
        bgPanel.add(wrapperPanel, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bgPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

        /* Create and display the form */
        System.out.println(UIManager.getLookAndFeel());
        java.awt.EventQueue.invokeLater(() -> new LoginFormFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bgPanel;
    private com.orbit.gui.loginPanel loginPanel1;
    private com.orbit.gui.registerPanel registerPanel1;
    private javax.swing.JPanel wrapperPanel;
    // End of variables declaration//GEN-END:variables
}
