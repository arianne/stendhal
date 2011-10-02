/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client;

import java.awt.Color;
import java.awt.Composite;

/**
 * General information about the current zone.
 */
public class ZoneInfo {
	/** Singleton instance. */
	private static final ZoneInfo instance = new ZoneInfo();
	
	/** Blend mode for coloring the zone, or <code>null</code>. */
	private Composite colorMethod;
	/** Color for the current zone, or <code>null</code>. */
	private Color color;
	
	/**
	 * Create a new ZoneInfo.
	 */
	private ZoneInfo() {
	}

	/**
	 * Get the ZoneInfo instance.
	 * 
	 * @return zone info
	 */
	public static ZoneInfo get() {
		return instance;
	}
	
	/**
	 * Call when zone changes. Clears zone dependent data.
	 */
	void zoneChanged() {
		colorMethod = null;
		color = null;
	}
	
	/**
	 * Set the color blend method.
	 * 
	 * @param method
	 */
	void setColorMethod(Composite method) {
		colorMethod = method;
	}
	
	/**
	 * Get the color blend method. Mode for applying the zone color to tile sets
	 * and entity sprites.
	 * 
	 * @return blend mode
	 */
	public Composite getColorMethod() {
		return colorMethod;
	}
	
	/**
	 * Set zone specific color.
	 * 
	 * @param rgb
	 */
	void setZoneColor(int rgb) {
		this.color = new Color(rgb);
	}
	
	/**
	 * Get the zone specific color. This should be applied to tile sets and
	 * most entities using the zone blend method.
	 * 
	 * @return zone color
	 */
	public Color getZoneColor() {
		return color;
	}
}
