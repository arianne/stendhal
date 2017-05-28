/***************************************************************************
 *                (C) Copyright 2003-2012 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.player.Player;

/**
 * prevents logging in inside the specified area
 *
 * @author hendrik
 */
public class NoLoginAreaBehaviour implements LoginListener, AreaBehaviour {
	private AreaEntity areaEntity;
	private String message;
	private int newX;
	private int newY;

	/**
	 * Create a nologin area.
	 *
	 * @param newX
	 *            x position to place the player at
	 * @param newY
	 *            y position to place the player at
	 * @param message
	 *            The message to send to the user when repositioned.
	 */
	public NoLoginAreaBehaviour(final int newX, final int newY, final String message) {
		this.newX = newX;
		this.newY = newY;
		this.message = message;
	}

	@Override
	public void addToWorld(AreaEntity parentAreaEntity) {
		this.areaEntity = parentAreaEntity;
		SingletonRepository.getLoginNotifier().addListener(this);
	}

	@Override
	public void removeFromWorld() {
		SingletonRepository.getLoginNotifier().removeListener(this);
	}

	@Override
	public void onLoggedIn(final Player player) {
		if (player.getZone().equals(areaEntity.getZone())) {
			if (areaEntity.getArea().contains(player.getX(), player.getY())) {
				player.setPosition(newX, newY);

				if (message != null) {
					player.sendPrivateText(message);
				}
			}
		}
	}

	/**
	 * sets the target position to which players are moved
	 *
	 * @param x x-coordinate
	 * @param y y-coordiante
	 */
	public void setTargetPosition(int x, int y) {
		this.newX = x;
		this.newY = y;
	}

}
