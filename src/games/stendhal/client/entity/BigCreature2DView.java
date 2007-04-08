/*
 * @(#) games/stendhal/client/entity/BigCreature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a big creature.
 */
public class BigCreature2DView extends Creature2DView {
	/**
	 * Create a 2D view of a big creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public BigCreature2DView(final BigCreature creature) {
		super(creature);
	}


	//
	// Entity2DView
	//

	@Override
	protected void buildAnimations(final RPObject object) {
		SpriteStore store = SpriteStore.get();
		Sprite creature = getAnimationSprite(object);

		animations.put("move_up", store.getAnimatedSprite(creature, 0, 4, 2, 2));
		animations.put("move_right", store.getAnimatedSprite(creature, 1, 4, 2, 2));
		animations.put("move_down", store.getAnimatedSprite(creature, 2, 4, 2, 2));
		animations.put("move_left", store.getAnimatedSprite(creature, 3, 4, 2, 2));

		animations.get("move_up")[3] = animations.get("move_up")[1];
		animations.get("move_right")[3] = animations.get("move_right")[1];
		animations.get("move_down")[3] = animations.get("move_down")[1];
		animations.get("move_left")[3] = animations.get("move_left")[1];
	}


	@Override
	protected Sprite getDefaultSprite() {
		return getAnimation("move_up")[0];
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 2.0, 2.0);
	}
}
