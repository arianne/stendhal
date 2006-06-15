package games.stendhal.server.scripting;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalServerExtension;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Player;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.server.game.RPWorld;

public class StendhalGroovyRunner extends StendhalServerExtension {
	private Map<String, StendhalGroovyScript> scripts;

	private final String scriptDir = "data/script/";

	public StendhalGroovyRunner(StendhalRPRuleProcessor rp,
			StendhalRPWorld world) {
		super(rp, world);
		scripts = new HashMap<String, StendhalGroovyScript>();
		StendhalRPRuleProcessor.register("script", this);
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
				perform(strs[i]);
			}
		}

	}

	@Override
	public synchronized boolean perform(String name) {
		return perform(name, null);
	}

	private synchronized boolean perform(String name, Player player) {
		boolean ret = false;
		StendhalGroovyScript gr;
		name = name.trim();
		if (getClass().getClassLoader().getResource(scriptDir + name) != null) {
			if ((gr = scripts.remove(name)) != null) {
				gr.unload();
			}
			gr = new StendhalGroovyScript(scriptDir + name, rules, world, player);
			ret = gr.load();
			scripts.put(name, gr);
		}
		return (ret);
	}

	@Override
	public String getMessage(String name) {
		StendhalGroovyScript gr = scripts.get(name);
		if (gr != null) {
			return (gr.getMessage());
		}
		return (null);
	}

	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onScript");

		System.out.println("onScript " + action);

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "script", true)) {
			return;
		}

		if (action.has("target")) {
			String script = action.get("target");
			String text = "Script " + script + "not found!";
			if (perform(script, player)) {
				text = "Script " + script + " was successfully executed.";
			} else {
				String msg = getMessage(script);
				if (msg != null) {
					text = msg;
				} else {
					text = "Script " + script
							+ " not found or unexpected error!";
				}
			}
			player.setPrivateText(text);
			rules.removePlayerText(player);
		}
		Log4J.finishMethod(logger, "onScript");
	}

}
