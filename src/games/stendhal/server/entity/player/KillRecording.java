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
package games.stendhal.server.entity.player;

import games.stendhal.common.MathHelper;

/**
 * recording of killings.
 *
 * @author hendrik
 */
class KillRecording {
	private static final String KILL_SLOT_NAME = "!kills";
	private static final String PREFIX_SHARED = "shared.";
	private static final String PREFIX_SOLO = "solo.";

	private final Player player;

	public KillRecording(final Player player) {
		this.player = player;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * without the help of any other player.
	 *
	 * @param name of the creature to check.
	 * @return true if this player has ever killed this creature on his own.
	 */
	public boolean hasKilledSolo(final String name) {
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SOLO + name);
		return MathHelper.parseIntDefault(count, 0) > 0;
	}

	/**
	 * Checks if the player has ever killed a creature with the given name
	 * with the help of any other player.
	 *
	 * @param name of the creature to check.
	 * @return true if this player has ever killed this creature in a team.
	 */
	public boolean hasKilledShared(final String name) {
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, PREFIX_SHARED + name);
		return MathHelper.parseIntDefault(count, 0) > 0;
	}
	/**
	 * Checks if the player has ever killed a creature, with or without the help
	 * of any other player.
	 *
	 * @param  name of the creature to check.
	 * @return true if this player has ever killed this creature on his own.
	 */
	public boolean hasKilled(final String name) {
		return hasKilledShared(name) || hasKilledSolo(name);
	}

	/**
	 * Stores in which way the player has killed a creature with the given name.
	 *
	 * @param name of the killed creature.
	 * @param mode
	 *            either "solo", "shared", or null.
	 */
	private void setKill(final String name, final String mode) {
		final String key = mode + "." + name;
		final String count = player.getKeyedSlot(KILL_SLOT_NAME, key);
		final int oldValue = MathHelper.parseIntDefault(count, 0);
		player.setKeyedSlot(KILL_SLOT_NAME, key, Integer.toString(oldValue + 1));
	}

	/**
	 * Stores that the player has killed 'name' solo. Overwrites shared kills of
	 * 'name'
	 * @param name of the killed entity
	 *
	 */
	public void setSoloKill(final String name) {
		setKill(name, "solo");
	}

	/**
	 * Stores that the player has killed 'name' with help of others. Does not
	 * overwrite solo kills of 'name'
	 * @param name of the killed entity
	 */
	public void setSharedKill(final String name) {
		setKill(name, "shared");
	}

	/**
	 * Changes kill count to specified value.
	 *
	 * @param name name of killed entity
	 * @param mode 'solo' or 'shared'
	 * @param count value to set
	 */
	public void setKillCount(final String name, final String mode, final int count) {
		final String key = mode + "." + name;
		player.setKeyedSlot(KILL_SLOT_NAME, key, Integer.toString(count));
	}

	/**
	 * Changes solo kill count to specified value.
	 *
	 * @param name name of killed entity
	 * @param count value to set
	 */
	public void setSoloKillCount(final String name, final int count) {
		setKillCount(name, "solo", count);
	}

	/**
	 * Changes shared kill count to specified value.
	 *
	 * @param name of killed entity
	 * @param count value to set
	 */
	public void setSharedKillCount(final String name, final int count) {
		setKillCount(name, "shared", count);
	}

	/**
	 * Return information about how much creatures with the given name player killed.
	 * @param name of the killed creature.
	 * @param mode either "solo", "shared", or null.
	 * @return number of killed creatures
	 */
	public int getKill(final String name, final String mode) {
		final String key = mode + "." + name;
		final int kills = MathHelper.parseIntDefault(player.getKeyedSlot(KILL_SLOT_NAME, key),0);
		return(kills);
	}

	/**
	 * Return how much the player has killed 'name' solo.
	 * @param name of the killed entity
	 * @return number of killed creatures
	 */
	public int getSoloKill(final String name) {
		return(getKill(name, "solo"));
	}

	/**
	 * Return how much the player has killed 'name' shared.
	 * @param name of the killed entity
	 * @return number of killed creatures
	 */
	public int getSharedKill(final String name) {
		return(getKill(name, "shared"));
	}

}
