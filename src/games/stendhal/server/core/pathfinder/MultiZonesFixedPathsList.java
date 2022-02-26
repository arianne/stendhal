/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.pathfinder;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * class for creating complete route of npc
 * across his world's path, with notifications at keypoints and road's end.
 */
public class MultiZonesFixedPathsList implements Observer {
	final SpeakerNPC npc;
	final List<List<RPZonePath>> pathes = new LinkedList<List<RPZonePath>>();
	final List<MultiZonesFixedPath> mzfpl = new LinkedList<MultiZonesFixedPath>();
	final Observer middle;
	final Observer end;
	final Logger logger = Logger.getLogger(this.getClass());
	int count;


	/**
	 * a kind of iterator over list
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		count++;
		logger.info("count: "+count);
		if(count!=pathes.size()) {
			middle.update(null, null);
			mzfpl.get(count).update(null, null);
		} else {
			end.update(null, null);
		}
	}

	/**
	 * constructor
	 * @param npc - npc to go
	 * @param pathes - list of all routes of npc across the world
	 * @param middle - observer for notifying about each route's over.
	 * @param end - observer for notifying about road's end.
	 */
	public MultiZonesFixedPathsList(
			 final SpeakerNPC npc,
			 final List<List<RPZonePath>> pathes,
			 final Observer middle,
			 final Observer end) {
		this.npc = npc;
		this.middle = middle;
		this.end = end;
		count = -1;
		fillMultiZonesList(pathes);
	}


	/**
	 * filling MultiZonesFixedPath list by content of pathes list.
	 * @param pathes - list of npc pathes
	 */
	private void fillMultiZonesList(List<List<RPZonePath>> pathes) {
		for (List<RPZonePath> i: pathes) {
			this.pathes.add(i);
			this.mzfpl.add(new MultiZonesFixedPath(npc, i, this));
		}
	}
}
