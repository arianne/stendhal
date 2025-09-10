/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import games.stendhal.common.CommandlineParser;
import games.stendhal.common.ErrorBuffer;
import games.stendhal.common.ErrorDrain;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.scripting.lua.LuaLoader;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.extension.StendhalServerExtension;
import marauroa.common.game.RPAction;


/**
 * ServerExtension to load Groovy, Lua, and Java scripts.
 *
 * @author intensifly
 */
public class ScriptRunner extends StendhalServerExtension implements ActionListener {

	private static final Logger logger = Logger.getLogger(ScriptRunner.class);

	private static final int REQUIRED_ADMINLEVEL = 1000;

	private final Map<String, ScriptingSandbox> scripts;

	/** Directory where data scripts are located. */
	private static final String scriptDir = "data/script/";

	/** Supported scripting language filename extensions. */
	private static final String[] supportedExt = {"groovy", "lua"};

	/** Names of detected scripts at time of initialization or directly registered. */
	private static final Set<String> registered = Sets.newHashSet();


	/**
	 * Constructor for ScriptRunner.
	 */
	public ScriptRunner() {
		super();
		scripts = new HashMap<String, ScriptingSandbox>();
		CommandCenter.register("script", this, REQUIRED_ADMINLEVEL);
	}

	@Override
	public void init() {
		final URL url = getClass().getClassLoader().getResource(scriptDir);
		if (url != null) {
			final File dir = new File(url.getFile());
			List<String> strs = new ArrayList<>();

			try {
				final Stream<Path> paths = Files.walk(Paths.get(dir.toString())).filter(Files::isRegularFile);
				for (String filepath: paths.map(s -> s.toString()).collect(Collectors.toList())) {
					// trim absolute path prefix
					filepath = filepath.substring(dir.toString().length() + 1);

					for (String ext: supportedExt) {
						ext = "." + ext;

						if (filepath.endsWith(ext)) {
							strs.add(filepath);
							break;
						}
					}
				}
			} catch (final IOException e1) {
				logger.error("Error while recursing scripts");
				e1.printStackTrace();
				return;
			}

			for (int i = 0; i < strs.size(); i++) {
				try {
					perform(strs.get(i));
				} catch (final Exception e) {
					logger.error("Error while loading " + strs.get(i) + ":", e);
				}
			}
		}

		registerResourceScripts();
		registerDataScripts();
	}

	/**
	 * Caches a script name for listing with {@code /script -list} command.
	 *
	 * @param obj
	 *   Script class, instance, or file base name.
	 */
	public static void registerName(Object obj) {
		String name;
		if (obj instanceof String) {
			name = (String) obj;
		} else if (obj instanceof Class) {
			name = ((Class<?>) obj).getSimpleName() + ".class";
		} else {
			name = obj.getClass().getSimpleName() + ".class";
		}
		final boolean newScript = !registered.contains(name);
		registered.add(name);

		if (newScript) {
			logger.info("Registered script: " + name);
		} else {
			logger.warn("Duplicate script: " + name);
		}
	}

	/**
	 * Registers names of detected compiled server scripts.
	 */
	private static void registerResourceScripts() {
		logger.info("Registering resource scripts ...");

		try {
			ArrayList<Class<?>> dir = getClasses("games.stendhal.server.script");
			for (final Class<?> clazz: dir) {
				registerName(clazz);
			}
		} catch (final ClassNotFoundException e) {
			logger.error(e, e);
		} catch (final SecurityException e) {
			logger.error(e, e);
		}
	}

