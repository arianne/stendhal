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

import games.stendhal.client.AnimatedSprite;
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
	 * The current outfit.
	 */
	private Sprite	outfit;

	/**
	 * The current outfit code.
	 */
	private int	outfitCode;


	/**
	 * Create a 2D view of an NPC.
	 *
	 * @param	entity		The entity to render.
	 */
	public NPC2DView(final RPEntity entity) {
		super(entity);

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
			int code = ((RPEntity) getEntity()).getOutfit();

			if (code != RPEntity.OUTFIT_UNSET) {
				/*
				 * Don't rebuild the same outfit
				 */
				if(outfitCode != code) {
					outfitCode = code;
					outfit = getOutfitSprite(store, code);
				}

			} else {
				// This NPC's outfit is read from a single file.
				outfitCode = -1;
				outfit = store.getSprite(translate("npc/" + getEntity().getEntityClass()));
			}
		} catch (Exception e) {
			logger.error("Cannot build animations", e);
			outfitCode = -1;
			outfit = store.getSprite(translate(getEntity().getEntityClass()));
		}

		return outfit;
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load sprites for.
	 */
	@Override
	protected void buildSprites(Map<String, AnimatedSprite> map, RPObject object) {
		buildSprites(map, object, 1.5, 2.0);
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
