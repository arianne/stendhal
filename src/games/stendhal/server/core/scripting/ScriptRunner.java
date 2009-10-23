/* $Id$ */

package games.stendhal.server.core.scripting;

import games.stendhal.common.CommandlineParser;
import games.stendhal.common.ErrorBuffer;
import games.stendhal.common.ErrorDrain;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.extension.StendhalServerExtension;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * ServerExtension to load Groovy and Java scripts.
 * 
 * @author intensifly
 */
public class ScriptRunner extends StendhalServerExtension implements
		ActionListener {

	private static final int REQUIRED_ADMINLEVEL = 1000;

	private final Map<String, ScriptingSandbox> scripts;

	private final String scriptDir = "data/script/";

	private static final Logger logger = Logger.getLogger(ScriptRunner.class);

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
			final String[] strs = dir.list(new FilenameFilter() {
				public boolean accept(final File dir, final String name) {
					return name.endsWith(".groovy");
				}
			});

			for (int i = 0; i < strs.length; i++) {
				try {
					perform(strs[i]);
				} catch (final Exception e) {
					logger.error("Error while loading " + strs[i] + ":", e);
				}
			}
		}

	}

	@Override
	public synchronized boolean perform(final String name) {
		return perform(name, "load", null, null);
	}
	
	// need that function to filter scripts names 
	public String searchTermToRegex(String searchTerm) {
		final String metaSymbols = new String("*?()[]{}+-.^$|\\");
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
					script = new ScriptInGroovy(scriptDir + trimmedName);
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
				ret = script.execute(player, args);
			}
		}

		return (ret);
	}

	/**
	 * Lists the available scripts
	 *
	 * @param player player requesting the list
	 * @param filterTerm filter
	 * @return true
	 */
	private boolean listScripts(final Player player, final List<String> filterTerm) {
		// *.groovy scripts is in data/script/
		final File dirGroovy = new File(scriptDir);
		// *.class scripts is in data/script/games/stendhal/server/script/
		final File dirClasses = new File(scriptDir+"games/stendhal/server/script/");
		final String[] scriptsGroovy = dirGroovy.list(new FilenameFilter() {
				public boolean accept(final File dir, final String name) {
					return (name.endsWith(".groovy") && (name.indexOf('$') == -1));
				}
			});
		final String[] scriptsJava = dirClasses.list(new FilenameFilter(){
				public boolean accept(final File dir, final String name) {
					// remove filenames with '$' inside because they are inner classes
					return (name.endsWith(".class") && (name.indexOf('$') == -1));
				}
			});

		// concatenating String arrays strs1 and strs2 to strs
		final String[] scripts= new String[scriptsGroovy.length + scriptsJava.length];
		System.arraycopy(scriptsGroovy, 0, scripts, 0, scriptsGroovy.length);
		System.arraycopy(scriptsJava, 0, scripts, scriptsGroovy.length, scriptsJava.length);

		StringBuilder stringBuilder = new StringBuilder();

		if (filterTerm.size()>0) {
			// FIXME: currently only one argument can be parsed becouse 
			// mother function (public void onAction()) hides arguments in case 
			// if several arguments given.
			stringBuilder.append("results for /script -list " + filterTerm.get(0) + " command:\n");
		} else {
			stringBuilder.append("results for /script -list command:\n");
		}

		for (int i = 0; i < scripts.length; i++) {
			// if arguments given, will look for matches.
			if (filterTerm.size()>0) {
				int j = 0;					
				//for (j = 0; j < args.size(); j++) {
					if (scripts[i].matches(searchTermToRegex(filterTerm.get(j)))) {
						stringBuilder.append(scripts[i]+"\n");
					}
				//}
			} else {
				stringBuilder.append(scripts[i]+"\n");
			}
		}	
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

	public void onAction(final Player player, final RPAction action) {

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
				"script", true)) {
			return;
		}
		String text = "usage: #/script #[-execute|-load>|-unload] #<filename> #[<args>]\n mode is either load (default) or remove";
		if (action.has("target")) {

			// concat target and args to get the original string back
			String cmd = action.get("target");
			if (action.has("args")) {
				cmd = cmd + " " + action.get("args");
			}

			// in the simplest case there is only one argument which is the
			// scriptname
			String mode = "execute";
			String script = cmd;

			// parse args if the is a space
			int pos = cmd.indexOf(' ');
			if (pos > -1) {
				// analyse the first word: mode or filename?
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

			// use the same routine as in the client to parse quoted arguments
			final CommandlineParser parser = new CommandlineParser(cmd);
			final ErrorDrain errors = new ErrorBuffer();

			final List<String> args = parser.readAllParameters(errors);

			new GameEvent(player.getName(), "script", script, mode, args.toString()).raise();

			// execute script
			script = script.trim();
			if ("list".equals(mode) || script.endsWith(".groovy") || script.endsWith(".class")) {
				final boolean res = perform(script, mode, player, args);

				if (res) {
					StringBuilder stringBuilder = new StringBuilder();
					if (!"list".equals(mode)) {
					stringBuilder.append("Script \"");
					stringBuilder.append(script);
					stringBuilder.append("\" was successfully ");
					stringBuilder.append(mode);
					} else {
						stringBuilder.append("script directories was successfully list");
					}
					if (mode == "execute") {
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
						if (mode == "execute") {
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
				text = "Invalid filename: It should end with .groovy or .class";
			}
		}

		player.sendPrivateText(text);
	}

}
