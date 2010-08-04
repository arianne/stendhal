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

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.DragLayer;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtWindowManager;
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
class EntitySlot extends WtPanel implements WtDropTarget {
	/**
	 * The background surface sprite.
	 */
	private static final Sprite background;

	/** the parent of the slot. */
	private IEntity parent;

	/**
	 * The entity view being held.
	 */
	private EntityView view;

	/** The placeholder sprite. */
	private final Sprite placeholder;

	static {
		background = SpriteStore.get().getSprite("data/gui/slot.png");
	}

	/**
	 * Create an entity slot.
	 * 
	 * @param name
	 * @param placeholder
	 * @param x
	 * @param y
	 * @param gameScreen
	 */
	protected EntitySlot(final String name, final Sprite placeholder, final int x, final int y,
			final IGameScreen gameScreen) {
		super(name, x, y, background.getWidth(), background.getHeight(),
				gameScreen);
		this.placeholder = placeholder;

		view = null;
	}

	protected static int getDefaultHeight() {
		return background.getHeight();
	}

	protected static int getDefaultWidth() {
		return background.getWidth();
	}

	/**
	 * Set the parent entity.
	 * 
	 * @param parent
	 */
	public void setParent(final IEntity parent) {
		this.parent = parent;
	}

	/**
	 * called when an object is dropped.
	 * 
	 * @param x
	 * @param y
	 * @param droppedObject
	 * @return true if succefully dropped
	 */
	public boolean onDrop(final int x, final int y, final WtDraggable droppedObject) {
		if (parent == null) {
			return false;
		}

		if (!(droppedObject instanceof MoveableEntityContainer)) {
			return false;
		}

		final MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;

		// Don't drag an item into the same slot...
		if ((view != null) && (container.getEntity() == view.getEntity())) {
			/*
			 * ...but consider it always a successful drop since a valid
			 * target was found. Otherwise an item dragged on top of
			 * itself goes through the slot to the target below. 
			 */
			return true;
		}

		final RPAction action = new RPAction();

		// looks like an equip
		action.put("type", "equip");

		// fill 'moved from' parameters
		container.fillRPAction(action);

		// 'move to'
		action.put("targetobject", parent.getID().getObjectID());
		action.put("targetslot", getName());

		StendhalClient.get().send(action);

		return true;
	}

	/**
	 * Set the slot entity.
	 * 
	 * @param entity
	 *            The new entity, or <code>null</code>.
	 */
	protected void setEntity(final IEntity entity) {
		if (view != null) {
			/*
			 * Don't replace the same object
			 */
			if (view.getEntity() == entity) {
				return;
			}

			view.release(gameScreen);
		}

		if (entity != null) {
			view = EntityViewFactory.create(entity);

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
	protected void drawContent(final Graphics2D childArea, final IGameScreen gameScreen) {
		super.drawContent(childArea, gameScreen);

		// draw the background image
		background.draw(childArea, 0, 0);

		// draw the entity (if there is any)
		if (view != null) {
			// Center the entity view (assume 1x1 tile)
			final int x = (getWidth() - IGameScreen.SIZE_UNIT_PIXELS) / 2;
			final int y = (getHeight() - IGameScreen.SIZE_UNIT_PIXELS) / 2;

			final Graphics2D vg = (Graphics2D) childArea.create(0, 0, getWidth(),
					getHeight());
			vg.translate(x, y);
			view.draw(vg);
			vg.dispose();
		} else if (placeholder != null) {
			// Center the placeholder sprite
			final int x = (getWidth() - placeholder.getWidth()) / 2;
			final int y = (getHeight() - placeholder.getHeight()) / 2;
			placeholder.draw(childArea, x, y);
		}
	}

	/**
	 * returns a draggable object.
	 */
	@Override
	protected WtDraggable getDragged(final int x, final int y) {
		if ((view != null) && !this.getParent().isMinimized()) {
			// Let the DragLayer handle the drawing and dropping.
			DragLayer.get().startDrag(view.getEntity());
		}

		return null;
	}

	/** right mouse button was clicked. */
	@Override
	public synchronized boolean onMouseRightClick(final Point p) {
		if (view != null) {
			// create the context menu
			final EntityViewCommandList list = new EntityViewCommandList(getName(), view.getActions(),
					view);
			setContextMenu(list);
		}
		return true;
	}

	/** doubleclick moves this item to the players inventory. */
	@Override
	public synchronized boolean onMouseDoubleClick(final Point p) {
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

		moveItemToBag();
		return true;
	}

	private void moveItemToBag() {
		final RPObject content = view.getEntity().getRPObject();
		final RPAction action = new RPAction();
		action.put("type", "equip");
		// source object and content from THIS container
		action.put("baseobject", parent.getID().getObjectID());
		action.put("baseslot", getName());
		action.put("baseitem", content.getID().getObjectID());
		// target is player's bag
		action.put("targetobject", User.get().getID().getObjectID());
		action.put("targetslot", "bag");
		StendhalClient.get().send(action);
	}

	@Override
	public synchronized boolean onMouseClick(Point p) {
		if (super.onMouseClick(p)) {
			return true;
		}

		boolean doubleClick = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("ui.doubleclick", "false"));
		if (doubleClick) {
			return false;
		}

		if (view == null) {
			return false;
		}

		if (parent instanceof Player) {
			return view.onHarmlessAction();
		} else {
			moveItemToBag();
			return true;
		}
	}

	@Override
	public StendhalCursor getCursor(Point point) {		
		if (view == null) {
			return StendhalCursor.NORMAL;
		}

		if (parent instanceof Player) {
			return view.getCursor();
		} else {
			return StendhalCursor.ITEM_PICK_UP_FROM_SLOT;
		}
	}
}
