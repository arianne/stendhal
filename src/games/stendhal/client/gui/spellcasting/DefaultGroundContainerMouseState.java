/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.spellcasting;

import java.awt.Point;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import games.stendhal.client.StaticGameLayers;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.DragLayer;
import games.stendhal.client.gui.GroundContainer;
import games.stendhal.client.gui.j2d.RemovableSprite;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.EntityViewCommandList;
import games.stendhal.client.gui.wt.core.WtWindowManager;

public class DefaultGroundContainerMouseState extends GroundContainerMouseState {

	public DefaultGroundContainerMouseState(GroundContainer ground) {
		super(ground);
	}

	@Override
	protected boolean onMouseClick(Point point) {
		// Context menu detection
		if (ignoreClick) {
			ignoreClick = false;
			return false;
		}
		// on MS Windows releasing the mouse after a drag&drop action is
		// counted as mouse click: https://sourceforge.net/support/tracker.php?aid=2976895
		if ((Math.abs(point.getX() - xOnMousePressed) > 10)
			|| (Math.abs(point.getY() - yOnMousePressed) > 10)) {
			return false;
		}

		// for the text pop up....
		final RemovableSprite text = ground.getScreen().getTextAt(point.x, point.y);
		if (text != null) {
			ground.getScreen().removeText(text);
			return true;
		}

		// get clicked entity
		final Point2D location = ground.getScreen().convertScreenViewToWorld(point);

		// for the clicked entity....
		final EntityView<?> view = ground.getScreen().getEntityViewAt(location.getX(), location.getY());
		boolean doubleClick = WtWindowManager.getInstance().getPropertyBoolean("ui.doubleclick", false);
		if ((view != null) && view.isInteractive()) {
			if (isCtrlDown()) {
				view.onAction();
				return true;
			} else if (isShiftDown()) {
				view.onAction(ActionType.LOOK);
				return true;
			} else if (!doubleClick) {
				return view.onHarmlessAction();
			}
		} else if (windowWasActiveOnMousePressed && !isCtrlDown()) {
			if (!doubleClick) {
				ground.createAndSendMoveToAction(location, false);
				// let it pass "unhandled", so that the possible double click
				// move can be sent to server as well
			}
		}

		return false;
	}

	@Override
	protected boolean onMouseDoubleClick(Point point) {
		final Point2D location = ground.getScreen().convertScreenViewToWorld(point);

		final EntityView<?> view = ground.getScreen().getEntityViewAt(location.getX(), location.getY());

		if ((view != null) && view.isInteractive()) {
			// ... do the default action
			view.onAction();
			return true;
		} else {
			ground.createAndSendMoveToAction(location, true);
			return true;
		}
	}

	@Override
	protected void onMouseRightClick(Point point) {
		ignoreClick = false;
		final Point2D location = ground.getScreen().convertScreenViewToWorld(point);
		final EntityView<?> view = ground.getScreen().getEntityViewAt(location.getX(), location.getY());

		if (view != null) {
			// ... show context menu (aka command list)
			final String[] actions = view.getActions();

			if (actions.length > 0) {
				final IEntity entity = view.getEntity();

				JPopupMenu menu = new EntityViewCommandList(entity.getType(), actions, view);
				menu.show(ground.getCanvas(), point.x - MENU_OFFSET, point.y - MENU_OFFSET);
				contextMenuFlag = true;
				/*
				 * Tricky way to detect recent popup menues. We need the
				 * information to prevent walking when hiding the menu.
				 */
				menu.addPopupMenuListener(new PopupMenuListener() {
					@Override
					public void popupMenuCanceled(PopupMenuEvent e) {
						//ignore
					}
					@Override
					public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
						/*
						 *  Hidden. inform onMouseClick; unfortunately this gets
						 *  called before onMousePressed, so we need to push it
						 *  pack to the event queue
						 */
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								contextMenuFlag = false;
							}
						});
					}
					@Override
					public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
						// ignore
					}
				});
			}
		}
	}

	@Override
	protected void onDragStart(Point point) {
		ignoreClick = false;
		// Find the entity under the starting point
		final Point2D location = ground.getScreen().convertScreenViewToWorld(point);
		final EntityView<?> view = ground.getScreen().getMovableEntityViewAt(location.getX(),
				location.getY());

		if (view != null) {
			// Let the DragLayer handle the drawing and dropping.
			DragLayer.get().startDrag(view.getEntity());
		}
	}

	@Override
	public void switchState() {
		this.ground.setNewMouseHandlerState(new SpellCastingGroundContainerMouseState(this.ground));
	}

	@Override
	public StendhalCursor getCursor(Point point) {
		StendhalCursor cursor = null;

		// is the cursor aiming at a text box?
		final RemovableSprite text = ground.getScreen().getTextAt(point.x, point.y);
		if (text != null) {
			return StendhalCursor.NORMAL;
		}

		Point2D point2 = ground.getScreen().convertScreenViewToWorld(point);
		final EntityView<?> view = ground.getScreen().getEntityViewAt(point2.getX(), point2.getY());
		// is the cursor aiming at an entity?
		if (view != null) {
			cursor = view.getCursor();
		}

		// is the cursor pointing on the ground?
		if (cursor == null) {
			cursor = StendhalCursor.WALK;
			StaticGameLayers layers = ground.getClient().getStaticGameLayers();
			if ((layers.getCollisionDetection() != null) && layers.getCollisionDetection().collides((int) point2.getX(), (int) point2.getY())) {
				cursor = StendhalCursor.STOP;
			} else if (ground.calculateZoneChangeDirection(point2) != null) {
				cursor = StendhalCursor.WALK_BORDER;
			}
		}
		return cursor;
	}

}
