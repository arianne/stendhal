package games.stendhal.server;

import games.stendhal.common.Pair;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * @deprecated use TurnListener/TurnNotfier 
 */
@Deprecated
public class StendhalScriptSystem {

	private List<Pair<ScriptCondition, ScriptAction>> scripts;

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalRPRuleProcessor.class);

	private StendhalScriptSystem() {
		scripts = new CopyOnWriteArrayList<Pair<ScriptCondition, ScriptAction>>();
	}

	private static StendhalScriptSystem instance;

	public static StendhalScriptSystem get() {
		if (instance == null) {
			instance = new StendhalScriptSystem();
		}

		return instance;
	}

	public Pair<ScriptCondition, ScriptAction> addScript(ScriptCondition condition, ScriptAction action) {
		Pair<ScriptCondition, ScriptAction> scriptPair = new Pair<ScriptCondition, ScriptAction>(condition, action);
		scripts.add(scriptPair);
		return (scriptPair);
	}

	public void removeScript(Pair<ScriptCondition, ScriptAction> scriptPair) {
		scripts.remove(scriptPair);
	}

	public void logic() {
		for (Pair<ScriptCondition, ScriptAction> script : scripts) {
			try {
				if ((script.first() == null) || script.first().fire()) {
					if (script.second() != null) {
						script.second().fire();
					}
				}
			} catch (Exception e) {
				logger.error("error in StendhalScriptSystem", e);
			}
		}
	}
}
