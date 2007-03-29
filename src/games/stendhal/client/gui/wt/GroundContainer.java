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

package games.stendhal.client.gui.wt;

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Chest;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.PassiveEntity;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtPanel;

import java.awt.Point;
import java.awt.geom.Point2D;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;

/**
 * 
 * This container is the ground
 * 
 * @author mtotz
 * 
 */

public class GroundContainer extends WtPanel implements Inspector {
	/** the game client */
	private StendhalClient client;

	/**
	 * The UI.
	 */
	private StendhalUI ui;

	/** the game screen */
	private GameScreen screen;


	/** creates a new groundcontainer */
	public GroundContainer(StendhalUI ui) {
		super("ground", 0, 0, ui.getWidth(), ui.getHeight());

		this.ui = ui;

		setMoveable(false);
		setCloseable(false);
		setFrame(false);
		setTitleBar(false);

		client = ui.getClient();
		screen = ui.getScreen();
	}

	/**
	 * 
	 * drops an item to the ground
	 * 
	 */
	@Override
	protected boolean checkDropped(int x, int y, WtDraggable droppedObject) {
		// check all childpanels
		boolean dropped = super.checkDropped(x, y, droppedObject);

		if (dropped) {
			return true;
		}
		// is ot an entity?
		if (droppedObject instanceof MoveableEntityContainer) {
			MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;
			Point2D point = screen.translate(new Point2D.Double(x, y));
			RPAction action = new RPAction();
			if (container.isContained()) {
				// looks like an drop
				action.put("type", "drop");
				// HACK: if ctrl is pressed, attempt to split stackables
				if (ui.isCtrlDown()) {
					action.put("quantity", "1");
				}
			} else {
				// it is a displace
				action.put("type", "displace");
			}
			// fill 'moved from' parameters
			container.fillRPAction(action);

			// tell the server where the item goes to
			action.put("x", (int) point.getX());
			action.put("y", (int) point.getY());
			client.send(action);
			return true;
		}
		// no valid item
		return false;
	}

	/** drags an item from the ground */
	@Override
	protected WtDraggable getDragged(int x, int y) {

		WtDraggable other = super.getDragged(x, y);
		if (other != null) {
			return other;
		}
		Point2D point = screen.translate(new Point2D.Double(x, y));
		GameObjects gameObjects = client.getGameObjects();
		Entity object = gameObjects.at_undercreature(point.getX(), point.getY());

		// only Items can be dragged
		if ((object != null) && (object instanceof PassiveEntity)) {
			return new MoveableEntityContainer(object, (int) point.getX(), (int) point.getY());
		}
		return null;
	}

	/**
	 * 
	 * 
	 * 
	 */
	@Override
	public synchronized boolean onMouseClick(Point p) {
		// base class checks if the click is within a child
		if (super.onMouseClick(p)) {
			// yes, click already processed
			return true;
		}

		// get clicked entity
		Point2D point = screen.translate(p);
		GameObjects gameObjects = client.getGameObjects();
		Entity entity = gameObjects.at(point.getX(), point.getY());

		// for the clicked entity....
		if (entity != null) {
			if (ui.isCtrlDown()) {
				entity.onAction(entity.defaultAction());
			} else if (ui.isShiftDown()) {
				entity.onAction(ActionType.LOOK);
			}
		}
		return true;
	}

	@Override
	public synchronized boolean onMouseDoubleClick(Point p) {
		// base class checks if the click is within a child
		if (super.onMouseDoubleClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		Point2D point = screen.translate(p);
		GameObjects gameObjects = client.getGameObjects();
		Entity entity = gameObjects.at(point.getX(), point.getY());

		if (entity != null) {
			// ... do the default action
			// String action = entity.defaultAction();
			entity.onAction(entity.defaultAction());
		} else {
			// moveto action
			RPAction action = new RPAction();
			action.put("type", "moveto");
			action.put("x", (int) point.getX());
			action.put("y", (int) point.getY());
			client.send(action);
			// TODO: let action do this
			return true;
		}
		return true;
	}

	/** process right click */
	@Override
	public synchronized boolean onMouseRightClick(Point p) {
		// base class checks if the click is within a child
		if (super.onMouseRightClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		Point2D point = screen.translate(p);
		GameObjects gameObjects = client.getGameObjects();
		Entity entity = gameObjects.at(point.getX(), point.getY());

		if (entity != null) {
			// ... show context menu (aka command list)
			String[] actions = entity.offeredActions();
			if (actions.length > 0) {
				CommandList list = new CommandList(entity.getType(), actions, entity);
				ui.getFrame().setContextMenu(list);
			}
		}

		return true;
	}


	//
	// Inspector
	//

	public EntityContainer inspectMe(Entity suspect, RPSlot content, EntityContainer container) {
		if ((container == null) || !container.isVisible()) {
			if (suspect instanceof Chest) {
				container = new EntityContainer(client, suspect.getType(), 4, 5);
			} else {
				container = new EntityContainer(client, suspect.getType(), 2, 2);
			}

			addChild(container);

			container.setSlot(suspect, content.getName());
			container.setVisible(true);
		}

		return container;
	}
}
