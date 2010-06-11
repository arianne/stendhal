package games.stendhal.client.gui.styled;

import games.stendhal.client.sprite.Sprite;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

/**
 * A <code>Border</code> that draws raised or lowered borders
 * based on a template image.
 */
public class PixmapBorder implements Border {
	/** Drawing width of the borders */
	private static final int WIDTH = 2;
	private static final Insets insets = new Insets(WIDTH, WIDTH, WIDTH, WIDTH);
	
	/** Image for painting the top and left borders */
	private final Image topLeftImage;
	/** Image for painting the bottom and right borders */
	private final Image bottomRightImage;
	
	private final int imageWidth, imageHeight;
	
	/**
	 * Create a new <code>PixmapBorder</code>.
	 * 
	 * @param template {@link Sprite} to be used as the base image for drawing
	 * 	the border
	 * @param raised if <code>true</code>, the border will appear raised, 
	 * 	otherwise it will look sunken
	 */
	public PixmapBorder(Sprite template, boolean raised) {
		if (raised) {
			bottomRightImage = makeModifiedImage(template, Color.BLACK);
			topLeftImage = makeModifiedImage(template, Color.WHITE);
		} else {
			bottomRightImage = makeModifiedImage(template, Color.WHITE);
			topLeftImage = makeModifiedImage(template, Color.BLACK);
		}
		imageWidth = template.getWidth();
		imageHeight = template.getHeight();
	}
	
	/**
	 * Create a painted over version if a {@link Sprite} image. The image is
	 * painted over with alpha 0.5.
	 * 
	 * @param template {@link Sprite} to used as the original image
	 * @param color painting color
	 * @return modified image
	 */
	private Image makeModifiedImage(Sprite template, Color color) {
		final int width = template.getWidth();
		final int height = template.getHeight();
		
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final BufferedImage image = gc.createCompatibleImage(width, height, Transparency.OPAQUE);
		
		Graphics2D g = image.createGraphics();
		template.draw(g, 0, 0);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.dispose();
		
		return image;
	}
	
	public Insets getBorderInsets(Component component) {
		return insets;
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component component, Graphics graphics, int x, int y,
			int width, int height) {
		Rectangle oldClip = graphics.getClipBounds();
		Graphics g = graphics.create();
		g.setColor(Color.CYAN);
		
		// *** Clipping for  top and left borders ***
		Polygon p = new Polygon();
		p.addPoint(x, y);
		p.addPoint(x + width, y);
		p.addPoint(x + width - WIDTH, y + WIDTH);
		p.addPoint(x + WIDTH, y + WIDTH);
		p.addPoint(x + WIDTH, y + height - WIDTH);
		p.addPoint(x, y + height);
		g.setClip(p);
		g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
		
		// top border
		for (int i = x; i < width; i += imageWidth) {
			g.drawImage(topLeftImage, i, y, null);
		}
		// left border
		for (int i = y; i < height; i += imageHeight) {
			g.drawImage(topLeftImage, x, i, null);
		}
		
		// *** Clipping for bottom and right borders ***
		// We have the same number of vertices as before, so it's efficient to 
		// reuse the polygon  
		p.reset();
		p.addPoint(x + width, y);
		p.addPoint(x + width, y + height);
		p.addPoint(x, y + height);
		p.addPoint(x + WIDTH, y + height - WIDTH);
		p.addPoint(x + width - WIDTH, y + height - WIDTH);
		p.addPoint(x + width - WIDTH, y + WIDTH);
		g.setClip(p);
		g.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
		
		// bottom border
		int tmpY = y + height - height % imageHeight;
		for (int i = x; i < width; i += imageWidth) {
			g.drawImage(bottomRightImage, i, tmpY, null);
		}
		int tmpX = x + width - width % imageWidth;
		// right border
		for (int i = y; i < height; i += imageHeight) {
			g.drawImage(bottomRightImage, tmpX, i, null);
		}
		
		g.dispose();
	}
}
