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
package games.stendhal.server.entity.npc.behaviour.impl.idle;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.creature.impl.idle.StandOnIdle;
import games.stendhal.server.entity.npc.NPC;


/**
 * Behavior for entity that should wander randomly while idle.
 *
 * Note: there is still some sprite jumping when entity stops, but that may be a client-side issue
 *
 * FIXME: need to handle reaching zone borders
 */
public class WanderIdleBehaviour extends StandOnIdle {

	/** Minimum amount of time in milliseconds entity must remain stopped. */
	private static final int STOP_DELAY_MIN = 5000;
	/** Minimum amount of time in milliseconds entity must remain moving. */
	private static final int MOVE_DELAY_MIN = 15000;

	/** Timestamp after which next change may be executed. */
	private long nextChange;
	/** Property to ensure good direction to change to. */
	private Direction lastKnownDir;
	/** Denotes a direction change should occur after entity position changes. */
	private boolean directionChangeQueued;
	/** Denotes stop should occur after entity position changes. */
	private boolean stopQueued;
	/** Current speed entity should be moving. */
	private double speed;


	public WanderIdleBehaviour() {
		super();
		final long initTime = System.currentTimeMillis();
		// entity is assumed to be in a stopped state at initialization
		nextChange = initTime + STOP_DELAY_MIN;
		lastKnownDir = Direction.DOWN;
		directionChangeQueued = false;
		stopQueued = false;
		speed = 0;
	}

	@Override
	public void reset() {
		// remove queues
		directionChangeQueued = false;
		stopQueued = false;
	}

	@Override
	public void perform(final NPC npc) {
		// FIXME: this check should be done in entity logic
		if (!npc.getZone().getPlayerAndFriends().isEmpty()) {
			if (npc.hasPath()) {
				npc.followPath();
			} else if (!retreatUnderFire(npc)) {
				if (!stopped()) {
					// speed must be updated every iteration
					npc.setSpeed(speed);
					npc.applyMovement();
				}
				performStopMove(npc);
			}
		}
	}

	/**
	 * Randomly stops or moves entity.
	 *
	 * @param npc
	 *   Entity being acted on.
	 */
	private void performStopMove(final NPC npc) {
		final long performTime = System.currentTimeMillis();
		final int diff = (int) (nextChange - performTime);
		if (diff >= 0) {
			if (!stopped()) {
				maybeChangeDirection();
			}
			return;
		}

		// randomly choose when to execute next change after wait period expired
		if (Rand.rand(40) == 0) {
			if (stopped()) {
				startMove(npc, performTime);
			} else {
				stop(npc, performTime);
			}
		}
	}

	/**
	 * Stops entity movement and sets timeout.
	 *
	 * @param npc
	 *   Entity being acted on.
	 * @param performTime
	 *   Time at which action is being performed.
	 */
	private void stop(final NPC npc, final long performTime) {
		// next update minimum delay
		nextChange = performTime + STOP_DELAY_MIN;
		final Direction dir = npc.getDirection();
		if (!Direction.STOP.equals(dir)) {
			lastKnownDir = dir;
		}
		// don't stop until after position change to prevent sprite jumping
		stopQueued = true;
	}

	/**
	 * Starts entity movement and sets timeout.
	 *
	 * @param npc
	 *   Entity being acted on.
	 * @param performTime
	 *   Time at which action is being performed.
	 */
	private void startMove(final NPC npc, final long performTime) {
		// next update minimum delay
		nextChange = performTime + MOVE_DELAY_MIN;
		if (Direction.STOP.equals(npc.getDirection())) {
			npc.setDirection(lastKnownDir);
		}
		lastKnownDir = npc.getDirection();

		// 1 in 4 chance of direction change with move
		if (Rand.rand(4) == 0) {
			changeDirection(npc);
		}
		speed = npc.getBaseSpeed();
	}

	/**
	 * Checks if entity is considered to be in a still state.
	 *
	 * Note: cannot rely on {@code ActiveEntity.stopped()}
	 *
	 * @return
	 *   {@code true} if entity's current direction represents "stopped".
	 */
	private boolean stopped() {
		return speed == 0;
	}


	/**
	 * Changes entity's direction and updates last known direction.
	 *
	 * @param npc
	 *   Entity that is changing direction.
	 */
	private void changeDirection(final NPC npc) {
		npc.changeDirection();
		final Direction dir = npc.getDirection();
		if (!Direction.STOP.equals(dir)) {
			lastKnownDir = dir;
		}
		directionChangeQueued = false;
	}

	/**
	 * Conditionally changes entity direction dependent on random roll.
	 */
	private void maybeChangeDirection() {
		// 1 in 20 chance to change direction
		if (Rand.rand(20) == 0) {
			// don't change direction until after position change to prevent sprite jumping
			directionChangeQueued = true;
		}
	}

	@Override
	public void onMoved(NPC npc) {
		if (stopQueued) {
			npc.stop();
			npc.applyMovement();
			speed = 0;
			stopQueued = false;
		}
		if (directionChangeQueued) {
			changeDirection(npc);
		}
	}

	@Override
	public boolean handleSimpleCollision(final NPC npc, final int nx, final int ny) {
		return handleObjectCollision(npc);
	}

	@Override
	public boolean handleObjectCollision(final NPC npc) {
		changeDirection(npc);
		return true;
	}
}
