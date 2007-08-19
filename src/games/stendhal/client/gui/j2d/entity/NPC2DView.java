/*
 * @(#) games/stendhal/client/gui/j2d/entity/NPC2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.OutfitStore;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * The 2D view of an NPC.
 */
public class NPC2DView extends RPEntity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Log4J.getLogger(NPC2DView.class);

	/**
	 * Create a 2D view of an NPC.
	 *
	 * @param	npc		The entity to render.
	 */
	public NPC2DView(final NPC npc) {
		super(npc);
	}


	//
	// RPEntity2DView
	//

	/**
	 * Calculate sprite image offset.
	 *
	 * @param	swidth		The sprite width (in world units).
	 * @param	sheight		The sprite height (in world units).
	 * @param	ewidth		The entity width (in world units).
	 * @param	eheight		The entity height (in world units).
	 */
	@Override
	protected void calculateOffset(final double swidth, final double sheight, final double ewidth, final double eheight) {
		/*
		 * X alignment centered, Y alignment bottom
		 */
		xoffset = (int) ((ewidth - swidth) / 2.0 * GameScreen.SIZE_UNIT_PIXELS);

		// TODO: Fix (y+1 entity hack is causing interference)
		yoffset = 0;
	}


	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		SpriteStore store = SpriteStore.get();


		try {
			int code = rpentity.getOutfit();

			if (code != RPEntity.OUTFIT_UNSET) {
				return OutfitStore.get().getOutfit(code);
			} else {
				// This NPC's outfit is read from a single file.
				return store.getSprite(translate("npc/" + entity.getEntityClass()));
			}
		} catch (Exception e) {
			logger.error("Cannot build animations", e);
			return store.getSprite(translate(entity.getEntityClass()));
		}
	}


	//
	// EntityChangeListener
	//

	/**
	 * An entity was changed.
	 *
	 * @param	entity		The entity that was changed.
	 * @param	property	The property identifier.
	 */
	@Override
	public void entityChanged(final Entity entity, final Object property)
	{
		super.entityChanged(entity, property);

		if(property == Entity.PROP_CLASS) {
			representationChanged = true;
		}
	}
}
