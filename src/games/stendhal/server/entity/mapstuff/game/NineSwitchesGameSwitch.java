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

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.useable.UseableEntity;

/**
 * A switch in the 9 switches game
 *
 * @author hendrik
 */
public class NineSwitchesGameSwitch extends UseableEntity{
	private NineSwitchesGameBoard board;

	/**
	 * creates a new Switch for the 9 switches game.
	 *
	 * @param board
	 */
	public NineSwitchesGameSwitch(NineSwitchesGameBoard board) {
		this.board = board;
		put("class", "switch");
		put("name", "arrow_switch");
		super.setMenu("Toggle|Use");
	}

	/**
	 * someone clicked a switch.
	 */
	@Override
	public boolean onUsed(RPEntity user) {
		board.usedSwitch(user, this);
		return true;
	}

	/**
	 * toggles the state of the switch.
	 */
	public void toggle() {
		setState((getState() + 1) % 2);
	}
}
