/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.client.*;
import games.stendhal.client.entity.*;
import games.stendhal.client.gui.wt.*;
import games.stendhal.client.gui.wt.core.*;
import games.stendhal.common.CollisionDetection;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class InGameGUI implements Inspector {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(InGameGUI.class);

	private StendhalClient client;

	/** the main frame */
	private WtBaseframe frame;

	/** this is the ground */
	private WtPanel ground;

	/** settings panel */
	private SettingsPanel settings;

	/** the dialog "really quit?" */
	private WtPanel quitDialog;

	private Sprite offlineIcon;

	private boolean offline;

	private int blinkOffline;

	private boolean ctrlDown;

	private boolean shiftDown;

	private boolean altDown;

	public InGameGUI(StendhalClient client) {
		this.client = client;

		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");

		GameScreen screen = GameScreen.get();
		// create the frame
		frame = new WtBaseframe(screen);
		// register native event handler
		screen.getComponent().addMouseListener(frame);
		screen.getComponent().addMouseMotionListener(frame);
		// create ground
		ground = new GroundContainer(client, screen.getWidthInPixels(), screen.getHeightInPixels());

		frame.addChild(ground);
		// the settings panel creates all other
		settings = new SettingsPanel(client, ground);
		ground.addChild(settings);

		// set some default window positions
		WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);
	}

	public void offline() {
		offline = true;
	}

	public void online() {
		offline = false;
	}


	/**
	 * Workaround until more refactoring is done.
	 * Called from j2DClient's key listener.
	 */
	public void updateModifiers(KeyEvent ev) {
		altDown = ev.isAltDown();
		ctrlDown = ev.isControlDown();
		shiftDown = ev.isShiftDown();
	}


	/**
	 * Stops all player actions and shows a dialog in which the player can
	 * confirm that he really wants to quit the program. If so, requests a
	 * logout via the StendhalClient class.
	 */
	public void showQuitDialog() {
		/*
		 * Stop the player
		 */
		client.stop();

		// quit messagebox already showing?
		if (quitDialog == null) {
			// no, so show it
			quitDialog = new WtMessageBox("quit", 220, 220, 200, "Quit Stendhal?",
			        WtMessageBox.ButtonCombination.YES_NO);
			quitDialog.registerClickListener(new WtClickListener() {

				public void onClick(String name, Point point) {
					quitDialog = null; // remove field as the messagebox is
					// closed now
					if (name.equals(WtMessageBox.ButtonEnum.YES.getName())) {
						// Yes-Button clicked...logut and quit.
						client.requestLogout();
					}
				};
			});
			frame.addChild(quitDialog);
		}
	}


	/*
	 * Draw the screen.
	 *
	 * @param screen
	 */
	public void draw(GameScreen screen) {
		/*
		 * Draw the GameLayers from bootom to top, relies on exact
		 * naming of the layers
		 */
		StaticGameLayers gameLayers = client.getStaticGameLayers();
		String set = gameLayers.getRPZoneLayerSet();

		GameObjects gameObjects = client.getGameObjects();

		gameLayers.draw(screen, set + "_0_floor");
		gameLayers.draw(screen, set + "_1_terrain");
		gameLayers.draw(screen, set + "_2_object");
		gameObjects.draw(screen);
		gameLayers.draw(screen, set + "_3_roof");
		gameLayers.draw(screen, set + "_4_roof_add");
		gameObjects.drawHPbar(screen);
		gameObjects.drawText(screen);


		// create the map if there is none yet
		if (gameLayers.changedArea()) {
			CollisionDetection cd = gameLayers.getCollisionDetection();
			if (cd != null) {
				gameLayers.resetChangedArea();
				settings.updateMinimap(cd, screen.expose().getDeviceConfiguration(), gameLayers.getArea());
			}
		}

		RPObject player = client.getPlayer();
		settings.setPlayer(player);

		frame.draw(screen.expose());

		if (offline && (blinkOffline > 0)) {
			offlineIcon.draw(screen.expose(), 560, 420);
		}

		if (blinkOffline < -10) {
			blinkOffline = 20;
		} else {
			blinkOffline--;
		}
	}

	/**
	 * @return Returns the altDown.
	 */
	public boolean isAltDown() {
		return altDown;
	}

	/**
	 * @return Returns the ctrlDown.
	 */
	public boolean isCtrlDown() {
		return ctrlDown;
	}

	/**
	 * @return Returns the shiftDown.
	 */
	public boolean isShiftDown() {
		return shiftDown;
	}

	/**
	 * @return Returns the window toolkit baseframe.
	 */
	public WtBaseframe getFrame() {
		return frame;
	}

	public EntityContainer inspectMe(Entity suspect, RPSlot content, EntityContainer container) {
		if (container == null || !container.isVisible()) {
			{
				if (suspect instanceof Chest) {
					container = new EntityContainer(client, suspect.getType(), 4, 5);
				} else {
					container = new EntityContainer(client, suspect.getType(), 2, 2);
				}
				ground.addChild(container);
			}

			container.setSlot(suspect, content.getName());
			container.setVisible(true);
		}
		return container;

	}
}
