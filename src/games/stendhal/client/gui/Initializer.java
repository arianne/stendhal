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

import games.stendhal.client.stendhal;
import games.stendhal.client.sprite.DataLoader;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * Code that needs to be run once before the initializing of any actual game
 * GUI components. This needs its own class, because the first game window can
 * either be the splash screen, or the main game window depending on if web
 * start was used. So to avoid code duplication, the potential initial windows
 * should call this class.
 */
class Initializer {
	/** Name of the font used for the html areas. Should match the file name without .ttf */
	private static final String FONT_NAME = "BlackChancery";
	/** Font used for the html areas */
	private static final String FONT = "data/gui/" + FONT_NAME + ".ttf";
	
	private static final Logger logger = Logger.getLogger(Initializer.class);
	
	static {
		initFont();
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
	 * Load the default decorative font.
	 */
	private static void initFont() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		// Don't needlessly load the font if user already has it installed
		boolean needsLoading = true;
		for (String font : ge.getAvailableFontFamilyNames()) {
			if (FONT_NAME.equals(font)) {
				needsLoading = false;
				break;
			}
		}
		if (needsLoading) {
			try {
				// Call via reflection to keep supporting java 1.5
				Method m = ge.getClass().getMethod("registerFont", Font.class);
				m.invoke(ge, Font.createFont(Font.TRUETYPE_FONT, DataLoader.getResourceAsStream(FONT)));
			} catch (IOException e) {
				logger.error("Error loading custom font", e);
			} catch (FontFormatException e) {
				logger.error("Error loading custom font", e);
			} catch (SecurityException e) {
				logger.error("Error loading custom font", e);
			} catch (NoSuchMethodException e) {
				logger.error("Error loading custom font. Java version 6 or later is required for that to work.");
			} catch (IllegalArgumentException e) {
				logger.error("Error loading custom font", e);
			} catch (IllegalAccessException e) {
				logger.error("Error loading custom font", e);
			} catch (InvocationTargetException e) {
				logger.error("Error loading custom font", e);
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
		}
		// Setting the name for Mac probably requires using the native LAF, and
		// we do not use it
	}
}
