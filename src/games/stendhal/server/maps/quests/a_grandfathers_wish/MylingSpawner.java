/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.a_grandfathers_wish;

import static games.stendhal.server.maps.quests.AGrandfathersWish.QUEST_SLOT;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.CloneManager;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Custom spawner so Creature is not attackable.
 */
public class MylingSpawner extends Entity implements TurnListener {

	private static final Logger logger = Logger.getLogger(MylingSpawner.class);

	// should never be more than 1 myling in world at a time
	private final List<Myling> activeMylings = new LinkedList<Myling>();
	private final List<SpeakerNPC> activeNialls = new LinkedList<SpeakerNPC>();

	private static final int respawnTurns = 2000;

	private final String[] dialogue = new String[] {
		"[name], you cured me!",
		"I have been stuck in that myling form for so long now. My"
			+ " grandpa must be worried sick about me.",
		"I need to get home as soon as possible to let him know I am"
			+ " alright. Stop by my house sometime. There is something I"
			+ " want to give you.",
		null // signals Niall is done talking
	};

	public MylingSpawner() {
		super();
	}

	/**
	 * Adds a new myling to world.
	 */
	public void respawn() {
		if (!mylingIsActive()) {
			final Myling myling = new Myling(this);
			myling.setPosition(getX(), getY());
			SingletonRepository.getRPWorld().getZone(getID().getZoneID()).add(myling);
			activeMylings.add(myling);
			// in case spawn was triggered by means other than turn notifier
			SingletonRepository.getTurnNotifier().dontNotify(this);
		}
	}

	@Override
	public void onTurnReached(final int currentTurn) {
		if (niallIsActive()) {
			// wait for Niall clones to be removed before respawning myling
			startSpawnTimer();
		} else {
			respawn();
		}
	}

	/**
	 * Starts turn notifier to spawn a new myling.
	 */
	public void startSpawnTimer() {
		SingletonRepository.getTurnNotifier().notifyInTurns(respawnTurns, this);
	}

	/**
	 * Handles removing myling from world & restarting spawn timer.
	 */
	public void onMylingRemoved() {
		removeActiveMylings();
		// reset for next myling spawn
		startSpawnTimer();
	}

	/**
	 * Handles creating Niall instance when myling is cured.
	 */
	public void onMylingCured(final Player player) {
		onMylingRemoved();
		player.setQuest(QUEST_SLOT, 3, "cure_myling:done");

		final CloneManager cloneM = SingletonRepository.getCloneManager();

		final SpeakerNPC curedNiall = cloneM.clone("Niall Breland");
		if (curedNiall == null) {
			logger.error("Couldn't create temporary clone of Niall Breland");
		} else {
			curedNiall.setPosition(getX(), getY());
			curedNiall.setCollisionAction(CollisionAction.STOP);
			getZone().add(curedNiall);
			activeNialls.add(curedNiall);

			SingletonRepository.getTurnNotifier().notifyInTurns(75, new TurnListener() {
				@Override
				public void onTurnReached(final int currentTurn) {
					// remove Niall from world so new Myling can spawn
					activeNialls.remove(curedNiall);
					SingletonRepository.getRPWorld().remove(curedNiall.getID());
				}
			});

			int talkDelay = 0;
			for (final String phrase : dialogue) {
				talkDelay += 10;
				SingletonRepository.getTurnNotifier().notifyInTurns(talkDelay, new TurnListener() {
					@Override
					public void onTurnReached(final int currentTurn) {
						if (phrase == null) {
							// walk to rope/ladder
							final List<Node> nodes = new LinkedList<Node>();
							nodes.add(new Node(6, 10));
							nodes.add(new Node(8, 10));
							// FIXME: if we don't add another node EntityGuide.nextNode()
							//        returns null when colliding with another entity
							nodes.add(new Node(8, 10));
							curedNiall.setPath(new FixedPath(nodes, false));
						} else {
							curedNiall.say(phrase.replace("[name]", player.getName()));
						}
					}
				});
			 }
		}
	}

	/**
	 * Checks if there are currently any mylings spawned in world.
	 */
	public boolean mylingIsActive() {
		return activeMylings.size() > 0;
	}

	/**
	 * Checks if a Niall instance created by curing myling is currently
	 * active.
	 */
	public boolean niallIsActive() {
		return activeNialls.size() > 0;
	}

	/**
	 * Removes any active spawned mylings from world.
	 */
	public void removeActiveMylings() {
		for (int idx = 0; idx < activeMylings.size(); idx++) {
			final Myling myling = activeMylings.get(idx);
			final StendhalRPZone zone = myling.getZone();
			if (zone != null && zone.has(myling.getID())) {
				zone.remove(myling);
			}
			activeMylings.remove(myling);
		}
	}

	/**
	 * Retrieved the currently active Niall instance.
	 *
	 * @return
	 *     <code>SpeakerNPC</code> Niall instance.
	 */
	public SpeakerNPC getActiveNiall() {
		final int activeCount = activeNialls.size();
		if (activeCount > 0) {
			return activeNialls.get(activeCount - 1);
		}

		return null;
	}
}
