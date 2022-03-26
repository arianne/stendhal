/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.scripting;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;

/**
 * Manager for scripts written in Java.
 *
 * @author hendrik
 */
public class ScriptInJava extends ScriptingSandbox {

	private static Logger logger = Logger.getLogger(ScriptInJava.class);

	private Script script;

	private final String classname;
	private URLClassLoader classloader;

	/**
	 * Creates a new script written in Java.
	 *
	 * @param scriptname Name of the script
	 */
	public ScriptInJava(final String scriptname) {
		super(scriptname);
		this.classname = "games.stendhal.server.script." + scriptname.substring(0, scriptname.length() - 6);
	}

	/**
	 * Creates a new instance of this script.
	 *
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	private void newInstance() throws MalformedURLException, ClassNotFoundException,
	        NoSuchMethodException, IllegalAccessException, InvocationTargetException,
	        InstantiationException {
		// Create new class loader
		// with current dir as CLASSPATH
		final File file = new File("./data/script");
		this.classloader = new URLClassLoader(new URL[] { file.toURI().toURL() });
		// load class through new loader
		final Class< ? > aClass = classloader.loadClass(classname);
		script = (Script) aClass.getDeclaredConstructor().newInstance();
	}

	/**
	 * Initial load of this script.
	 *
	 * @param admin
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	@Override
	public boolean load(final Player admin, final List<String> args) {
		final Class< ? >[] signature = new Class< ? >[] { Player.class, List.class, ScriptingSandbox.class };
		final Object[] params = new Object[] { admin, args, this };

		try {
			newInstance();
			final Method[] methods = Script.class.getMethods();
			for (final Method method : methods) {
				logger.debug(method);
			}
			final Method theMethod = Script.class.getMethod("load", signature);
			theMethod.invoke(script, params);
		} catch (final Exception e) {
			logger.debug(e, e);
			setMessage(e.toString());
			return false;
		}
		return true;
	}

	@Override
	public boolean execute(final Player admin, final List<String> args) {
		final Class< ? >[] signature = new Class[] { Player.class, List.class };
		final Object[] params = new Object[] { admin, args };

		if (script == null) {
			return false;
		}

		try {
			preExecute(admin, args);
			final Method theMethod = script.getClass().getMethod("execute", signature);
			theMethod.invoke(script, params);
		} catch (final Exception e) {
			logger.error(e, e);
			setMessage(e.getMessage());
			postExecute(admin, args, false);
			return false;
		} catch (final Error e) {
			logger.error(e, e);
			setMessage(e.getMessage());
			postExecute(admin, args, false);
			return false;
		}
		postExecute(admin, args, true);
		return true;
	}

	/**
	 * Executes this script.
	 *
	 * @param admin
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	@Override
	public void unload(final Player admin, final List<String> args) {
		final Class< ? >[] signature = new Class< ? >[] { Player.class, List.class };
		final Object[] params = new Object[] { admin, args };

		try {
			final Method theMethod = script.getClass().getMethod("unload", signature);
			theMethod.invoke(script, params);
		} catch (final Exception e) {
			logger.error(e, e);
			setMessage(e.getMessage());
		}

		try {
			this.classloader.close();
		} catch (IOException e) {
			logger.warn("tried to close loader", e);
		}

		super.unload(admin, args);
	}

}
