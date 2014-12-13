/***************************************************************************
 *                (C) Copyright 2003-2014 - Faiumoni E.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Manager for daylight colored zones.
 */
public class WeatherUpdater implements TurnListener {
	/** Logger instance. */
	private static final Logger LOGGER = Logger.getLogger(WeatherUpdater.class);
	/** Weather attribute string. */
	private static final String WEATHER = "weather";
	/** Time between checking if the color should be changed. Seconds. */
	private static final int CHECK_INTERVAL = 79;
	/** Upper limit of the temperature attribute. */
	private static final int TEMP_RANGE = 30;
	/** Prevalence of rain. Roughly the percentage of rainy time. */
	private static final int RAININESS = 10;
	/** Prevalence of fog. Roughly the percentage of foggy time. */
	private static final int FOGGINESS = 4;
	
	/** Singleton instance. */
	private static final WeatherUpdater INSTANCE = new WeatherUpdater();

	/** Managed zone attributes, and their modifiers. */
	private final Map<ZoneAttributes, Modifiers> zones = new HashMap<ZoneAttributes, Modifiers>();	
	/**
	 * Rain attribute. The descriptions are <em>modifiers</em> to be appended
	 * after "rain" or "snow"
	 */
	private final WeatherAttribute rain = new WeatherAttribute(300 / RAININESS, "_light", "", "_heavy");
	/**
	 * Temperature. Just to be used as a modifier to decide between rain and
	 * snow.
	 */
	private final WeatherAttribute temperature = new WeatherAttribute(TEMP_RANGE);
	/** Fogginess attribute. Foggy about 1/25 of time. */
	private final WeatherAttribute fog = new WeatherAttribute(200 / FOGGINESS , "fog", "thick_fog");

	/**
	 * Create a new Daylight instance. Do not use this.
	 */
	private WeatherUpdater() {
		onTurnReached(0);
	}

	/**
	 * Get the Daylight instance.
	 *
	 * @return singleton instance
	 */
	public static WeatherUpdater get() {
		return INSTANCE;
	}

	/**
	 * Make a zone color managed by the daylight colorer.
	 *
	 * @param attr attributes of the zone
	 */
	public void manageAttributes(ZoneAttributes attr) {
		String desc = attr.get(WEATHER);
		Modifiers mods = Modifiers.getModifiers(desc);
		zones.put(attr, mods);
		String weather = describeWeather(Calendar.getInstance(), mods);
		updateAndNotify(attr, weather);
	}

	@Override
	public final void onTurnReached(int currentTurn) {
		updateWeatherStates();
		SingletonRepository.getTurnNotifier().notifyInSeconds(CHECK_INTERVAL, this);
	}

	/**
	 * Update the zone color according to the hour.
	 */
	private void updateWeatherStates() {
		boolean changed = temperature.update();
		changed |= rain.update();
		changed |= fog.update();
		if (changed) {
			updateZones();
		}
	}
	
	/**
	 * Describe the weather based on the current attribute states, time and
	 * zone's weather modifiers.
	 * 
	 * @param calendar determines the time used for  the description
	 * @param mods weather modifiers
	 * 
	 * @return weather description, or <code>null</code> for clear skies
	 */
	private String describeWeather(Calendar calendar, Modifiers mods) {
		String weather = rain.getDescription(mods.rain);
		if (weather != null) {
			weather = rainOrSnow(calendar, mods.temperature) + weather;
		} else {
			weather = fog.getDescription(mods.fog);
		}
		return weather;
	}
	
	/**
	 * Describe either rain or snow, depending on the time, temperature and 
	 * temperature modifiers.
	 * 
	 * @param calendar calendar for checking current time
	 * @param temperatureMod zone's temperature modifier
	 * @return Either "snow" or "rain", depending on the time and temperature
	 */
	private String rainOrSnow(Calendar calendar, int temperatureMod) {
		// Year time modifier. January is the coldest with effect of -60
		int month = calendar.get(Calendar.MONTH);
		month = 10 * Math.abs(month - 6);
		// Day time modifier. Nights are slightly colder, 02 being the coldest
		// with effect of -6
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour = Math.abs((hour + 26) % 24 - 12);
		int temp = temperature.getValue() - hour - month + temperatureMod;
		LOGGER.debug("Modified temp: " + temp);
		if (temp <= -30) {
			return "snow";
		}
		return "rain";
	}
	
