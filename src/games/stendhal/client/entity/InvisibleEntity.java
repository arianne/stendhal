/**
 * @(#) src/games/stendhal/client/entity/InvisibleEntity.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;
import games.stendhal.client.GameScreen;

/**
 * An generic entity that is not drawn.
 */
public class InvisibleEntity extends Entity {

	/**
	 * Create an invisible 1x1 entity.
	 *
	 * @param	object		Raw object to build from.
	 */
	public InvisibleEntity(RPObject object) {
		super(object);
	}

	//
	//
	//

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
	}

	@Override
	public int getZIndex() {
		return 3000;
	}

	@Override
	protected void loadSprite(RPObject object) {
	}
}
