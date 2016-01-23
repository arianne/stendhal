/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.puzzle;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import games.stendhal.server.entity.Entity;

/**
 * a puzzle building block
 *
 * @author hendrik
 */
public class PuzzleBuildingBlock {
	private String zoneName;
	private String name;
	private Entity entity;
	private List<String> dependencies = new LinkedList<>();
	private HashMap<String, String> values = new HashMap<>();
	private HashMap<String, String> definitions = new HashMap<>();

	/**
	 * creates a PuzzleBuildingBlock
	 *
	 * @param zoneName name of zone
	 * @param name unique name of entity in zone
	 * @param entity Entity
	 */
	public PuzzleBuildingBlock(String zoneName, String name, Entity entity) {
		this.zoneName = zoneName;
		this.name = name;
		this.entity = entity;
		this.dependencies = new LinkedList<>();
	}

	/**
	 * defines a property
	 *
	 * @param variable name of property
	 * @param expression expression
	 * @param defaultValue default value
	 */
	public void defineProperty(String variable, String expression, String defaultValue) {
		values.put(variable, defaultValue);
		if (expression != null) {
			definitions.put(variable, expression);
			// TODO: update dependencies
		}
	}

	/**
	 * processes all expressions and updates properties
	 */
	private void processExpressions() {
		boolean notificationRequired = false;
		for (Map.Entry<String, String> entry : definitions.entrySet()) {
			String variable = entry.getKey();
			String expression = entry.getValue();
			String value = evaluateExpression(expression);
			if (putInternal(variable, value)) {
				notificationRequired = true;
			}
		}
		if (notificationRequired) {
			PuzzleEventDispatcher.get().notify(this);
		}
	}

	/**
	 * evaluates an expression
	 *
	 * @param expression expression
	 * @return result
	 */
	private String evaluateExpression(String expression) {
		// TODO
		return "true";
	}

	/**
	 * sets the name of a property internally without triggering the notification 
	 * mechanism. This method is useful for batch processing with a combined
	 * notifications at the end.
	 * 
	 * @param variable name of property
	 * @param value value of property
	 * @return true, if notification is required; false otherwise
	 */
	private boolean putInternal(String variable, String value) {
		String oldValue = values.get(variable);
		values.put(variable, value);

		// notification is required, if and only if there was a real change
		if (oldValue == null) {
			return value != null;
		} else {
			return !oldValue.equals(value);
		}
	}

	/**
	 * sets the name of a property
	 *
	 * @param variable name of property
	 * @param value value of property
	 */
	public void put(String variable, String value) {
		if (putInternal(variable, value)) {
			PuzzleEventDispatcher.get().notify(this);
		}
	}

	/**
	 * gets the value of a property
	 *
	 * @param variable name of property
	 * @return value of property
	 */
	public String get(String variable) {
		return values.get(variable);
	}

	/**
	 * gets the name of the zone
	 *
	 * @return zone
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * gets the name of building block
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * listens to input change events
	 */
	public void onInputChanged() {
		processExpressions();
		entity.update();
	}

	/**
	 * gets a list of dependencies that this entity needs to listen to
	 *
	 * @return list of dependencies
	 */
	public List<String> getDependencies() {
		return ImmutableList.copyOf(dependencies);
	}
}
