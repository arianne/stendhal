/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.util.Observer;


public interface CreatureSpawner extends TurnListener {

	/** Longest possible respawn time in turns (half a year - should be longer than the server is up
	 *  in one phase). */
	static final int MAX_RESPAWN_TIME = 200 * 60 * 24 * 30 * 6;
	/** Minimum respawn time in turns (about 10s) */
	static final int MIN_RESPAWN_TIME = 33;


	void addObserver(Observer observer);

	void removeObserver(Observer observer);

	/**
	 * Notifies this spawner that a creature was spawned.
	 *
	 * @param spawned
	 *   The new creature.
	 */
	void onSpawned(Creature spawned);

	/**
	 * Notifies this spawner about the death of a creature that was spawned with it.
	 *
	 * @param removed
	 *   The creature that was removed.
	 */
	void onRemoved(Creature removed);
}
