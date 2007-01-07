import marauroa.common.game.IRPZone;
import games.stendhal.common.Direction;
import games.stendhal.server.*
import games.stendhal.server.entity.*
import games.stendhal.server.entity.creature.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.entity.portal.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.entity.player.*
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.scripting.*
import games.stendhal.server.pathfinder.Path

/**
 * Manages friendly entities
 */
public class Friends implements TurnListener {
	private ScriptInGroovy game;
	
	private int turnCounter = 0;
	
	private Portal portal;
	
	private OneWayPortalDestination portalDestination;

	/**
	 * Constructor
	 *
	 * @param game ScriptInGroovy
	 */
	public Friends(ScriptInGroovy game) {
		this.game = game;
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
		npc.setHP((int) (Math.random() * 80) + 10);
		npc.set(x, y);
		npc.setDirection(Direction.DOWN);
		game.add(npc)
	}

	/**
	 * Creates three soldiers to block the entrance
	 */
	public void createSoldiers() {

		// main entrance
		createSoldier("Soldier", 55, 47);
		createSoldier("Soldier", 56, 47);
		createSoldier("Soldier", 57, 47);

		// backdoor
		createSoldier("Soldier", 43, 23);
	}

	/**
	 * Creates a sheep for the Orcs to target
	 */
	public void createSheep() {
		Creature creature = new Sheep();
		creature.setX(56);
		creature.setY(46);
		game.add(creature);
	}

	public void onTurnReached(int currentTurn, String message) {
		int wait = 6;
		switch (turnCounter) {

			case 0:
				shout("Katinka shouts: Help. There are two Orcs approaching our Wildlife Refuge");
				wait = 5 * 3;
				break;

			case 1:
				shout("Head of Soldiers shouts: Katinka, stay calm.");
				break;

			case 2:
				shout("Head of Soldiers shouts: I will send one of our soldiers called Marcus to help you.");
				wait = 60 * 3;
				break;

			case 3:
				shout("Marcus shouts: I killed those two Orcs. But further investigation showed:");
				break;

			case 4:
				shout("Marcus shouts: They were only in the vanguard of a huge bunch of Warrior Orcs.");
				break;

			case 5:
				shout("Marcus shouts: We need reinforcements within about 10 minutes.");
				wait = 10 * 3;
				break;

			case 6:
				shout("Io Flotto shouts: I created a portal near Carmen in the south west of Semos.");
				break;

			case 7:
				shout("Io Flotto shouts: You can use it to get to the Ados Wildlife Refuge in time.");
				wait = 120 * 3;
				break;

			case 8:
				shout("Katinka shouts: Argh! They have eaten our boars. Help us!");
				// shout("Dr. Feelgood shouts: Help! Help us! The Ados Wildlife Refuge is under heavy attack by a bunch of hungry Orc Warriors.");
				wait = 600 * 3;
				break;
			
			case 9:
				// remove the portal
				StendhalRPWorld.getInstance().remove(portal.getID());
				StendhalRPWorld.getInstance().remove(portalDestination.getID());
				return;
			
		}
		TurnNotifier.get().notifyInTurns(wait, this, "");
		turnCounter++;
	}

	public void createPortal() {
		StendhalRPZone zone1 = (StendhalRPZone) StendhalRPWorld.getInstance().getRPZone(new IRPZone.ID("0_semos_city"));
		StendhalRPZone zone2 = (StendhalRPZone) StendhalRPWorld.getInstance().getRPZone(new IRPZone.ID("0_ados_outside_nw"));

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setX(9);
		portal.setY(41);
		portal.setNumber(100);
		portal.setDestination("0_ados_outside_nw", 10);
		zone1.addPortal(portal);

		portalDestination = new OneWayPortalDestination();
		zone2.assignRPObjectID(portalDestination);
		portalDestination.setX(53);
		portalDestination.setY(108);
		portalDestination.setNumber(10);
		zone2.addPortal(portalDestination);
	}

	public void shout(String text) {
		List players = StendhalRPRuleProcessor.get().getPlayers();
		for (player in players) {
			player.sendPrivateText(text);
		}
	}
}


game.setZone("0_ados_outside_nw");

if (player == null || ((args.length > 0) && (args[0].equals("reset")))) {

} else {

	Friends friends = new Friends(game);
	friends.createSoldiers();
	friends.createSheep();
	friends.createPortal();
	TurnNotifier.get().notifyInTurns(0, friends, "");
}