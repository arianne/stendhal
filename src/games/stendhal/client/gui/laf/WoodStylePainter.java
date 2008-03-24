package games.stendhal.client.gui.laf;

import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;

/**
 * Painter class for our Wood look and feel.
 *
 * @author Martin Fuchs
 */
public class WoodStylePainter extends SynthPainter {

	public static final Color borderColor = new Color(0.6f, 0.5f, 0.2f);

	private BufferedImage img;

	public WoodStylePainter(String textureName) {
		try {
	        img = ImageIO.read(SpriteStore.get().getResourceURL(textureName));
        } catch(Exception e) {
	        e.printStackTrace();
	        img = null;
        }
	}

	/**
	 * Completely fill an area with the given image without scaling (fast implementation).
	 * @param g
	 * @param img
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	static void drawTiles(Graphics g, BufferedImage img, int x1, int y1, int x2, int y2) {
		int w = img.getWidth();
		int h = img.getHeight();

		for(int y = y1; y<y2; y+=h) {
			for(int x = x1; x<x2; x+=w) {
				g.drawImage(img, x, y, w, h, null, null);
			}
		}
    }

	private void paint(Graphics g, int x, int y, int w, int h) {
		drawTiles(g, img, x, y, x+w, y+h);
	}


	@Override
    public void paintInternalFrameTitlePaneBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
		paint(g, x, y, w, h);
    }

	@Override
    public void paintInternalFrameTitlePaneBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
//		g.setColor(borderColor);
//		g.fillRect(x, y, w, h);
    }

	@Override
    public void paintInternalFrameBorder(SynthContext context, Graphics g, int x, int y, int w, int h) {
	}


	@Override
    public void paintPanelBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
		paint(g, x, y, w, h);
	}


	@Override
    public void paintTextFieldBackground(SynthContext context, Graphics g, int x, int y, int w, int h) {
		paint(g, x, y, w, h);
    }

}
