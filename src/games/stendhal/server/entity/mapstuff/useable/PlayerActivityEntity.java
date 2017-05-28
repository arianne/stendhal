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
package games.stendhal.server.entity.mapstuff.useable;


import java.lang.ref.WeakReference;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * An entity that performs some activity for a player. The activity takes some
 * time to perform and can succeed or fail. The player must be standing next to
 * the entity when it finished to succeed. The activity must finish before being
 * initiated again.
 */
public abstract class PlayerActivityEntity extends UseableEntity {

	private static final long PENALTY_TIMEOUT = 30 * 60 * 1000;
	private static final long PENALTY_COUNT = 3000;

	/**
	 * Create a player activity entity.
	 */
	public PlayerActivityEntity() {
		setResistance(0);
	}

	/**
	 * Process the results of the activity.
	 *
	 * @param player
	 *            The player that performed the activity.
	 */
	protected void activityDone(final Player player) {
		// Verify that the player is still standing next to this, else their
		// activity fails.
		if (nextTo(player)) {
			if (isPenalized(player)) {
				onFinished(player, false);
			} else {
				onFinished(player, isSuccessful(player));
			}
		} else {
			player.sendPrivateText("You are too far away from the " + this.getName() + ", try to come closer.");
			onFinished(player, false);
		}
	}

	/**
	 * Get the time it takes to perform this activity.
	 *
	 * @return The time to perform the activity (in seconds).
	 */
	protected abstract int getDuration();

	/**
	 * Decides if the activity can be done.
	 * @param player for whom to perform the activity
	 *
	 * @return <code>true</code> if can be done
	 */
	protected abstract boolean isPrepared(final Player player);

	/**
	 * Decides if the activity was successful.
	 * @param player for whom to perform the activity
	 *
	 * @return <code>true</code> if successful.
	 */
	protected abstract boolean isSuccessful(final Player player);

	/**
	 * Called when the activity has finished.
	 *
	 * @param player
	 *            The player that did the activity.
	 * @param successful
	 *            If the activity was successful.
	 */
	protected abstract void onFinished(final Player player,
			final boolean successful);

	/**
	 * Called when the activity has started.
	 *
	 * @param player
	 *            The player starting the activity.
	 */
	protected abstract void onStarted(final Player player);

	/**
	 * Check for excessive usage
	 *
	 * @param player
	 *            The player starting the activity.
	 *
	 * @return <code>true</code> if the usage was excessive
	 */
	protected boolean isPenalized(final Player player) {
		String timestamp = player.get("source_usage", this.getName() + ".lastused");
		int usageCount = 0;
		if (MathHelper.parseLong(timestamp) > System.currentTimeMillis() - PENALTY_TIMEOUT) {
			usageCount = player.getInt("source_usage", this.getName() + ".count");
		}
		usageCount++;
		player.put("source_usage", this.getName() + ".lastused",  Long.toString(System.currentTimeMillis()));
		player.put("source_usage", this.getName() + ".count", usageCount);

		return usageCount > PENALTY_COUNT;
	}

	//
	// UseListener
	//

	/**
	 * Is called when a player initiates the activity.
	 *
	 * @param entity
	 *            The initiating entity.
	 *
	 * @return <code>true</code> if the entity was used.
	 */
	@Override
	public boolean onUsed(final RPEntity entity) {

		if (!(entity instanceof Player)) {
			return false;
		}

		final Player player = (Player) entity;

		// The player must be next to the source to start to use it.
		if (!player.nextTo(this)) {
			player.sendPrivateText("You are too far away from " + this.getName()+
				", try to come closer.");
			return false;
		}

		if (isPrepared(player)) {
			final Activity activity = new Activity(player);

			/*
			 * You can't start a new activity before the last one has finished.
			 */
			if (SingletonRepository.getTurnNotifier().getRemainingTurns(activity) == -1) {
				player.faceToward(this);
				onStarted(player);

				SingletonRepository.getTurnNotifier().notifyInSeconds(getDuration(), activity);
			}
		}

		player.notifyWorldAboutChanges();
		return true;
	}


	/**
	 * An occurrence of activity.
	 */
	protected class Activity implements TurnListener {
		/*
		 * The player holder.
		 */
		protected WeakReference<Player> ref;

		/**
		 * Create an activity.
		 *
		 * @param player
		 *            The player.
		 */
		public Activity(final Player player) {
			ref = new WeakReference<Player>(player);
		}

		//
		// Activity
		//

		/**
		 * Get the holder entity.
		 *
		 * @return The holder entity.
		 */
		public PlayerActivityEntity getEntity() {
			return PlayerActivityEntity.this;
		}

		/**
		 * Get the player.
		 *
		 * @return The player (or <code>null</code> if GC'd).
		 */
		public Player getPlayer() {
			return ref.get();
		}

		//
		// TurnListener
		//

		/**
		 * This method is called when the turn number is reached.
		 *
		 * @param currentTurn
		 *            The current turn number.
		 */
		@Override
		public void onTurnReached(final int currentTurn) {
			final Player player = getPlayer();

			if (player != null) {
				activityDone(player);
			}
		}

		//
		// Object
		//

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof Activity) {
				final Activity activity = (Activity) obj;

				/*
				 * Equal in context of same parent entity
				 */
				if (getEntity() != activity.getEntity()) {
					return false;
				}

				return (getPlayer() == activity.getPlayer());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			final Object player = getPlayer();

			if (player != null) {
				return player.hashCode();
			} else {
				return 0;
			}
		}
	}
}
