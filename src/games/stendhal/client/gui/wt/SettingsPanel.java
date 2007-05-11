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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.client.gui.wt.core.*;
import games.stendhal.common.CollisionDetection;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

/**
 * The panel where you can adjust your settings
 *
 * @author mtotz
 */
public class SettingsPanel extends WtPanel implements WtClickListener, WtCloseListener {

	/** width of this panel */
	private static final int WIDTH = 165;

	/** height of this panel */
	private static final int HEIGHT = 235;

	private StendhalClient client;

	/** the Character panel */
	private Character character;

	/** the Key ring panel */
	private KeyRing keyring;

	/** the buddy list panel */
	private BuddyListDialog nbuddies;
	private ManagedWindow buddies;

	public BuyWindow buywindow;

	private GameButtonHelper gbh;

	/** the minimap panel */
	private Minimap minimap;

	/** the inventory */
	private EntityContainer inventory;

        /** the spells list */
        private EntityContainer spells;

	/** the player */
	private User player;

	/**spells button*/
	private WtButton spellsButton;

	/** map of the buttons (for faster access) ) */
	private Map<String, WtButton> buttonMap;

	private static final boolean newCode =
			(System.getProperty("stendhal.newgui") != null);

	/** Creates a new instance of OptionsPanel */
	public SettingsPanel(StendhalUI ui, WtPanel frame) {
		super("settings", (frame.getWidth() - WIDTH) / 2, 0, WIDTH, HEIGHT);

		this.client = ui.getClient();

		setTitletext("Settings");

		setFrame(true);
		setTitleBar(true);
		setMinimizeable(true);
		setMinimized(true);
		setCloseable(false);

		buttonMap = new HashMap<String, WtButton>();

		character = new Character(ui);
		character.registerCloseListener(this);
		ui.addWindow(character);

		keyring = new KeyRing(client);
		keyring.registerCloseListener(this);
		ui.addWindow(keyring);

		if(newCode) {
			nbuddies = new BuddyListDialog(StendhalUI.get());
			buddies = nbuddies;
		} else {
			Buddies obuddies = new Buddies(StendhalUI.get());
			buddies = obuddies;
		}

		buddies.registerCloseListener(this);
		ui.addWindow(buddies);

		buywindow = new BuyWindow(StendhalUI.get());
		buywindow.registerCloseListener(this);
		buywindow.setVisible(false);
		ui.addWindow(buywindow);

		gbh = new GameButtonHelper(this, StendhalUI.get());
		gbh.registerCloseListener(this);
		gbh.setVisible(false);
		ui.addWindow(gbh);

		inventory = new EntityContainer(client, "bag", 3, 4);
		inventory.registerCloseListener(this);
		ui.addWindow(inventory);

		spells = new EntityContainer(client, "spells", 3, 4);
		spells.registerCloseListener(this);
		ui.addWindow(spells);

		minimap = new Minimap(client);
		minimap.registerCloseListener(this);
		ui.addWindow(minimap);

		WtButton button;

		button = new WtButton("minimap", 150, 25, "Enable Minimap");
		button.moveTo(5, 5);
		button.setPressed(minimap.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("minimap", button);

		button = new WtButton("character", 150, 25, "Enable Character");
		button.moveTo(5, 35);
		button.setPressed(character.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("character", button);

		button = new WtButton("bag", 150, 25, "Enable Inventory");
		button.moveTo(5, 65);
		button.setPressed(inventory.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("bag", button);

		button = new WtButton("keyring", 150, 25, "Enable Key Ring");
		button.moveTo(5, 95);
		button.setPressed(keyring.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("keyring", button);

		button = new WtButton("buddies", 150, 25, "Enable Buddies");
		button.moveTo(5, 125);
		button.setPressed(buddies.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("buddies", button);

		button = new WtButton("gametools", 150, 25, "Enable Game Tools");
		button.moveTo(5, 155);
		button.setPressed(gbh.isVisible());
		button.registerClickListener(this);
		addChild(button);
		buttonMap.put("gametools", button);

		spellsButton = new WtButton("spells", 150, 25, "Enable Spells Window");
		spellsButton.moveTo(5, 185);
		spellsButton.setPressed(spells.isVisible());
		spellsButton.registerClickListener(this);
		addChild(spellsButton);
		buttonMap.put("spells", spellsButton);
		spellsButton.setVisible(false);

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
	public void setPlayer(User user) {
		if(user == null) {
			return;
		}

		if(newCode) {
			/*
			 * Hack! Need to update list when changes arrive
			 */
			if(nbuddies.isVisible())
				nbuddies.update();
		}

		// check if the player object has changed.
		// Note: this is an exact object reference check
		if (user != player) {
			this.player = user;

			character.setPlayer(player);
			keyring.setSlot(player, "keyring");
			inventory.setSlot(player, "bag");
			minimap.setPlayer(player);

                        spells.setSlot(player, "spells");

			if (player.getSlot("spells") != null) {
				spellsButton.setVisible(true);
			}
		}

		/*
		 * Hack! Need to update when changes arrive
		 */
		if(keyring.isVisible())
			keyring.update();
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
		} else if (name.equals("keyring")) {
			// check keyring panel
			keyring.setVisible(state);
		} else if (name.equals("buddies")) {
			// check buddy panel
			buddies.setVisible(state);
		} else if (name.equals("gametools")) {
			// check gametools panel
			gbh.setVisible(state);
		} else if (name.equals("spells")) {
                        spells.setVisible(state);
                }
	}

	/** a window is closed */
	public void onClose(String name) {
		/*
		 * Unset button
		 */
		WtButton button = buttonMap.get(name);

		if(button != null) {
			button.setPressed(false);
		}
	}
}
