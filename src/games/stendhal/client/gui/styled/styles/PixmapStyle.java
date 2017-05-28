/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.styled.styles;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;

import games.stendhal.client.gui.styled.PixmapBorder;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.MathHelper;

/**
 * Base class for the pixmap styles.
 */
class PixmapStyle implements Style {
	private static final int DEFAULT_FONT_SIZE = 12;

	private final Color highLightColor;
	private final Color shadowColor;
	private final Color plainColor;
	private final Color foreground;

	/**
	 * The background texture.
	 */
	private Sprite background;

	/**
	 * The border.
	 */
	private Border border;
	/**
	 * Downwards border (for buttons etc).
	 */
	private Border borderDown;

	/**
	 * The default font.
	 */
	private Font font;

	/**
	 * Create a new AbstractPixmapStyle.
	 *
	 * @param baseSprite background image location
	 * @param createDefaultBorder if <code>true</code> create PixmapBorders
	 * 	from the background sprite, otherwise the caller <em>must</em> set the
	 * 	borders using {@link #setBorder(Border)} and
	 * 	{@link #setBorderDown(Border)} before using the style
	 * @param highLightColor color for drawing highlights
	 * @param shadowColor color for drawing shadows
	 * @param plainColor color that roughly represents the background.
	 * @param foreground color used for text and anything else to be drawn in
	 * 	the component foreground color
	 */
	PixmapStyle(String baseSprite, boolean createDefaultBorder,
			Color highLightColor, Color shadowColor, Color plainColor,
			Color foreground) {
		/*
		 * Load the texture
		 */
		final SpriteStore st = SpriteStore.get();
		background = st.getSprite(baseSprite);

		if (createDefaultBorder) {
			border = new PixmapBorder(background, true);
			borderDown = new PixmapBorder(background, false);
		}

		this.highLightColor = highLightColor;
		this.shadowColor = shadowColor;
		this.plainColor = plainColor;
		this.foreground = foreground;

		WtWindowManager.getInstance().registerSettingChangeListener("ui.font_size",
				new SettingChangeAdapter("ui.font_size", Integer.toString(DEFAULT_FONT_SIZE)) {
			@Override
			public void changed(String newValue) {
				int size = MathHelper.parseIntDefault(newValue, DEFAULT_FONT_SIZE);
				font = new Font("Dialog", Font.PLAIN, size);
			}
		});
	}

	/**
	 * Get the normal font.
	 *
	 * @return A font.
	 */
	@Override
	public Font getFont() {
		return font;
	}

	/**
	 * Get the background texture.
	 *
	 * @return A texture sprite.
	 */
	@Override
	public Sprite getBackground() {
		return background;
	}

	/**
	 * Get component border.
	 *
	 * @return A border, or <code>null</code> to use default.
	 */
	@Override
	public Border getBorder() {
		return border;
	}

	/**
	 * Get lowered component border.
	 *
	 * @return A border, or <code>null</code> to use default.
	 */
	@Override
	public Border getBorderDown() {
		return borderDown;
	}

	@Override
	public Color getHighLightColor() {
		return highLightColor;
	}

	@Override
	public Color getShadowColor() {
		return shadowColor;
	}

	@Override
	public Color getPlainColor() {
		return plainColor;
	}

	@Override
	public Color getForeground() {
		return foreground;
	}

	/**
	 * Set the border to be used, instead the default PixmapBorder.
	 *
	 * @param border new border
	 */
	void setBorder(Border border) {
		this.border = border;
	}

	/**
	 * Set the downward border to be used, instead the default PixmapBorder.
	 *
	 * @param border new border
	 */
	void setBorderDown(Border border) {
		this.borderDown = border;
	}
}
