/***************************************************************************
 *                (C) Copyright 2003-2010 - Faiumoni e. V.                 *
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

import games.stendhal.server.entity.mapstuff.area.AreaEntity;

/**
 * a target marker
 *
 * @author hendrik
 */
public class TargetMarker extends AreaEntity {

	/**
	 * creates a new GameBoard
	 *
	 * @param width  width of the board
	 * @param height height of the board
	 */
	public TargetMarker(int width, int height) {
		super(width, height);
		init();
	}

	private void init() {
		setRPClass("game_board");
		put("type", "game_board");
		put("class", "target");
	}
}
