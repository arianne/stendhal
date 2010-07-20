package games.stendhal.client.gui.stats;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;

/**
 * A bar indicator component for karma.
 */
public class KarmaIndicator extends JComponent implements FeatureChangeListener {
	private static final long serialVersionUID = 3462088641737184898L;

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
		setVisible(false);
		final SpriteStore store = SpriteStore.get();
		image = store.getSprite(IMAGE_FILE_NAME);
		
		// We don't draw the background
		setOpaque(false);
		StendhalClient.get().addFeatureChangeListener(this);
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
		// Scale to ]0, 1[
		double normalized = 0.5 + Math.atan(SCALING * karma) / Math.PI;
	
		// ...and then to ]0, image.getWidth()[
		return (int) (image.getWidth() * normalized);
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

	/**
	 * disables the karma indicator.
	 */
	public void featureDisabled(String name) {
		if (name.equals("karma_indicator")) {
			setVisible(false);
		}
	}

	/**
	 * enables the karma indicator.
	 */
	public void featureEnabled(String name, String value) {
		if (name.equals("karma_indicator")) {
			setVisible(true);
		}
	}
}
