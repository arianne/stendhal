/***************************************************************************
 *                    (C) Copyright 2003-2022 - Arianne                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Actions registered with this class will be run at end of server startup.
 */
public class CachedActionManager {

	private static final Logger logger = Logger.getLogger(CachedActionManager.class);

	/** The singleton instance. */
	private static CachedActionManager instance;

	private List<Runnable> cached;


	/**
	 * Singleton access method.
	 *
	 * @return
	 *     The static instance.
	 */
	public static CachedActionManager get() {
		if (instance == null) {
			instance = new CachedActionManager();
		}

		return instance;
	}

	/**
	 * Singleton constructor.
	 */
	private CachedActionManager() {
		cached = new LinkedList<>();
	}

	/**
	 * Registers a new action.
	 *
	 * @param action
	 *     Action to be run at end of server startup.
	 */
	public void register(final Runnable action) {
		if (cached == null) {
			logger.warn("Cannot cache new action after server startup");
			return;
		}

		cached.add(action);
	}

	/**
	 * Runs all registered actions & clears cache.
	 */
	void run() {
		if (cached == null) {
			logger.error("Tried to re-run cached actions");
			return;
		}

		if (cached.size() > 0) {
			logger.info("Running cached startup actions");

			for (final Runnable action: cached) {
				action.run();
			}
		}

		cached = null;
	}
}
