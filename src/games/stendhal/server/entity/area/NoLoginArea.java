/*
 * @(#) src/games/stendhal/server/entity/area/LifeDrainArea.java
 *
 *$Id$
 */

package games.stendhal.server.entity.area;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * An area prevents login and moves the player somewhere else.
 *
 */
public class NoLoginArea extends AreaEntity implements LoginListener {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = Log4J.getLogger(NoLoginArea.class);
	private int newX;
	private int newY;


	/**
	 * Create a nologin area.
	 *
	 * @param   width       Width of  this area
	 * @param   height      Height of this area
	 * @param   newX        x position to place the player at
	 * @param   newY        y position to place the player at
	 */
	public NoLoginArea(int width, int height, int newX, int newY) {
		super(width, height);
		this.newX = newX;
		this.newY = newY;
		super.put("server-only", 1);
		LoginNotifier.get().addListener(this);
	}


	public void onLoggedIn(Player player) {
		if (player.getZone().equals(this.getZone())) {
			if (this.getArea().contains(player.getX(), player.getY())) {
				logger.warn("Login in NoLoginArea, moving player to new location");
				player.setPosition(newX, newY);
			}
		}
	}

}
