/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deathmatch;

import java.util.Date;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * this is the internal class which handles an active deathmatch session.
 */
class DeathmatchEngine implements TurnListener {
	/** The amount of milliseconds to wait before bail takes effect. */
	private static final long BAIL_DELAY = 600;

	private static Logger logger = Logger.getLogger(DeathmatchEngine.class);

	private final Player player;
	private final EventRaiser raiser;

	private final DeathmatchInfo dmInfo;

	private CreatureSpawner spawner;

	private boolean keepRunning = true;


	/**
	 * Creates a new ScriptAction to handle the deathmatch logic.
	 *
	 * @param player
	 *            Player for whom this match is created
	 * @param deathmatchInfo
	 *            Information about the place of the deathmatch
	 * @param raiser
	 */
	public DeathmatchEngine(final Player player, final DeathmatchInfo deathmatchInfo, final EventRaiser raiser) {
		this.dmInfo = deathmatchInfo;
		this.player = player;
		this.raiser = raiser;
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

	@Override
	public void onTurnReached(final int currentTurn) {
		if (condition()) {
			action();
		}
		if (keepRunning) {
			SingletonRepository.getTurnNotifier().notifyInTurns(0, this);
		}
	}

	private void action() {

		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

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

		// check whether the deathmatch was completed
		if (deathmatchState.getQuestLevel() >= player.getLevel()
				+ CreatureSpawner.NUMBER_OF_CREATURES - 2) {
			// logger.info("May be done");
			if (spawner.areAllCreaturesDead()) {
				logger.info("Player " + player.getName()
						+ " completed deathmatch");
				spawner.spawnDailyMonster(player, dmInfo);
				deathmatchState.setLifecycleState(DeathmatchLifecycle.VICTORY);
				player.setQuest("deathmatch", deathmatchState.toQuestString());

				// make the npc attend the player so they can say victory
				raiser.say(player.getName() + ", you have completed this deathmatch and can now claim #victory.");
			    raiser.setCurrentState(ConversationStates.ATTENDING);
				raiser.setAttending(player);

				// remove this ScriptAction since we're done
				keepRunning = false;
			}
			// all creature are there
			return;
		}

		int numberPlayers = dmInfo.getArena().getPlayers().size();
		// spawn new monster, with the spawn delay proportional to player level
		// and inversely proportional to the number of players in the ring
		// and always spawning if there is no creature
		// for level 20 players it is always just 20 seconds
		if (((new Date()).getTime() - deathmatchState.getStateTime() > (300*(player.getLevel()-20)/(1+numberPlayers) + CreatureSpawner.SPAWN_DELAY)) || spawner.areAllCreaturesDead()) {
			final DeathMatchCreature mycreature = spawner.spawnNewCreature(
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
		final Item helmet = player.getFirstEquipped("trophy helmet");
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
		player.teleport(dmInfo.getEntranceSpot().getZone(),
				dmInfo.getEntranceSpot().getX(),
				dmInfo.getEntranceSpot().getY(), null, null);

		spawner.removePlayersMonsters();
	}

}
