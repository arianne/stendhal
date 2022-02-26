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
package games.stendhal.server.entity.npc.interaction;

import java.util.LinkedList;

import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * class for make one npc follower of other.
 * @author yoriy
 */
public final class NPCFollowing implements Observer {
	final private SpeakerNPC follower;
	final private SpeakerNPC leader;
	final private Observer finish;

	/**
	 * constructor
	 * @param leader - NPC for follow him.
	 * @param follower - follower of leader.
	 * @param finish - code to continue after follower meet leader.
	 */
	public NPCFollowing(final SpeakerNPC leader, final SpeakerNPC follower, final Observer finish) {
		this.leader=leader;
		this.follower=follower;
		this.finish=finish;
	}

	@Override
	public void update(Observable o, Object arg) {
		follower.clearPath();
		follower.pathnotifier.deleteObservers();
		moveToProperDistance();
	}

	/**
	 * return 1/3 of follower's path
	 * @param path
	 * @return - a part of path
	 */
	public FixedPath getOneThirdOfPath(FixedPath path) {
		final LinkedList<Node> templ = new LinkedList<Node>();
		for(int i=0; i<path.getNodeList().size()/2; i++) {
			templ.add(path.getNodeList().get(i));
		}
		return new FixedPath(templ, false);
	}

	/**
	 * move follower close to leader.
	 */
	private void moveToProperDistance() {
		final double dist = leader.squaredDistance(follower);
		int range = leader.getPerceptionRange();
		if (dist > range+1) {
			follower.setMovement(leader, 0, range, dist*1.5);
			follower.setPath(getOneThirdOfPath(follower.getPath()));
			follower.pathnotifier.addObserver(this);
		} else {
			follower.stop();
			follower.pathnotifier.deleteObservers();
			finish.update(null, null);
		}
	}
}
