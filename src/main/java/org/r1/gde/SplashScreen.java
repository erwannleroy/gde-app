package org.r1.gde;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
	Image splashScreen;
	ImageIcon imageIcon;

	public SplashScreen() {
		splashScreen = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("splash.png"));
		// Create ImageIcon from Image
		imageIcon = new ImageIcon(splashScreen);
		// Set JWindow size from image size
		setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
		// Get current screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Get x coordinate on screen for make JWindow locate at center
		int x = (screenSize.width - getSize().width) / 2;
		// Get y coordinate on screen for make JWindow locate at center
		int y = (screenSize.height - getSize().height) / 2;
		// Set new location for JWindow
		setLocation(x, y);
	}

	// Paint image onto JWindow
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(splashScreen, 0, 0, this);
	}

}