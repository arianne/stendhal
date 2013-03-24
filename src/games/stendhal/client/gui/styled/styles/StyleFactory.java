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

import games.stendhal.client.gui.styled.Style;

import java.awt.Color;

import org.apache.log4j.Logger;

public class StyleFactory {
	private static final Logger logger = Logger.getLogger(StyleFactory.class);
	
	/**
	 * Create the appropriate style for a style identifier.
	 * 
	 * @param styleId style number
	 * @return pixmap style
	 */
	public static Style createStyle(int styleId) {
		switch (styleId) {
		case 0:
			return new PixmapStyle("data/gui/panelwood119.jpg", null,
					new Color(163, 120, 97), new Color(50, 25, 12),
					new Color(107, 72, 50), Color.white);
		case 1:
			// TileAqua
			return new PixmapStyle("data/gui/panel_tile_aqua_001.png",
					"data/gui/border_aqua_001.png", new Color(137,157,157),
					Color.black, Color.white, Color.white);
		case 2:
			// BrickBrown
			return new PixmapStyle("data/gui/panel_brick_brown_001.png",
					"data/gui/border_brown_001.png", new Color(219, 191, 130),
					new Color(134, 106, 45), Color.white, Color.white);
		case 3:
			// Aubergine
			return new PixmapStyle("data/gui/panel_aubergine_001.png",
					"data/gui/border_violet_001.png", new Color(184, 149, 193),
					new Color(42, 7, 51), Color.white, Color.white);
		case 4:
			// Honeycomb
			return new PixmapStyle("data/gui/panel_honeycomb_001.png",
					"data/gui/border_yellow_001.png", new Color(142, 90, 0),
					new Color(248, 240, 42), Color.white, Color.black);
		case 5:
			// ParquetBrown
			return new PixmapStyle("data/gui/panel_parquet_brown_001.png",
					"data/gui/border_brown_001.png", new Color(90, 38, 0),
					new Color(225, 173, 110), Color.white, Color.white);
	
		default:
			// Wood
			logger.warn("Style not found. Using the default.");
			return new PixmapStyle("data/gui/panelwood119.jpg", null,
					new Color(163, 120, 97), new Color(50, 25, 12),
					new Color(107, 72, 50), Color.white);
		}
	}
}