	/**
	 * Registers names of detected data scripts.
	 */
	private static void registerDataScripts() {
		logger.info("Registering data scripts ...");

		final File dirData = new File(scriptDir);

		// *.groovy scripts in data/script/
		final String[] lg = dirData.list(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					return (name.endsWith(".groovy") && (name.indexOf('$') == -1));
				}
			});
		if (lg != null) {
			for (final String name: lg) {
				registerName(name);
			}
		}

		// *.lua scripts in data/script/
		final String[] ll = dirData.list(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return (name.endsWith(".lua") && (name.indexOf('$') == -1));
			}
		});
		if (ll != null) {
			for (final String name: ll) {
				registerName(name);
			}
		}

		// *.class scripts in data/script/games/stendhal/server/script/
		final File dirClasses = new File(scriptDir + "games/stendhal/server/script/");
		final String[] lj = dirClasses.list(new FilenameFilter(){
				@Override
				public boolean accept(final File dir, final String name) {
					// remove filenames with '$' inside because they are inner classes
					return (name.endsWith(".class") && (name.indexOf('$') == -1));
				}
			});
		if (lj != null) {
			for (final String name: lj) {
				registerName(name);
			}
		}
	}

	@Override
	public synchronized boolean perform(final String name) {
		return perform(name, "load", null, null);
	}

	// need that function to filter scripts names
	public String searchTermToRegex(String searchTerm) {
		final String metaSymbols = "*?()[]{}+-.^$|\\";
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < searchTerm.length(); i++) {
			final char c = searchTerm.charAt(i);
			final int n = metaSymbols.indexOf(c);
			if (n == -1) {
				stringBuilder.append(c);
			} else {
				switch (c) {
					case '*':
						stringBuilder.append(".*?");
						break;
					case '?':
						stringBuilder.append(".");
						break;
					default:
						stringBuilder.append('\\');
						stringBuilder.append(c);
						break;
				}
			}
		}
		return stringBuilder.toString();
	}

	private synchronized boolean perform(final String name, final String mode,
			final Player player, final List<String> args) {
		boolean ret = false;

		final String rootDir = scriptDir;

		// block exploit
		if (name.indexOf("..") >= 0) {
			return false;
		}

		// list mode
		if ("list".equals(mode)) {
			return listScripts(player, args);
		}

		final String trimmedName = name.trim();
		// if the script is already running get it, else null it
		ScriptingSandbox script = scripts.get(trimmedName);

		// unloading
		if ("load".equals(mode) || "remove".equals(mode)
				|| "unload".equals(mode)) {

			script = scripts.remove(trimmedName);
			if (script != null) {
				script.unload(player, args);
				ret = true;
			}
			script = null;
		}

		// load and/or execute
		if ("load".equals(mode) || "execute".equals(mode)) {
			boolean ignoreExecute = false;

			// load script if it is not already loaded,
			// if we want to execute we'll also load it if needed
			if (script == null) {
				if (trimmedName.endsWith(".groovy")) {
					script = new ScriptInGroovy(rootDir + trimmedName);
					ignoreExecute = true;
				} else if (trimmedName.endsWith(".lua")) {
					script = LuaLoader.get().createScript(rootDir + trimmedName);
					ignoreExecute = true;
				} else if (trimmedName.endsWith(".class")) {
					script = new ScriptInJava(trimmedName);
				}
				if (script != null) {
					ret = script.load(player, args);
					scripts.put(trimmedName, script);
				}
			}

			if ("execute".equals(mode) && !ignoreExecute) {
				if (script != null) {
					ret = script.execute(player, args);
				} else {
					logger.error("Script not executed: " + trimmedName);
				}
			}
		}

		return (ret);
	}

	/**
	 * Fetch classes of available scripts.
	 *
	 * @param pckgname the package name of scripts
	 * @return list of script classes
	 * @throws ClassNotFoundException if getting the class loader or reading the
	 * 	script resources fail
	 */
	private static ArrayList<Class<?>> getClasses(final String packageName) throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			ClassLoader classLoader = ScriptRunner.class.getClassLoader();
			ImmutableSet<ClassInfo> infos = ClassPath.from(classLoader).getTopLevelClasses(packageName);
			for (ClassInfo info : infos) {
				if ("package-info".equals(info.getSimpleName())) {
					continue;
				}
				classes.add(info.load());
			}
			return classes;
		} catch (IOException e) {
			throw new ClassNotFoundException("failed to list classes");
		}
	}


	/**
	 * Lists the available scripts
	 *
	 * @param player player requesting the list
	 * @param filterTerm filter
	 * @return true
	 */
	private boolean listScripts(final Player player, List<String> filterTerm) {
		List<String> allScripts = new LinkedList<String>();
		allScripts.addAll(registered);
		Collections.sort(allScripts, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareTo(s2);
			}
		});

		StringBuilder stringBuilder = new StringBuilder("Available scripts");
		if (!filterTerm.isEmpty()) {
			stringBuilder.append(" (results for ");
			for (int i = 0; i < filterTerm.size(); i++) {
				stringBuilder.append(" " + filterTerm.get(i));
			}
			stringBuilder.append(")");
		}
		stringBuilder.append(":");

		for (int i = 0; i < allScripts.size(); i++) {
			final String scriptName = allScripts.get(i);
			// if arguments given, will look for matches.
			if (!filterTerm.isEmpty()) {
				int j = 0;
				for (j = 0; j < filterTerm.size(); j++) {
					if (allScripts.get(i).matches(searchTermToRegex(filterTerm.get(j)))) {
						stringBuilder.append("\n- " + scriptName);
					}
				}
			} else {
				stringBuilder.append("\n- " + scriptName);
			}
		}

		stringBuilder.append("\n(end of listing).");
		player.sendPrivateText(stringBuilder.toString());
		return true;
	}

	@Override
	public String getMessage(final String name) {
		final ScriptingSandbox gr = scripts.get(name);
		if (gr != null) {
			return (gr.getMessage());
		}
		return (null);
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "script", true)) {
			return;
		}
		String sender = player.getName();
		if (action.has("sender") && (player.getName().equals("postman"))) {
			sender = action.get("sender");
		}

		String text = "usage: #/script #[-list|-execute|-load|-unload] #<filename> #[<args>]\n mode is either load (default) or remove";
		if (action.has("target")) {

			// concat target and args to get the original string back
			String cmd = action.get("target");
			if (action.has("args")) {
				cmd = cmd + " " + action.get("args");
			}
			// in the simplest case there is only one argument which is the
			// script name
			String mode = "execute";
			String script = cmd;

			/*
			// parse args if there is a space
			int pos = cmd.indexOf(' ');
			if (pos > -1) {
				// Analyze the first word: mode or filename?
				String temp = cmd.substring(0, pos);
				cmd = cmd.substring(pos + 1);
				if (temp.startsWith("-")) {
					// it is "mode"
					mode = temp.substring(1);
					pos = cmd.indexOf(' ');

					if (pos > -1) {
						temp = cmd.substring(0, pos);
						cmd = cmd.substring(pos + 1);
					} else {
						temp = cmd;
					}
				}
				script = temp;
			}
			 */

			// parts of script command.
			final List<String> parts = Arrays.asList(cmd.split(" "));
			cmd = "";
			int scp = 0;
			if (!parts.isEmpty()) {
				if (parts.get(0).startsWith("-")) {
					// it is "mode"
					mode = parts.get(0).substring(1);
					scp = 1;
				}
				// determine where is script name
				if (scp < parts.size()) {
					script = parts.get(scp);
				}
				StringBuilder sb = new StringBuilder(cmd);
				// concatenating script arguments
				for (int i = scp + 1; i < parts.size(); i++) {
					sb.append(' ').append(parts.get(i));
				}
				cmd = sb.toString();
				// for list mode we dont have script name
				if ("list".equals(mode)) {
					cmd = script + " " + cmd;
				}
			}

			// use the same routine as in the client to parse quoted arguments
			final CommandlineParser parser = new CommandlineParser(cmd);
			final ErrorDrain errors = new ErrorBuffer();

			final List<String> args = parser.readAllParameters(errors);

			new GameEvent(sender, "script", script, mode, args.toString()).raise();

			// execute script
			script = script.trim();
			if ("list".equals(mode) || script.endsWith(".groovy") || script.endsWith(".lua") || script.endsWith(".class")) {
				boolean res = false;
				// remove mode from list of arguments
				final int modeArgIdx = args.indexOf("-" + mode);
				if (modeArgIdx >= 0) {
					args.remove(modeArgIdx);
				}
				res = perform(script, mode, player, args);
				if (res) {
					StringBuilder stringBuilder = new StringBuilder();
					if (!"list".equals(mode)) {
					stringBuilder.append("Script \"");
					stringBuilder.append(script);
					stringBuilder.append("\" was successfully ");
					stringBuilder.append(mode);
					} else {
						// we dont want spam messages.
						//stringBuilder.append("script directories was successfully list");
						return;
					}
					if ("execute".equals(mode)) {
						stringBuilder.append("d");
					} else {
						stringBuilder.append("ed");
					}
					stringBuilder.append(".");
					text = stringBuilder.toString();
				} else {
					final String msg = getMessage(script);
					if (msg != null) {
						text = msg;
					} else {
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("Script \"");
						stringBuilder.append(script);
						stringBuilder.append("\" was either not found, or encountered an error during ");
						if ("execute".equals(mode)) {
							stringBuilder.append("execution");
						} else {
							stringBuilder.append(mode);
							stringBuilder.append("ing");
						}
						stringBuilder.append(".");
						text = stringBuilder.toString();
					}
				}
			} else {
				text = "Invalid filename: It should end with .groovy, .lua, or .class";
			}
		}

		player.sendPrivateText(text);
	}
}
