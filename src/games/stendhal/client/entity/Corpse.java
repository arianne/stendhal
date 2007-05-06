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

import games.stendhal.client.GameScreen;
import games.stendhal.client.gui.wt.EntityContainer;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class Corpse extends PassiveEntity implements Inspectable {

	private Inspector _inspector;

	private EntityContainer contentWindow;



	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.INSPECT;
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at=handleAction(action);
		switch (at) {
			case INSPECT:
				contentWindow = _inspector.inspectMe(this, rpObject.getSlot("content"), contentWindow);
				break;

			default:
				super.onAction(at, params);
				break;
		}
	}

	// /** whether the inspect window is showing for this corpse. */
	// public boolean isContentShowing(EntityContainer entityContainer) {
	// return (entityContainer != null) && !contentWindow.isClosed();
	// }

	public void setInspector(final Inspector inspector) {
		_inspector = inspector;

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
		return new Corpse2DView(this);
	}


	/**
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	@Override
	protected double getHeight() {
		//TODO: Ugg - Don't couple visual size with logical size
		return (double) getView().getSprite().getHeight() / GameScreen.SIZE_UNIT_PIXELS;
	}


	/**
	 * Get the entity width.
	 *
	 * @return	The width.
	 */
	@Override
	protected double getWidth() {
		// Ugg - Don't couple visual size with logical size
		return (double) getView().getSprite().getWidth() / GameScreen.SIZE_UNIT_PIXELS;
	}
}
