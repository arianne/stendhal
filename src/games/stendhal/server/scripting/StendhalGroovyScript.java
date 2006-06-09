package games.stendhal.server.scripting;

import groovy.lang.GroovyShell;
import groovy.lang.Binding;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import games.stendhal.common.Pair;
import games.stendhal.server.*;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.Player;
import marauroa.common.game.RPObject;
import marauroa.common.Log4J;
import org.apache.log4j.Logger;
import games.stendhal.server.StendhalRPAction;

public class StendhalGroovyScript {
	private String groovyScript;

	private Binding groovyBinding;

	private List<NPC> loadedNPCs = new CopyOnWriteArrayList<NPC>();

	private List<RPObject> loadedRPObjects = new CopyOnWriteArrayList<RPObject>();

	private List<Pair<ScriptCondition, ScriptAction>> loadedScripts = new CopyOnWriteArrayList<Pair<ScriptCondition, ScriptAction>>();

	private StendhalScriptSystem scripts;

	private String groovyExceptionMessage;

	StendhalRPRuleProcessor rules;

	StendhalRPWorld world;

	StendhalRPZone zone;

	private static final Logger logger = Log4J
			.getLogger(StendhalGroovyScript.class);

	public StendhalGroovyScript(String filename, StendhalRPRuleProcessor rp,
			StendhalRPWorld world) {
		groovyScript = filename;
		groovyBinding = new Binding();
		this.rules = rp;
		this.world = world;
		this.scripts = StendhalScriptSystem.get();
		groovyBinding.setVariable("game", this);
		groovyBinding.setVariable("logger", logger);
	}

	public boolean setZone(String name) {
		zone = (StendhalRPZone) world.getRPZone(name);
		return (zone != null);
	}

	public StendhalRPZone addZone(String name) {
		try {
			zone = world.addArea(name);
			logger.info("Groovy added area: " + name);
		} catch (Exception e) {
			logger.error("Exception while tyring to add area: " + e);
			zone = null;
		}
		return (zone);
	}

	public boolean transferPlayer(Player player, String zoneName, int x, int y) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(zoneName);
		if (zone != null && StendhalRPAction.placeat(zone, player, x, y)) {
			StendhalRPAction.changeZone(player, zone.getID().getID());
			StendhalRPAction.transferContent(player);
			world.modify(player);
			return (true);
		}
		return (false);
	}

	public boolean playerIsInZone(Player player, String zoneName) {
		if (zoneName.equals(player.get("zoneid"))) {
			return (zone.has(player.getID()));
		}
		return (false);
	}

	public void add(NPC npc) {
		if (zone != null) {
			zone.assignRPObjectID(npc);
			zone.addNPC(npc);
			rules.addNPC(npc);
			loadedNPCs.add(npc);
			logger.info("Groovy added NPC: " + npc);
		}
	}

	public void add(RPObject object) {
		if (zone != null) {
			zone.assignRPObjectID(object);
			zone.add(object);
			loadedRPObjects.add(object);
			logger.info("Groovy added object: " + object);
		}
	}

	public Pair<ScriptCondition, ScriptAction> add(ScriptCondition condition,
			ScriptAction action) {
		Pair<ScriptCondition, ScriptAction> script = scripts.addScript(
				condition, action);
		loadedScripts.add(script);
		logger.info("Groovy added a script.");
		return (script);
	}

	public Creature[] getCreatures() {
		return (world.getRuleManager().getEntityManager().getCreatures()
				.toArray(new Creature[1]));
	}

	public Item[] getItems() {
		return (world.getRuleManager().getEntityManager().getItems()
				.toArray(new Item[1]));
	}

	public Item getItem(String name) {
		Item item = world.getRuleManager().getEntityManager().getItem(name);
		if (zone != null) {
			zone.assignRPObjectID(item);
		}
		return (item);
	}

	public Creature add(Creature template, int x, int y) {
		Creature creature = new Creature(template);
		if (zone != null) {
			zone.assignRPObjectID(creature);
			StendhalRPAction.placeat(zone, creature, x, y);
			zone.add(creature);
			rules.addNPC(creature);
			loadedNPCs.add(creature);
			logger.info("Groovy added creature: " + creature);
		}
		return (creature);
	}

	public boolean load() {
		GroovyShell interp = new GroovyShell(groovyBinding);
		boolean ret = true;
		Log4J.startMethod(logger, "load");
		try {
			File f = new File(groovyScript);
			interp.evaluate(f);
		} catch (Exception e) {
			logger.error("Exception while sourcing file " + groovyScript);
			e.printStackTrace();
			groovyExceptionMessage = e.getMessage();
			ret = false;
		}
		Log4J.finishMethod(logger, "load");
		return (ret);
	}

	public String getMessage() {
		return (groovyExceptionMessage);
	}

	public void remove(NPC npc) {
		logger.info("Removing groovy added NPC: " + npc);
		try {
			String id = npc.getID().getZoneID();
			zone = (StendhalRPZone) world.getRPZone(id);
			NPCList.get().remove(npc.getName());
			rules.removeNPC(npc);
			zone.getNPCList().remove(npc);
			zone.remove(npc);
			loadedNPCs.remove(npc);
		} catch (Exception e) {
			logger.warn("Exception while removing groovy added NPC: " + e);
		}
	}

	public void remove(RPObject object) {
		try {
			logger.info("Removing groovy added object: " + object);
			String id = object.getID().getZoneID();
			zone = (StendhalRPZone) world.getRPZone(id);
			zone.remove(object);
			loadedRPObjects.remove(object);
		} catch (Exception e) {
			logger.warn("Exception while removing groovy added object: " + e);
		}
	}

	public void remove(Pair<ScriptCondition, ScriptAction> script) {
		scripts.removeScript(script);
		loadedScripts.remove(script);
	}

	/** removes the first loaded script that has the required action */
	public void remove(ScriptAction scriptAction) {
		for (Pair<ScriptCondition, ScriptAction> script : loadedScripts) {
			if (script.second() == scriptAction) {
				logger.info("Removing groovy added script.");
				remove(script);
				return;
			}
		}
	}

	/** removes the first loaded script that has the required condition */
	public void remove(ScriptCondition scriptCondition) {
		for (Pair<ScriptCondition, ScriptAction> script : loadedScripts) {
			if (script.first() == scriptCondition) {
				logger.info("Removing groovy added script.");
				remove(script);
			}
		}
	}

	public void unload() {
		Log4J.startMethod(logger, "unload");

		for (Pair<ScriptCondition, ScriptAction> script : loadedScripts) {
			logger.info("Removing groovy added script.");
			remove(script);
		}

		for (NPC npc : loadedNPCs) {
			remove(npc);
		}

		for (RPObject object : loadedRPObjects) {
			remove(object);
		}

		Log4J.finishMethod(logger, "unload");
	}

}
