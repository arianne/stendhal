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
/*
 * SettingsPanel.java
 *
 * Created on 26. Oktober 2005, 20:12
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.CloseListener;

import java.awt.Component;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The panel where you can adjust your settings.
 * 
 * @author mtotz
 */
@SuppressWarnings("serial")
public final class SettingsPanel extends ClientPanel implements ChangeListener, CloseListener
{
	/**
	 * The button height.
	 */
	private static final int BUTTON_HEIGHT = 25;

	/**
	 * The button width.
	 */
	private static final int BUTTON_WIDTH = 150;

	/**
	 * The button spacing.
	 */
	private static final int SPACING = 5;

	/** width of this panel. */
	private static final int WIDTH = BUTTON_WIDTH + SPACING + SPACING;

	/** map of the buttons (for faster access) ). */
	private Map<String, Entry> entries;

	/** Creates a new instance of OptionsPanel. */
	public SettingsPanel(final int frameWidth) {
		super("Settings", WIDTH, SPACING * 2);

		setLocation(frameWidth - WIDTH, 0);

		// don't allow closing this window
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		setLayout(null);

		entries = new HashMap<String, Entry>();
	}

	/**
	 * Add a window entry.
	 * 
	 * @param window
	 *            The window.
	 * @param label
	 *            The menu label.
	 */
	public void addEntry(ClientPanel panel, String label) {
		panel.registerCloseListener(this);

		String mnemonic = panel.getName();

		int y = SPACING + (entries.size() * (BUTTON_HEIGHT + SPACING));

		JToggleButton button = new JToggleButton(label);
		button.setName(mnemonic);
		button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

		button.setLocation(SPACING, y);
		button.setSelected(panel.isVisible());
		button.addChangeListener(this);

		setClientSize(SPACING + BUTTON_WIDTH + SPACING, y + BUTTON_HEIGHT + SPACING);

		add(button);

		entries.put(mnemonic, new Entry(button, panel));
	}

	/** a button was clicked. */
	public void stateChanged(ChangeEvent e) {
		/*
		 * Set window visibility
		 */
		Entry entry = entries.get(((Component)e.getSource()).getName());

		if (entry != null) {
			entry.setVisible(entry.isPressed());
		}

		// re-activate the settings panel after opening other windows
		try {
	        setSelected(true);
        } catch(PropertyVetoException e1) {
        }

        // restore keyboard focus to the chat window
		restoreTargetFocus();
	}

	/** a window is closed. */
	public void onClose(String name) {
		/*
		 * Unset button
		 */
		Entry entry = entries.get(name);

		if (entry != null) {
			entry.setPressed(false);
		}
	}


	/**
	 * A menu entry.
	 */
	protected static class Entry {
		protected JToggleButton button;
		protected Component window;

		public Entry(JToggleButton button, Component window) {
			this.button = button;
			this.window = window;
		}

		public boolean isPressed() {
			return button.isSelected();
		}

		public void setPressed(boolean pressed) {
			button.setSelected(pressed);
		}

		public void setVisible(boolean visible) {
			window.setVisible(visible);
		}
	}

}
