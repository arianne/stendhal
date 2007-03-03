package games.stendhal.client.entity;

import games.stendhal.client.GameScreen;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

/**
 * An entity which one cannot walk over.
 */
public class WalkBlocker extends Entity {

	/**
	 * creates a new WalkBlocker
	 *
	 * @param object rpobject
	 */
	public WalkBlocker( RPObject object) {
		super( object);
	}
	
	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	@Override
	public void draw(GameScreen screen) {
		// portals are invisible; use a Door to get a changing sprite
	}

	@Override
	public int getZIndex() {
		return 3000;
	}

}
