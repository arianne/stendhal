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
package games.stendhal.client.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;

import org.apache.log4j.Logger;

import games.stendhal.client.stendhal;
import games.stendhal.client.sprite.DataLoader;

/**
 * Code that needs to be run once before the initializing of any actual game
 * GUI components. This needs its own class, because the first game window can
 * either be the splash screen, or the main game window depending on if web
 * start was used. So to avoid code duplication, the potential initial windows
 * should call this class.
 */
class Initializer {
	/** Name of the font used for the html areas. Should match the file name without .ttf */
	private static final String DECORATIVE_FONT_NAME = "BlackChancery";
	/** Name of the font used for the tally marks. Should match the file name without .ttf */
	private static final String TALLY_FONT_NAME = "Tally";

	private static final Logger logger = Logger.getLogger(Initializer.class);

	static {
		initFont(DECORATIVE_FONT_NAME);
		initFont(TALLY_FONT_NAME);
		initApplicationName();
	}

	/**
	 * Call this from the window classes that can be the first game windows the
	 * user sees.
	 */
	static void init() {
		// Do nothing. All the work is done in the static initializer to ensure
		// it gets run once, and only once.
	}

	/**
	 * Load a custom font.
	 *
	 * @param fontName Name of the font
	 */
	private static void initFont(String fontName) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		// Don't needlessly load the font if user already has it installed
		boolean needsLoading = true;
		for (String font : ge.getAvailableFontFamilyNames()) {
			if (fontName.equals(font)) {
				needsLoading = false;
				break;
			}
		}
		if (needsLoading) {
			String resource = "data/font/" + fontName + ".ttf";
			try {
				ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, DataLoader.getResourceAsStream(resource)));
			} catch (IOException|FontFormatException e) {
				logger.error("Error loading custom font '" + resource + '"', e);
			}
		}
	}

	/**
	 * Set the application name for the windowing system.
	 */
	private static void initApplicationName() {
		/*
		 * WM_CLASS for X window managers that use it
		 * (A workaround, see java RFE 6528430)
		 *
		 * Used for example in collapsing the window list in gnome 2, and
		 * for the application menu in gnome 3.
		 */
		try {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			java.lang.reflect.Field awtAppClassNameField =
				toolkit.getClass().getDeclaredField("awtAppClassName");
			awtAppClassNameField.setAccessible(true);
			awtAppClassNameField.set(toolkit, stendhal.GAME_NAME);
		} catch (NoSuchFieldException e) {
			logger.debug("Not setting X application name " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.debug("Not setting X application name " + e.getMessage());
		} catch (IllegalAccessException e) {
			logger.debug("Not setting X application name: " + e.getMessage());
		} catch (RuntimeException e) { // InaccessibleObjectException: is not available on Java 8
			logger.debug("Not setting X application name: " + e.getMessage());
		}
		// Setting the name for Mac probably requires using the native LAF, and
		// we do not use it
	}
}
