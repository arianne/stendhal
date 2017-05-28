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
package games.stendhal.client;

import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

class StendhalPerceptionListener implements IPerceptionListener {
	private static final Logger logger = Logger.getLogger(StendhalPerceptionListener.class);

	/**
	 *
	 */
	private PerceptionDispatcher dispatch = new PerceptionDispatcher();

	private final RPObjectChangeDispatcher rpobjDispatcher;

	private final UserContext userContext;

	public StendhalPerceptionListener(final PerceptionDispatcher dispatch, final RPObjectChangeDispatcher rpobjDispatcher, final UserContext userContext, final Map<ID, RPObject> world_objects) {
		this.userContext = userContext;
		this.dispatch = dispatch;
		this.rpobjDispatcher = rpobjDispatcher;
		this.world_objects = world_objects;
	}

	@Override
	public boolean onAdded(final RPObject object) {
		if (userContext.isUser(object)) {
			userContext.setPlayer(object);
		}
		dispatch.onAdded(object);
		rpobjDispatcher.dispatchAdded(object);
		return false;
	}

	@Override
	public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
		dispatch.onModifiedAdded(object, changes);
		rpobjDispatcher.dispatchModifyAdded(object, changes);
		return true;
	}

	@Override
	public boolean onModifiedDeleted(final RPObject object, final RPObject changes) {
		dispatch.onModifiedDeleted(object, changes);
		rpobjDispatcher.dispatchModifyRemoved(object, changes);
		return true;
	}

	@Override
	public boolean onDeleted(final RPObject object) {
		dispatch.onDeleted(object);
		rpobjDispatcher.dispatchRemoved(object);
		return false;
	}

	@Override
	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		dispatch.onMyRPObject(added, deleted);
		try {
			RPObject.ID id = null;

			if (added != null) {
				id = added.getID();
			}

			if (deleted != null) {
				id = deleted.getID();
			}

			if (id == null) {
				// Unchanged.
				return true;
			}


			final RPObject object = world_objects.get(id);

			userContext.setPlayer(object);

			if (deleted != null) {
				rpobjDispatcher.dispatchModifyRemoved(object, deleted);
			}

			if (added != null) {
				rpobjDispatcher.dispatchModifyAdded(object, added);
			}
		} catch (final Exception e) {
			logger.error("onMyRPObject failed, added=" + added
					+ " deleted=" + deleted, e);
		}

		return true;
	}

	@Override
	public void onSynced() {
		dispatch.onSynced();



	}


	private final Map<ID, RPObject> world_objects;

	@Override
	public void onUnsynced() {
		dispatch.onUnsynced();

	}

	@Override
	public void onException(final Exception e,
			final marauroa.common.net.message.MessageS2CPerception perception) {
		dispatch.onException(e, perception);
		logger.error("perception caused an error: " + perception, e);
		System.exit(-1);
	}

	@Override
	public boolean onClear() {
		dispatch.onClear();
		return false;
	}

	@Override
	public void onPerceptionBegin(final byte type, final int timestamp) {
		dispatch.onPerceptionBegin(type, timestamp);
	}

	@Override
	public void onPerceptionEnd(final byte type, final int timestamp) {
		dispatch.onPerceptionEnd(type, timestamp);
	}
}
