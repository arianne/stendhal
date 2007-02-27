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

import marauroa.common.game.*;
import games.stendhal.client.*;
import games.stendhal.client.gui.wt.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.List;

public class Chest extends AnimatedEntity {
	private boolean open;

	private RPSlot content;
	
	private EntityContainer wtEntityContainer;

	/** true means the user requested to open this chest */
	private boolean requestOpen;

	public Chest(RPObject base)
			throws AttributeNotFoundException {
		super( base);
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
				wtEntityContainer=client.getGameGUI().inspect(this, content, 4, 5);
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
			
			if(wtEntityContainer!=null) {
				wtEntityContainer.destroy();
				wtEntityContainer=null;
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
	public String defaultAction() {
		return "Look";
	}


	@Override
	protected void buildOfferedActions(List list) {
		list.add("Look");

		if (open) {
			list.add("Inspect");
			list.add("Close");
		} else {
			list.add("Open");
		}
	}


	@Override
	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Inspect")) {
			client.getGameGUI().inspect(this, content, 4, 5);
		} else if (action.equals("Open") || action.equals("Close")) {
			if (!open) {
				// If it was closed, open it and inspect it...
				requestOpen = true;
			}

			RPAction rpaction = new RPAction();
			rpaction.put("type", "use");
			int id = getID().getObjectID();
			rpaction.put("target", id);
			client.send(rpaction);
		} else {
			super.onAction(client, action, params);
		}
	}

	@Override
	public int getZIndex() {
		return 5000;
	}
}
