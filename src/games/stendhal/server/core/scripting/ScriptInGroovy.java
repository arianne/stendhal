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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * manages a script written in groovy
 */
public class ScriptInGroovy extends ScriptingSandbox {

	private final String groovyScript;

	private final Binding groovyBinding;

	private static final Logger logger = Logger.getLogger(ScriptInGroovy.class);

	/**
	 * manages a script written in groovy
	 *
	 * @param filename filename
	 */
	public ScriptInGroovy(final String filename) {
		super(filename);
		groovyScript = filename;
		groovyBinding = new Binding();
		groovyBinding.setVariable("game", this);
		groovyBinding.setVariable("logger", logger);
		groovyBinding.setVariable("storage", new HashMap<Object, Object>());

		groovyBinding.setVariable("rules", SingletonRepository.getRuleProcessor());
		groovyBinding.setVariable("world", SingletonRepository.getRPWorld());
	}


	/**
	 * Initial load of this script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	@Override
	public boolean load(final Player player, final List<String> args) {
		groovyBinding.setVariable("player", player);
		if (args != null) {
			groovyBinding.setVariable("args", args.toArray(new String[args.size()]));
		} else {
			groovyBinding.setVariable("args", new String[0]);
		}
		final GroovyShell interp = new GroovyShell(groovyBinding);
		boolean ret = true;

		preExecute(player, args);
		try {
			final File f = new File(groovyScript);
			interp.evaluate(f);
		} catch (final Exception e) {
			logger.error("Exception while sourcing file " + groovyScript, e);
			setMessage(e.getMessage());
			ret = false;
		} catch (final Error e) {
			logger.error("Exception while sourcing file " + groovyScript, e);
			setMessage(e.getMessage());
			ret = false;
		}

		postExecute(player, args, ret);
		return (ret);
	}

	/**
	 * Executes this script.
	 *
	 * @param player
	 *            the admin who load it or <code>null</code> on server start.
	 * @param args
	 *            the arguments the admin specified or <code>null</code> on
	 *            server start.
	 */
	@Override
	public boolean execute(final Player player, final List<String> args) {
		return load(player, args);
	}
}
