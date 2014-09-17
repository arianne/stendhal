/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
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

import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.SettingChangeListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.MathHelper;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * Utilities for system level windows.
 */
public class WindowUtils {
	/** A key that should not get mixed with anything else. */
	private static final String WINDOW_CLOSE = "org.stendhalgame:window_closing";
	/** Font size property name. */
	private static final String FONT_SIZE_PROPERTY = "ui.font_size";
	/** Default font point size. */
	private static final int DEFAULT_FONT_SIZE = 12;
	
	/**
	 * Utility class - no instantiation.
	 */
	private WindowUtils() {
	}
	// getRootPane() in JDialog and JFrame has no common ancestor
	
	/**
	 * Make the dialog close when the used presses escape. The event will be
	 * the same as when the user closes the window using the window manager.
	 * 
	 * @param dialog dialog to make obey the escape key
	 */
	public static void closeOnEscape(final JDialog dialog) {
		closeOnEscape(dialog, dialog.getRootPane());
	}
	
	/**
	 * Make the window close when the used presses escape. The event will be
	 * the same as when the user closes the window using the window manager.
	 * 
	 * @param frame window to make obey the escape key
	 */
	static void closeOnEscape(final JFrame frame) {
		closeOnEscape(frame, frame.getRootPane());
	}
	
	/**
	 * Track the windows location so that it can be restored at next client
	 * start.
	 * 
	 * @param window tracked window
	 * @param windowId identifier for the window. This should be unique for
	 * 	each window type. The restored location is looked up by the identifier.
	 */
	public static void trackLocation(final Window window, String windowId) {
		final ManagedWindow mw = new ManagedWindowDecorator(window, windowId);
		WtWindowManager manager = WtWindowManager.getInstance();
		// Avoid specifying any, if the type of the window data has not been
		// saved before.
		manager.setDefaultProperties(mw.getName(), false, mw.getX(), mw.getY());
		manager.formatWindow(mw);
		
		window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				WtWindowManager.getInstance().moveTo(mw, mw.getX(), mw.getY());
			}
		});
	}
		
	/**
	 * Make the window close when the used presses escape. The event will be
	 * the same as when the user closes the window using the window manager.
	 * 
	 * @param window window to make obey the escape key
	 * @param root the root container of the window
	 */
	private static void closeOnEscape(final Window window, final JRootPane root) {
		InputMap map = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), WINDOW_CLOSE);
		Action dispatchClosing = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
			}
		};
		root.getActionMap().put(WINDOW_CLOSE, dispatchClosing);
	}
	
	/**
	 * Register a component that should watch for default font size changes.
	 * Typically the component should be the top level window.
	 * 
	 * @param component root component for the tree whose font size shall be
	 * 	changed at default font size changes
	 */
	public static void watchFontSize(final Component component) {
		final SettingChangeListener listener = new SettingChangeAdapter(FONT_SIZE_PROPERTY,
				Integer.toString(DEFAULT_FONT_SIZE)) {
			@Override
			public void changed(String newValue) {
				int size = MathHelper.parseIntDefault(newValue, DEFAULT_FONT_SIZE);
				scaleComponentFonts(component, size);
				component.validate();
				component.setSize(component.getPreferredSize());
			}
		};
		
		WtWindowManager.getInstance().registerSettingChangeListener(FONT_SIZE_PROPERTY, listener);
		
		/*
		 * Dialogs typically get disposed when they are closed. Remove the
		 * listener so that the the dialog and its subcomponents can be
		 * reclaimed by the garbage collector.
		 */
		if (component instanceof Window) {
			((Window) component).addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					WtWindowManager.getInstance().deregisterSettingChangeListener(FONT_SIZE_PROPERTY, listener);
				}
			});
		}
	}
	
	/**
	 * Scale the font of a component and its subcomponents.
	 * 
	 * @param component root component
	 * @param size new font size 
	 */
	private static void scaleComponentFonts(Component component, float size) {
		Font f = component.getFont().deriveFont(size);
		component.setFont(f);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) {
				scaleComponentFonts(child, size);
			}
		}
	}
	
	/**
	 * A wrapper for system windows that lets the {@link WtWindowManager} store
	 * and restore their locations.
	 */
	private static class ManagedWindowDecorator implements ManagedWindow {
		/** The actual window. */
		private final Window window;
		/** Window identifier. */
		private final String name;
		
		/**
		 * Create a managed window decorator with an identity for a window. 
		 * 
		 * @param window window to decorate
		 * @param windowId identifier of the decorated window
		 */
		ManagedWindowDecorator(Window window, String windowId) {
			this.window = window;
			name = "system." + windowId;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getX() {
			return window.getX();
		}

		@Override
		public int getY() {
			return window.getY();
		}

		@Override
		public boolean isMinimized() {
			// Treat as always visible to avoid confusion
			return false;
		}

		@Override
		public boolean isVisible() {
			// Treat as always visible to avoid confusion
			return false;
		}

		@Override
		public boolean moveTo(int x, int y) {
			window.setLocation(x, y);
			return true;
		}

		@Override
		public void setMinimized(boolean minimized) {
			// ignore
		}

		@Override
		public void setVisible(boolean visible) {
			// ignore
		}
	}
}
