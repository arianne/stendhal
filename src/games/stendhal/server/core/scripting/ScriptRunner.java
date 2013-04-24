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
import java.util.Arrays;
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
				@Override
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
	 * Lists the available scripts
	 *
	 * @param player player requesting the list
	 * @param filterTerm filter
	 * @return true
	 */
	private boolean listScripts(final Player player, List<String> filterTerm) {
		// *.groovy scripts is in data/script/
		final File dirGroovy = new File(scriptDir);
		// *.class scripts is in data/script/games/stendhal/server/script/
		final File dirClasses = new File(scriptDir+"games/stendhal/server/script/");
		final String[] scriptsGroovy = dirGroovy.list(new FilenameFilter() {
				@Override
				public boolean accept(final File dir, final String name) {
					return (name.endsWith(".groovy") && (name.indexOf('$') == -1));
				}
			});
		final String[] scriptsJava = dirClasses.list(new FilenameFilter(){
				@Override
				public boolean accept(final File dir, final String name) {
					// remove filenames with '$' inside because they are inner classes
					return (name.endsWith(".class") && (name.indexOf('$') == -1));
				}
			});
        		
		int scriptsGroovyLength = 0;
		if (scriptsGroovy!=null) {
			scriptsGroovyLength=scriptsGroovy.length;
		}
		int scriptsJavaLength = 0;
		if (scriptsJava!=null) {
			scriptsJavaLength=scriptsJava.length;
		}
		final int scriptsLength = scriptsGroovyLength + scriptsJavaLength; 
		// concatenating String arrays scriptsGroovy and scriptsJava to scripts
		final String[] scripts= new String[scriptsLength];
		
		if (scriptsGroovy!=null) {
		System.arraycopy(scriptsGroovy, 0, scripts, 0, scriptsGroovyLength);
		}
		
		if (scriptsJava!=null) {
		System.arraycopy(scriptsJava, 0, scripts, scriptsGroovyLength, scriptsJavaLength);
		}
		
		StringBuilder stringBuilder = new StringBuilder();

		if (!filterTerm.isEmpty()) {
			stringBuilder.append("results for /script ");
			for (int i=0; i<filterTerm.size(); i++) {
				stringBuilder.append(" "+ filterTerm.get(i));
			}
			stringBuilder.append(":\n");
		}

		for (int i = 0; i < scriptsLength; i++) {
			// if arguments given, will look for matches.
			if (!filterTerm.isEmpty()) {
				int j = 0;					
				for (j = 0; j < filterTerm.size(); j++) {
					if (scripts[i].matches(searchTermToRegex(filterTerm.get(j)))) {
						stringBuilder.append(scripts[i]+"\n");
					}
				}
			} else {
				stringBuilder.append(scripts[i]+"\n");
			}
		}	
		stringBuilder.append("(end of listing).");
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
				for (int i = scp+1; i<parts.size(); i++) {
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
			if ("list".equals(mode) || script.endsWith(".groovy") || script.endsWith(".class")) {
				boolean res = false;
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
				text = "Invalid filename: It should end with .groovy or .class";
			}
		}

		player.sendPrivateText(text);
	}

}
