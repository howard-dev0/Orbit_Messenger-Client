/* @author eduho */
package com.orbit.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Graphics;
import javax.swing.JPanel;

public class SVGBackgroundPanel extends JPanel {
    private FlatSVGIcon backgroundIcon;

    // Default constructor for the NetBeans GUI Builder
    public SVGBackgroundPanel(String link) {
        // Points to the logo we saw in your tree earlier
        backgroundIcon = new FlatSVGIcon(link);
    }

    @Override
    protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if (backgroundIcon != null) {
        backgroundIcon.derive(getWidth(), getHeight())
                      .paintIcon(this, g, 0, 0);
    }
}
}