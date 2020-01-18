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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Transparency;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Inspector;
import games.stendhal.client.entity.factory.EntityMap;
import games.stendhal.client.gui.layout.AnimatedLayout;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import marauroa.common.game.RPSlot;
/**
 * A wrapper container for WtPanels outside the game screen.
 */
class ContainerPanel extends JScrollPane implements Inspector, InternalManagedWindow.WindowDragListener {
	/** Property name of the stored window order. */
	private static final String WINDOW_ORDER_PROPERTY = "ui.window_order";
	/**
	 * Window order by name, including any windows that the user might not
	 * have open but have stored order.
	 */
	private final List<String> windowOrder;

	/** The actual content panel. */
	private final PhantomLayoutPanel panel;
	/**
	 * Temporary position of a dragged internal window in the content panel.
	 */
	private int draggedPosition;

	/**
	 * Create a ContainerPanel.
	 */
	public ContainerPanel() {
		panel = new PhantomLayoutPanel();
		/*
		 * An ugly way to turn off animations on slow systems. As a side effect
		 * gets turned off also on systems where the tranlucency has been
		 * explicitly disabled.
		 */
		if (TransparencyMode.TRANSPARENCY == Transparency.TRANSLUCENT) {
			panel.setLayout(new AnimatedLayout(new SBoxLayout(SBoxLayout.VERTICAL)));
		} else {
			panel.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
		}
		setViewportView(panel);
		setBorder(null);
		String orderProp = WtWindowManager.getInstance().getProperty(WINDOW_ORDER_PROPERTY, "character;bag;keyring"); /*;portfolio");*/
		windowOrder = new ArrayList<String>(Arrays.asList(orderProp.split(";")));
		getVerticalScrollBar().setUnitIncrement(16);
	}

	/**
	 * Set whether the panel should animate layout changes.
	 *
	 * @param animate <code>true</code> if layout changes should be animated,
	 * otherwise <code>false</code>
	 */
	public void setAnimated(boolean animate) {
		LayoutManager layout = panel.getLayout();
		if (layout instanceof AnimatedLayout) {
			((AnimatedLayout) layout).setAnimated(animate);
		}
	}

	/**
	 * Add a component that should be repainted in the drawing loop. This is
	 * not a particularly pretty way to do it, but individual timers for item
	 * slots end up being more expensive, and the RepaintManager merges the
	 * draw request anyway.
	 *
	 * @param child component to add
	 */
	void addRepaintable(JComponent child) {
		int position = panel.getComponentCount();
		if (child instanceof InternalManagedWindow) {
			InternalManagedWindow window = (InternalManagedWindow) child;
			window.addWindowDragListener(this);
			position = findWindowPosition(window.getName());
		}

		if (child instanceof Inspectable) {
			((Inspectable) child).setInspector(this);
		}

		child.setIgnoreRepaint(true);
		child.setAlignmentX(LEFT_ALIGNMENT);
		panel.add(child, position);
		panel.revalidate();
	}

	/**
	 * Find the correct position to add a named component.
	 *
	 * @param window component name
	 * @return component position
	 */
	private int findWindowPosition(String window) {
		int loc = windowOrder.indexOf(window);
		if (loc != -1) {
			int i = 0;
			for (Component c : panel.getComponents()) {
				if (c instanceof ManagedWindow) {
					String name = c.getName();
					// Added windows always have a valid position (see below)
					if (loc < windowOrder.indexOf(name)) {
						return i;
					}
				}
				i++;
			}
		} else {
			// Ensure that all added windows have a valid position
			windowOrder.add(window);
			fireWindowOrderChanged();
		}

		return panel.getComponentCount();
	}

	/**
	 * Saves the window order as a window manager property. Called when the
	 * stored window order has changed.
	 */
	private void fireWindowOrderChanged() {
		StringBuilder builder = new StringBuilder();
		Iterator<String> it = windowOrder.iterator();
		while (it.hasNext()) {
			builder.append(it.next());
			if (it.hasNext()) {
				builder.append(';');
			}
		}

		WtWindowManager.getInstance().setProperty(WINDOW_ORDER_PROPERTY, builder.toString());
	}

