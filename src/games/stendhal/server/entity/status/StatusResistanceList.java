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

import games.stendhal.server.entity.RPEntity;

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
public class StatusResistanceList {
	private WeakReference<RPEntity> entityRef;
	
	/** The logger instance */
	final Logger logger;
	
	/** Container for current resistances */
	private Map<StatusType, Double> resistances;
	
	/**
	 * Default constructor the container for status resistances.
	 */
	public StatusResistanceList(RPEntity entity) {
		this.logger = Logger.getLogger(StatusResistanceList.class);
		
		this.entityRef = new WeakReference<RPEntity>(entity);
		this.resistances = new HashMap<StatusType, Double>();
		
		// FIXME: Change to ".isDebugEnabled" and ".debug"
		if (logger.isInfoEnabled()) {
			logger.info("Creating new empty ResistanceList");
		}
	}
	
	/**
	 * Constructor for pre-existing resistance list.
	 */
	public StatusResistanceList(RPEntity entity, Map<StatusType, Double> resistances) {
		this.logger = Logger.getLogger(StatusResistanceList.class);
		
		this.entityRef = new WeakReference<RPEntity>(entity);
		this.resistances = resistances;
		
		// FIXME: Change to ".isDebugEnabled" and ".debug"
		if (logger.isInfoEnabled()) {
			logger.info("Created new non-empty ResistanceList");
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
				logger.error("Cannot set StatusType resistance to " + newValue.toString());
				return;
			} else {
				// Adjust existent resistance
				resistances.put(statusType, newValue);
				
				// FIXME: Change to ".isDebugEnabled()" and ".debug()"
				if (logger.isInfoEnabled()) {
					logger.info("Adjusting value of " + statusType.toString() +
							" to " + newValue.toString());
				}
			}
		} else {
			// Create new resistance
			resistances.put(statusType, value);
			
			// FIXME: Change to ".isDebugEnabled()" and ".debug()"
			if (logger.isInfoEnabled()) {
				logger.info("Created new resistance to " + statusType.toString()
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
	public RPEntity getEntity() {
		return entityRef.get();
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
			// FIXME: Change to ".isDebugEnabled()"
			if (logger.isInfoEnabled()) {
				logger.warn("resistances were not initialized.");
			}
			
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
	 * Completely remove an entity's resistance to a status type.
	 * 
	 * @param statusType
	 * 		Resisted status type
	 */
	public void removeStatusResistance(StatusType statusType) {
		resistances.remove(statusType);
		
		// FIXME: Change to ".isDebugEnabled()"
		if (logger.isInfoEnabled()) {
			logger.info("RPEntity ID " + entityRef.get().getID().toString() +
					": Removed " + statusType.toString() + " resistance");
		}
	}
	
}
