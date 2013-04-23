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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import marauroa.common.game.RPSlot;

/**
 * A wrapper container for WtPanels outside the game screen.
 */
class ContainerPanel extends JScrollPane implements Inspector {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6660529477793122360L;

	/** The actual content panel. */
	private final JPanel panel;
	/**
	 * Components that should be repainted in the game loop. Uses copy-on-write,
	 * because modifying the list (not a very common operation) is done in the
	 * event dispatch thread, but it's iterated over in the game loop.
	 */
	private final List<JComponent> repaintable = new CopyOnWriteArrayList<JComponent>();

	/**
	 * Create a ContainerPanel.
	 */
	public ContainerPanel() {
		panel = new JPanel();
		panel.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		setViewportView(panel);
		setBorder(null);
	}
	
	@Override
	public void remove(Component component) {
		super.remove(component);
		repaintable.remove(component);
	}
	
	/**
	 * Add a JComponent to the ContainerPanel.
	 * 
	 * @param child component to be added
	 * @param constraints packing constraints
	 */
	void add(JComponent child, Object constraints) {
		child.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(child, constraints);
		panel.revalidate();
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
		repaintable.add(child);
		panel.revalidate();
		/*
		 * Prevent moving the window. This may provide a nice way for users to
		 * reorder the windows eventually. The container would just need to
		 * reorder the windows based on y-order when the dragging ends. 
		 */
		if (child instanceof InternalManagedWindow) {
			((InternalManagedWindow) child).setMovable(false);
		}
		
		if (child instanceof Inspectable) {
			((Inspectable) child).setInspector(this);
		}
	}
	
	/**
	 * Request repainting of all the child panels.
	 */
	void repaintChildren() {
		for (JComponent child : repaintable) {
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
}
