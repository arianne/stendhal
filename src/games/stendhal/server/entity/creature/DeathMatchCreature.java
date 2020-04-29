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
package games.stendhal.server.entity.creature;

import java.util.Map.Entry;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deathmatch.DeathmatchInfo;
import games.stendhal.server.maps.deathmatch.DeathmatchState;

/**
 * <p>A creature that will give no XP to killers.
 * <p>It calculates the DM score (points) due to the DM starter.
 * <p>All players who did damage get the kill attributed.
 *
 * @author hendrik
 */
public class DeathMatchCreature extends Creature {

	private int points;

	// save only the name to enable GC of the player object
	private String playerName;

	private final DeathmatchInfo deathmatchInfo;


	public DeathMatchCreature(final DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	/**
	 * DeathCreature.
	 *
	 * @param copy
	 *            creature to wrap
	 */
	public DeathMatchCreature(final Creature copy, final DeathmatchInfo deathmatchInfo) {
		super(copy);

		this.deathmatchInfo = deathmatchInfo;
	}

	/**
	 * Only this player gets a points reward.
	 *
	 * @param player
	 *            Player to reward
	 */
	public void setPlayerToReward(final Player player) {
		this.playerName = player.getName();
	}

	@Override
	public Creature getNewInstance() {
		return new DeathMatchCreature(this, deathmatchInfo);
	}

	@Override
	protected void rewardKillers(final int oldXP) {
		for (Entry<Entity, Integer> entry : damageReceived.entrySet()) {
			int damageDone = entry.getValue();
			if (damageDone == 0) {
				continue;
			}

			Player killer = entityAsOnlinePlayer(entry.getKey());
			if (killer == null) {
				continue;
			}

			final String killerName = killer.getName();

			// set the DM points score only for the player who started the DM
			if (killerName.equals(playerName)) {
				points = (int) (killer.getLevel()
					* ((float) damageDone / (float) totalDamageReceived));
				final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(killer.getQuest("deathmatch"));
				deathmatchState.addPoints(points);
				killer.setQuest("deathmatch", deathmatchState.toQuestString());
			} else {
				deathmatchInfo.addAidedKill(killerName);
			}

			// For some quests etc., it is required that the player kills a
			// certain creature without the help of others.
			// Find out if the player killed this RPEntity on his own, but
			// don't overwrite solo with shared.
			final String killedName = getName();

			if (killedName != null) {
				if (damageDone == totalDamageReceived) {
					killer.setSoloKill(killedName);
				} else {
					killer.setSharedKill(killedName);
				}
			}

			killer.notifyWorldAboutChanges();
		}
	}

	/**
	 * Calculates the deathmatch points for this kill.
	 *
	 * @return number of points to reward
	 */
	public int getDMPoints() {
		return points;
	}

}
