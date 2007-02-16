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

	/** buffered collision detection layer for minimap */
	private CollisionDetection cd;

	/** buffered GraphicsConfiguration for minimap */
	private GraphicsConfiguration gc;

	/** buffered zone name for minimap */
	private String zone;

	/** buffered gameObjects for character panel */
	private GameObjects gameObjects;

	/** the Character panel */
	private Character character;

	/** the buddy list panel */
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
		setTitletext("Settings");

		setFrame(true);
		setTitleBar(true);
		setMinimizeable(true);
		setMinimized(true);
		setCloseable(false);

		character = new Character(gameObjects);
		character.registerCloseListener(this);
		frame.addChild(character);

		buddies = new Buddies(gameObjects);
		buddies.registerCloseListener(this);
		frame.addChild(buddies);

		inventory = new EntityContainer(gameObjects, "bag", 3, 4);
		inventory.registerCloseListener(this);
		frame.addChild(inventory);

		buttonMap = new HashMap<String, WtButton>();
		buttonMap.put("minimap", new WtButton("minimap", 150, 30,"Enable Minimap"));
		buttonMap.put("character", new WtButton("character", 150, 30,"Enable Character"));
		buttonMap.put("bag", new WtButton("bag", 150, 30, "Enable Inventory"));
		buttonMap.put("buddies", new WtButton("buddies", 150, 30, "Enable Buddies"));

		int y = 10;

		for (WtButton button : buttonMap.values()) {
			button.moveTo(10, y);
			y += 40;
			button.setPressed(true);
			button.registerClickListener(this);
			addChild(button);
		}

		this.gameObjects = gameObjects;
		this.frame = frame;
	}

	/** we're using the window manager */
	protected boolean useWindowManager() {
		return true;
	}

	/** updates the minimap */
	public void updateMinimap(CollisionDetection cd, GraphicsConfiguration gc,String zone) {
		this.cd = cd;
		this.gc = gc;
		this.zone = zone;

		// close the old minimap if there is one
		if (minimap != null) {
			minimap.removeCloseListener(this);
			minimap.destroy();
			frame.removeChild(minimap);
			minimap = null;
		}

		if (buttonMap.get("minimap").isPressed()) {
			// add a new one
			minimap = new Minimap(cd, gc, zone);
			minimap.registerCloseListener(this);
			frame.addChild(minimap);
		}
	}

	/** updates the minimap */
	public void setPlayer(RPObject playerObject) {
		if (playerObject == null) {
			return;
		}

		Player newPlayer = (Player) gameObjects.get(playerObject.getID());
		// check if the player object has changed. Note: this is an exact object
		// reference check
		if (newPlayer == player) {
			return;
		}

		this.player = newPlayer;

		if (character != null) {
			character.setPlayer(player);
		}

		if (inventory != null) {
			inventory.setSlot(player, "bag");
		}

	}

	/** draw the panel */
	public Graphics draw(Graphics g) {
		if(isClosed())
			return g;

		if (minimap != null && player != null) {
			minimap.setPlayer(player);
		}

		return super.draw(g);
	}

	/** a button was clicked */
	public void onClick(String name, Point point) {
		WtButton button = buttonMap.get(name);
		boolean state = button.isPressed();

		// check minimap panel
//		if (name.equals("minimap")) {
//			if(minimap != null) {
//				minimap.setVisible(state);
//			}
//		}

		if (name.equals("minimap")) {
			// minimap disabled?
			if (!state) {
				if(minimap != null) {
					frame.removeChild(minimap);
					minimap.destroy();
					minimap = null;
				}
			} else {
				// minimap enabled
				minimap = new Minimap(cd, gc, zone);
				minimap.registerCloseListener(this);
				frame.addChild(minimap);
			}
		}

		// check character panel
		if (name.equals("character")) {
			character.setVisible(state);
		}

		// check inventory panel
		if (name.equals("bag")) {
			inventory.setVisible(state);
		}

		// check buddy panel
		if (name.equals("buddies")) {
			buddies.setVisible(state);
		}
	}

	/** a window is closed */
	public void onClose(String name) {
		if (name.equals("minimap")) {
			frame.removeChild(minimap);
			minimap.destroy();
			minimap = null;
		}

		/*
		 * Unset button
		 */
		buttonMap.get(name).setPressed(false);
	}
}
