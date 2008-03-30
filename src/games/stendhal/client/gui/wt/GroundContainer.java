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
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityView;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.Desktop;
import games.stendhal.client.gui.MouseHandlerAdapter;
import games.stendhal.client.gui.j2d.Text;
import games.stendhal.client.gui.j2d.entity.Entity2DView;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPSlot;

/**
 * 
 * This container is the game play ground.
 * 
 * @author mtotz
 * 
 */
@SuppressWarnings("serial")
public class GroundContainer extends JInternalFrame implements Inspector
{
	/** the game client. */
	private StendhalClient client;

	/** the game screen. */
	private IGameScreen screen;

	/* The game screen panel */
	Desktop desktop;

	final Dimension size;

	/**
	 * creates a new GroundContainer. 
	 */
	public GroundContainer(Desktop desktop, final StendhalClient client, final IGameScreen screen, final Dimension size) {
		setName("baseframe");
//		super("baseframe", 0, 0, width, height);

		this.size = size;
		this.client = client;
		this.screen = screen;
		this.desktop = desktop;

		// register native event handler
		desktop.addMouseListener(new MyMouseHandlerAdapter());
//		desktop.addMouseMotionListener(this);
	}

	class MyMouseHandlerAdapter extends MouseHandlerAdapter {

    	/** Processes popup menus. */
    	@Override
    	protected void onPopup(MouseEvent e) {
    		Point pt = getMousePoint(e);
    		if (pt == null) {
    			return;
    		}

    		Point2D point = screen.convertScreenViewToWorld(pt);

    		Entity2DView view = screen.getEntityViewAt(point.getX(), point.getY());

    		if (view != null) {
    			// ... show context menu (aka command list)
    			String[] actions = view.getActions();

    			if (actions.length > 0) {
    				Entity entity = view.getEntity();

    				new CommandList(entity.getType(), actions, view).show(e.getComponent(), e.getX(), e.getY());
    			}
    		}
    	}

    	/** Process double clicks. */
    	@Override
    	public void onLDoubleClick(MouseEvent e) {
    		Point pt = getMousePoint(e);
    		if (pt == null) {
    			return;
    		}

    		Point2D point = screen.convertScreenViewToWorld(pt);

    		// for the text pop up....
    		Text text = screen.getTextAt(point.getX(), point.getY());
    		if (text != null) {
    			screen.removeText(text);
    			return;
    		}

    		EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

    		if (view != null) {
    			// ... do the default action
    			view.onAction();
    		} else {
    			// moveto action
    			RPAction action = new RPAction();
    			action.put("type", "moveto");
    			action.put("x", (int) point.getX());
    			action.put("y", (int) point.getY());
    			client.send(action);
    			// TODO: let action do this
    		}
    	}

    	@Override
    	public synchronized void onLeftClick(MouseEvent e) {
    		Point pt = getMousePoint(e);
    		if (pt == null) {
    			return;
    		}

    		// get clicked entity
    		Point2D point = screen.convertScreenViewToWorld(pt);

    		// for the text pop up....
    		Text text = screen.getTextAt(point.getX(), point.getY());
    		if (text != null) {
    			screen.removeText(text);
    			return;
    		}

    		// for the clicked entity....
    		EntityView view = screen.getEntityViewAt(point.getX(), point.getY());

    		if (view != null) {
    			if (e.isControlDown()) {
    				view.onAction();
    			} else if (e.isShiftDown()) {
    				view.onAction(ActionType.LOOK);
    			}
    		}
    	}

	}

	private Point getMousePoint(MouseEvent e) {
		Point offset = desktop.getOffset();

		int x = e.getPoint().x - offset.x;
		int y = e.getPoint().y - offset.y;

		if (x >= size.width || y >= size.height) {
			return null;
		}

		if (x < 0 || y < 0) {
			return null;
		}

		return new Point(x, y);
	}

	//
	// Inspector
	//

	public EntityContainer inspectMe(Entity suspect, RPSlot content, EntityContainer container, int width, int height) {
		if (container == null || !container.isVisible()) {
			container = new EntityContainer(suspect.getType(), width, height);

			// immediately free the memory after closing inspect windows
			container.setClosable(true);
			container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			desktop.add(container);

			container.setSlot(suspect, content.getName());
			container.setVisible(true);
		}

		return container;
	}
}
