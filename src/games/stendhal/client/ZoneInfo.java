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

import games.stendhal.client.gui.j2d.Blend;

import java.awt.Color;
import java.awt.Composite;
import java.util.Calendar;

/**
 * General information about the current zone.
 */
public class ZoneInfo {
	/** Singleton instance. */
	private static final ZoneInfo instance = new ZoneInfo();
	
	/** Color to be used near midnight at daylight colored zones. */
	private static final Color MIDNIGHT_COLOR = new Color(0x47408c);
	/**
	 * Color to be used at early night, and early morning before sunrise at
	 * daylight colored zones.
	 */
	private static final Color EARLY_NIGHT_COLOR = new Color(0x774590);
	/** Color to be used at sunset and sunrise at daylight colored zones */
	private static final Color SUNSET_COLOR = new Color(0xc0a080);
	
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
	 * @param color
	 */
	void setZoneColor(Color color) {
		this.color = color;
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
	
	/**
	 * Choose color method and color to mimic daylight at server time. The
	 * game lives eternal summer so the nights are short and do not get very
	 * dark.  
	 */
	void setColorByDaytime() {
		setColorMethod(Blend.Multiply);
		Calendar now = Calendar.getInstance();

		int hour = now.get(Calendar.HOUR_OF_DAY);
		// anything but precise, but who cares
		int diffToMidnight = Math.abs((12 - hour) % 12);
		if (diffToMidnight > 3) {
			setZoneColor(null);
		} else if (diffToMidnight == 3) {
			setZoneColor(SUNSET_COLOR);
		} else if (diffToMidnight == 2) {
			setZoneColor(EARLY_NIGHT_COLOR);
		} else {
			setZoneColor(MIDNIGHT_COLOR);
		}
	}
}
