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

game.setZone("0_ados_outside_nw");
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

friends.shout("Katinka shouts: Argh! They killed our boars.");

friends.shout("Dr. Feelgood shouts: Help! Help us! The Ados Wildlife Refuge is under heavy attack by a bunch of hungry Orc Warriors.");