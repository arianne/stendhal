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

/**
 * An generic entity that is not drawn.
 */
public class InvisibleEntity extends Entity {
	int height = 1;
	int width = 1;
	
	
	
	@Override
	public void onChangedAdded(RPObject base, RPObject diff) {
		super.onChangedAdded(base, diff);
		if (diff.has("width")) {
			width = diff.getInt("width");
		} else if (base.has("width")) {
			width = base.getInt("width");
		}
		if (diff.has("height")) {
			height = diff.getInt("height");
		} else if (base.has("height")) {
			height = base.getInt("height");
		}
	}


	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, width, height);
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	protected Entity2DView createView() {
		return new InvisibleEntity2DView(this);
	}
}
