/*
 * @(#) games/stendhal/client/gui/j2d/entity/NPC2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.OutfitStore;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.NPC;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.util.List;
import marauroa.common.game.RPAction;
import org.apache.log4j.Logger;

/**
 * The 2D view of an NPC.
 */
class NPC2DView extends RPEntity2DView {
	/**
	 * Log4J.
	 */
	private static final Logger logger = Logger.getLogger(NPC2DView.class);

	/**
	 * Create a 2D view of an NPC.
	 * 
	 * @param npc
	 *            The entity to render.
	 */
	public NPC2DView(final NPC npc) {
		super(npc);
	}

	//
	// RPEntity2DView
	//

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		final SpriteStore store = SpriteStore.get();

		try {
			final int code = rpentity.getOutfit();

			if (code != RPEntity.OUTFIT_UNSET) {
				return OutfitStore.get().getOutfit(code);
			} else {
				// This NPC's outfit is read from a single file.
				return store.getSprite(translate("npc/"
						+ entity.getEntityClass()));
			}
		} catch (final Exception e) {
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
	 * @param entity
	 *            The entity that was changed.
	 * @param property
	 *            The property identifier.
	 */
	@Override
	public void entityChanged(final IEntity entity, final Object property) {
		super.entityChanged(entity, property);

		if (property == IEntity.PROP_CLASS) {
			representationChanged = true;
		}
	}

	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);
		// NPC can't be pushed
		list.remove(ActionType.PUSH.getRepresentation());
		if (User.isAdmin()) {
			list.add(ActionType.ADMIN_VIEW_NPC_TRANSITIONS.getRepresentation());
		}
	}

	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case ADMIN_VIEW_NPC_TRANSITIONS:
			final RPAction action = new RPAction();
			action.put("type", "script");
			action.put("target", "DumpTransitionsEx.class");
			action.put("args", this.getEntity().getTitle());
			at.send(action);
			break;
		default:
			super.onAction(at);
			break;
		}
	}
}
