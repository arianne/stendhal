package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * this is the internal class which handles an active deathmatch session
 */
class DeathmatchEngine implements TurnListener {

	private static final long BAIL_DELAY = 2000; // wait 2 seconds before
													// bail takes effect

	private static Logger logger = Logger.getLogger(DeathmatchEngine.class);

	private final Player player;
	private DeathmatchInfo dmInfo;

	private CreatureSpawner spawner;

	private boolean keepRunning = true;

	/**
	 * Creates a new ScriptAction to handle the deathmatch logic.
	 * 
	 * @param player
	 *            Player for whom this match is created
	 * @param deathmatchInfo
	 *            Information about the place of the deathmatch
	 */
	public DeathmatchEngine(Player player, DeathmatchInfo deathmatchInfo) {
		this.dmInfo = deathmatchInfo;
		this.player = player;

		initialize();
	}

	protected void initialize() {
		spawner = new CreatureSpawner();
	}

	private boolean condition() {
		if ("cancel".equals(player.getQuest("deathmatch"))) {
			return false;
		}
		if (player.getQuest("deathmatch").startsWith("done")) {
			return false;
		}

		if (dmInfo.isInArena(player)) {
			return true;
		} else {
			player.setQuest("deathmatch", "cancel");
			return true;
		}
	}

	public void onTurnReached(int currentTurn) {
		if (condition()) {
			action();
		}
		if (keepRunning) {
			TurnNotifier.get().notifyInTurns(0, this);
		}
	}

	private void action() {

		DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		switch (deathmatchState.getLifecycleState()) {

		case BAIL:
			if (((new Date()).getTime() - deathmatchState.getStateTime() > BAIL_DELAY)) {
				handleBail();

				keepRunning = false;
				return;
			}
			break;

		case CANCEL:
			spawner.removePlayersMonsters();

			// and finally remove this ScriptAction
			keepRunning = false;
			return;
			
		default: 
			//cannot happen we switch on a enum

		}

		// check wheter the deathmatch was completed
		if (deathmatchState.getQuestLevel() >= player.getLevel()
				+ CreatureSpawner.NUMBER_OF_CREATURES - 2) {
			// logger.info("May be done");
			if (spawner.areAllCreaturesDead()) {
				logger.info("Player " + player.getName()
						+ " completed deathmatch");
				spawner.spawnDailyMonster(player, dmInfo);
				deathmatchState.setLifecycleState(DeathmatchLifecycle.VICTORY);
				deathmatchState.setQuestLevel(spawner.calculatePoints());
				player.setQuest("deathmatch", deathmatchState.toQuestString());
				// remove this ScriptAction since we're done

				keepRunning = false;
			}
			return; // all creature are there
		}

		// spawn new monster
		if (((new Date()).getTime() - deathmatchState.getStateTime() > CreatureSpawner.SPAWN_DELAY)) {
			DeathMatchCreature mycreature = spawner.spawnNewCreature(
					deathmatchState.getQuestLevel(), player, dmInfo);

			// in case there is not enough space to place the creature,
			// mycreature is null
			if (mycreature != null) {

				deathmatchState.increaseQuestlevel();
			}

			deathmatchState.refreshTimestamp();
		}

		player.setQuest("deathmatch", deathmatchState.toQuestString());
	}

	private void handleBail() {
		player.setQuest("deathmatch", "cancel");
		Item helmet = player.getFirstEquipped("trophy_helmet");
		if (helmet != null) {
			int defense = 1;
			if (helmet.has("def")) {
				defense = helmet.getInt("def");
			}
			if (defense > 1) {
				defense--;
			} else {
				defense = 1;
			}
			helmet.put("def", "" + defense);
			player.updateItemAtkDef();
		} else {
			int xp = player.getLevel() * 80;
			if (xp > player.getXP()) {
				xp = player.getXP();
			}
			player.subXP(xp);
		}

		// send the player back to the entrance area
		// StendhalRPZone entranceZone =
		// StendhalRPWorld.get().getZone(zoneName);
		player.teleport(dmInfo.getEntranceSpot().getZone(),
				dmInfo.getEntranceSpot().getX(),
				dmInfo.getEntranceSpot().getY(), null, null);

		spawner.removePlayersMonsters();
	}

}
