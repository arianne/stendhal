import games.stendhal.server.*
import games.stendhal.server.entity.*
import games.stendhal.server.entity.creature.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

/**
 * Manages friendly entities
 */
public class Friends {
	private StendhalGroovyScript game;
	private StendhalRPRuleProcessor rules;

	/**
	 * Constructor
	 *
	 * @param game StendhalGroovyScript
	 */
	public Friends(StendhalGroovyScript game, StendhalRPRuleProcessor rules) {
		this.game = game;
		this.rules = rules;
	}

	/**
	 * Creates a soldier
	 *
	 * @param name Name of the NPC
	 * @param x x-postion
	 * @param y y-postion
	 */
	public void createSoldier(String name, int x, int y) {
		ScriptingNPC npc = new ScriptingNPC(name);		
		npc.setClass("youngsoldiernpc");
		npc.set(x, y);
		npc.setDirection(Direction.DOWN);
		game.add(npc)
	}

	/**
	 * Creates three soldiers to block the entrance
	 */
	public void createSoldiers() {
		createSoldier("Soldier", 55, 47);
		createSoldier("Soldier", 56, 47);
		createSoldier("Soldier", 57, 47);
	}

	/**
	 * Creates a sheep for the Orcs to target
	 */
	public void createSheep() {
		Creature creature = new Sheep();
		creature.setx(56);
		creature.sety(46);
		game.add(creature);
	}

	public void shout(String text) {
		List players = rules.getPlayers();
		for (player in players) {
			player.sendPrivateText(text);
		}
	}
}

public class MapManager {
	private StendhalGroovyScript game;
	private Map storage;

	/**
	 * Constructor
	 *
	 * @param game StendhalGroovyScript
	 * @param storage Storage
	 */
	public MapManager(StendhalGroovyScript game, Map storage) {
		this.game = game;
		this.storage = storage;
		if (storage.get("signs") == null) {
			storage.put("signs", new HashSet());
		}
	}

	public void putSign(int x, int y, String text) {
		Sign sign=new Sign()
		sign.setx(x)
		sign.sety(y)
		sign.setText(text)
		storage.get("signs").add(sign);
		game.add(sign)
	}

	public void putSigns() {
		putSign(48, 38, "Elephants");
		putSign(48, 41, "Lions and Tigers");
		putSign(54, 30, "Crabs");
		putSign(61, 41, "Boars and Deers");
		putSign(66, 38, "Bears and Black Bears");
	}
}

game.setZone("0_ados_outside_nw");

if (player == null || ((args.length > 0) && (args[0].equals("reset")))) {
	MapManager mapManager = new MapManager(game, storage);
	mapManager.putSigns();
} else {

	Friends friends = new Friends(game, rules);
	friends.createSoldiers();
	friends.createSheep();
	friends.shout("Katinka shouts: Help. There are two Orcs approaching our Wildlife Refuge");
	
	friends.shout("Head of Soldiers shouts: Katinka, stay calm.");
	friends.shout("Head of Soldiers shouts: I will send one of our soldiers called Marcus to help you.");
	
	friends.shout("Marcus shouts: I killed those Orcs. But we need reinforcements in about 5 minutes:");
	friends.shout("Marcus shouts: Those two Orcs were only in the vanguard of a huge bunch of Warrior Orcs.");
	
	friends.shout("Io Flotto shouts: I created a portal near Carmen in the south west of Semos.");
	friends.shout("Io Flotto shouts: You can use it to get to the Ados Wildlife Refuge in time.");

	friends.shout("Katinka shouts: Argh! They have eaten our boars. Help us!");

	friends.shout("Dr. Feelgood shouts: Help! Help us! The Ados Wildlife Refuge is under heavy attack by a bunch of hungry Orc Warriors.");
}