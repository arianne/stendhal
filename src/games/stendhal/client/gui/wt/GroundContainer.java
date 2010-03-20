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
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.wt.core.WtBaseframe;
import games.stendhal.client.gui.wt.core.WtDraggable;
import games.stendhal.client.gui.wt.core.WtDropTarget;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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
	private final StendhalClient client;

	/**
	 * The UI.
	 */
	private final j2DClient ui;

	/** the game screen. */
	private final IGameScreen screen;

	/**
	 * creates a new groundcontainer.
	 * 
	 * @param client
	 * @param gameScreen
	 * @param width
	 * @param height
	 */
	public GroundContainer(final StendhalClient client,
			final IGameScreen gameScreen, final int width, final int height) {
		super(width, height, gameScreen);

		this.client = client;
		this.screen = gameScreen;

		ui = j2DClient.get();
	}

	/** drags an item from the ground . */
	@Override
	protected WtDraggable getDragged(final int x, final int y) {
		final WtDraggable other = super.getDragged(x, y);

		if (other != null) {
			return other;
		}

		final Point2D point = screen.convertScreenViewToWorld(x, y);
		final EntityView view = screen.getMovableEntityViewAt(point.getX(), point
				.getY());

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
	public synchronized boolean onMouseClick(final Point p) {
		// base class checks if the click is within a child
		if (super.onMouseClick(p)) {
			// yes, click already processed
			return true;
		}

		// get clicked entity
		final Point2D point = screen.convertScreenViewToWorld(p);

		// for the text pop up....
		final Text text = screen.getTextAt(point.getX(), point.getY());
		if (text != null) {
			screen.removeText(text);
			return true;
		}

		// for the clicked entity....
		final EntityView view = screen.getEntityViewAt(point.getX(), point.getY());
		if (view != null) {
			if (ui.isCtrlDown()) {
				view.onAction();
				return true;
			} else if (ui.isShiftDown()) {
				view.onAction(ActionType.LOOK);
				return true;
			}
		} else if (getWtPanelAt(p) == null) {
			createAndSendMoveToAction(point);
			return true;
		}

		return false;
	}

	@Override
	public synchronized boolean onMouseDoubleClick(final Point p) {
		// base class checks if the click is within a child
		if (super.onMouseDoubleClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		final Point2D point = screen.convertScreenViewToWorld(p);

		// for the text pop up....
		final Text text = screen.getTextAt(point.getX(), point.getY());
		if (text != null) {
			screen.removeText(text);
			return true;
		}

		final EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

		if (view != null) {
			// ... do the default action
			view.onAction();
			return true;
		} else {
			createAndSendMoveToAction(point);
			return true;
		}
	}

	private void createAndSendMoveToAction(final Point2D point) {
		final RPAction action = new RPAction();
		action.put("type", "moveto");
		action.put("x", (int) point.getX());
		action.put("y", (int) point.getY());
		client.send(action);
	}

	/** Processes right click. */
	@Override
	public synchronized boolean onMouseRightClick(final Point p) {
		// base class checks if the click is within a child
		if (super.onMouseRightClick(p)) {
			// yes, click already processed
			return true;
		}
		// doubleclick is outside of all windows
		final Point2D point = screen.convertScreenViewToWorld(p);

		final EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

		if (view != null) {
			// ... show context menu (aka command list)
			final String[] actions = view.getActions();

			if (actions.length > 0) {
				final IEntity entity = view.getEntity();

				setContextMenu(new CommandList(entity.getType(), actions, view));
			}
			return true;
		}

		return false;
	}


	
	
	//
	// WtDropTarget
	//
	
	/**
	 * called when an object is dropped.
	 * 
	 * @param x
	 * @param y
	 * @param droppedObject
	 * @return true if droppedobject instance of MovableentityContainer false
	 *         otherwise
	 */
	public boolean onDrop(final int x, final int y, final WtDraggable droppedObject) {
		// Not an entity?
		if (!(droppedObject instanceof MoveableEntityContainer)) {
			return false;
		}

		final MoveableEntityContainer container = (MoveableEntityContainer) droppedObject;

		final RPAction action = new RPAction();

		if (container.isContained()) {
			// looks like an drop
			action.put("type", "drop");
		} else {
			// it is a displace
			action.put("type", "displace");
		}

		// if ctrl is pressed, attempt to split stackables
		if (ui.isCtrlDown()) {
			action.put("quantity", 1);
		}

		// fill 'moved from' parameters
		container.fillRPAction(action);

		// 'move to'
		final Point2D point = screen.convertScreenViewToWorld(x, y);
		action.put("x", (int) point.getX());
		action.put("y", (int) point.getY());

		client.send(action);
		return true;
	}

	//
	// Inspector
	//

	public EntityContainer inspectMe(final IEntity suspect, final RPSlot content,
			final EntityContainer container, final int width, final int height,
			final IGameScreen gameScreen) {
		if ((container != null) && container.isVisible()) {
			return container;
		} else {
			EntityContainer newContainer = new EntityContainer(suspect.getType(), width, height,
					gameScreen);

			addChild(newContainer);

			newContainer.setSlot(suspect, content.getName(), gameScreen);
			newContainer.setVisible(true);
			return newContainer;
		}
	}
	
	
}
