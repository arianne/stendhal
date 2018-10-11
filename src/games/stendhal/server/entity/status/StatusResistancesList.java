/***************************************************************************
 *                   (C) Copyright 2015 - Marauroa                         *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author AntumDeluge
 * 		Some code based on games.stendhal.server.entity.status.StatusList
 *
 * Container that holds information about the entities current resistances
 * to status effects.
 */
public class StatusResistancesList {
	/** The logger instance */
	private static final Logger logger = Logger.getLogger(StatusResistancesList.class);

	/** Container for current resistances */
	private Map<StatusType, Double> resistances;

	/**
	 * Default constructor the container for status resistances.
	 */
	public StatusResistancesList() {
		this.resistances = new HashMap<StatusType, Double>();
		logger.debug("Creating new empty ResistanceList");
	}

	/**
	 * Get the Map class for parsing.
	 *
	 * @return
	 * 		Map class of resistance list
	 */
	public Map<StatusType, Double> getMap() {
		return this.resistances;
	}

	/**
	 * Find the resistance to a specified status type.
	 *
	 * @param statusType
	 * 		Status type to be resisted
	 * @return
	 * 		Resistance value
	 */
	public double getStatusResistance(StatusType statusType) {
		Double res = 0.0;

		/* Resistances are not initialized. Returning 0 resistance value */
		if (resistances == null) {
			logger.warn("resistances list was not initialized.");
			return res;
		}

		res = resistances.get(statusType);

		/* Resistance does not exist. */
		if (res == null) {
			return 0.0;
		}

		/* Safeguarding */
		if (res > 1.0) {
			res = 1.0;
		} else if (res < 0.0) {
			res = 0.0;
		}

		return res;
	}

	/**
	 * Helper method to find if status list is empty after initialized.
	 *
	 * @return
	 * 		<code>true</true> if empty, <code>false</code> if not
	 */
	public Boolean isEmpty() {
		return this.resistances.isEmpty();
	}

	/**
	 * Reset or create resistances after construction.
	 *
	 * @param resistanceList
	 * 		Status resistances to be created
	 */
	public void setStatusResistances(Map<StatusType, Double> resistanceList) {
		this.resistances = resistanceList;
	}

	@Override
	public String toString() {
		StringBuilder st = new StringBuilder();
		int idx = 0;
		for (Map.Entry<StatusType, Double> stype : resistances.entrySet()) {
			if (idx > 0) {
				st.append(", " + stype.getKey().toString() + "=" + stype.getValue().toString());
			} else {
				st.append(stype.getKey().toString() + "=" + stype.getValue().toString());
			}

			idx++;
		}

		return st.toString();
	}
}
