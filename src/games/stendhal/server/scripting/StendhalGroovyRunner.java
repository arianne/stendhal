/* $Id$ */

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

import org.apache.log4j.Logger;

/**
 * ServerExtension to load groovy scripts
 *
 * @author intensifly
 */
public class StendhalGroovyRunner extends StendhalServerExtension {
	private Map<String, StendhalGroovyScript> scripts;

	private final String scriptDir = "data/script/";
	private static final Logger logger = Log4J.getLogger(StendhalGroovyRunner.class);

	/**
	 * Constructor for StendhalGroovyRunner 
	 * @param rp     StendhalRPRuleProcessor
	 * @param world  StendhalRPWorld
	 */
	public StendhalGroovyRunner(StendhalRPRuleProcessor rp,
            StendhalRPWorld world) {
        super(rp, world);
        scripts = new HashMap<String, StendhalGroovyScript>();
        StendhalRPRuleProcessor.register("script", this);
        AdministrationAction.registerCommandLevel("script", 1000);
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
		return perform(name, "load", null, null);
	}

	private synchronized boolean perform(String name, String mode, Player player, String[] args) {
		boolean ret = false;
		StendhalGroovyScript gr = scripts.get(name);
		name = name.trim();
        if("load".equals(mode) || "remove".equals(mode) || "unload".equals(mode)) {
            if ((gr = scripts.remove(name)) != null) {
                gr.unload();
                ret = true;
            }
            gr = null;
        }
        if("load".equals(mode) || "execute".equals(mode)) {
            if (getClass().getClassLoader().getResource(scriptDir + name) != null) {
            	if (gr == null) {
            		gr = new StendhalGroovyScript(scriptDir + name, rules, world);
            	}
                ret = gr.load(player, args);
                scripts.put(name, gr);
            }            
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

        if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player,
                "script", true)) {
            return;
        }
        String text = "usage: /script [-execute|-load>|-unload] <filename> [<args>]\n  mode is either load (default) or remove";
        if (action.has("target")) {

        	// concat target and args to get the original string back
            String cmd = action.get("target");
            if (action.has("args")) {
            	cmd = cmd + " " + action.get("args");
            }

            // in the simplest case there is only one argument which is the scriptname
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
            String[] args = cmd.split(" ");

            // execute script
            script = script.trim();
            if (script.endsWith(".groovy")) {
	            boolean res = perform(script, mode, player, args);
	            if (res) {
		                text = "Script " + script + " was successfully executed (" + mode + ").";
	            } else {
	                String msg = getMessage(script);
	                if (msg != null) {
	                    text = msg;
	                } else {
	                    text = "Script "
	                        + script
	                        + " not found or unexpected error while performing "
	                        + mode;
	                }
	            }
            } else {
            	text = "Invalid filename: It should end with .groovy";
            }
        }
        player.sendPrivateText(text);
        Log4J.finishMethod(logger, "onScript");
    }

}
