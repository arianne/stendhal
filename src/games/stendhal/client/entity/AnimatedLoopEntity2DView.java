/*
 * @(#) games/stendhal/client/entity/AnimatedLoopEntity2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of an entity that always loops images.
 */
public class AnimatedLoopEntity2DView extends AnimatedEntity2DView {
	/**
	 * The current frame.
	 */
	protected int	frame;

	/**
	 * The number of frames.
	 */
	protected int	frames;


	/**
	 * Create a 2D view of a animated loop visual.
	 *
	 * @param	entity		The entity to render.
	 */
	public AnimatedLoopEntity2DView(final AnimatedEntity entity, int frames) {
		super(entity);

		this.frames = frames;
		frame = 0;
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Populate named animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 */
	public void buildAnimations(Map<String, Sprite []> map, RPObject object) {
		String resource = translate(object.get("type"));
		SpriteStore store = SpriteStore.get();

// XXX - Switch to later (once getSptite() is used)
//		map.put("default",
//			store.getAnimatedSprite(resource, 0, frames, 1.0, 1.0));

		for (int i = 0; i < frames; i++) {
			map.put(Integer.toString(i),
				store.getAnimatedSprite(resource, i, 1, 1, 1));
		}
	}


	/**
	 * This method gets the default image.
	 * <strong>All sub-classes MUST provide a <code>0</code>
	 * named animation, or override this method</strong>.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	@Override
	protected Sprite getDefaultSprite() {
// XXX - Switch to later (once getSptite() is used)
//		return getAnimation("default")[0];
		return getAnimation("0")[0];
	}


	@Override
	protected String getState() {
// XXX - Switch to later (once getSptite() is used)
//		return "default";
		String state = Integer.toString(frame);

		if(++frame >= frames) {
			frame = 0;
		}

		return state;
	}


	//
	// Entity2DView
	//

	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
	}


	/**
	 * Determines on top of which other entities this entity should be
	 * drawn. Entities with a high Z index will be drawn on top of ones
	 * with a lower Z index.
	 * 
	 * Also, players can only interact with the topmost entity.
	 * 
	 * @return	The drawing index.
	 */
	public int getZIndex() {
		return 3000;
	}
}
