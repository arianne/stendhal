/*
 * @(#) games/stendhal/client/entity/NPC2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.OutfitStore;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

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
