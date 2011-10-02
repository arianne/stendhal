/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import games.stendhal.common.CRC;
import games.stendhal.common.MathHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import marauroa.common.game.RPObject;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * A container for arbitrary map attributes.
 */
public class ZoneAttributes {
	private static final Logger logger = Logger.getLogger(ZoneAttributes.class);
	
	/** Color to be used near midnight at daylight colored zones. */
	private static final Integer MIDNIGHT_COLOR = 0x47408c;
	/**
	 * Color to be used at early night, and early morning before sunrise at
	 * daylight colored zones.
	 */
	private static final Integer EARLY_NIGHT_COLOR = 0x774590;
	/** Color to be used at sunset and sunrise at daylight colored zones. */
	private static final Integer SUNSET_COLOR = 0xc0a080;
	
	/** Container to wrap the contents to pass as a layer. */
	private final TransferContent content = new TransferContent();
	/** An object for storing the attributes. */
	private final RPObject attr = new RPObject();
	/**
	 * <code>true</code>, if the the current binary content is valid, 
	 * <code>false</code> if it needs to be rewritten.
	 */
	private boolean valid;
	/**
	 * <code>true</code>, if the zone should be coloured according to the
	 * normal sunlight, <code>false</code> otherwise. 
	 */
	private boolean colorByDaytime;
	
	/**
	 * Create new ZoneAttributes.
	 * 
	 * @param mapName name of the map
	 */
	public ZoneAttributes(String mapName) {
		attr.setID(RPObject.INVALID_ID);
		content.name = mapName + ".data_map";
		content.cacheable = false;
	}
	
	/**
	 * Set an attribute.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		attr.put(key, value);
		invalidate();
	}
	
	/**
	 * Call this, if the zone should be colored by the daylight. The attributes
	 * will set the appropriate blend mode, and keep updating the the color
	 * value as needed.
	 */
	public void setColorByDaytime() {
		put("color_method", "multiply");
		colorByDaytime = true;
	}
	
	/**
	 * Get the contents.
	 * 
	 * @return Attributes packed as a layer. The content is a serialized
	 *	RPObject with the attributes.
	 */
	TransferContent getContents() {
		if (colorByDaytime) {
			updateDaytimeColor();
		}
		if (!valid) {
			validate();
		}
		
		return content;
	}
	
	/**
	 * Flag the binary contents needing update.
	 */
	private void invalidate() {
		valid = false;
	}
	
	/**
	 * Regenerate the binary contents.
	 */
	private void validate() {
		final ByteArrayOutputStream array = new ByteArrayOutputStream();
		final OutputSerializer serializer = new OutputSerializer(array);
		try {
			attr.writeObject(serializer);
		} catch (IOException e) {
			logger.error("Failed to set attributes", e);
		}
		
		content.data = array.toByteArray();
		content.timestamp = CRC.cmpCRC(content.data);
		valid = true;
	}
	
	/**
	 * Update the zone color according to the hour.
	 */
	private void updateDaytimeColor() {
		Calendar cal = Calendar.getInstance();

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		// anything but precise, but who cares
		int diffToMidnight = Math.min(hour, 24 - hour);
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
	
	/**
	 * Set the zone color. Flags the contents invalid if the color changed.
	 * 
	 * @param color
	 */
	private void setZoneColor(Integer color) {
		String oldColor = attr.get("color");
		if (color == null) {
			if (attr.remove("color") != null) {
				invalidate();
			}
			return;
		}
		if (!color.equals(MathHelper.parseInt(oldColor))) {
			attr.put("color", color.intValue());
			invalidate();
		}
	}
}
