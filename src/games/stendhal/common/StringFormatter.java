/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * StringFormatter.java
 *
 * Created on 22. Oktober 2005, 14:32
 */

package games.stendhal.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Each parameter name in the control string is replaced by its value.
 * 
 * @author matthias
 */
public class StringFormatter {

	/** start of each parameter. no escaping. */
	private static final String PARAMETER_START = "${";

	/** end of each parameter. no escaping. */
	private static final String PARAMETER_END = "}";

	/** Shows that the cache should be refreshed. */
	private boolean refreshCache;

	/** Cached formatted String. */
	private String cachedString;

	/** The static parts of the control-string. */
	private List<String> staticParts;

	/** Names of the parameter in the correct order. */
	private List<String> parameterPositions;

	/** Names/values of the parameter. */
	private Map<String, String> parameter;

	/** Creates a new instance of StringFormatter. */
	public StringFormatter(String formatString) {
		staticParts = new ArrayList<String>();
		parameterPositions = new ArrayList<String>();
		parameter = new HashMap<String, String>();
		String current = formatString;
		int index;
		boolean hasStart = false;

		do {
			index = current.indexOf(hasStart ? PARAMETER_END : PARAMETER_START);
			if (index >= 0) {
				// we found something
				if (hasStart) {
					// found the end of the parameter definition
					String param = current.substring(PARAMETER_START.length(),
							index);
					current = current.substring(index + PARAMETER_END.length());
					parameter.put(param, "");
					parameterPositions.add(param);
				} else {
					// found start
					String s = current.substring(0, index);
					current = current.substring(index);
					staticParts.add(s);
				}
				hasStart = !hasStart;
			}
		} while (index >= 0);

		staticParts.add(current);
	}

	/** Sets the value of a parameter. */
	public void set(String param, String value) {
		if (parameter.containsKey(param)) {
			parameter.put(param, value);
			refreshCache = true;
		}
	}

	/** Sets the value of a parameter. */
	public void set(String param, int value) {
		if (parameter.containsKey(param)) {
			parameter.put(param, Integer.toString(value));
			refreshCache = true;
		}
	}

	/** toString formats the string. */
	@Override
	public String toString() {
		if ((cachedString == null) || refreshCache) {
			// recalculate the string
			StringBuilder buf = new StringBuilder();
			Iterator<String> staticIt = staticParts.iterator();
			Iterator<String> paramIt = parameterPositions.iterator();
			while (staticIt.hasNext()) {
				buf.append(staticIt.next());
				if (paramIt.hasNext()) {
					buf.append(parameter.get(paramIt.next()));
				}
			}
			cachedString = buf.toString();
		}

		return cachedString;
	}

}
