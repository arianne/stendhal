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
import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.player.Player;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
	public void put(String key, String value) {
		// Interpret special values
		if ("color_method".equals(key) && "time".equals(value)) {
			DaylightUpdater.get().manageAttributes(this);
		} else {
			if ("color".equals(key)) {
				/*
				 * Accept hex strings as well. Check the prefix manually to avoid
				 * stupid compatibility problems with octal numbers.
				 */
				try {
					if (value.startsWith("0x") || value.startsWith("0X") || value.startsWith("#")) {
						value = Integer.decode(value).toString();
					}
				} catch (RuntimeException e) {
					logger.error("Failed to decode color '" + value + "'", e);
				}
			}
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
	private static class DaylightUpdater implements TurnListener {
		/** Time between checking if the color should be changed. Seconds. */
		private static final int CHECK_INTERVAL = 61;
		/** Singleton instance. */
		private static final DaylightUpdater instance = new DaylightUpdater();
		/** Color corresponding to the current time. */
		Integer currentColor;

		/** Managed zones, and their attributes */
		private final List<ZoneAttributes> zones = new ArrayList<ZoneAttributes>();

		/**
		 * Create a new Daylight instance. Do not use this.
		 */
		private DaylightUpdater() {
			onTurnReached(0);
		}

		/**
		 * Get the Daylight instance.
		 *
		 * @return singleton instance
		 */
		static DaylightUpdater get() {
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
			setZoneColors(DaylightPhase.current().getColor());
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
					if (color != null) {
						attr.put("blend_method", "bleach");
					} else {
						attr.remove("blend_method");
					}
				}
			}
			currentColor = color;
		}

		/**
		 * Set the color of a zone. Sets the blend mode of the effect layers to
		 * bleach, if needed. Notifies all the players with a recent enough
		 * client.
		 *
		 * @param attr attributes of the zone
		 * @param color new color value
		 */
		private void setZoneColor(ZoneAttributes attr, Integer color) {
			if (color == null) {
				attr.remove("color");
				attr.remove("blend_method");
			} else {
				attr.put("color", color.toString());
				attr.put("blend_method", "bleach");
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
