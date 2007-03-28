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

import games.stendhal.client.GameObjects;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.wt.core.*;
import games.stendhal.common.CollisionDetection;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPObject;

/**
 * The panel where you can adjust your settings
 * 
 * @author mtotz
 */
public class SettingsPanel extends WtPanel implements WtClickListener, WtCloseListener {

	/** width of this panel */
	private static final int WIDTH = 200;

	private StendhalClient client;

	/** the Character panel */
	private Character character;

	/** the buddy list panel */
	private BuddyListDialog nbuddies;
	private ManagedWindow buddies;

	/** the minimap panel */
	private Minimap minimap;

	/** the inventory */
	private EntityContainer inventory;

	/** the player */
	private Player player;

	/** map of the buttons (for faster access) ) */
	private Map<String, WtButton> buttonMap;

	private static final boolean newCode =
			(System.getProperty("stendhal.newgui") != null);

	/** Creates a new instance of OptionsPanel */
	public SettingsPanel(StendhalUI ui, WtPanel frame) {
		super("settings", (frame.getWidth() - WIDTH) / 2, 0, WIDTH, 200);

		this.client = ui.getClient();

		setTitletext("Settings");

		setFrame(true);
		setTitleBar(true);
		setMinimizeable(true);
		setMinimized(true);
		setCloseable(false);

		character = new Character(ui);
		character.registerCloseListener(this);
		frame.addChild(character);

		if(newCode) {
			nbuddies = new BuddyListDialog(StendhalUI.get());
			((j2DClient) StendhalUI.get()).addDialog(nbuddies.getDialog());
			nbuddies.registerCloseListener(this);
			buddies = nbuddies;
		} else {
			Buddies obuddies = new Buddies(StendhalUI.get());
			frame.addChild(obuddies);
			obuddies.registerCloseListener(this);
			buddies = obuddies;
		}

		buddies.setVisible(true);

		inventory = new EntityContainer(client, "bag", 3, 4);
		inventory.registerCloseListener(this);
		frame.addChild(inventory);

		minimap = new Minimap(client);
		minimap.registerCloseListener(this);
		frame.addChild(minimap);

		buttonMap = new HashMap<String, WtButton>();

		WtButton button;

		button = new WtButton("minimap", 150, 30, "Enable Minimap");
		button.moveTo(10, 10);
		button.setPressed(minimap.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("minimap", button);

		button = new WtButton("character", 150, 30, "Enable Character");
		button.moveTo(10, 50);
		button.setPressed(character.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("character", button);

		button = new WtButton("bag", 150, 30, "Enable Inventory");
		button.moveTo(10, 90);
		button.setPressed(inventory.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("bag", button);

		button = new WtButton("buddies", 150, 30, "Enable Buddies");
		button.moveTo(10, 130);
		button.setPressed(buddies.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("buddies", button);
	}

	/** we're using the window manager */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/** updates the minimap */
	public void updateMinimap(CollisionDetection cd, GraphicsConfiguration gc, String zone) {
		minimap.update(cd, gc, zone);
	}

	/** updates the minimap */
	public void setPlayer(RPObject playerObject) {
		if (playerObject == null) {
			return;
		}

		if(newCode) {
			/*
			 * Hack! Need to update list when changes arrival
			 */
			if(nbuddies.isVisible())
				nbuddies.update();
		}

		GameObjects gameObjects = client.getGameObjects();
		Player newPlayer = (Player) gameObjects.get(playerObject.getID());

		// check if the player object has changed. Note: this is an exact object
		// reference check
		if (newPlayer != player) {
			this.player = newPlayer;

			character.setPlayer(player);
			inventory.setSlot(player, "bag");
			minimap.setPlayer(player);
		}
	}

	/** a button was clicked */
	public void onClick(String name, Point point) {
		boolean state = buttonMap.get(name).isPressed();

		if (name.equals("minimap")) {
			// check minimap panel
			minimap.setVisible(state);
		} else if (name.equals("character")) {
			// check character panel
			character.setVisible(state);
		} else if (name.equals("bag")) {
			// check inventory panel
			inventory.setVisible(state);
		} else if (name.equals("buddies")) {
			// check buddy panel
			buddies.setVisible(state);
		}
	}

	/** a window is closed */
	public void onClose(String name) {
		/*
		 * Unset button
		 */
		buttonMap.get(name).setPressed(false);
	}
}
