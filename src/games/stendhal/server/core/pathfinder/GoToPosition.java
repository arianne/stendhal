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

import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.Registrator;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * class for guide NPC to certain point in his zone
 * @author yoriy
 */
public class GoToPosition implements Observer {
		private final GuidedEntity ent;
		private final Registrator finishnotifier = new Registrator();
		private FixedPath path;
		private final Node position;
		// flag for checking started state
		private boolean s = false;

	/**
	 * Constructor
	 *
	 * @param entity - path notifier owner
	 * @param position target position
	 * @param o observer
	 */
	public GoToPosition(GuidedEntity entity, Node position, Observer o) {
		ent=entity;
		finishnotifier.setObserver(o);
		this.position = position;
	}

	/**
	 * final part of travel
	 */
	public void finish()
	{
		// removing ourselves from npc's path end notifications
		ent.pathnotifier.removeObserver(this);
		finishnotifier.setChanges();
		finishnotifier.notifyObservers();
	}

	/**
	 * update function
	 * @param o - not used
	 * @param arg - arguments, not used
	 */
	@Override
	public void update(Observable o, Object arg) {
	    // are we at our destination?
		Node current = new Node(ent.getX(), ent.getY());
		if(current.equals(position)) {
			finish();
		} else {
			// do we need to walk?
		    path = new FixedPath(Path.searchPath(ent, position.getX(), position.getY()),false);
			if(path.getNodeList().size()==0) {
				finish();
			} else {
				// do we started already?
				if(s) {
					// yes, finishing
					s=false;
					finish();
				} else {
					// no, adding ourselves to npc's path end notifications
					ent.pathnotifier.setObserver(this);
					ent.setPath(path);
					s=true;
				}
			}
		}
	}
}
