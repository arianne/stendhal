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

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.layout.SBoxLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import marauroa.common.game.RPSlot;

/**
 * A wrapper container for WtPanels outside the game screen.
 */
class ContainerPanel extends JScrollPane implements Inspector, InternalManagedWindow.WindowDragListener {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6660529477793122360L;

	/** The actual content panel. */
	private final JPanel panel;
	/**
	 * Temporary position of a dragged internal window in the content panel.
	 */
	private int draggedPosition;

	/**
	 * Create a ContainerPanel.
	 */
	public ContainerPanel() {
		panel = new JPanel();
		panel.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		setViewportView(panel);
		setBorder(null);
	}
	
	/**
	 * Add a component that should be repainted in the drawing loop. This is
	 * not a particularly pretty way to do it, but individual timers for item
	 * slots end up being more expensive, and the RepaintManager merges the
	 * draw request anyway.
	 * 
	 * @param child
	 */
	void addRepaintable(JComponent child) {
		child.setIgnoreRepaint(true);
		panel.add(child);
		panel.revalidate();
		
		if (child instanceof InternalManagedWindow) {
			((InternalManagedWindow) child).addWindowDragListener(this);
		}
		
		if (child instanceof Inspectable) {
			((Inspectable) child).setInspector(this);
		}
	}
	
	/**
	 * Request repainting of all the child panels.
	 */
	void repaintChildren() {
		for (Component child : panel.getComponents()) {
			child.repaint();
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension size = panel.getPreferredSize();
		JComponent scrollBar = getVerticalScrollBar();
		if (scrollBar.isVisible()) {
			/*
			 * Try to claim a bit more space if the user enlarges the window and
			 * there's not enough space sidewise.
			 */
			size.width += scrollBar.getWidth();
		}
		return size;
	}

	/**
	 * Inspect an entity slot. Show the result within the ContainerPanel.
	 * 
	 * @param entity the inspected entity
	 * @param content slot to be inspected
	 * @param container previously created slot window for the inspected slot,
	 * 	or <code>null</code> if there's no such window
	 * @param width number of slot columns
	 * @param height number of slot rows
	 */
	@Override
	public SlotWindow inspectMe(IEntity entity, RPSlot content,
			SlotWindow container, int width, int height) {
		if ((container != null) && container.isVisible()) {
			// Nothing to do. 
			return container;
		} else {
			SlotWindow window = new SlotWindow(entity.getName(), width, height);
			window.setSlot(entity, content.getName());
			window.setAcceptedTypes(EntityMap.getClass("item", null, null));
			window.setVisible(true);
			window.setAlignmentX(LEFT_ALIGNMENT);
			addRepaintable(window);
			return window;
		}
	}
	
	/**
	 * Get the vertical center point of a component.
	 * 
	 * @param component component to be checked
	 * @return the Y coordinate of the component center point
	 */
	private int componentYCenter(Component component) {
		return component.getY() + component.getHeight() / 2;
	}
	
	@Override
	public void windowDragged(Component component, Point point) {
		int centerY = point.y + component.getHeight() / 2;
		for (int i = 0; i < panel.getComponentCount(); i++) {
			Component tmp = panel.getComponent(i);
			if (tmp != component) {
				if ((draggedPosition < i) && (centerY > componentYCenter(tmp))) {
					draggedPosition = i;
					break;
				} else if ((draggedPosition >= i) && (centerY < componentYCenter(tmp))) {
					draggedPosition = i - 1;
					break;
				}
			}
		}
	}

	@Override
	public void startDrag(Component component) {
		draggedPosition = panel.getComponentZOrder(component);
		panel.setComponentZOrder(component, 0);
	}

	@Override
	public void endDrag(Component component) {
		panel.setComponentZOrder(component, draggedPosition);
		panel.revalidate();
	}
}