	/**
	 * Update a zone's weather attribute, and notify players of the changes.
	 * 
	 * @param attr zone's attribute set
	 * @param weather new weather description
	 */
	private void updateAndNotify(ZoneAttributes attr, String weather) {
		String oldWeather = attr.get(WEATHER);
		// Objects.equals()...
		if ((weather != null && !weather.equals(oldWeather)) 
				|| (weather == null && oldWeather != null)) {
			LOGGER.debug("Weather on " + attr.getZone().describe() + ": " + weather);
			if (weather != null) {
				attr.put(WEATHER, weather);
			} else {
				attr.remove(WEATHER);
			}
			// Notify resident players about the changed weather
			for (Player player : attr.getZone().getPlayers()) {
				// Old clients do not understand content transfer that just
				// update the old map, and end up with no entities on the screen
				if (player.isClientNewerThan("0.97")) {
					StendhalRPAction.transferContent(player);
				}
			}
		}
	}
	
	/**
	 * Check and update all managed zones.
	 */
	private void updateZones() {
		/*
		 * The night time modifier on temperature would technically be a change,
		 * but it's rather useless to check zones just for that. One of the
		 * attributes will soon change anyway.
		 */
		Calendar calendar = Calendar.getInstance();
		LOGGER.debug("Weather change: rain=" + rain.getValue() + ", temp=" + temperature.getValue() + ", fog=" + fog.getValue());
		LOGGER.debug("Weather on typical zone: " + describeWeather(calendar, Modifiers.getModifiers(null)));
		LOGGER.debug("Rain would be:" + rainOrSnow(calendar, 0));
		for (Entry<ZoneAttributes, Modifiers> entry : zones.entrySet()) {
			ZoneAttributes attr = entry.getKey();
			Modifiers mods = entry.getValue();
			String weather = describeWeather(calendar, mods);
			updateAndNotify(attr, weather);
		}
	}
	
	/**
	 * Describes a zone's weather modifier parameters.
	 */
	private static class Modifiers {
		/** Shared instance for zones that do not need any modifiers. */
		private static final Modifiers NO_MODS = new Modifiers(0, 0, 0);
		/** Raininess modifier. Added to global rain state. */
		final int rain;
		/** Temperature modifier. Added to global temperature state. */
		final int temperature;
		/** Fogginess modifier. Added to global fog state. */
		final int fog;
		
		/**
		 * Construct a modifier set.
		 * 
		 * @param rainMod rain state modifier
		 * @param tempMod temperature state modifier
		 * @param fogMod fog state modifier
		 */
		private Modifiers(int rainMod, int tempMod, int fogMod) {
			rain = rainMod;
			temperature = tempMod;
			fog = fogMod;
		}
		
		/**
		 * Get a suitable set of modifiers based on a weather description.
		 * 
		 * @param weatherDesc zone's weather description string
		 * @return modifiers
		 */
		static Modifiers getModifiers(String weatherDesc) {
			// FIXME: implement
			return NO_MODS;
		}
	}
	
	/**
	 * Weather attribute with a range, and optionally a set of descriptions.
	 */
	private static class WeatherAttribute {
		/** Maximum value of the attribute. */
		private final int maxValue;
		/** State descriptions. */
		private final String[] desc;
		/** Current value of the attribute. */
		private int value;
		/** The change direction of the attribute. [-2, 2] */
		private int change;
		
		/**
		 * Create a WeatherAttribute with a maximum value and a set of
		 * descriptions.
		 * 
		 * @param max maximum value of the attribute
		 * @param desc descriptions. If there are fewer descriptions than
		 * 	possible values, then the descriptions correspond to the
		 * 	<em>high</em> end values, and the description for the lower end
		 * 	values are <code>null</code>
		 */
		WeatherAttribute(int max, String... desc) {
			maxValue = max;
			value = Rand.rand(max + 1);
			this.desc = desc;
		}
		
		/**
		 * Update the attribute's internal state randomly.
		 * 
		 * @return <code>true</code> if value of the attribute changed. Note
		 * 	that the corresponding <em>description</em> did not necessarily
		 * 	change, and its changing may depend on the maps' weather modifiers.
		 */
		boolean update() {
			change += Rand.rand(5) - 2;
			// Favor stability. This also keeps the change rate at range [-1, 1] 
			change /= 2;
			int oldValue = value;
			value += change;
			value = Math.max(Math.min(value, maxValue), 0);
			return value != oldValue;
		}
		
		/**
		 * Get the value of the attribute. The value is in range [0, max], where
		 * <code>max</code> is the value given to the constructor.
		 * 
		 * @return attribute value
		 */
		int getValue() {
			return value;
		}
		
		/**
		 * Get the description corresponding to the current value of the
		 * attribute, taking in account a weather modifier.
		 *  
		 * @param modifier weather modifier
		 * @return description, and <code>null</code> if the corresponding
		 * 	weather state has no description.
		 */
		String getDescription(int modifier) {
			int adjusted = Math.max(Math.min(value, maxValue), 0);
			int idx = adjusted - (maxValue - desc.length) - 1;
			if (idx >= 0) {
				return desc[idx];
			}
			return null;
		}
	}
}

