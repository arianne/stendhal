package games.stendhal.client.gui.j2d;

import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;

public class AchievementBoxFactory {
	
	private Graphics2D graphics;
	
	public AchievementBoxFactory(Graphics2D graphics) {
		this.graphics = graphics;
	}
	
	/**
	 * Create a sprite for a reached achievement
	 * @param title
	 * @param description
	 * @param category
	 * @return the drawn sprite
	 */
	public Sprite createAchievementBox(String title, String description, String category) {
		System.out.println("Ich würde jetzt malen, wenn ich wüsste wie...");
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final Image image = gc.createCompatibleImage(50, 50, Transparency.BITMASK);
		final Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.RED);
		g2d.fill3DRect(0, 0, 100, 100, true);
		g2d.setColor(Color.BLUE);
		g2d.fill3DRect(1, 1, 99, 99, true);
		return new ImageSprite(image);
	}

}
