/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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


import games.stendhal.client.gui.styled.PixmapBorder;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.MathHelper;

import java.awt.Color;
import java.awt.Font;

import javax.swing.border.Border;

/**
 * The wood style.
 */
public class WoodStyle implements Style {
	private static final int DEFAULT_FONT_SIZE = 12;
	
	private static final Color highLightColor = new Color(163, 120, 97);
	private static final Color shadowColor = new Color(50, 25, 12);
	private static final Color plainColor = new Color(107, 72, 50);

	/**
	 * A shared instance.
	 */
	private static Style sharedInstance;

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
	 * Create new WoodStyle.
	 */
	public WoodStyle() {
		/*
		 * Load the texture
		 */
		final SpriteStore st = SpriteStore.get();
		background = st.getSprite("data/gui/panelwood119.jpg");

		border = new PixmapBorder(background, true);
		borderDown = new PixmapBorder(background, false);

		WtWindowManager.getInstance().registerSettingChangeListener("ui.font_size", 
				new SettingChangeAdapter("ui.font_size", Integer.toString(DEFAULT_FONT_SIZE)) {
			@Override
			public void changed(String newValue) {
				int size = MathHelper.parseIntDefault(newValue, DEFAULT_FONT_SIZE);
				font = new Font("Dialog", Font.PLAIN, size);
			}
		});
	}

	//
	// WoodStyle
	//

	/**
	 * Get a shared instance.
	 * 
	 * @return A shared instance.
	 */
	public static synchronized Style getInstance() {
		if (sharedInstance == null) {
			sharedInstance = new WoodStyle();
		}

		return sharedInstance;
	}

	//
	// Style
	//

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
	 * Get the foreground color appropriate for the background texture.
	 * 
	 * @return A color.
	 */
	@Override
	public Color getForeground() {
		return Color.white;
	}

	@Override
	public Color getHighLightColor() {
		return highLightColor;
	}

	@Override
	public Color getShadowColor() {
		return shadowColor;
	}
	
	/**
	 * Get a color that roughly represents the background.
	 * 
	 * @return plain color
	 */
	@Override
	public Color getPlainColor() {
		return plainColor;
	}
}
