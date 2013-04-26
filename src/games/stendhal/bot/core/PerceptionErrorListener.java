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
package games.stendhal.bot.core;

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

/**
 * Logs errors during perception handling to stdout and stderr.
 */
public class PerceptionErrorListener implements IPerceptionListener {
	@Override
	public boolean onAdded(final RPObject object) {
		return false;
	}

	@Override
	public boolean onClear() {
		return false;
	}

	@Override
	public boolean onDeleted(final RPObject object) {
		return false;
	}

	@Override
	public void onException(final Exception exception,
			final MessageS2CPerception perception) {
		System.out.println(perception);
		System.err.println(perception);
		if (exception != null) {
			exception.printStackTrace();
		}
	}

	@Override
	public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
		return false;
	}

	@Override
	public boolean onModifiedDeleted(final RPObject object, final RPObject changes) {
		return false;
	}

	@Override
	public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
		return false;
	}

	@Override
	public void onPerceptionBegin(final byte type, final int timestamp) {
		// ignore
	}

	@Override
	public void onPerceptionEnd(final byte type, final int timestamp) {
		// ignore
	}

	@Override
	public void onSynced() {
		// ignore
	}

	@Override
	public void onUnsynced() {
		// ignore
	}
}
