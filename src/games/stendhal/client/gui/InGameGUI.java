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
import games.stendhal.common.Direction;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.SwingUtilities;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class InGameGUI implements KeyListener, Inspector {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(InGameGUI.class);

	private StendhalClient client;

	private GameObjects gameObjects;

	private GameScreen screen;

	/** a nicer way of handling the keyboard */
	private Map<Integer, Object> pressed;

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

	private long lastKeyRelease;

	private int[] veryFastKeyEvents = new int[4]; // at leat one more than

	// checked

	private long lastKeyEventsCleanUpStart;

	public InGameGUI(StendhalClient client) {

		client.setGameGUI(this);
		this.client = client;

		gameObjects = client.getGameObjects();
		screen = GameScreen.get();

		pressed = new HashMap<Integer, Object>();

		offlineIcon = SpriteStore.get().getSprite("data/gui/offline.png");

		buildGUI();
	}

	public void offline() {
		offline = true;
	}

	public void online() {
		offline = false;
	}

	private void buildGUI() {
		// create the frame
		frame = new WtBaseframe(screen);
		// register native event handler
		screen.getComponent().addMouseListener(frame);
		screen.getComponent().addMouseMotionListener(frame);
		// create ground
		ground = new GroundContainer(screen, gameObjects, this);
		frame.addChild(ground);
		// the settings panel creates all other
		settings = new SettingsPanel(ground, gameObjects);
		ground.addChild(settings);

		// set some default window positions
		WtWindowManager windowManager = WtWindowManager.getInstance();
		windowManager.setDefaultProperties("corpse", false, 0, 190);
		windowManager.setDefaultProperties("chest", false, 100, 190);
	}

	protected Direction keyCodeToDirection(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_LEFT:
				return Direction.LEFT;

			case KeyEvent.VK_RIGHT:
				return Direction.RIGHT;

			case KeyEvent.VK_UP:
				return Direction.UP;

			case KeyEvent.VK_DOWN:
				return Direction.DOWN;

			default:
				return null;
		}
	}

	public void onKeyPressed(KeyEvent e) {
		if (e.isShiftDown()) {
			/*
			 * We are going to use shift to move to previous/next line of text
			 * with arrows so we just ignore the keys if shift is pressed.
			 */
			return;
		}

		switch (e.getKeyCode()) {
			case KeyEvent.VK_L:
				if(e.isControlDown()) {
					/*
					 * Ctrl+L
					 * Make game log visible
					 */
					SwingUtilities.getRoot(client.getGameLog()).setVisible(true);
				}

				break;

			case KeyEvent.VK_R:
				if(e.isControlDown()) {
					/*
					 * Ctrl+R
					 * Remove text bubbles
					 */
					client.clearTextBubbles();
				}

				break;

			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				/*
				 * Ctrl means face, otherwise move
				 */
				client.addDirection(
					keyCodeToDirection(e.getKeyCode()),
					e.isControlDown());

				break;
		}
	}

	public void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				/*
				 * Ctrl means face, otherwise move
				 */
				client.removeDirection(
					keyCodeToDirection(e.getKeyCode()),
					e.isControlDown());
		}
	}

	public void keyPressed(KeyEvent e) {
		// detect X11 auto repeat still beeing active
		if ((lastKeyRelease > 0) && (lastKeyRelease + 1 >= e.getWhen())) {
			veryFastKeyEvents[veryFastKeyEvents.length - 1]++;
			if ((veryFastKeyEvents[0] > 2) && (veryFastKeyEvents[1] > 2) && (veryFastKeyEvents[2] > 2)) {
				StendhalClient.get().addEventLine("Detecting serious bug in keyboard handling.", Color.RED);
				StendhalClient
				        .get()
				        .addEventLine(
				                "Try executing xset -r in a terminal windows. Please write a bug report at http://sourceforge.net/tracker/?group_id=1111&atid=101111 including the name and version of your operating system and distribution",
				                Color.BLACK);
			}
		}
		altDown = e.isAltDown();
		ctrlDown = e.isControlDown();
		shiftDown = e.isShiftDown();

		if (!pressed.containsKey(Integer.valueOf(e.getKeyCode()))) {
			onKeyPressed(e);
			pressed.put(Integer.valueOf(e.getKeyCode()), null);
		}
	}

	public void keyReleased(KeyEvent e) {
		lastKeyRelease = e.getWhen();
		altDown = e.isAltDown();
		ctrlDown = e.isControlDown();
		shiftDown = e.isShiftDown();

		onKeyReleased(e);
		pressed.remove(Integer.valueOf(e.getKeyCode()));
	}

	/**
	 * Rotates the veryFastKeyEvents array
	 */
	private void rotateKeyEventCounters() {
		if (lastKeyEventsCleanUpStart + 300 < System.currentTimeMillis()) {
			lastKeyEventsCleanUpStart = System.currentTimeMillis();

			for (int i = veryFastKeyEvents.length - 1; i > 0; i--) {
				veryFastKeyEvents[i - 1] = veryFastKeyEvents[i];
			}
			veryFastKeyEvents[veryFastKeyEvents.length - 1] = 0;
		}
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

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 27) {
			// escape typed
			showQuitDialog();
		}
	}

	/**
	 * This methods inspects an entity by enabling all the droppable areas. To
	 * stop inspecting this method is called with entity=null
	 */
	public EntityContainer inspect(Entity entity, RPSlot slot) {
		return inspect(entity, slot, 2, 2);
	}

	public EntityContainer inspect(Entity entity, RPSlot slot, int width, int height) {
		if ((entity == null) || (slot == null) || (ground == null)) {
			return null;
		}

		EntityContainer container = new EntityContainer(gameObjects, entity.getType(), width, height);
		container.setSlot(entity, slot.getName());
		if (!container.hasParent()) {
			ground.addChild(container);
		}
		container.setVisible(true);

		return container;
	}

	public void draw(GameScreen screen) {
		// create the map if there is none yet
		StaticGameLayers gl = client.getStaticGameLayers();
		if (gl.changedArea()) {
			CollisionDetection cd = gl.getCollisionDetection();
			if (cd != null) {
				gl.resetChangedArea();
				settings.updateMinimap(cd, screen.expose().getDeviceConfiguration(), gl.getArea());
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

		rotateKeyEventCounters();
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
					container = new EntityContainer(gameObjects, suspect.getType(), 4, 5);
				} else {
					container = new EntityContainer(gameObjects, suspect.getType(), 2, 2);
				}
				ground.addChild(container);
			}

			container.setSlot(suspect, content.getName());
			container.setVisible(true);
		}
		return container;

	}
}
