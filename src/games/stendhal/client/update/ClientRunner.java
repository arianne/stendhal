/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.update;

import java.lang.reflect.Method;

/**
 * @author hendrik
 */
class ClientRunner {

	/**
	 * Starts stendhal.
	 *
	 * @param args
	 *            args
	 */
	static void run(final String[] args) {


		try {
			// invoke real client with reflection in order to prevent
			// a class-load-time dependency.

			// get class and create an object of it
			final Class<?> clazz = Class.forName("games.stendhal.client.update.Bootstrap");
			final Object object = clazz.getDeclaredConstructor().newInstance();

			// get param values of boot method
			final Object[] params = new Object[2];
			params[0] = "games.stendhal.client.stendhal";
			params[1] = args;

			// get types of params
			final Class<?>[] paramTypes = new Class[2];
			for (int i = 0; i < params.length; i++) {
				paramTypes[i] = params[i].getClass();
			}

			// get method and invoke it
			// IGNORE THIS WARNING BECAUSE THIS CODE NEEDS TO BE COMPILED FOR
			// OLDER JREs.
			final Method method = clazz.getMethod("boot", paramTypes);
			method.invoke(object, params);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
