package games.stendhal.server.scripting;

import games.stendhal.common.Pair;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalScriptSystem;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public abstract class ScriptingSandbox {

	private ArrayList<NPC> loadedNPCs = new ArrayList<NPC>();
	private ArrayList<RPObject> loadedRPObjects = new ArrayList<RPObject>();
	private ArrayList<Pair<ScriptCondition, ScriptAction>> loadedScripts = new ArrayList<Pair<ScriptCondition, ScriptAction>>();
	private StendhalScriptSystem scripts;
	private String exceptionMessage;
	private StendhalRPZone zone;
	private String filename = null;

	private static final Logger logger = Log4J
			.getLogger(ScriptingSandbox.class);

	public ScriptingSandbox(String filename) {
		this.filename = filename;
		this.scripts = StendhalScriptSystem.get();
	}

	public StendhalRPZone getZone(RPObject rpobject) {
		return (StendhalRPZone) StendhalRPWorld.get().getRPZone(rpobject.getID());
	}
	
	public boolean setZone(String name) {
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(name);
		return (zone != null);
	}
	
	public boolean setZone(StendhalRPZone zone) {
		this.zone = zone;
		return (zone != null);
	}

	public StendhalRPZone addZone(String name) {
		try {
			zone = StendhalRPWorld.get().addArea(name);
			logger.info(filename + " added area: " + name);
		} catch (Exception e) {
			logger.error("Exception while tyring to add area: " + e);
			zone = null;
		}
		return (zone);
	}

	/**
	 * @param player
	 * @param zoneName
	 * @param x
	 * @param y
	 * @return
	 * @deprecated use Player.teleport() directly instead
	 */
	@Deprecated
	public boolean transferPlayer(Player player, String zoneName, int x, int y) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(zoneName);
		return player.teleport(zone, x, y, null, null);
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
			StendhalRPRuleProcessor.get().addNPC(npc);
			loadedNPCs.add(npc);
			logger.info(filename + " added NPC: " + npc);
		}
	}

	public void add(RPObject object) {
		if (zone != null) {
			zone.assignRPObjectID(object);
			zone.add(object);
			loadedRPObjects.add(object);
			logger.info(filename + " added object: " + object);
		}
	}

	public Pair<ScriptCondition, ScriptAction> add(ScriptCondition condition,
			ScriptAction action) {
		Pair<ScriptCondition, ScriptAction> script = scripts.addScript(
				condition, action);
		loadedScripts.add(script);
		logger.info(filename + " added a script.");
		return (script);
	}

	public Creature[] getCreatures() {
		return (StendhalRPWorld.get().getRuleManager().getEntityManager().getCreatures()
				.toArray(new Creature[1]));
	}

	public Creature getCreature(String clazz) {
		return StendhalRPWorld.get().getRuleManager().getEntityManager().getCreature(clazz);
	}

	public Item[] getItems() {
		return (StendhalRPWorld.get().getRuleManager().getEntityManager().getItems()
				.toArray(new Item[1]));
	}

	public Item getItem(String name) {
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(name);
		if (zone != null) {
			zone.assignRPObjectID(item);
		}
		return (item);
	}
	
	public List<RPObject> getCreatedRPObjects() {
		return loadedRPObjects;
	}

	public Creature add(Creature template, int x, int y) {
		Creature creature = template.getInstance();
		if (zone != null) {
			zone.assignRPObjectID(creature);
			if (StendhalRPAction.placeat(zone, creature, x, y)) {
				zone.add(creature);
				StendhalRPRuleProcessor.get().addNPC(creature);
				loadedNPCs.add(creature);
				logger.info(filename + " added creature: " + creature);
			} else {
				logger.info(filename + " could not add a creature: " + creature);
				creature = null;
			}
		}
		return (creature);
	}

	public void addGameEvent(String source, String event, List<String> params) {
		StendhalRPRuleProcessor.get().addGameEvent(source, event, params.toArray(new String[params.size()]));
	}
	
	public void modify(RPEntity entity) {
		entity.notifyWorldAboutChanges();
	}

	public void privateText(Player player, String text) {
        player.sendPrivateText(text);
	}
	
	// ------------------------------------------------------------------------
	
	public abstract boolean load(Player player, String[] args);

	public String getMessage() {
		return exceptionMessage;
	}
	
	protected void setMessage(String message) {
		this.exceptionMessage = message;
	}

	public void remove(NPC npc) {
		logger.info("Removing " + filename + " added NPC: " + npc);
		try {
			String id = npc.getID().getZoneID();
			zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(id);
			NPCList.get().remove(npc.getName());
			StendhalRPRuleProcessor.get().removeNPC(npc);
			zone.getNPCList().remove(npc);
			zone.remove(npc);
			loadedNPCs.remove(npc);
		} catch (Exception e) {
			logger.warn("Exception while removing " + filename + " added NPC: " + e);
		}
	}

	public void remove(RPObject object) {
		try {
			logger.info("Removing script added object: " + object);
			String id = object.getID().getZoneID();
			zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(id);
			zone.remove(object);
			loadedRPObjects.remove(object);
		} catch (Exception e) {
			logger.warn("Exception while removing " + filename + " added object: " + e);
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
				logger.info("Removing " + filename + " added script.");
				remove(script);
				return;
			}
		}
	}

	/** removes the first loaded script that has the required condition */
	public void remove(ScriptCondition scriptCondition) {
		for (Pair<ScriptCondition, ScriptAction> script : loadedScripts) {
			if (script.first() == scriptCondition) {
				logger.info("Removing " + filename + " added script.");
				remove(script);
			}
		}
	}
	
	public void unload(Player player, String[] args) {
		Log4J.startMethod(logger, "unload");

		for (Pair<ScriptCondition, ScriptAction> script : (List<Pair<ScriptCondition, ScriptAction>>) loadedScripts.clone()) {
			logger.info("Removing " + filename + " added script.");
			remove(script);
		}

		for (NPC npc : (List<NPC>) loadedNPCs.clone()) {
			remove(npc);
		}

		for (RPObject object : (List<RPObject>) loadedRPObjects.clone()) {
			remove(object);
		}

		Log4J.finishMethod(logger, "unload");
	}

	public boolean execute(Player player, String[] args) {
		// do nothing
		return true;
	}

}
