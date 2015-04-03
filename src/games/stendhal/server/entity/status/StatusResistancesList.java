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

import games.stendhal.server.entity.Entity;

import java.lang.ref.WeakReference;
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
	private WeakReference<Entity> entityRef;
	
	/** The logger instance */
	final Logger logger;
	
	/** Container for current resistances */
	private Map<StatusType, Double> resistances;
	
	/**
	 * Default constructor the container for status resistances.
	 * 
	 * @param entity
	 * 		The entity that is using this list
	 */
	public StatusResistancesList(Entity entity) {
		this.logger = Logger.getLogger(StatusResistancesList.class);
		
		this.entityRef = new WeakReference<Entity>(entity);
		this.resistances = new HashMap<StatusType, Double>();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new empty ResistanceList");
		}
	}
	
	/**
	 * Constructor for pre-existing resistance list.
	 */
	public StatusResistancesList(Entity entity, Map<StatusType, Double> resistances) {
		this.logger = Logger.getLogger(StatusResistancesList.class);
		
		this.entityRef = new WeakReference<Entity>(entity);
		this.resistances = resistances;
		
		if (logger.isDebugEnabled()) {
			logger.debug("Created new non-empty ResistanceList");
		}
	}
	
	/**
	 * Adjusts or creates an entity's resistance to a status effect.
	 * 
	 * @param statusType
	 * 		Resisted status type
	 * @param value
	 * 		Adjusted resistance value
	 */
	public void adjustStatusResistance(StatusType statusType, Double value) {
		Double previousValue = resistances.get(statusType);
		if (previousValue != null) {
			Double newValue = previousValue + value;
			if ((newValue > 1.0) || (newValue < 0.0)) {
				logger.error("Cannot set " + statusType.toString()
						+ " resistance to " + newValue.toString());
				return;
			} else {
				// Adjust existent resistance
				resistances.put(statusType, newValue);
			}
		} else {
			// Create new resistance
			resistances.put(statusType, value);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Created new resistance to " + statusType.toString()
						+ " at " + value.toString());
			}
		}
		
		// If entity is no longer resistant to status type then remove
		// reference completely.
		if (resistances.get(statusType) <= 0.0) {
			this.removeStatusResistance(statusType);
		}
	}
	
	/**
	 * Gets the entity that uses this list.
	 *
	 * @return RPEntity or <code>null</code>
	 */
	public Entity getEntity() {
		return entityRef.get();
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
	 * @param type
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
	 * Completely remove an entity's resistance to a status type.
	 * 
	 * @param statusType
	 * 		Resisted status type
	 */
	public void removeStatusResistance(StatusType statusType) {
		resistances.remove(statusType);
		
		if (logger.isDebugEnabled()) {
			logger.debug("Removed " + statusType.toString() + " resistance");
		}
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
	
}
