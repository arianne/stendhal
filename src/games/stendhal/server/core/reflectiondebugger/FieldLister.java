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
package games.stendhal.server.core.reflectiondebugger;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import marauroa.common.Pair;

/**
 * Lists the contents all fields of a class and its super classes.
 *
 * @author hendrik
 */
public class FieldLister {
	private static final String NULL_STRING = "null";
	private static Logger logger = Logger.getLogger(FieldLister.class);

	private TreeMap<String, Pair<String, String>> fieldsTypesValues;
	private Object object = null;

	/**
	 * Creates a new FieldLister.
	 *
	 * @param object object to inspect
	 */
	public FieldLister(final Object object) {
		this.object = object;
	}

	/**
	 * lists all direct fields for this class and the
	 * corresponding values of the known object.
	 *
	 * @param clazz Class
	 */
	private void list(final Class< ? > clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();
			String type = field.getType().toString();
			String value = getValue(field);
			fieldsTypesValues.put(name, new Pair<String, String>(type, value));
		}
	}

	/**
	 * Extracts the value of a field and converts it into a String. This method
	 * can deal with null values.
	 *
	 * @param field Field
	 * @return String representation of value
	 */
	private String getValue(final Field field) {
		Object objValue = null;
		try {
			objValue = field.get(object);
		} catch (IllegalArgumentException e) {
			logger.error(e, e);
		} catch (IllegalAccessException e) {
			logger.error(e, e);
		}
		String value = NULL_STRING;
		if (objValue != null) {
			value = objValue.toString();
		}
		return value;
	}

	/**
	 * Scans the object for direct and indirect fields.
	 * Use getResult to get the result.
	 */
	public void scan() {
		fieldsTypesValues = new KeepFirstTreeMap<String, Pair<String, String>>();
		if (object == null) {
			return;
		}

		// Note: class.getFields() returns all direct and indirect public fields.
		// class.getDeclaredFields returns all direct fields including private
		// ones. So in order to get all fields we need to call getDeclaredFields
		// for this class and all parent classes.

		Class< ? > clazz = object.getClass();
		do {
			list(clazz);
			clazz = clazz.getSuperclass();
		} while (clazz != null);
	}

	/**
	 * Retrieves the result. Note: You need to call scan first.
	 *
	 * @return Map with field names as keys and a pair of type and value.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Pair<String, String>> getResult() {
		return (Map<String, Pair<String, String>>) fieldsTypesValues.clone();
	}
}
