/*
 * @(#) games/stendhal/client/entity/Player2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

/**
 * The 2D view of an player.
 */
public class Player2DView extends RPEntity2DView {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(Player2DView.class);

	/**
	 * The current outfit.
	 */
	private Sprite	outfit;

	/**
	 * The current outfit code.
	 */
	private int	outfitCode;

	/**
	 * The player entity.
	 */
	private Player	player;


	/**
	 * Create a 2D view of an player.
	 *
	 * @param	player		The entity to render.
	 */
	public Player2DView(final Player player) {
		super(player);

		this.player = player;

		outfit = null;
		outfitCode = -1;
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

		try {
			int code = player.getOutfit();

			/*
			 * Don't rebuild the same outfit
			 */
			if(outfitCode != code) {
				outfitCode = code;
				outfit = getOutfitSprite(store, code);
			}
		} catch (Exception e) {
			logger.error("Cannot build animations", e);
			object.put("outfit", 0);
			outfitCode = 0;
			outfit = getOutfitSprite(store, 0);
		}

		return outfit;
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
		return new Rectangle.Double(getX(), getY(), 1.0, 2.0);
	}


	//
	// <EntityView>
	//

	/**
	 * Update representation.
	 */
	protected void update() {
		buildRepresentation();

		super.update();
	}
}
