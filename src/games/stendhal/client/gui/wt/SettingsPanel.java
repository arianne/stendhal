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
import games.stendhal.client.entity.Player;
// Native window code (not yet)
//import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.core.*;
import games.stendhal.common.CollisionDetection;
import java.awt.Graphics;
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
public class SettingsPanel extends WtPanel implements WtClickListener,WtCloseListener {
	/** width of this panel */
	private static final int WIDTH = 200;

	/** buffered gameObjects for character panel */
	private GameObjects gameObjects;

	/** the Character panel */
	private Character character;

	/** the buddy list panel */
// Native window code (not yet)
//	private BuddyListDialog buddies;
	private Buddies buddies;

	/** the minimap panel */
	private Minimap minimap;

	/** the inventory */
	private EntityContainer inventory;

	/** the frame */
	private WtPanel frame;

	/** the player */
	private Player player;

	/** map of the buttons (for faster access) ) */
	private Map<String, WtButton> buttonMap;

	/** Creates a new instance of OptionsPanel */
	public SettingsPanel(WtPanel frame, GameObjects gameObjects) {
		super("settings", (frame.getWidth() - WIDTH) / 2, 0, WIDTH, 200);

		this.gameObjects = gameObjects;
		this.frame = frame;

		setTitletext("Settings");

		setFrame(true);
		setTitleBar(true);
		setMinimizeable(true);
		setMinimized(true);
		setCloseable(false);

		character = new Character(gameObjects);
		character.registerCloseListener(this);
		frame.addChild(character);

// Native window code (not yet)
//		buddies = new BuddyListDialog(j2DClient.getInstance());
		buddies = new Buddies(gameObjects);
		buddies.registerCloseListener(this);
		frame.addChild(buddies);

		inventory = new EntityContainer(gameObjects, "bag", 3, 4);
		inventory.registerCloseListener(this);
		frame.addChild(inventory);

		minimap = new Minimap();
		minimap.registerCloseListener(this);
		frame.addChild(minimap);

		buttonMap = new HashMap<String, WtButton>();

		WtButton button;

		button = new WtButton("minimap", 150, 30,"Enable Minimap");
		button.moveTo(10, 10);
		button.setPressed(minimap.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("minimap", button);

		button = new WtButton("character", 150, 30,"Enable Character");
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
	protected boolean useWindowManager() {
		return true;
	}

	/** updates the minimap */
	public void updateMinimap(CollisionDetection cd, GraphicsConfiguration gc,String zone) {
		minimap.update(cd, gc, zone);
	}

	/** updates the minimap */
	public void setPlayer(RPObject playerObject) {
		if (playerObject == null) {
			return;
		}

// Native window code (not yet)
//		/*
//		 * Hack! Need to update list when changes arrival
//		 */
//		if(buddies.isVisible())
//			buddies.update();

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
