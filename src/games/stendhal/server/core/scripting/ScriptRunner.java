/* $Id$ */

package games.stendhal.server.core.scripting;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.extension.StendhalServerExtension;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import marauroa.common.game.RPAction;

/**
 * ServerExtension to load groovy scripts.
 * 
 * @author intensifly
 */
public class ScriptRunner extends StendhalServerExtension implements
		ActionListener {

	private static final int REQUIRED_ADMINLEVEL = 1000;

	private Map<String, ScriptingSandbox> scripts;

	private final String scriptDir = "data/script/";

	private static final Logger logger = Logger.getLogger(ScriptRunner.class);

	/**
	 * Constructor for StendhalGroovyRunner.
	 * 
	 */
	public ScriptRunner() {
		super();
		scripts = new HashMap<String, ScriptingSandbox>();
		CommandCenter.register("script", this, REQUIRED_ADMINLEVEL);

	}

	@Override
	public void init() {
		URL url = getClass().getClassLoader().getResource(scriptDir);
		if (url != null) {
			File dir = new File(url.getFile());
			String[] strs = dir.list(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					return name.endsWith(".groovy");
				}
			});
			for (int i = 0; i < strs.length; i++) {
				try {
					perform(strs[i]);
				} catch (Exception e) {
					logger.error("Error while loading " + strs[i] + ":", e);
				}
			}
		}

	}

	@Override
	public synchronized boolean perform(String name) {
		return perform(name, "load", null, null);
	}

	// TODO: document and clean this method
	private synchronized boolean perform(String name, String mode,
			Player player, String[] args) {
		boolean ret = false;
		name = name.trim();

		// block exploit
		if (name.indexOf("..") >= 0) {
			return (ret);
		}

		// if the script is already running get it, else null it
		ScriptingSandbox script = scripts.get(name);

		// unloading
		if ("load".equals(mode) || "remove".equals(mode)
				|| "unload".equals(mode)) {

			script = scripts.remove(name);
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
				if (name.endsWith(".groovy")) {
					script = new ScriptInGroovy(scriptDir + name);
					ignoreExecute = true;
				} else if (name.endsWith(".class")) {
					script = new ScriptInJava(name);
				}
				if (script != null) {
					ret = script.load(player, args);
					scripts.put(name, script);
				}
			}

			if ("execute".equals(mode) && !ignoreExecute) {
				ret = script.execute(player, args);
			}
		}

		return (ret);
	}

	@Override
	public String getMessage(String name) {
		ScriptingSandbox gr = scripts.get(name);
		if (gr != null) {
			return (gr.getMessage());
		}
		return (null);
	}

	public void onAction(Player player, RPAction action) {

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
				"script", true)) {
			return;
		}
		String text = "usage: #/script #[-execute|-load>|-unload] #<filename> #[<args>]\n  mode is either load (default) or remove";
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

			// split remaining args
			String[] args = cmd.split("\\s+");
			if ((args.length == 1) && args[0].equals("")) {
				args = new String[0];
			}

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"script", script, mode, Arrays.asList(args).toString());

			// execute script
			script = script.trim();
			if (script.endsWith(".groovy") || script.endsWith(".class")) {
				boolean res = perform(script, mode, player, args);
				if (res) {
					text = "Script \"" + script + "\" was successfully " + mode
							+ (mode == "execute" ? "d" : "ed") + ".";
				} else {
					String msg = getMessage(script);
					if (msg != null) {
						text = msg;
					} else {
						text = "Script \""
								+ script
								+ "\" was either not found, or encountered an error during "
								+ (mode == "execute" ? "execution" : mode
										+ "ing") + ".";
					}
				}
			} else {
				text = "Invalid filename: It should end with .groovy or .class";
			}
		}
		player.sendPrivateText(text);

	}

}
