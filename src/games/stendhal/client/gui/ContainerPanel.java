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

import games.stendhal.client.gui.layout.SBoxLayout;

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A wrapper container for WtPanels outside the game screen.
 */
public class ContainerPanel extends JScrollPane {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -6660529477793122360L;

	/** The actual content panel. */
	private final JPanel panel;
	/** Components that should be repainted in the game loop.  */
	private final List<JComponent> repaintable = new LinkedList<JComponent>();

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
	 * Add a JComponent to the ContainerPanel.
	 * 
	 * @param child component to be added
	 * @param constraints packing constraints
	 */
	public void add(JComponent child, Object constraints) {
		child.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(child, constraints);
	}
	
	/**
	 * Add a component that should be repainted in the drawing loop. This is
	 * not a particularly pretty way to do it, but individual timers for item
	 * slots end up being more expensive, and the RepaintManager merges the
	 * draw request anyway.
	 * 
	 * @param child
	 */
	public void addRepaintable(JComponent child) {
		panel.add(child);
		repaintable.add(child);
		/*
		 * Prevent moving the window. This may provide a nice way for users to
		 * reorder the windows eventually. The container would just need to
		 * reorder the windows based on y-order when the dragging ends. 
		 */
		if (child instanceof InternalManagedWindow) {
			((InternalManagedWindow) child).setMovable(false);
		}
	}
	
	/**
	 * Request repainting of all the child panels.
	 */
	public void repaintChildren() {
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
}
