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

import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;

public abstract class PerceptionListenerImpl implements IPerceptionListener {

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
	public void onException(final Exception exception, final MessageS2CPerception perception) {

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

	}

	@Override
	public void onPerceptionEnd(final byte type, final int timestamp) {
	}

	@Override
	public void onSynced() {

	}

	@Override
	public void onUnsynced() {


	}

}
