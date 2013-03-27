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

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

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
			return new PixmapStyle("data/gui/panelwood119.jpg", true,
					new Color(163, 120, 97), new Color(50, 25, 12),
					new Color(107, 72, 50), Color.white);
		case 1:
			// TileAqua
			Color highlight = new Color(137,157,157);
			PixmapStyle style = new PixmapStyle("data/gui/panel_tile_aqua_001.png",
					false, highlight, Color.black, Color.white, Color.white);
			style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					highlight, highlight, Color.black, Color.black));
			style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					highlight, highlight, Color.black, Color.black));
			return style;
		case 2:
			// BrickBrown
			highlight = new Color(219, 191, 130);
			Color shadow = new Color(134, 106, 45);
			style = new PixmapStyle("data/gui/panel_brick_brown_001.png",
					false, highlight, shadow, Color.white, Color.white);
			style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					highlight, highlight, shadow, shadow));
			style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					highlight, highlight, shadow, shadow));
			return style;
		case 3:
			// Aubergine
			highlight = new Color(184, 149, 193);
			shadow = new Color(42, 7, 51);
			style = new PixmapStyle("data/gui/panel_aubergine_001.png",
					false, highlight, shadow, Color.white, Color.white);
			style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					highlight, highlight, shadow, shadow));
			style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					highlight, highlight, shadow, shadow));
			return style;
		case 4:
			// Honeycomb
			highlight = new Color(142, 90, 0);
			shadow = new Color(42, 7, 51);
			style = new PixmapStyle("data/gui/panel_honeycomb_001.png",
					false, highlight, shadow, Color.white, Color.black);
			Color bhigh = new Color(255, 255, 180);
			Color bshadow = new Color(100, 80, 20);
			
			style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					bhigh, bhigh, bshadow, bshadow));
			style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					bhigh, bhigh, bshadow, bshadow));
			return style;
		case 5:
			// ParquetBrown
			highlight = new Color(225, 173, 110);
			shadow = new Color(90, 38, 0);
			style = new PixmapStyle("data/gui/panel_parquet_brown_001.png",
					false, shadow, highlight, Color.white, Color.white);
			style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
					highlight, highlight, shadow, shadow));
			style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
					highlight, highlight, shadow, shadow));
			
			return style;
		default:
			// Wood
			logger.warn("Style not found. Using the default.");
			return createStyle(0);
		}
	}
}
