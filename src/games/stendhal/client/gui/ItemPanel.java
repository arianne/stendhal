/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import games.stendhal.client.IGameScreen;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.j2d.entity.EntityView;
import games.stendhal.client.gui.j2d.entity.EntityViewFactory;
import games.stendhal.client.gui.styled.cursor.CursorRepository;
import games.stendhal.client.gui.styled.cursor.StendhalCursor;
import games.stendhal.client.gui.wt.EntityViewCommandList;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.EquipActionConsts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class ItemPanel extends JComponent implements DropTarget {
	/** 
	 * Amount in pixels to shift the popup menu under the mouse, compared to
	 * the left up corner.
	 */
	private static final int POPUP_MENU_OFFSET = 10;
	private static final CursorRepository cursorRepository = new CursorRepository();
	
	/**
	 * The background surface sprite.
	 */
	private static final Sprite background = SpriteStore.get().getSprite("data/gui/slot.png");
	
	/** The placeholder sprite, or <code>null</code>. */
	private final Sprite placeholder;
	
	/**
	 * The entity view being held.
	 */
	private EntityView view;
	/** The entity to whom the displayed slot belongs to */
	private IEntity parent;
	/**
	 * Current associated popup menu. Using
	 * {@link #setComponentPopupMenu(JPopupMenu)} is
	 * <b>not</b> safe because of a swing bug that can under some conditions
	 * display the menu at the location of the mouse click and prevent the
	 * correct menu displaying code (which does the offset adjustments) from
	 * being called.<p>
	 * Fix for bug #3069835.
	 */
	private JPopupMenu popupMenu;
	
	/**
	 * Create a new ItemPanel.
	 * 
	 * @param slotName name of the slot this refers to 
	 * @param placeholder image used in an empty panel, or <code>null</code>
	 */
	public ItemPanel(final String slotName, final Sprite placeholder) {
		this.placeholder = placeholder;
		setName(slotName);
		
		Dimension size = new Dimension(background.getWidth(), background.getHeight()); 
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setOpaque(false);
		
		// DnD handling
		ItemPanelMouseHandler drag = new ItemPanelMouseHandler();
		addMouseMotionListener(drag);
		addMouseListener(drag);
	}
	
	/**
	 * Set the slot entity.
	 * 
	 * @param entity The new entity, or <code>null</code>.
	 */
	protected void setEntity(final IEntity entity) {
		if (view != null) {
			/*
			 * Don't replace the same object
			 */
			if (view.getEntity() == entity) {
				return;
			}

			view.release(null);
		}

		if (entity != null) {
			view = EntityViewFactory.create(entity);

			if (view != null) {
				view.setContained(true);
				if (parent.isUser()) {
					setCursor(cursorRepository.get(view.getCursor()));
				} else {
					setCursor(cursorRepository.get(StendhalCursor.ITEM_PICK_UP_FROM_SLOT));
				}
			} else {
				Logger.getLogger(ItemPanel.class).error("Failed to create view for entity: "
						+ entity);
			}
		} else {
			view = null;
			setCursor(null);
		}
		
		// The old popup menu is no longer valid
		popupMenu = null;
		repaint();
	}
	
	/**
	 * Set the containing entity.
	 * 
	 * @param parent
	 */
	protected void setParent(IEntity parent) {
		this.parent = parent;
	}

	@Override
	public void paintComponent(Graphics g) {
		// draw the background image
		background.draw(g, 0, 0);
		
		if (view != null) {
			// Center the entity view (assume 1x1 tile)
			final int x = (getWidth() - IGameScreen.SIZE_UNIT_PIXELS) / 2;
			final int y = (getHeight() - IGameScreen.SIZE_UNIT_PIXELS) / 2;

			final Graphics2D vg = (Graphics2D) g.create(0, 0, getWidth(),
					getHeight());
			vg.translate(x, y);
			view.draw(vg);
			vg.dispose();
		} else if (placeholder != null){
			placeholder.draw(g, (getWidth() - placeholder.getWidth()) / 2, 
					(getHeight() - placeholder.getHeight()) / 2);
		}
	}

	public void dropEntity(IEntity entity, Point point) {
		// Don't drag an item into the same slot
		if ((view != null) && (entity == view.getEntity())) {
			return;
		}
		
		
		final RPAction action = new RPAction();
		// looks like an equip
		action.put("type", "equip");

		// fill 'moved from' parameters
		final RPObject rpObject = entity.getRPObject();
		if (rpObject.isContained()) {
			// the item is inside a container
			action.put(EquipActionConsts.BASE_OBJECT, rpObject.getContainer().getID().getObjectID());
			action.put(EquipActionConsts.BASE_SLOT, rpObject.getContainerSlot().getName());
		}
		action.put(EquipActionConsts.BASE_ITEM, rpObject.getID().getObjectID());

		// 'move to'
		action.put(EquipActionConsts.TARGET_OBJECT, parent.getID().getObjectID());
		action.put(EquipActionConsts.TARGET_SLOT, getName());

		StendhalClient.get().send(action);
	}
	
	/**
	 * Handler for mouse use. Takes care of both drags and clicks. 
	 */
	private class ItemPanelMouseHandler extends MouseHandler {
		@Override
		protected void onDragStart(Point point) {
			if (view != null) {
				DragLayer.get().startDrag(view.getEntity());
			}
		}

		@Override
		protected boolean onMouseClick(Point point) {
			// ignore empty slots
			if (view == null) {
				return true;
			}
			
			boolean doubleClick = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("ui.doubleclick", "false"));
			if (doubleClick) {
				return false;
			}

			// Click on entity. Decide on action
			if (parent.isUser()) {
				return view.onHarmlessAction();
			} else {
				moveItemToBag();
				return true;
			}
		}

		@Override
		protected boolean onMouseDoubleClick(Point point) {
			// ignore empty slots
			if (view == null) {
				return false;
			}

			/*
			 * moveto events are not the default for items the player is
			 * carrying along
			 */
			if (parent.isUser()) {
				view.onAction();
				return true;
			}

			// otherwise try to grab the item
			moveItemToBag();
			return true;
		}

		@Override
		protected void onMouseRightClick(Point point) {
			if (view != null) {
				if (popupMenu == null) {
					// create the context menu
					popupMenu = new EntityViewCommandList(getName(), view.getActions(), view) {
						@Override
						protected void doAction(final String command) {
							super.doAction(command);
							setVisible(false);
						}
					};
				}
				/*
				 * Relocate under the cursor. Offset a bit so that the first
				 * entry is under the mouse.
				 */
				popupMenu.show(ItemPanel.this, point.x - POPUP_MENU_OFFSET,
						point.y - POPUP_MENU_OFFSET);
			}
		}
		
		/**
		 * Send an action for grabbing the item to the bag.
		 */
		private void moveItemToBag() {
			final RPObject content = view.getEntity().getRPObject();
			final RPAction action = new RPAction();
			
			action.put("type", "equip");
			// source object and content from THIS container
			action.put(EquipActionConsts.BASE_OBJECT, parent.getID().getObjectID());
			action.put(EquipActionConsts.BASE_SLOT, getName());
			action.put(EquipActionConsts.BASE_ITEM, content.getID().getObjectID());
			// target is player's bag
			action.put(EquipActionConsts.TARGET_OBJECT, User.get().getID().getObjectID());
			action.put(EquipActionConsts.TARGET_SLOT, "bag");
			StendhalClient.get().send(action);
		}
	}
}
