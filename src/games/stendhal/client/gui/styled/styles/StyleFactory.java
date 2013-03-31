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
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

/**
 * Factory for creating Styles.
 */
public class StyleFactory {
	private static final Logger logger = Logger.getLogger(StyleFactory.class);
	
	/**
	 * Create the appropriate style for a style identifier.
	 * 
	 * @param styleId style name
	 * @return pixmap style matching the name
	 */
	public static Style createStyle(String styleId) {
		StyleDefinition creator = StyleDefinition.parse(styleId);
		return creator.create();
	}
	
	/**
	 * Get names of all available styles.
	 * 
	 * @return style names
	 */
	public static Collection<String> getAvailableStyles() {
		Collection<String> rval = new ArrayList<String>();
		for (StyleDefinition s : StyleDefinition.values()) {
			rval.add(s.toString());
		}
		
		return rval;
	}
	
	/**
	 * Available style definitions.
	 */
	private static enum StyleDefinition {
		WOOD("Wood (default)") {
			@Override
			Style create() {
				return new PixmapStyle("data/gui/panelwood119.jpg", true,
						new Color(163, 120, 97), new Color(50, 25, 12),
						new Color(107, 72, 50), Color.white);
			}
		},
		
		TILE_AQUA("Tile Aqua") {
			@Override
			Style create() {
				Color highlight = new Color(137,157,157);
				PixmapStyle style = new PixmapStyle("data/gui/panel_tile_aqua_001.png",
						false, highlight, Color.black, Color.white, Color.white);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, highlight, Color.black, Color.black));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, highlight, Color.black, Color.black));
				return style;
			}
		},
		
		BRICK_BROWN("Brick Brown") {
			@Override
			Style create() {
				Color highlight = new Color(219, 191, 130);
				Color shadow = new Color(134, 106, 45);
				PixmapStyle style = new PixmapStyle("data/gui/panel_brick_brown_001.png",
						false, highlight, shadow, Color.white, Color.white);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, highlight, shadow, shadow));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, highlight, shadow, shadow));
				return style;
			}
		},
		
		AUBERGINE("Aubergine") {
			@Override
			Style create() {
				Color highlight = new Color(184, 149, 193);
				Color shadow = new Color(42, 7, 51);
				PixmapStyle style = new PixmapStyle("data/gui/panel_aubergine_001.png",
						false, highlight, shadow, Color.white, Color.white);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, highlight, shadow, shadow));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, highlight, shadow, shadow));
				return style;
			}
		},
		
		HONEYCOMB("Honeycomb") {
			@Override
			Style create() {
				Color highlight = new Color(142, 90, 0); // Light brown (also text shadow)
				Color shadow = new Color(255, 255, 255); // White (also inactive text color)
				Color foreground = new Color(107, 47, 0); // Brown
				PixmapStyle style = new PixmapStyle("data/gui/panel_honeycomb_001.png",
						false, highlight, shadow, Color.white, foreground);
				Color bhigh = new Color(255, 255, 180);
				Color bshadow = new Color(100, 80, 20);
				
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						bhigh, bhigh, bshadow, bshadow));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						bhigh, bhigh, bshadow, bshadow));
				return style;
			}
		},
		
		PARQUET_BROWN("Parquet Brown") {
			@Override
			Style create() {
				Color highlight = new Color(225, 173, 110);
				Color shadow = new Color(90, 38, 0);
				PixmapStyle style = new PixmapStyle("data/gui/panel_parquet_brown_001.png",
						false, shadow, highlight, Color.white, Color.white);
				style.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,
						highlight, highlight, shadow, shadow));
				style.setBorderDown(BorderFactory.createBevelBorder(BevelBorder.LOWERED,
						highlight, highlight, shadow, shadow));
				
				return style;
			}
		};
		
		// ---- implementation ----
		
		/** Human readable name of the style. */ 
		private final String name;
		/** Create the Style. */
		abstract Style create();
		
		/**
		 * Create a new StyleDefinition.
		 * 
		 * @param name human readable name of the style
		 */
		private StyleDefinition(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		/**
		 * Find the StyleDefinition matching a style name.
		 * 
		 * @param name style name to look up
		 * @return StyleDefinition with the name, or the default definition if
		 * 	no matching style was found
		 */
		static StyleDefinition parse(String name) {
			for (StyleDefinition s : values()) {
				if (s.toString().equals(name)) {
					return s;
				}
			}
			
			logger.warn("Style '" + name + "'not found. Using the default.");
			
			return WOOD;
		}
	}
}
