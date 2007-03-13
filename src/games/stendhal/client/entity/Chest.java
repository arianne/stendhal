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

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.gui.wt.EntityContainer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class Chest extends AnimatedEntity implements Inspectable {
	private boolean open;

	private Inspector _inspector=null;

	private RPSlot content;

	private EntityContainer wtEntityContainer;

	/** true means the user requested to open this chest */
	private boolean requestOpen;

	public Chest(RPObject base) throws AttributeNotFoundException {
		super(base);
		requestOpen = false;
	}

	@Override
	protected void buildAnimations(RPObject base) {
		SpriteStore store = SpriteStore.get();

		sprites.put("close", store.getAnimatedSprite(
				translate(base.get("type")), 0, 1, 1, 1));
		sprites.put("open", store.getAnimatedSprite(
				translate(base.get("type")), 1, 1, 1, 1));
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "close";
		return sprites.get("close")[0];
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("open")) {
			open = true;
			animation = "open";
			// we're wanted to open this?
			if (requestOpen) {
				wtEntityContainer = _inspector.inspectMe(this, content,wtEntityContainer);
				requestOpen = false;
			}
		}

		if (diff.hasSlot("content")) {
			content = diff.getSlot("content");
		}

		if (base.hasSlot("content")) {
			content = base.getSlot("content");
		}
	}

	@Override
	public void onChangedRemoved(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedRemoved(base, diff);

		if (diff.has("open")) {
			open = false;
			animation = "close";
			requestOpen = false;

			if (wtEntityContainer != null) {
				wtEntityContainer.destroy();
				wtEntityContainer = null;
			}

		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.LOOK;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		if (open) {
			list.add(ActionType.INSPECT.getRepresentation());
			list.add(ActionType.CLOSE.getRepresentation());
		} else {
			list.add(ActionType.OPEN.getRepresentation());
		}
	}

	@Override
	public void onAction(ActionType at, String... params) {
		// ActionType at =handleAction(action);
		switch (at) {
		case INSPECT:
			wtEntityContainer = _inspector.inspectMe(this, content,
					wtEntityContainer);// inspect(this, content, 4, 5);
			break;
		case OPEN:
		case CLOSE:
			if (!open) {
				// If it was closed, open it and inspect it...
				requestOpen = true;
			}

			RPAction rpaction = new RPAction();
			rpaction.put("type", at.toString());
			int id = getID().getObjectID();
			rpaction.put("target", id);
			at.send(rpaction);
			break;
		default:
			super.onAction(at, params);
			break;
		}

	}

	@Override
	public int getZIndex() {
		return 5000;
	}

	public void setInspector(Inspector inspector) {
		_inspector = inspector;

	}
}
