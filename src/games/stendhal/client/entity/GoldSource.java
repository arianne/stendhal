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

/**
 * @author daniel
 *
 */
public class GoldSource extends AnimatedEntity {

	public GoldSource(RPObject object) throws AttributeNotFoundException {
		super(object);
	}

	@Override
	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();
		
		for (int i = 0; i <= 7; i++) {
			sprites.put(Integer.toString(i), store.getAnimatedSprite(translate("gold_source"), i, 1, 1, 1));
		}	
	}

	@Override
	protected Sprite defaultAnimation() {
		animation = "0";
		return sprites.get("0")[0];
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
		return ActionType.PROSPECT;
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		list.add(ActionType.PROSPECT.getRepresentation());
	}

	@Override
	public void onAction(ActionType at, String... params) {
		switch (at) {
			case PROSPECT:
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
		return 3000;
	}

	@Override
    protected String getAnimation() {
	    Integer i = new Integer(animation);
	    if (i==7){
	    	i=0;
	    } else {
	    	i++;
	    	
	    }
	    animation=i.toString();
	
	    return animation;
    }
}
