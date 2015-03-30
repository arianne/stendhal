/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.Testing;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.ArrayList;

/**
 * The game board for the 9 switches game.
 *
 * @author hendrik
 */
public class NineSwitchesGameBoard implements TurnListener {
	private StendhalRPZone zone;
	private int x;
	private int y;

	private SpeakerNPC npc;
	private ArrayList<NineSwitchesGameSwitch> switches;

	private String playerName;
	/** Possible balloon colors */
	private static final Integer[] balloonColors = {
		0xff0000, // Red
		0xffff00, // Yellow
		0x00ff00, // Green
		0x00ffff, // Cyan
		0x0000ff, // Blue
		0xff00ff // Magenta
	};

	/**
	 * creates a new NineSwitcheGameBoard.
	 *
	 * @param zone zone the board is placed into
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public NineSwitchesGameBoard(StendhalRPZone zone, int x, int y) {
		this.zone = zone;
		this.x = x;
		this.y = y;
		createSwitches();
	}

	/**
	 * use the specified switch.
	 *
	 * @param user player who used the switch
	 * @param gameSwitch switch which was used
	 */
	public void usedSwitch(RPEntity user, NineSwitchesGameSwitch gameSwitch) {
		if (playerName == null) {
			user.sendPrivateText(npc.getName() + ": " + user.getName() + ", please talk to me to start a game.");
			return;
		}

		if (!user.getName().equals(playerName)) {
			user.sendPrivateText(npc.getName() + ": Hey " + user.getName() + ", " + playerName + " is currently playing. Please wait a little.");
			return;
		}

		switchGameSwitch(gameSwitch);
		boolean completed = checkBoard();
		if (completed) {
			npc.say("Congratulations, " + user.getName() + " you won! Here take this balloon.");
			// TODO: Remove when outfit testing finished
			Outfit balloonOutfit;
			if (Testing.OUTFITS) {
				balloonOutfit = new Outfit(1, null, null, null, null, null,
						null);
			} else {
				balloonOutfit = new Outfit(1, null, null, null, null);
			}
			user.setOutfit(balloonOutfit);
			user.put("outfit_colors", "detail", Rand.rand(balloonColors));
			
			playerName = null;
			TurnNotifier.get().dontNotify(this);
		}
	}

	/**
	 * creates the switches
	 */
	private void createSwitches() {
		switches = new ArrayList<NineSwitchesGameSwitch>();
		for (int iRow = 0; iRow < 3; iRow++) {
			for (int iCol = 0; iCol < 3; iCol++) {
				NineSwitchesGameSwitch gameSwitch = new NineSwitchesGameSwitch(this);
				gameSwitch.setPosition(x + iCol, y + iRow);
				zone.add(gameSwitch);
				switches.add(gameSwitch);
			}
		}
		resetBoard();
	}


	/**
	 * toggles the switches linked to the used one.
	 *
	 * @param gameSwitch used switch
	 */
	private void switchGameSwitch(NineSwitchesGameSwitch gameSwitch) {
		int index = switches.indexOf(gameSwitch);
		int row = index / 3;
		int col = index % 3;

		if (row > 0) {
			switches.get((row - 1) * 3 + col).toggle();
		}
		if (col > 0) {
			switches.get(row * 3 + col - 1).toggle();
		}
		gameSwitch.toggle();
		if (row < 2) {
			switches.get((row + 1) * 3 + col).toggle();
		}
		if (col < 2) {
			switches.get(row * 3 + col + 1).toggle();
		}
	}

	/**
	 * check if all arrows point to the right.
	 *
	 * @return true if the game was completed, false otherwise
	 */
	private boolean checkBoard() {
		for (NineSwitchesGameSwitch gameSwitch : switches) {
			if (gameSwitch.getState() == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * resets all switches to point back to the left.
	 */
	private void resetBoard() {
		for (NineSwitchesGameSwitch gameSwitch : switches) {
			gameSwitch.setState(0);
		}
		switches.get(4).setState(1);
	}

	/**
	 * sets the name of the active player or <code>null</code> if no game is active.
	 *
	 * @param playerName name of player
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		resetBoard();
		if (playerName != null) {
			TurnNotifier.get().notifyInSeconds(60, this);
		}
	}

	/**
	 * gets the name of the active player.
	 *
	 * @return name of player, or <code>null</code> if no game is active.
	 */
	public String getPlayerName() {
		return this.playerName;
	}

	/**
	 * sets the SpeakerNPC who manages this game.
	 *
	 * @param npc SpeakerNPC
	 */
	public void setNPC(SpeakerNPC npc) {
		this.npc = npc;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		npc.say("Sorry " + playerName + ", your time is up.");
		setPlayerName(null);
		resetBoard();
	}

	/**
	 * removes the game board with all its switches from the world
	 */
	public void remove() {
		for (NineSwitchesGameSwitch gameSwitch : switches) {
			zone.remove(gameSwitch);
		}
	}
}
