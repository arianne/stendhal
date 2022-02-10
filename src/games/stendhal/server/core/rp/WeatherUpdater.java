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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.mapstuff.WeatherEntity;
import marauroa.common.Pair;

/**
 * Manager for zones with changing weather.
 */
public class WeatherUpdater implements TurnListener {

	/** Logger instance. */
	private static final Logger LOGGER = Logger.getLogger(WeatherUpdater.class);

	/** The singleton instance. */
	private static WeatherUpdater instance;

	/** The keyword used by the weather adjustments parser. */
	public static final String WEATHER_KEYWORD = "varying";
	/** Weather attribute string. */
	private static final String WEATHER = "weather";
	/** Time between checking if the weather should be changed. Seconds. */
	private static final int CHECK_INTERVAL = 79;
	/**
	 * The tendency of weather attributes to keep their current value. Must be
	 * at least 1.
	 */
	private static final int WEATHER_STABILITY = 6;
	/** Upper limit of the temperature attribute. */
	private static final int TEMP_RANGE = 30;
	/** Prevalence of rain. Roughly the percentage of rainy time. */
	private static final double RAININESS = 10;
	/** Prevalence of fog. Roughly the percentage of foggy time. */
	private static final double FOGGINESS = 4;
	/**
	 * Rough percentage of rains that are thunderstorms. Note that triggering
	 * thunder also demands a warm temperature, and this percentage
	 * <em>only</em> is in effect when the temperature is high enough.
	 */
	private static final double THUNDER_PREVALENCE = 5;

	/** Data about managed zones. */
	private final Collection<ZoneData> zones = new ArrayList<ZoneData>();

	/**
	 * Rain attribute. The descriptions are <em>modifiers</em> to be appended
	 * after "rain" or "snow"
	 */
	private final WeatherAttribute rain = new WeatherAttribute((int) Math.round(3 * 100 / RAININESS - 1), "_light", "", "_heavy");
	/**
	 * Temperature. Just to be used as a modifier to decide between rain and
	 * snow.
	 */
	private final WeatherAttribute temperature = new WeatherAttribute(TEMP_RANGE);
	/** Fogginess attribute. Foggy about 1/25 of time. */
	private final WeatherAttribute fog = new WeatherAttribute((int) Math.round(200 / FOGGINESS - 1) , "fog", "fog_heavy");
	/**
	 * Thunder attribute. This is used to turn on and off the weather entities
	 * on managed zones. Roughly one in 20 rains will be thunders.
	 */
	private final WeatherAttribute thunder = new WeatherAttribute((int) Math.round(200 / THUNDER_PREVALENCE - 1), "", "");


