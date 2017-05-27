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
package games.stendhal.server.core.rule.defaultruleset.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

public abstract class AbstractCreator<T> {

	private static final Logger logger = Logger.getLogger(AbstractCreator.class);

	protected final Constructor< ? > construct;

	private final String creatorFor;

	/**
	 * Create a new AbstracCreator.
	 *
	 * @param construct
	 * @param creatorFor
	 */
	public AbstractCreator(Constructor<?> construct, String creatorFor) {
		super();
		this.construct = construct;
		this.creatorFor = creatorFor;
	}

	protected abstract T createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException;

	public T create() {
		try {
			return createObject();
		} catch (final IllegalAccessException ex) {
			logger.error("Error creating object: Used constructor is not accessible." , ex);
		} catch (final InstantiationException ex) {
			logger.error("Error creating object: Object cannot be instantiated (i.e. class may be abstract)", ex);
		} catch (final InvocationTargetException ex) {
			logger.error("Error creating object: Exception thrown during constructor call.", ex.getCause());
		} catch (final ClassCastException ex) {
			/*
			 * Wrong type (i.e. not [subclass of])
			 */
			logger.error("Implementation for is no an subclass of "+creatorFor );
		}

		return null;
	}

}
