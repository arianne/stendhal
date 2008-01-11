/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * EntitySlot.java
 *
 * Created on 19. Oktober 2005, 21:14
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.GameScreen;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.awt.Point;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * This is a container which contains exactly one Entity. The name do not have
 * to be unique. Items can be dropped to this container when it is empty. Note
 * that the onDrop() method simply informs the server that the item was dropped.
 * Whatever the server decides will be the next content of this EntitySlot
 * 
 * @author mtotz
 */
public class EntitySlot extends WtPanel implements WtDropTarget {
	/**
	 * The background surface sprite.
	 */
	private static final Sprite background;

	/** the parent of the slot. */
	private Entity parent;

	/**
	 * The entity view being held.
	 */
	private Entity2DView view;

	/**
	 * The client.
	 */
	private StendhalClient client;

	/** The placeholder sprite. */
	private Sprite placeholder;

	static {
		background = SpriteStore.get().getSprite("data/gui/slot.png");
	}

	/**
	 * Create an entity slot.
	 */
	public EntitySlot(StendhalClient client, String name, Sprite placeholder,
			int x, int y) {
		super(name, x, y, background.getWidth(), background.getHeight());
		this.client = client;
		this.placeholder = placeholder;

		view = null;
	}

	public static int getDefaultHeight() {
		return background.getHeight();
	}

	public static int getDefaultWidth() {
		return background.getWidth();
	}

	/**
	 * Set the parent entity.
	 */
	public void setParent(Entity parent) {
		this.parent = parent;
	}

	/** called when an object is dropped. */
	public boolean onDrop(final int x, final int y, WtDraggable droppedObject) {
		if (parent == null) {
			return false;
		}

		if (!(droppedObject instanceof MoveableEntityContainer)) {
			return false;
		}

		MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;

		// Don't drag an item into the same slot
		if ((view != null) && (container.getEntity() == view.getEntity())) {
			return false;
		}

		RPAction action = new RPAction();

		// looks like an equip
		action.put("type", "equip");

		// fill 'moved from' parameters
		container.fillRPAction(action);

		// 'move to'
		action.put("targetobject", parent.getID().getObjectID());
		action.put("targetslot", getName());

		client.send(action);

		return true;
	}

	/**
	 * Set the slot entity.
	 * 
	 * @param entity
	 *            The new entity, or <code>null</code>.
	 */
	public void setEntity(final Entity entity) {
		if (view != null) {
			/*
			 * Don't replace the same object
			 */
			if (view.getEntity() == entity) {
				return;
			}

			view.release();
		}

		if (entity != null) {
			view = GameScreen.get().createView(entity);

			if (view != null) {
				view.setContained(true);
			}
		} else {
			view = null;
		}
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param childArea
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(Graphics2D childArea) {
		super.drawContent(childArea);

		// draw the background image
		background.draw(childArea, 0, 0);

		// draw the entity (if there is any)
		if (view != null) {
			// Center the entity view (assume 1x1 tile)
			int x = (getWidth() - IGameScreen.SIZE_UNIT_PIXELS) / 2;
			int y = (getHeight() - IGameScreen.SIZE_UNIT_PIXELS) / 2;

			Graphics2D vg = (Graphics2D) childArea.create(0, 0, getWidth(),
					getHeight());
			vg.translate(x, y);
			view.draw(vg);
			vg.dispose();
		} else if (placeholder != null) {
			// Center the placeholder sprite
			int x = (getWidth() - placeholder.getWidth()) / 2;
			int y = (getHeight() - placeholder.getHeight()) / 2;
			placeholder.draw(childArea, x, y);
		}
	}

	/**
	 * returns a draggable object.
	 */
	@Override
	protected WtDraggable getDragged(int x, int y) {
		if (view != null) {
			return new MoveableEntityContainer(view.getEntity());
		}

		return null;
	}

	/** right mouse button was clicked. */
	@Override
	public synchronized boolean onMouseRightClick(Point p) {
		if (view != null) {
			// create the context menu
			CommandList list = new CommandList(getName(), view.getActions(),
					view);
			setContextMenu(list);
		}
		return true;
	}

	/** doubleclick moves this item to the players inventory. */
	@Override
	public synchronized boolean onMouseDoubleClick(Point p) {
		// check if the baseclass wants to process the event
		if (super.onMouseDoubleClick(p)) {
			return true;
		}

		// click into an empty slot should be ignored
		if (view == null) {
			return false;
		}

		// moveto events are not the default for items in a bag
		if (parent instanceof Player) {
			view.onAction();
			return true;
		}

		RPObject content = view.getEntity().getRPObject();

		RPAction action = new RPAction();
		action.put("type", "equip");
		// source object and content from THIS container
		action.put("baseobject", parent.getID().getObjectID());
		action.put("baseslot", getName());
		action.put("baseitem", content.getID().getObjectID());
		// target is player's bag
		action.put("targetobject", User.get().getID().getObjectID());
		action.put("targetslot", "bag");
		client.send(action);

		return true;
	}
}
