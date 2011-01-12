package games.stendhal.client.gui.j2d;

import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class AchievementBoxFactory {
	private static final int MARGIN = 10;
	
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
		// Calculate size for the message box
		Font font = graphics.getFont();
		Font largeFont = font.deriveFont(20f);
		Rectangle2D titleRect = largeFont.getStringBounds(title, graphics.getFontRenderContext());
		Rectangle2D textRect = font.getStringBounds(description, graphics.getFontRenderContext());
		int width = (int) Math.max(titleRect.getWidth(), textRect.getWidth());
		int height = (int) (titleRect.getHeight() + textRect.getHeight());
		width += 2 * MARGIN;
		height += 2 * MARGIN;
		
		// Create the background sprite
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		final BufferedImage image = gc.createCompatibleImage(width, height, Transparency.BITMASK);
		final Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		
		// Draw the texts
		g2d.setColor(Color.BLACK);
		g2d.setFont(largeFont);
		g2d.drawString(title, MARGIN, MARGIN + (int) titleRect.getHeight());
		
		g2d.setFont(font);
		g2d.drawString(description, MARGIN, height - MARGIN);
		
		g2d.dispose();
		
		return new ImageSprite(image);
	}

}