	/**
	 * Get the WeatherUpdater instance.
	 *
	 * @return singleton instance
	 */
	public static WeatherUpdater get() {
		if (instance == null) {
			instance = new WeatherUpdater();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 *
	 * Create a new WeaterUpdater instance. Do not use this.
	 */
	private WeatherUpdater() {
		onTurnReached(0);
	}

	/**
	 * Make a zone weather managed by the weather updater. Modifiers to the
	 * default weather can be described in form
	 * "varying(rain=value1, temperature=value2, fog=value3)", where any or all
	 * of the modifiers can be omitted, and their order does not matter.
	 * Description parameter "varying" is interpreted as "varying()", that is,
	 * no weather modifiers.
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
		WeatherEntity entity = new WeatherEntity();
		ZoneData data = new ZoneData(attr, mods, entity);
		zones.add(data);
		attr.getZone().add(entity);

		Pair<String, Boolean> weather = describeWeather(Calendar.getInstance(), mods);
		updateAndNotify(data, weather);
	}

	@Override
	public final void onTurnReached(int currentTurn) {
		updateWeatherStates();
		SingletonRepository.getTurnNotifier().notifyInSeconds(CHECK_INTERVAL, this);
	}

	/**
	 * Update the zone weather states.
	 */
	private void updateWeatherStates() {
		boolean changed = temperature.update();
		changed |= rain.update();
		changed |= fog.update();
		changed |= thunder.update();
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
	 * @return A pair of weather description and a boolean for thunder. The
	 * 	description will be <code>null</code> for clear skies
	 */
	private Pair<String, Boolean> describeWeather(Calendar calendar, Modifiers mods) {
		// Rain probability should get roughly a raise by 5% for each point,
		// as described for manageAttributes()
		int mod = (int) (mods.rain * 0.05 * rain.getMax());
		String weather = rain.getDescription(mod);
		if (weather != null) {
			Pair<String, Boolean> rainDesc = describeRain(calendar, mods.temperature);
			return new Pair<String, Boolean>(rainDesc.first() + weather, rainDesc.second());
		} else {
			// Similarly 5% for fog
			mod = (int) (mods.fog * 0.05 * fog.getMax());
			weather = fog.getDescription(mod);
			if (weather != null) {
				return new Pair<String, Boolean>(weather, Boolean.FALSE);
			}
		}
		return new Pair<String, Boolean>(null, Boolean.FALSE);
	}

	/**
	 * Describe either rain or snow, depending on the time, temperature and
	 * temperature modifiers.
	 *
	 * @param calendar calendar for checking current time
	 * @param temperatureMod zone's temperature modifier
	 * @return A pair of "rain" or "snow", and a boolean marking if the rain is
	 * 	thunder
	 */
	private Pair<String, Boolean> describeRain(Calendar calendar, int temperatureMod) {
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
		LOGGER.debug("Modified temp: " + temp + " zone modifier: " + temperatureMod);
		if (temp <= -30) {
			return new Pair<String, Boolean>("snow", Boolean.FALSE);
		}
		// Require warmth for thunder
		return new Pair<String, Boolean>("rain", temp >= -5 && thunder.getDescription(0) != null);
	}

	/**
	 * Update a zone's weather attribute, and notify players of the changes.
	 *
	 * @param zone zone's data set
	 * @param weather Pair of new weather description string, and a Boolean
	 *	determining if thunder should be activated
	 */
	private void updateAndNotify(ZoneData zone, Pair<String, Boolean> weather) {
		ZoneAttributes attr = zone.getAttributes();
		zone.getEntity().setThunder(weather.second());

		String desc = weather.first();
		String oldWeather = attr.get(WEATHER);
		// Objects.equals()...
		if (!Objects.equals(desc, oldWeather)) {
			LOGGER.debug("Weather on " + attr.getZone().describe() + ": "
				+ desc + (weather.second() ? ", thundering" : ""));
			if (desc != null) {
				attr.put(WEATHER, desc);
			} else {
				attr.remove(WEATHER);
			}
			// Notify resident players about the changed weather
			attr.getZone().notifyOnlinePlayers();
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
			LOGGER.debug("Weather change: rain=" + rain.getValue() + "/"
					+ rain.getMax() + ", temp="	+ temperature.getValue() + "/"
					+ temperature.getMax() + ", fog=" + fog.getValue() + "/"
					+ fog.getMax() + ", thunder=" + thunder.getValue() + "/"
					+ thunder.getMax());
			LOGGER.debug("Weather on typical zone: " + describeWeather(calendar, Modifiers.getModifiers(WEATHER_KEYWORD)));
			LOGGER.debug("Rain would be:" + describeRain(calendar, 0).first());
		}
		for (ZoneData zone : zones) {
			updateAndNotify(zone, describeWeather(calendar, zone.getModifiers()));
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
		/** The change direction of the attribute. [-1, 1] */
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
			change += Rand.rand(2 * WEATHER_STABILITY + 1) - WEATHER_STABILITY;
			// Favor stability. This also keeps the change rate at range [-1, 1]
			change /= WEATHER_STABILITY;
			int oldValue = value;
			value = MathHelper.clamp(value + change, 0, maxValue);
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
			int adjusted = MathHelper.clamp(value + modifier, 0, maxValue);
			int idx = adjusted - (maxValue - desc.length) - 1;
			if (idx >= 0) {
				return desc[idx];
			}
			return null;
		}
	}

	/**
	 * Container for weather related zone data.
	 */
	private static class ZoneData {
		/** Zone's attribute map. */
		private final ZoneAttributes attributes;
		/** Weather modifiers of the zone. */
		private final Modifiers modifiers;
		/** Weather entity of the zone. */
		private final WeatherEntity entity;

		/**
		 * Create a ZoneData.
		 *
		 * @param attributes zone's attributes
		 * @param modifiers zone's weather modifiers
		 * @param entity weather entity of the zone
		 */
		ZoneData(ZoneAttributes attributes, Modifiers modifiers, WeatherEntity entity) {
			this.attributes = attributes;
			this.modifiers = modifiers;
			this.entity = entity;
		}

		/**
		 * Get the zone's attribute map.
		 *
		 * @return zone attributes
		 */
		ZoneAttributes getAttributes() {
			return attributes;
		}

		/**
		 * Get the zone's weather modifiers.
		 *
		 * @return modifiers
		 */
		Modifiers getModifiers() {
			return modifiers;
		}

		/**
		 * Get the zone's weather entity.
		 *
		 * @return weather entity
		 */
		WeatherEntity getEntity() {
			return entity;
		}
	}
}
