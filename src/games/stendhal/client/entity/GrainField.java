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

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class GrainField extends AnimatedEntity {

	private int width;

	private int height;

	private String clazz;

	private int maxRipeness;


	GrainField()  {
		width = 1;
		height = 2;
		clazz = "grain_field";
		maxRipeness = 5;
	}

	public double getHeight() {
		return height;
	}


	public double getWidth() {
		return width;
	}

	void init(final RPObject object) {

		// default values are for compatibility to server <= 0.56


		if (object.has("max_ripeness")) {
			maxRipeness = object.getInt("max_ripeness");
		}
		if (object.has("width")) {
			width = object.getInt("width");
		}
		if (object.has("height")) {
			height = object.getInt("height");
		}

		// load sprites
		if (object.has("class")) {
			clazz = object.get("class");
		}
		super.init(object);
	}

	@Override
	protected void buildAnimations(final RPObject object) {
		// Note: This method is called from the parent constructor, so our
		// own constructor was not able to do any initialisation, yet.
		// So we have to load the object now. But after this method
		// The values we loaded are overriden by the default values, so
		// init has to be called again in our constructor.
//		init(object);

		SpriteStore store = SpriteStore.get();
		for (int i = 0; i <= maxRipeness; i++) {
			sprites.put(Integer.toString(i), store.getAnimatedSprite(translate(clazz), i, 1, width, height));
		}
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
	}

	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("ripeness")) {
			animation = diff.get("ripeness");

		} else if (base.has("ripeness")) {
			animation = base.get("ripeness");
		}
	}

	//

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + height - 1, 1, 1);
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.HARVEST;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);

		list.add(ActionType.HARVEST.getRepresentation());
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at=handleAction(action);
		switch (at) {
			case HARVEST:
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


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new GrainField2DView(this);
	}
}
