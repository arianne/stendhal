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
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.player.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	 * The zone where these attributes belong to.
	 */
	private final StendhalRPZone zone;
	/**
	 * <code>true</code>, if the the current binary content is valid, 
	 * <code>false</code> if it needs to be rewritten.
	 */
	private boolean valid;
	
	/**
	 * Cereate new ZoneAttributes.
	 * 
	 * @param zone
	 */
	public ZoneAttributes(StendhalRPZone zone) {
		attr.setID(RPObject.INVALID_ID);
		// old client ignore layers ending in _map, thus the odd choice of name
		content.name = zone.getName() + ".data_map";
		content.cacheable = false;
		this.zone = zone;
	}
	
	/**
	 * Get the zone where these attributes belong to.
	 * 
	 * @return zone
	 */
	StendhalRPZone getZone() {
		return zone;
	}
	
	/**
	 * Set an attribute.
	 * 
	 * @param key
	 * @param value
	 */
	void put(String key, String value) {
		// Interpret special values
		if ("color_method".equals(key) && "time".equals(value)) {
			Daylight.get().manageAttributes(this);
		} else {
			attr.put(key, value);
		}
		invalidate();
	}
	
	/**
	 * Remove an attribute.
	 * 
	 * @param key attribute name
	 */
	void remove(String key) {
		attr.remove(key);
		invalidate();
	}
	
	/**
	 * Set all attributes.
	 * 
	 * @param map map of attributes
	 */
	public void putAll(Map<String, String> map) {
		for (Entry<String, String> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Get the contents.
	 * 
	 * @return Attributes packed as a layer. The content is a serialized
	 *	RPObject with the attributes.
	 */
	TransferContent getContents() {
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
	 * Manager for daylight colored zones.
	 */
	private static class Daylight implements TurnListener {
		/** Time between checking if the color should be changed. Seconds. */ 
		private static final int CHECK_INTERVAL = 61;
		/** Singleton instance. */
		private static final Daylight instance = new Daylight();
		/** Color corresponding to the current time. */
		Integer currentColor;
		
		/** Managed zones, and their attributes */
		private final List<ZoneAttributes> zones = new ArrayList<ZoneAttributes>(); 
		
		/**
		 * Create a new Daylight instance. Do not use this.
		 */
		private Daylight() {
			onTurnReached(0);
		}
		
		/**
		 * Get the Daylight instance.
		 * 
		 * @return singleton instance
		 */
		static Daylight get() {
			return instance;
		}
		
		/**
		 * Make a zone color managed by the daylight colorer.
		 *  
		 * @param attr attributes of the zone
		 */
		void manageAttributes(ZoneAttributes attr) {
			zones.add(attr);
			// Set the initial values
			attr.put("color_method", "multiply");
			setZoneColor(attr, currentColor);
		}

		public void onTurnReached(int currentTurn) {
			updateDaytimeColor();
			SingletonRepository.getTurnNotifier().notifyInSeconds(CHECK_INTERVAL, this);
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
				setZoneColors(null);
			} else if (diffToMidnight == 3) {
				setZoneColors(SUNSET_COLOR);
			} else if (diffToMidnight == 2) {
				setZoneColors(EARLY_NIGHT_COLOR);
			} else {
				setZoneColors(MIDNIGHT_COLOR);
			}
		}
		
		/**
		 * Set the current daylight color. Notifies all the managed zones if
		 * the color has changed.
		 * 
		 * @param color
		 */
		private void setZoneColors(Integer color) {
			if (((color == null) && (currentColor != null))
					|| ((color != null) && !color.equals(currentColor))) {
				for (ZoneAttributes attr : zones) {
					setZoneColor(attr, color);
				}
			}
			currentColor = color;
		}
		
		/**
		 * Set the color of a zone. Notifies all the players with a recent
		 * enough client
		 * 
		 * @param attr attributes of the zone
		 * @param color new color value
		 */
		private void setZoneColor(ZoneAttributes attr, Integer color) {
			if (color == null) {
				attr.remove("color");
			} else {
				attr.put("color", color.toString());
			}
			// Notify resident players about the changed color
			for (Player player : attr.getZone().getPlayers()) {
				// Old clients do not understand content transfer that just
				// update the old map, and end up with no entities on the screen
				if (player.isClientNewerThan("0.97")) {
					StendhalRPAction.transferContent(player);
				}
			}
		}
	}
}
