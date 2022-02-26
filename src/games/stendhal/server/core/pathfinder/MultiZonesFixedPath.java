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

import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.Registrator;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;

/**
 * class for NPC's multi-zones traveling
 *
 * @author yoriy
 */
public class MultiZonesFixedPath implements Observer {
	private final GuidedEntity ent;
	private final List<RPZonePath> route;
	private Integer count;
	private final Registrator finishnotifier = new Registrator();

	/**
	 * constructor
	 *
	 * @param entity - pathnotifier owner
	 * @param rt route
	 * @param o Observer
	 */
	public MultiZonesFixedPath(final GuidedEntity entity, final List<RPZonePath> rt,
			final Observer o) {
		ent = entity;
		count = -1;
		route = rt;
		finishnotifier.setObserver(o);
	}

	/**
	 *  remove npc from his zone
	 */
	private void removeFromZone() {
		ent.getZone().remove(ent);
	}

	/**
	 *  add npc to next zone in list
	 */
	private void addToZone() {
		// adding observers only at first update
		if (count == 0) {
			ent.pathnotifier.addObserver(this);
			ent.pathnotifier.notifyObservers();
		}
		int x = route.get(count).get().second().get(0).getX();
		int y = route.get(count).get().second().get(0).getY();
		ent.setPosition(x, y);
		StendhalRPZone zone = route.get(count).get().first();
		ent.setPath(new FixedPath(route.get(count).get().second(), false));
		if (ent.getZone() != null) {
			ent.getZone().remove(ent);
		}
		zone.add(ent);
	}

	@Override
	public void update(Observable o, Object arg) {
		// will run at local path's end; have to change path to another
		if (count != (route.size() - 1)) {
			removeFromZone();
			++count;
			addToZone();
		} else {
			// last route finished
			ent.pathnotifier.removeObserver(this);
			finishnotifier.setChanges();
			finishnotifier.notifyObservers();
		}
	}
}
