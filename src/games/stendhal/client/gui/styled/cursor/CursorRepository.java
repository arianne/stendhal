/***************************************************************************
 *                      (C) Copyright 2010 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.client.gui.styled.cursor;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import games.stendhal.client.sprite.DataLoader;

/**
 * Loads and caches cursors
 *
 * @author hendrik
 */
public class CursorRepository {
	private static Logger logger = Logger.getLogger(CursorRepository.class);

	private Map<StendhalCursor, Cursor> cursorMap = new EnumMap<StendhalCursor, Cursor>(StendhalCursor.class);

	public Cursor get(StendhalCursor stendhalCursor) {
		Cursor res = cursorMap.get(stendhalCursor);
		if (res == null) {
			res = loadCursor(stendhalCursor);
		}
		return res;
	}

	private Cursor loadCursor(StendhalCursor stendhalCursor) {
		String imageName = "data/sprites/cursor/" + stendhalCursor.getImageName();

		// load image file
		URL url = DataLoader.getResource(imageName);
		if (url == null) {
			logger.error("Can't find image: " + imageName, new Throwable());
			cursorMap.put(stendhalCursor, Cursor.getDefaultCursor());
			return Cursor.getDefaultCursor();
		}

		// use ImageIO to read the image in
		Image image;
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			logger.error("Can't read image: " + imageName, new Throwable());
			cursorMap.put(stendhalCursor, Cursor.getDefaultCursor());
			return Cursor.getDefaultCursor();
		}

		// create cursor
		Point hotSpot = stendhalCursor.getHotSpot();
		String name = stendhalCursor.toString().toLowerCase(Locale.ENGLISH);
		Cursor res = Toolkit.getDefaultToolkit().createCustomCursor(image, hotSpot, name);
		cursorMap.put(stendhalCursor, res);
		return res;
	}
}
