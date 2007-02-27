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

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityFabric;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Color;
import java.awt.Graphics;
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
	/** the (background) sprite for this slot */
	private Sprite graphic;

	/** the content of the slot */
	private RPObject content;

	/** the parent of the slot */
	private Entity parent;

	/** need this to find the sprite for each RPObject */
	private GameObjects gameObjects;

	/** sprite for showing the quartity */
	private Sprite quantityImage;

	/** cached old quantity */
	private int oldQuantity;

	/** cached sprite for the entity */
	private Sprite sprite;

	/** Creates a new instance of RPObjectSlot */
	public EntitySlot(String name, Sprite graphic, int x, int y,
			GameObjects gameObjects) {
		super(name, x, y, graphic.getWidth(), graphic.getHeight());
		this.graphic = graphic;
		this.gameObjects = gameObjects;
	}

	/** */
	public void setParent(Entity parent) {
		this.parent = parent;
	}

	/** called when an object is dropped. */
	public boolean onDrop(WtDraggable droppedObject) {
		if ((droppedObject instanceof MoveableEntityContainer) && (parent != null)) {
			MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;
			RPAction action = new RPAction();

			// Entity contained=container.getEntity();
			// if(contained.distance(StendhalClient.get().getPlayer())>2)
			// {
			// System.out.println (contained);
			//
			// RPAction rpaction = new RPAction();
			// rpaction.put("type","moveto");
			// rpaction.put("x",(int)contained.getX());
			// rpaction.put("y",(int)contained.getY());
			// StendhalClient.get().send(rpaction);
			// }
			//      
			// looks like an equip
			action.put("type", "equip");
			// fill 'moved from' parameters
			container.fillRPAction(action);
			// tell the server who we are
			action.put("targetobject", parent.getID().getObjectID());
			action.put("targetslot", getName());

			System.out.println(action);
			StendhalClient.get().send(action);
		}

		return false;
	}

	/** clears the content of this slot */
	public void clear() {
		content = null;
		sprite = null;
	}

	/** adds an object to this slot, this replaces any previous content */
	public void add(RPObject object) {
		content = object;
		sprite = gameObjects.spriteType(content);
	}

	/**
	 * ensures that the quantity image is set
	 */
	private void checkQuantityImage(int quantity) {
		if ((quantityImage == null) || (quantity != oldQuantity)) {
			oldQuantity = quantity;
			quantityImage = GameScreen.get().createString(
					Integer.toString(quantity), Color.white);
		}
	}

	/**
	 * draws the panel into the graphics object
	 * 
	 * @param g
	 *            graphics where to render to
	 * @return a graphics object for deriving classes to use. It is already
	 *         clipped to the correct client region
	 */
	@Override
	public Graphics draw(Graphics g) {
		if(isClosed()) {
			return g;
		}

		Graphics childArea = super.draw(g);

		// draw the background image
		graphic.draw(childArea, 0, 0);
		// draw the content (if there is any)
		if ((content != null) && (sprite != null)) {
			// be sure to center the sprite
			int x = (getWidth() - sprite.getWidth()) / 2;
			int y = (getHeight() - sprite.getHeight()) / 2;
			sprite.draw(childArea, x, y);

			// draw the amount if this item is stackable
			if (content.has("quantity")) {
				int quantity = content.getInt("quantity");
				checkQuantityImage(quantity);
				quantityImage.draw(childArea, 0, 0);
			}

		}

		return childArea;
	}

	/**
	 * returns a draggable object
	 */
	@Override
	protected WtDraggable getDragged(int x, int y) {
		if (content != null) {
			return new MoveableEntityContainer(content, parent, getName(),
					gameObjects);
		}

		return null;
	}

	/** right mouse button was clicked */
	@Override
	public synchronized boolean onMouseRightClick(Point p) {
		if (content != null) {
			// create the context menu
			StendhalClient client = StendhalClient.get();
			Entity entity = EntityFabric.createEntity(content);
			CommandList list = new CommandList(getName(), entity
					.offeredActions(), client, entity);
			list.setContext(parent.getID().getObjectID(), getName());
			setContextMenu(list);
		}
		return true;
	}

	/** doubleclick moves this item to the players inventory */
	@Override
	public synchronized boolean onMouseDoubleClick(Point p) {
		// check if the baseclass wants to process the event
		if (super.onMouseDoubleClick(p)) {
			return true;
		}
    
    // click into an empty slot should be ignored
    if(content == null) {
      return(false);
    }
    
    // moveto events are not the default for items in a bag
    if(parent instanceof Player) {
      Entity entity = EntityFabric.createEntity(content);
      if(entity != null) {
        String action = entity.defaultAction();
        entity.onAction(StendhalClient.get(), action, Integer.toString(parent.getID().getObjectID()), getName());
        return(true);
      }
      return (false);
    }
    
		RPObject player = StendhalClient.get().getPlayer();

		// if(parent.distance(StendhalClient.get().getPlayer())>2)
		// {
		// RPAction rpaction = new RPAction();
		// rpaction.put("type","moveto");
		// System.out.println (parent);
		// rpaction.put("x",(int)parent.getX());
		// rpaction.put("y",(int)parent.getY());
		// StendhalClient.get().send(rpaction);
		// }

		RPAction action = new RPAction();
		action.put("type", "equip");
		// source object and content from THIS container
		action.put("baseobject", parent.getID().getObjectID());
		action.put("baseslot", getName());
		action.put("baseitem", content.getID().getObjectID());
		// target is player's bag
		action.put("targetobject", player.getID().getObjectID());
		action.put("targetslot", "bag");
		StendhalClient.get().send(action);

		return true;
	}

}
