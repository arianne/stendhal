/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import marauroa.common.game.RPObject;

/**
 * A chest entity.
 */
public class Ring extends Item {
	/**
	 * Whether the chest is currently working.
	 */
	private boolean working;

	/**
	 * Create a Ring entity.
	 */
	Ring() {
	}

	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	@Override
	protected Entity2DView createView() {
		return new Ring2DView(this);
	}


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		/*
		 * A ring works either by not having amount of having amount>0
		 */
		working=(((!object.has("amount")) || object.has("amount") && object.getInt("amount")>0));
		changed();
	}

	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * A ring works either by not having amount of having amount>0
		 */
		working=(((!object.has("amount")) || object.has("amount") && object.getInt("amount")>0));
		changed();
	}


	@Override
	public ActionType defaultAction() {
		return ActionType.LOOK;
	}
	
	public boolean isWorking() {
		return working;
	}
}
