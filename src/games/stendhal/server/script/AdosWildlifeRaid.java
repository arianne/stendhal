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
				shout("Katinka 喊道: 救命. 两个兽人接近我们的野外营地。");
				wait = 5 * 3;
				break;

			case 1:
				shout("士兵头领说: Katinka, 别慌.");
				break;

			case 2:
				shout("士兵头领说: 我会派几个 Marcus 士兵帮助你。");
				wait = 60 * 3;
				break;

			case 3:
				shout("Marcus 士兵: 我杀了这两个兽人，但从探子的消息看出:");
				break;

			case 4:
				shout("Marcus 士兵: 他们两个只是一支强大兽人部队的前锋。.");
				break;

			case 5:
				shout("Marcus 士兵: 我们要在10分钟内加强防御工事");
				wait = 10 * 3;
				break;

			case 6:
				shout("Io Flotto 说: 我在Semons村西面临近Carmen的地方修建了一个通道。");
				break;

			case 7:
				shout("Io Flotto 说: 你们可以从那里及时赶到 Ados 野外营地.");
				wait = 120 * 3;
				break;

			case 8:
				shout("Katinka 大喊: 啊! 他们吃了我们的船，快救救我们!");
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
