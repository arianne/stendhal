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
import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import games.stendhal.server.entity.mapstuff.PuzzleEntity;
import groovy.lang.Script;

/**
 * a puzzle building block
 *
 * @author hendrik
 */
public class PuzzleBuildingBlock {
	private String zoneName;
	private String name;
	private PuzzleEntity entity;
	private List<String> dependencies = new LinkedList<>();
	private HashMap<String, Object> values = new HashMap<>();
	private HashMap<String, Script> definitions = new HashMap<>();

	/**
	 * creates a PuzzleBuildingBlock
	 *
	 * @param zoneName name of zone
	 * @param name unique name of entity in zone
	 * @param entity Entity
	 */
	public PuzzleBuildingBlock(String zoneName, String name, PuzzleEntity entity) {
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
	 */
	public void defineProperty(String variable, String expression) {
		if (expression != null) {
			definitions.put(variable,
					PuzzleEventDispatcher.get().parseExpression(this, expression));

			Scanner sc = new Scanner(expression);
		    Pattern pattern = Pattern.compile("\"[^\"]*\"|[A-Za-z0-9._]+");
		    String token = sc.findInLine(pattern);
		    while (token != null) {
		    	if (token.charAt(0) != '"' && token.contains(".")) {
		    		this.dependencies.add(token.substring(0, token.lastIndexOf('.')));
		    	}
		        token = sc.findInLine(pattern);
		    }
		    sc.close();
		}
	}

	/**
	 * processes all expressions and updates properties
	 */
	private void processExpressions() {
		boolean notificationRequired = false;
		for (Map.Entry<String, Script> entry : definitions.entrySet()) {
			String variable = entry.getKey();
			Script script = entry.getValue();
			Object value = script.run();
			if (putInternal(variable, value)) {
				notificationRequired = true;
			}
		}
		if (notificationRequired) {
			PuzzleEventDispatcher.get().notify(this);
		}
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
	private boolean putInternal(String variable, Object value) {
		Object oldValue = values.get(variable);
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
	public void put(String variable, Object value) {
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
	public Object get(String variable) {
		return values.get(variable);
	}

	/**
	 * gets the value of a property
	 *
	 * @param variable name of property
	 * @param clazz class to cast the value to
	 * @return value of property
	 */
	public <T> T get(String variable, Class<T> clazz) {
		return clazz.cast(values.get(variable));
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
		if (entity != null) {
			entity.puzzleExpressionsUpdated();
		}
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
