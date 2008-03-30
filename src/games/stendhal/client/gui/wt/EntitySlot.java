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
import games.stendhal.client.gui.DragDropOwner;
import games.stendhal.client.gui.DragDropSource;
import games.stendhal.client.gui.DragDropTarget;
import games.stendhal.client.gui.MouseHandlerAdapter;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

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
@SuppressWarnings("serial")
public class EntitySlot extends JPanel implements WtDropTarget, DragDropOwner
{
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

	/** The placeholder sprite. */
	private Sprite placeholder;

	static {
		background = SpriteStore.get().getSprite("data/gui/slot.png");
	}

	/**
	 * Create an entity slot.
	 */
	public EntitySlot(String name, Sprite placeholder, int x, int y) {
		setName(name);

		setLocation(x, y);
		setSize(background.getWidth(), background.getHeight());

		this.placeholder = placeholder;

		view = null;

		addMouseListener(new MyMouseHandlerAdapter());

		new DragDropTarget(this).associate(this);
		new DragDropSource(this).associate(this);
	}

	/** called from EntityContainer */
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
	public boolean onDrop(final DropTargetDropEvent dsde, final WtDraggable droppedObject) {
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

		StendhalClient.get().send(action);

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
    public void paint(Graphics childArea) {
		super.paint(childArea);

		// draw the background image
		background.draw(childArea, 0, 0);

		// draw the entity (if there is any)
		if (view != null) {
			// Center the entity view (assume 1x1 tile)
			int x = (getWidth() - IGameScreen.SIZE_UNIT_PIXELS) / 2;
			int y = (getHeight() - IGameScreen.SIZE_UNIT_PIXELS) / 2;

			Graphics2D vg = (Graphics2D) childArea.create(0, 0, getWidth(), getHeight());
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

	private final class MyMouseHandlerAdapter extends MouseHandlerAdapter {

		/** A popup menu has been triggered. */
    	@Override
        protected void onPopup(MouseEvent e) {
            if (view != null) {
            	// create the context menu
            	CommandList popup = new CommandList(getName(), view.getActions(), view);

            	popup.display(e);
            }
        }

        /** doubleclick moves this item to the players inventory. */
    	@Override
    	protected void onLDoubleClick(MouseEvent e) {
            // click into an empty slot should be ignored
            if (view == null) {
            	return;
            }

            // moveto events are not the default for items in a bag
            if (parent instanceof Player) {
            	view.onAction();
            	return;
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
    
            StendhalClient.get().send(action);
        }

	}

	/**
	 * returns a draggable object.
	 */
    public WtDraggable getDragged(Point pt) {
		if (view != null) {
			return new MoveableEntityContainer(view.getEntity());
		}

		return null;
	}

	/** Return position of the client area. */
	public Point getClientPos() {
		return new Point(0, 0);	//@@
	}
}
