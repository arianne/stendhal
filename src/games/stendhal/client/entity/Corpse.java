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
import games.stendhal.client.SpriteStore;
import games.stendhal.client.gui.wt.EntityContainer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

public class Corpse extends PassiveEntity implements Inspectable {
	private Inspector _inspector;

	private EntityContainer contentWindow;

	public Corpse(RPObject object) throws AttributeNotFoundException {
		super(object);
	}

	@Override
	public Rectangle2D getArea() {

		return new Rectangle.Double(x, y, (double) sprite.getWidth()
				/ GameScreen.SIZE_UNIT_PIXELS, (double) sprite.getHeight()
				/ GameScreen.SIZE_UNIT_PIXELS);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, (double) sprite.getWidth()
				/ GameScreen.SIZE_UNIT_PIXELS, (double) sprite.getHeight()
				/ GameScreen.SIZE_UNIT_PIXELS);
	}

	@Override
	protected void loadSprite(RPObject object) {
		String corpseType = object.get("type");

		if (object.get("class").equals("player")) {
			corpseType = corpseType + "_player";
		} else if (object.get("class").equals("giant_animal")) {
			corpseType = corpseType + "_giantrat";
		} else if (object.get("class").equals("huge_animal")) {
			corpseType = corpseType + "_giantrat";
		} else if (object.get("class").equals("mythical_animal")) {
			corpseType = corpseType + "_giantrat";
		}

		SpriteStore store = SpriteStore.get();
		sprite = store.getSprite(translate(corpseType));
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.INSPECT;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {

		super.buildOfferedActions(list);
		;

	}

	@Override
	public void onAction(ActionType at, String... params) {
		// ActionType at=handleAction(action);
		switch (at) {
		case INSPECT:
			contentWindow = _inspector.inspectMe(this, rpObject
					.getSlot("content"), contentWindow);
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

	@Override
	public int getZIndex() {
		return 5500;
	}

	public void setInspector(Inspector gadget) {
		_inspector = gadget;

	}
}
