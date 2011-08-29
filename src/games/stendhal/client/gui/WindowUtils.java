/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
	/** A key that should not get mixed with anything else */
	private static final String WINDOW_CLOSE = "org.stendhalgame:window_closing"; 
	
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
	public static void closeOnEscape(final JFrame frame) {
		closeOnEscape(frame, frame.getRootPane());
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
	        public void actionPerformed(ActionEvent event) {
	            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	        }
	    };
	    root.getActionMap().put(WINDOW_CLOSE, dispatchClosing);
	}
}
