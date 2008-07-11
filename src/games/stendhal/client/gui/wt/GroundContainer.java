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

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.wt.core.WtBaseframe;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;

import java.awt.Point;
import java.awt.geom.Point2D;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;

/**
 * 
 * This container is the ground.
 * 
 * @author mtotz
 * 
 */
public class GroundContainer extends WtBaseframe implements WtDropTarget,
		Inspector {
	/** the game client. */
	private StendhalClient client;

	/**
	 * The UI.
	 */
	private StendhalUI ui;

	/** the game screen. */
	private IGameScreen screen;

	/** creates a new groundcontainer. 
	 * @param client 
	 * @param screen 
	 * @param width 
	 * @param height */
	public GroundContainer(final StendhalClient client,
			final IGameScreen screen, final int width, final int height) {
		super(width, height);

		this.client = client;
		this.screen = screen;

		ui = StendhalUI.get();
	}

	/** drags an item from the ground .*/
	@Override
	protected WtDraggable getDragged(int x, int y) {
		WtDraggable other = super.getDragged(x, y);

		if (other != null) {
			return other;
		}

		Point2D point = screen.convertScreenViewToWorld(x, y);
		EntityView view = screen.getMovableEntityViewAt(point.getX(),
				point.getY());

		// only Items can be dragged
		if (view != null) {
			return new MoveableEntityContainer(view.getEntity());
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
		Point2D point = screen.convertScreenViewToWorld(p);

		// for the text pop up....
		Text text = screen.getTextAt(point.getX(), point.getY());
		if (text != null) {
			screen.removeText(text);
			return true;
		}

		// for the clicked entity....
		EntityView view = screen.getEntityViewAt(point.getX(), point.getY());
		if (view != null) {
			if (ui.isCtrlDown()) {
				view.onAction();
				return true;
			} else if (ui.isShiftDown()) {
				view.onAction(ActionType.LOOK);
				return true;
			}
		}

		return false;
	}

	@Override
	public synchronized boolean onMouseDoubleClick(Point p) {
		// base class checks if the click is within a child
		if (super.onMouseDoubleClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		Point2D point = screen.convertScreenViewToWorld(p);

		// for the text pop up....
		Text text = screen.getTextAt(point.getX(), point.getY());
		if (text != null) {
			screen.removeText(text);
			return true;
		}

		EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

		if (view != null) {
			// ... do the default action
			view.onAction();
			return true;
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
	}

	/** Processes right click. */
	@Override
	public synchronized boolean onMouseRightClick(Point p) {
		// base class checks if the click is within a child
		if (super.onMouseRightClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		Point2D point = screen.convertScreenViewToWorld(p);

		EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

		if (view != null) {
			// ... show context menu (aka command list)
			String[] actions = view.getActions();

			if (actions.length > 0) {
				Entity entity = view.getEntity();

				setContextMenu(new CommandList(entity.getType(), actions, view));
			}
			return true;
		}

		return false;
	}

	//
	// WtDropTarget
	//

	/** called when an object is dropped. 
	 * @param x 
	 * @param y 
	 * @param droppedObject 
	 * @return true if droppedobject instance of MovableentityContainer false otherwise*/
	public boolean onDrop(final int x, final int y, WtDraggable droppedObject) {
		// Not an entity?
		if (!(droppedObject instanceof MoveableEntityContainer)) {
			return false;
		}

		MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;

		RPAction action = new RPAction();

		if (container.isContained()) {
			// looks like an drop
			action.put("type", "drop");
		} else {
			// it is a displace
			action.put("type", "displace");
		}

		// HACK: if ctrl is pressed, attempt to split stackables
		if (ui.isCtrlDown()) {
			action.put("quantity", 1);
		}

		// fill 'moved from' parameters
		container.fillRPAction(action);

		// 'move to'
		Point2D point = screen.convertScreenViewToWorld(x, y);
		action.put("x", (int) point.getX());
		action.put("y", (int) point.getY());

		client.send(action);
		return true;
	}

	//
	// Inspector
	//

	public EntityContainer inspectMe(Entity suspect, RPSlot content,
			EntityContainer container, int width , int height) {
		if ((container == null) || !container.isVisible()) {
			container = new EntityContainer(suspect.getType(), width, height);
			

			addChild(container);

			container.setSlot(suspect, content.getName());
			container.setVisible(true);
		}

		return container;
	}
}
