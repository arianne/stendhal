/*
 * @(#) games/stendhal/client/entity/NPC2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of an NPC.
 */
public class NPC2DView extends RPEntity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(NPC2DView.class);


	/**
	 * Create a 2D view of an NPC.
	 *
	 * @param	entity		The entity to render.
	 */
	public NPC2DView(final RPEntity entity) {
		super(entity);
	}


	//
	// RPEntity2DView
	//

	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @param	object		The object to get animations for.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	protected Sprite getAnimationSprite(final RPObject object) {
		SpriteStore store = SpriteStore.get();
		String clazz = object.get("class");


		try {
			if (object.has("outfit")) {
				return getOutfitSprite(store, object.getInt("outfit"));
			} else {
				// This NPC's outfit is read from a single file.
				return store.getSprite(translate("npc/" + clazz));
			}
		} catch (Exception e) {
			logger.error("Cannot build animations", e);
			return store.getSprite(translate(clazz));
		}
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
	@Override
	public void buildAnimations(Map<String, Sprite []> map, final RPObject object) {
		buildAnimations(map, object, 1.5, 2.0);
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
		return new Rectangle.Double(getX(), getY(), 1.5, 2.0);
	}
}
