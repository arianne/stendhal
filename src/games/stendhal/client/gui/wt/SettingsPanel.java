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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.wt.core.WtButton;
import games.stendhal.client.gui.wt.core.WtClickListener;
import games.stendhal.client.gui.wt.core.WtCloseListener;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.common.constants.SoundLayer;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * The panel where you can adjust your settings.
 * 
 * @author mtotz
 */
public class SettingsPanel extends WtPanel implements WtClickListener,
		WtCloseListener {
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
	private final Map<String, Entry> entries;

	/**
	 * Creates a new instance of OptionsPanel.
	 * 
	 * @param frameWidth
	 * @param gameScreen
	 */
	public SettingsPanel(final int frameWidth, final IGameScreen gameScreen) {
		super("settings", (frameWidth - WIDTH) / 2, 0, WIDTH, SPACING * 2,
				gameScreen);

		setTitletext("Settings");

		setFrame(true);
		setTitleBar(true);
		setMinimizeable(true);
		setMinimized(true);
		setCloseable(false);

		entries = new HashMap<String, Entry>();
	}

	/**
	 * Add a window entry.
	 * 
	 * @param window
	 *            The window.
	 * @param label
	 *            The menu label.
	 * @param gameScreen
	 */
	public void add(final ManagedWindow window, final String label,
			final IGameScreen gameScreen) {
		window.registerCloseListener(this);

		final String mnemonic = window.getName();

		final int y = SPACING + (entries.size() * (BUTTON_HEIGHT + SPACING));

		final WtButton button = new WtButton(mnemonic, BUTTON_WIDTH, BUTTON_HEIGHT,
				label, gameScreen);

		button.moveTo(SPACING, y);
		button.setPressed(window.isVisible());
		button.registerClickListener(this);

		resizeToFitClientArea(SPACING + BUTTON_WIDTH + SPACING, y
				+ BUTTON_HEIGHT + SPACING);

		addChild(button);
		entries.put(mnemonic, new Entry(button, window));
	}

	/** we're using the window manager. */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/**
	 * a button was clicked.
	 * 
	 * @param name
	 * @param point
	 */
	public void onClick(final String name, final Point point, final IGameScreen gameScreen) {
		/*
		 * Set window visibility
		 */
		final Entry entry = entries.get(name);

		if (entry != null) {
			entry.setVisible(entry.isPressed());
		}
	}

	/**
	 * a window is closed.
	 * 
	 * @param name
	 */
	public void onClose(final String name) {
		/*
		 * Unset button
		 */
		final Entry entry = entries.get(name);

		if (entry != null) {
			entry.setPressed(false);
		}
	}

	//
	//

	/**
	 * A menu entry.
	 */
	private static class Entry {
		private WtButton button;
		private ManagedWindow window;

		private Entry(final WtButton button, final ManagedWindow window) {
			this.button = button;
			this.window = window;
		}

		public boolean isPressed() {
			return button.isPressed();
		}

		public void setPressed(final boolean pressed) {
			button.setPressed(pressed);
		}

		public void setVisible(final boolean visible) {
			window.setVisible(visible);
		}
	}

	@Override
	protected void playOpenSound() {
		ClientSingletonRepository.getSound().getGroup(SoundLayer.USER_INTERFACE.groupName).play("click-4", 0, null, null, false, true);
	}
}
