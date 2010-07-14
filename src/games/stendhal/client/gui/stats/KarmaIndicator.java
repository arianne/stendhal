package games.stendhal.client.gui.stats;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import javax.swing.JComponent;

/**
 * A bar indicator component for karma.
 */
public class KarmaIndicator extends JComponent {
	/** 
	 * Scaling factor for interpreting karma to bar length. Smaller means
	 * smaller change in karma bar for a karma change. 
	 */
	private static final double SCALING = 0.02;
	private static final String IMAGE_FILE_NAME = "data/gui/karma_scale.png";
	
	/** Karma scaled to pixels */
	private int karma;
	private final Sprite image;
	
	/**
	 * Create a new karma indicator.
	 */
	public KarmaIndicator() {
		final SpriteStore store = SpriteStore.get();
		image = store.getSprite(IMAGE_FILE_NAME);
		
		// We don't draw the background
		setOpaque(false);
	}
	
	/**
	 * Set the karma value.
	 * 
	 * @param karma
	 */
	public void setValue(double karma) {
		this.karma = scale(karma);
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension pref = new Dimension(image.getWidth(), image.getHeight());
		
		Insets insets = getInsets();
		pref.width += insets.left + insets.right;
		pref.height += insets.top + insets.bottom;
		
		return pref;
	}
	
	/**
	 * Scale a karma value to bar length.
	 * 
	 * @param karma player karma
	 * @return length of the drawn bar in pixels
	 */
	private int scale(double karma) {
		// Scale to ]-1, 1[
		double normalized = Math.atan(SCALING * karma) / Math.PI * 2;
	
		// ...and then to ]0, image.getWidth()[
		return (int) (image.getWidth() / 2 + normalized * image.getWidth() / 2);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Insets insets = getInsets();
		
		// Paint black what is not covered by the colored bar
		g.setColor(Color.BLACK);
		g.fillRect(insets.left, insets.top, image.getWidth(), image.getHeight());
		// Draw appropriate length of the image
		g.clipRect(insets.left, insets.top, karma, getHeight());
		image.draw(g, insets.left, insets.top);
	}
}
