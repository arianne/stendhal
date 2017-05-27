/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.TransparencyMode;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.sprite.ImageSprite;
import games.stendhal.client.sprite.Sprite;

public class AchievementBoxFactory {
	/** Default font name */
	private static final String FONT_NAME = "BlackChancery";
	/** Location of the achievement category icons */
	private static final String ACHIEVEMENT_IMAGE_FOLDER = "data/sprites/achievements/";
	/** Background image. */
	private static final String BACKGROUND = "data/gui/banner_background.png";
	// These are dependent on the sprite
	/** Space to leave at the top of the sprite above the text. */
	private static final int TOP_MARGIN = 2;
	/** Space to leave at the sides of the sprite. */
	private static final int SIDE_MARGIN = 20;
	/** Space to leave at the bottom of the sprite below the text */
	private static final int BOTTOM_MARGIN = 25;
	/** Space to leave between the category image and text */
	private static final int IMAGE_PAD = 5;

	/** Used for calculating the line metrics */
	private static final Graphics2D graphics = (new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)).createGraphics();

	/**
	 * Create a sprite for a reached achievement
	 * @param title
	 * @param description
	 * @param category
	 * @return the drawn sprite
	 */
	public Sprite createAchievementBox(String title, String description, String category) {
		final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		// Get the category image
		// initialize category image with empty image in case loading the image fails
		BufferedImage categoryImage = gc.createCompatibleImage(32, 32, TransparencyMode.TRANSPARENCY);
		String imageName = ACHIEVEMENT_IMAGE_FOLDER + category.toLowerCase(Locale.ENGLISH) + ".png";
		try {
			categoryImage = ImageIO.read(DataLoader.getResourceAsStream(imageName));
		} catch (IOException e) {
			Logger.getLogger(AchievementBoxFactory.class).error("Error loading achievement box image: " + imageName, e);
		} catch (RuntimeException e) {
			Logger.getLogger(AchievementBoxFactory.class).error("Error loading achievement box image: " + imageName, e);
		}
		// Calculate size for the message box
		String fontName = WtWindowManager.getInstance().getProperty("ui.logfont", FONT_NAME);
		Font font = new Font(fontName, Font.PLAIN, 14);
		Font largeFont = font.deriveFont(20f);
		Rectangle2D titleRect = largeFont.getStringBounds(title, graphics.getFontRenderContext());
		Rectangle2D textRect = font.getStringBounds(description, graphics.getFontRenderContext());
		int width = (int) Math.max(titleRect.getWidth(), textRect.getWidth())+categoryImage.getWidth();
		int height = (int) Math.max(categoryImage.getHeight(), (titleRect.getHeight() + textRect.getHeight()));
		width += 2 * SIDE_MARGIN + IMAGE_PAD;
		height += TOP_MARGIN + BOTTOM_MARGIN;

		// Create the background sprite
		final BufferedImage image = gc.createCompatibleImage(width, height, TransparencyMode.TRANSPARENCY);
		final Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(AlphaComposite.Src);
		BackgroundPainter bp = new BackgroundPainter(BACKGROUND);
		bp.paint(g2d, width, height);

		// Draw the texts
		g2d.setColor(Color.BLACK);
		g2d.setFont(largeFont);
		g2d.drawString(title, SIDE_MARGIN + IMAGE_PAD + categoryImage.getWidth(), TOP_MARGIN + (int) titleRect.getHeight());

		g2d.setFont(font);
		g2d.drawString(description, SIDE_MARGIN + IMAGE_PAD + categoryImage.getWidth(), height - BOTTOM_MARGIN);

		// Draw the image (the usable height starts right from the top)
		int y = (height - BOTTOM_MARGIN - categoryImage.getHeight()) / 2 + TOP_MARGIN;
		g2d.setComposite(AlphaComposite.SrcOver);
		g2d.drawImage(categoryImage, SIDE_MARGIN, y, null);

		g2d.dispose();

		return new ImageSprite(image);
	}

}
