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

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import games.stendhal.client.gui.wt.core.SettingChangeAdapter;
import games.stendhal.client.gui.wt.core.SettingChangeListener;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.MathHelper;

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

	/** Property name used to determine if window dimensions should be restored. */
	private static final String SAVE_DIMENSIONS_PROPERTY = "ui.dimensions";
	/** Prefix for per window size properties. */
	private static final String PROP_PREFIX = "ui.window.";

	/** Windows whose size is being tracked. */
	private static final Map<Window, ManagedWindowDecorator> trackedWindows = new HashMap<Window, ManagedWindowDecorator>();

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
	 * @param followSize track the size of the window too
	 */
	public static void trackLocation(Window window, String windowId,
			final boolean followSize) {
		final ManagedWindowDecorator mw = new ManagedWindowDecorator(window, windowId);
		final WtWindowManager manager = WtWindowManager.getInstance();
		// Avoid specifying any, if the type of the window data has not been
		// saved before.
		manager.setDefaultProperties(mw.getName(), false, mw.getX(), mw.getY());
		manager.formatWindow(mw);
		if (followSize) {
			trackedWindows.put(window, mw);
		}

		window.addComponentListener(new ComponentAdapter() {
			final String widthProperty = PROP_PREFIX + mw.getName() + ".width";
			final String heightProperty = PROP_PREFIX + mw.getName() + ".height";

			@Override
			public void componentMoved(ComponentEvent e) {
				WtWindowManager.getInstance().moveTo(mw, mw.getX(), mw.getY());
			}

			@Override
			public void componentResized(ComponentEvent event) {
				// Ignore tracking until the size has been restored, so that the
				// stored size does not get overwritten before it has a chance
				// of being used
				if (!followSize || !mw.getRestored()) {
					return;
				}
				WtWindowManager manager = WtWindowManager.getInstance();
				if (!mw.isMaximized()) {
					manager.setProperty(widthProperty, Integer.toString(mw.getWidth()));
					manager.setProperty(heightProperty, Integer.toString(mw.getHeight()));
				}
			}
		});

		// Some maximization changes are not for some reason detected by the
		// size changes. Check the corresponding low level window events.
		window.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				manager.setProperty(PROP_PREFIX + mw.getName() + ".maximized",
						Boolean.toString(mw.isMaximized()));
			}
		});
	}

	/**
	 * Restore the size of a tracked window.
	 *
	 * @param window window whose size should be restored
	 *
	 * @throws IllegalArgumentException in case restoring a window that is not
	 *	tracked is tried
	 */
	public static void restoreSize(Window window) {
		ManagedWindowDecorator dec = trackedWindows.get(window);
		if (dec == null) {
			throw new IllegalArgumentException("Trying to restore a window that is not being tracked");
		}
		dec.setRestored(true);
		WtWindowManager wm = WtWindowManager.getInstance();
		if (!"true".equals(wm.getProperty(SAVE_DIMENSIONS_PROPERTY, "true"))) {
			return;
		}

		String maximizedProp = PROP_PREFIX + dec.getName() + ".maximized";
		if ("true".equals(wm.getProperty(maximizedProp, "false"))) {
			if (window instanceof Frame) {
				((Frame) window).setExtendedState(Frame.MAXIMIZED_BOTH);
				return;
			} else {
				// Should not happen, but handle the situation gracefully
				wm.setProperty(maximizedProp, "false");
			}
		}
		int width = wm.getPropertyInt(PROP_PREFIX + dec.getName() + ".width", -1);
		int height = wm.getPropertyInt(PROP_PREFIX + dec.getName() + ".height", -1);
		if (width != -1 && height != -1) {
			window.setSize(width, height);
		}
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
		/** Size restored status of the window. */
		private boolean restored;

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

		/**
		 * Check if the window is maximized.
		 *
		 * @return <code>true</code> if the window is maximized, otherwise
		 * 	<code>false</code>
		 */
		boolean isMaximized() {
			if (window instanceof Frame) {
				return ((Frame) window).getExtendedState() == Frame.MAXIMIZED_BOTH;
			}
			return false;
		}

		/**
		 * Get the size restoration status of the window. The size has been
		 * restored if {@link WindowUtils#restoreSize} has been called with the
		 * window of this decorator as the parameter.
		 *
		 * @return <code>true</code> if the window size has been restored,
		 * otherwise <code>false</code>
		 */
		boolean getRestored() {
			return restored;
		}

		/**
		 * Set the size restoration status of this window.
		 *
		 * @param restored new status
		 */
		void setRestored(boolean restored) {
			this.restored = restored;
		}

		/**
		 * Get the window width.
		 *
		 * @return width
		 */
		int getWidth() {
			return window.getWidth();
		}

		/**
		 * Get the window height.
		 *
		 * @return height
		 */
		int getHeight() {
			return window.getHeight();
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
