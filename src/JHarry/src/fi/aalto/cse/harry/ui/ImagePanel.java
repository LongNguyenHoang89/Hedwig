package fi.aalto.cse.harry.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Long
 * @see http
 *      ://stackoverflow.com/questions/299495/how-to-add-an-image-to-a-jpanel
 */

public class ImagePanel extends JPanel {

    private volatile BufferedImage image;

    public ImagePanel() {
	super();
    }
    
    public void UpdateImage(BufferedImage img){
	this.image = img;
	repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.drawImage(image, 0, 0, null);
    }
}
