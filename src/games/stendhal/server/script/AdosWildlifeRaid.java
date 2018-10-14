package games.stendhal.server.script;


import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.PlayerList;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptingNPC;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

public class AdosWildlifeRaid implements TurnListener {

	private int turnCounter = 0;
	private Portal portal;
	private OneWayPortalDestination portalDestination;


	/**
	 * Creates a soldier
	 *
	 * @param zone zone
	 * @param name Name of the NPC
	 * @param x x-postion
	 * @param y y-postion
	 */
	public void createSoldier(StendhalRPZone zone, String name, int x, int y) {
		ScriptingNPC npc = new ScriptingNPC(name);
		npc.setEntityClass("youngsoldiernpc");
		npc.setHP((int) (Math.random() * 80) + 10);
		npc.setPosition(x, y);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	/**
	 * Creates three soldiers to block the entrance
	 *
	 * @param zone zone
	 */
	public void createSoldiers(StendhalRPZone zone) {

		// main entrance
		createSoldier(zone, "Soldier", 55, 47);
		createSoldier(zone, "Soldier", 56, 47);
		createSoldier(zone, "Soldier", 57, 47);

		// backdoor
		createSoldier(zone, "Soldier", 43, 23);
	}

	/**
	 * Creates a sheep for the Orcs to target
	 *
	 * @param zone zone
	 */
	public void createSheep(StendhalRPZone zone) {
		Creature creature = new Sheep();
		creature.setPosition(56, 46);
		zone.add(creature);
	}

	@Override
	public void onTurnReached(int currentTurn) {
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
				StendhalRPWorld.get().remove(portal.getID());
				StendhalRPWorld.get().remove(portalDestination.getID());
				return;

		}
		TurnNotifier.get().notifyInTurns(wait, this);
		turnCounter++;
	}

	public void createPortal() {
		StendhalRPZone zone1 = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_city"));
		StendhalRPZone zone2 = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_ados_outside_nw"));

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setPosition(9, 41);
		portal.setDestination("0_ados_outside_nw", "wildlife");
		zone1.add(portal);

		portalDestination = new OneWayPortalDestination();
		zone2.assignRPObjectID(portalDestination);
		portalDestination.setPosition(53, 108);
		portalDestination.setIdentifier("wildlife");
		zone2.add(portalDestination);
	}

	public void shout(String text) {
		PlayerList players = StendhalRPRuleProcessor.get().getOnlinePlayers();
		for (Player player : players.getAllPlayers()) {
			player.sendPrivateText(text);
		}
	}


	public void addToWorld() {
		StendhalRPZone zone = StendhalRPWorld.get().getZone("0_ados_outside_nw");
		createSoldiers(zone);
		createSheep(zone);
		createPortal();
		TurnNotifier.get().notifyInTurns(0, this);
	}
}
