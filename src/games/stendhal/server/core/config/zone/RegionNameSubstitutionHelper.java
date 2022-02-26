/* $Id$ */
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
package games.stendhal.server.core.config.zone;

import java.util.HashMap;
import java.util.Map;
/**
 * Helper class to substitute region names
 * i.e. it should name "magic" to "magic city"
 *
 * @author madmetzger
 */
public class RegionNameSubstitutionHelper {

	/** The singleton instance. */
	private static RegionNameSubstitutionHelper instance;

	private final Map<String, String> replacements = new HashMap<String, String>();


	/**
	 * Singleton access method
	 *
	 * @return the singleton instance
	 */
	public static RegionNameSubstitutionHelper get() {
		if(instance == null) {
			instance = new RegionNameSubstitutionHelper();
		}

		return instance;
	}

	private RegionNameSubstitutionHelper() {
		replacements.put("magic", "magic city");
		replacements.put("wofol", "wofol city");
		replacements.put("sedah", "sedah city");
		replacements.put("adventure", getDefaultRegion());
		replacements.put("admin", getDefaultRegion());
		replacements.put("hell", getDefaultRegion());
		replacements.put("xxxx", getDefaultRegion());
		replacements.put("testing", getDefaultRegion());
		//TODO: remove when bug #3060003 is fixed
		replacements.put("pillar", "semos");
		replacements.put("plain", "semos");
	}

	/**
	 * Replaces the given zone name if configured. If no replacement is defined the original name is returned
	 *
	 * @param name
	 * @return the replaced name
	 */
	public String replaceRegionName(String name) {
		if(replacements.containsKey(name)) {
			return replacements.get(name);
		}
		return name;
	}

	/**
	 * @return the name of the default region
	 */
	public String getDefaultRegion() {
		return "no_region";
	}
}
