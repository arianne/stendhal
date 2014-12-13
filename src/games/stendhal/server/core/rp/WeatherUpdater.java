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

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Manager for daylight colored zones.
 */
public class WeatherUpdater implements TurnListener {
	/** The keyword used by the weather adjustments parser. */
	public static final String WEATHER_KEYWORD = "auto";
	
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
	private final WeatherAttribute rain = new WeatherAttribute(3 * 100 / RAININESS, "_light", "", "_heavy");
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
	 * Make a zone color managed by the daylight colorer. Modifiers to the
	 * default weather can be described in form
	 * "auto(rain=value1, temperature=value2, fog=value3)", where any or all
	 * of the modifiers can be omitted, and their order does not matter.
	 * Description parameter "auto" is interpreted as "auto()", that is, no 
	 * weather modifiers.
	 * <br/>
	 * The values are interpreted so that:
	 * <ul>
	 * <li>One step of <code>rain</code> corresponds to adding about 5% to rain
	 * 	probability</li>
	 * <li>One step of <code>temperature</code> corresponds roughly to one month
	 * 	of time difference (compared to coldest month).</li>
	 * <li>One step of <code>fog</code> corresponds to adding about 5%
	 *	to fog probability</li>
	 * </ul>
	 * Negative values are interpreted as corresponding decreases. Note that
	 * rain and especially fog have low probabilities to begin with, so they get
	 * easily turned off completely.
	 * <br/>
	 * If a modifier is specified more than once, the last one takes effect.
	 *
	 * @param attr Attributes of the zone
	 * @param desc The weather attribute value specified for the zone. This is
	 * 	used to determine any zone specific modifiers
	 */
	public void manageAttributes(ZoneAttributes attr, String desc) {
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
		// Rain probability should get roughly a raise by 5% for each point,
		// as described for manageAttributes()
		int mod = (int) (mods.rain * 0.05 * rain.getMax());
		String weather = rain.getDescription(mod);
		if (weather != null) {
			weather = rainOrSnow(calendar, mods.temperature) + weather;
		} else {
			// Similarly 5% for fog
			mod = (int) (mods.fog * 0.05 * fog.getMax());
			weather = fog.getDescription(mod);
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
		// Day time modifier. Nights are slightly colder, 03 being the coldest
		// with effect of -6
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		hour = Math.abs((hour + 21) % 24 - 12) / 2;
		// temperatureMod corresponds to a month, as described in
		// manageAttributes() documentation
		int temp = temperature.getValue() - hour - month + (temperatureMod * 10);
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
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Weather change: rain=" + rain.getValue() + ", temp=" + temperature.getValue() + ", fog=" + fog.getValue());
			LOGGER.debug("Weather on typical zone: " + describeWeather(calendar, Modifiers.getModifiers(WEATHER_KEYWORD)));
			LOGGER.debug("Rain would be:" + rainOrSnow(calendar, 0));
		}
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
		/**
		 * Regexp to match weather expressions with the content between
		 * parentheses as the first capturing group.<br/>
		 * 
		 * <em>Some people, when confronted with a problem, think "I know,
		 * I'll use regular expressions." Now they have two problems.
		 * ­­&mdash; J. Zawinski</em> 
		 */
		private static final Pattern PATTERN = Pattern.compile(WEATHER_KEYWORD + "(?:\\((.*)\\))?");
		
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
			weatherDesc = weatherDesc.trim();
			Matcher matcher = PATTERN.matcher(weatherDesc);
			if (!matcher.matches()) {
				LOGGER.warn("Failed to parse weather expression: " + weatherDesc + " - fall back to default");
				return NO_MODS;
			}
			// stuff between parentheses
			String contents = matcher.group(1);
			if (contents != null) {
				contents = contents.trim();
				if (!contents.isEmpty()) {
					return getModifiersFromParams(contents);
				}
			}
			// No params, use the default
			return NO_MODS;
		}
		
		/**
		 * Get modifiers from a list of parameters.
		 * 
		 * @param paramContents contents of the parameter list
		 * @return modifiers
		 */
		private static Modifiers getModifiersFromParams(String paramContents) {
			int rain = 0;
			int temperature = 0;
			int fog = 0;
			for (String param : paramContents.split(",")) {
				String[] parts = param.split("=");
				if (parts.length != 2) {
					LOGGER.warn("Malformed weather parameter: '" + param + "'");
					continue;
				}
				int value = MathHelper.parseInt(parts[1].trim());
				String modType = parts[0].trim();
				if ("rain".equals(modType)) {
					rain = value;
				} else if ("temperature".equals(modType)) {
					temperature = value;
				} else if ("fog".equals(modType)) {
					fog = value;
				} else {
					LOGGER.warn("Unknown weather modifier: '" + modType + "'");
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("rain, temperature, fog = " + rain + " " + temperature + " " + fog);
			}
			if (rain != 0 || temperature != 0 || fog != 0) {
				return new Modifiers(rain, temperature, fog);
			}
			// If the parameters were not non-zero, or no parameters
			// were successfully read, use the NO_MODS constant
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
		 * Get the maximum value of the attribute.
		 * 
		 * @return maximum value
		 */
		int getMax() {
			return maxValue;
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
			int adjusted = Math.max(Math.min(value + modifier, maxValue), 0);
			int idx = adjusted - (maxValue - desc.length) - 1;
			if (idx >= 0) {
				return desc[idx];
			}
			return null;
		}
	}
}

