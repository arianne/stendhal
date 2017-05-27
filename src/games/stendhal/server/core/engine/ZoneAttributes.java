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
package games.stendhal.server.core.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import games.stendhal.common.CRC;
import games.stendhal.server.core.rp.DaylightUpdater;
import games.stendhal.server.core.rp.WeatherUpdater;
import marauroa.common.game.RPObject;
import marauroa.common.net.OutputSerializer;
import marauroa.common.net.message.TransferContent;

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
	 * Create new ZoneAttributes.
	 *
	 * @param zone the zone for which the attribute set is created
	 */
	public ZoneAttributes(StendhalRPZone zone) {
		attr.setID(RPObject.INVALID_ID);
		setBaseName(zone.getName());
		content.cacheable = false;
		this.zone = zone;
	}

	/**
	 * Set the base name of the layers in the zone. Normally you do not need
	 * to call this, as the name is got from the zone. Setting it is necessary
	 * if the zone name does not match the base name of the tile layers, as
	 * is in the case of special zones like the bank vault. For those zones
	 * the base name comes from the parent zone used to create the special zone,
	 * and the name must be set to the same for the attributes layer.
	 *
	 * @param name base zone name
	 */
	public final void setBaseName(String name) {
		// old client ignore layers ending in _map, thus the odd choice of name
		content.name = name + ".data_map";
	}

	/**
	 * Get the zone where these attributes belong to.
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
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
		if ("weather".equals(key) && value != null && value.startsWith(WeatherUpdater.WEATHER_KEYWORD)) {
			WeatherUpdater.get().manageAttributes(this, value);
		} else if ("color_method".equals(key) && "time".equals(value)) {
			DaylightUpdater.get().manageAttributes(this);
		} else {
			if ("color".equals(key)) {
				/*
				 * Accept only hex strings. Check the prefix manually to avoid
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
	 * Get the current value of an attribute.
	 *
	 * @param key attribute key
	 * @return attribute value, or <code>null</code> if the attribute is not set
	 */
	public String get(String key) {
		return attr.get(key);
	}

	/**
	 * Remove an attribute.
	 *
	 * @param key attribute name
	 */
	public void remove(String key) {
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

}