	/**
	 * Check if the stored window order has changed, and call
	 * {@link #fireWindowOrderChanged} if needed.
	 *
	 * @param movedWindow name of the moved window
	 */
	private void checkWindowOrder(String movedWindow) {
		// Name of the component preceding the dragged window.
		String previous = null;
		for (Component c : panel.getComponents()) {
			/*
			 * Ignore invisible components. These can appear both in the order
			 * list and in the panel. Such as the spells window when spells are
			 * not available. Checking the order relative to these can result in
			 * incorrect saved window order. Any match is fake, as these
			 * components do not exist as far as the user is concerned.
			 */
			if (c.isVisible() && c instanceof ManagedWindow) {
				String name = ((ManagedWindow) c).getName();
				if (movedWindow.equals(name)) {
					int newIndex;
					if (previous == null) {
						// Moved to first position
						newIndex = 0;
					} else {
						// Move after the preceding component
						newIndex = windowOrder.indexOf(previous) + 1;
					}

					// Move to new location. Be careful about removing the old
					// to avoid breaking the order
					int oldIndex = windowOrder.indexOf(name);
					if (newIndex > oldIndex) {
						windowOrder.add(newIndex, name);
						windowOrder.remove(name);
						fireWindowOrderChanged();
					} else if (newIndex < oldIndex) {
						windowOrder.remove(name);
						windowOrder.add(newIndex, name);
						fireWindowOrderChanged();
					}
					// else old location, no need to change
					return;
				}
				previous = name;
			}
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
	 *
	 * @return inspect window
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
			if (tmp != component && tmp != panel.getPhantom()) {
				if ((draggedPosition < i) && (centerY > componentYCenter(tmp))) {
					draggedPosition = i;
					panel.setComponentZOrder(panel.getPhantom(), draggedPosition);
					panel.revalidate();
					break;
				} else if ((draggedPosition >= i) && (centerY < componentYCenter(tmp))) {
					draggedPosition = i;
					panel.setComponentZOrder(panel.getPhantom(), draggedPosition);
					panel.revalidate();
					break;
				}
			}
		}
	}

	@Override
	public void startDrag(Component component) {
		draggedPosition = panel.getComponentZOrder(component);
		panel.hideComponent(component);
		panel.setComponentZOrder(component, 0);
	}

	@Override
	public void endDrag(Component component) {
		panel.setComponentZOrder(component, draggedPosition);
		panel.revealComponent();
		panel.revalidate();
		if (component instanceof ManagedWindow) {
			checkWindowOrder(((ManagedWindow) component).getName());
		}
	}

	/**
	 * A container that can hide a contained component from the layout manager,
	 * or anything else that uses {@link #getComponents} to access the
	 * subcomponents, and present a {@link PhantomComponent} in its place.
	 */
	private static class PhantomLayoutPanel extends JPanel {
		/**
		 * Currently hidden component, or <code>null</code> if nothing is
		 * hidden.
		 */
		Component hidden;
		/**
		 * The phantom component, or <code>null</code> if nothing is hidden.
		 */
		PhantomComponent phantom;

		/**
		 * Hide a specific component in {@link #getComponents} and present a
		 * PhantomComponent in its place.
		 *
		 * @param component component to hide
		 */
		void hideComponent(Component component) {
			hidden = component;
			phantom = new PhantomComponent(hidden);
			add(phantom, getComponentZOrder(hidden));
		}

		/**
		 * Restore the visibility of the previously hidden component.
		 */
		void revealComponent() {
			remove(phantom);
			hidden = null;
			phantom = null;
		}

		/**
		 * Get the current phantom component.
		 *
		 * @return current phantom or <code>null</code> if nothing is hidden
		 */
		Component getPhantom() {
			return phantom;
		}

		@Override
		public Component[] getComponents() {
			Component[] components = super.getComponents();
			if (phantom == null) {
				return components;
			}

			// Very inefficient, but this is not performance critical code
			List<Component> list = new ArrayList<Component>(Arrays.asList(components));
			list.remove(hidden);

			return list.toArray(new Component[list.size()]);
		}
	}

	/**
	 * A component that does nothing except takes space, with the same minimum,
	 * maximum and preferred sizes as another component.
	 */
	private static class PhantomComponent extends JComponent {
		/** The mimicked component. */
		private final Component component;

		/**
		 * Create a PhantomComponent.
		 *
		 * @param component parent component, whose size constraints this
		 * 	component should mimic
		 */
		PhantomComponent(Component component) {
			this.component = component;
		}

		@Override
		public Dimension getPreferredSize() {
			return component.getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			return component.getMinimumSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return component.getMinimumSize();
		}
	}
}
