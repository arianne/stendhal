/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.events;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;

/**
 * Other classes can register here to be notified on teleports
 *
 * @author hendrik
 */
public final class TeleportNotifier {

	private static Logger logger = Logger.getLogger(TeleportNotifier.class);

	/** The singleton instance. */
	private static TeleportNotifier instance;

	/** listeners */
	private final Set<TeleportListener> listeners = new HashSet<TeleportListener>();


	/**
	 * Return the TeleportNotifier instance.
	 *
	 * @return TeleportNotifier the Singleton instance
	 */
	public static TeleportNotifier get() {
		if (instance == null) {
			instance = new TeleportNotifier();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private TeleportNotifier() {
		// singleton
	}

	/**
	 * notifies about a teleport
	 *
	 * @param player player who teleported
	 * @param playerAction true, if the player actively teleported; false for all teleports
	 */
	public void notify(final Player player, boolean playerAction) {
		for (TeleportListener listener : listeners) {
			try {
				listener.onTeleport(player, playerAction);
			} catch (RuntimeException e) {
				logger.error(e + " in " + listener + " for " + player, e);
			}
		}
	}

	/**
	 * registers a TeleportListener.
	 *
	 * @param listener TeleportListener
	 */
	public void registerListener(final TeleportListener listener) {
		if (listener == null) {
			logger.error("Trying to notify null-object", new Throwable());
			return;
		}
		listeners.add(listener);
	}

	/**
	 * unregisters a TeleportListener so that it will not be called anymore.
	 *
	 * @param listener TeleportListener
	 */
	public void unregisterListener(final TeleportListener listener) {
		listeners.remove(listener);
	}
}
