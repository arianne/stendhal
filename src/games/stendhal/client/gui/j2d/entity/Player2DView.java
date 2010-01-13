/*
 * @(#) games/stendhal/client/gui/j2d/entity/Player2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.IGameScreen;
import games.stendhal.client.OutfitStore;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * The 2D view of a player.
 */
class Player2DView extends RPEntity2DView {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Player2DView.class);

	/**
	 * Sprite representing away.
	 */
	private static Sprite awaySprite;
	
	/**
	 * Sprite representing grumpy.
	 */
	private static Sprite grumpySprite;

	
	/**
	 * Sprite representing recently killing of other player.
	 */
	private static Sprite skullSprite;

	static {
		final SpriteStore store = SpriteStore.get();
		final Sprite gotAwaySprite = store.getSprite("data/sprites/ideas/away.png");
		final Sprite gotGrumpySprite = store.getSprite("data/sprites/ideas/grumpy.png");
		final Sprite gotPkSprite = store.getSprite("data/sprites/ideas/pk.png");
		skullSprite = store.getAnimatedSprite(gotPkSprite , 0, 0, 12, 16, 24, 200);
		awaySprite = store.getAnimatedSprite(gotAwaySprite, 0, 0, 4,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
				2000);
		grumpySprite = store.getAnimatedSprite(gotGrumpySprite, 0, 0, 4,
				IGameScreen.SIZE_UNIT_PIXELS, IGameScreen.SIZE_UNIT_PIXELS,
				2000);
	}


	//
	// RPEntity2DView
	//

	/**
	 * Draw the entity status bar.
	 * 
	 * @param g2d
	 *            The graphics context.
	 * @param x
	 *            The drawn X coordinate.
	 * @param y
	 *            The drawn Y coordinate.
	 * @param width
	 *            The drawn width.
	 */
	@Override
	protected void drawStatusBar(final Graphics2D g2d, final int x,
			final int y, final int width) {
		/*
		 * Shift bar slightly to avoid overlap with smaller entities
		 */
		super.drawStatusBar(g2d, x, y + 6, width);
	}

	/**
	 * Get the full directional animation tile set for this entity.
	 * 
	 * @return A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		final OutfitStore store = OutfitStore.get();

		try {
			return store.getOutfit(((RPEntity) entity).getOutfit());
		} catch (final Exception e) {
			logger.warn("Cannot build outfit. Setting failsafe outfit.", e);
			return store.getFailsafeOutfit();
		}
	}

	/**
	 * Determine is the user can see this entity while in ghostmode.
	 * 
	 * @return <code>true</code> if the client user can see this entity while in
	 *         ghostmode.
	 */
	@Override
	protected boolean isVisibleGhost() {
		/*
		 * Admins see all
		 */
		if (User.isAdmin()) {
			return true;
		}

		return false;
	}

	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions. <strong>NOTE: The first entry
	 * should be the default.</strong>
	 * 
	 * @param list
	 *            The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		if (!((RPEntity) entity).isGhostMode()) {
			super.buildActions(list);

			list.add(ActionType.ADD_BUDDY.getRepresentation());
		}

	}

	/**
	 * Draw the entity.
	 * 
	 * @param g2d
	 *            The graphics to drawn on.
	 */
	@Override
	protected void draw(final Graphics2D g2d, final int x, final int y, final int width, final int height) {
		super.draw(g2d, x, y, width, height);

		if (((Player) entity).isAway()) {
			awaySprite.draw(g2d, x + (width * 3 / 4), y - 10);
		}
		if (((Player) entity).isGrumpy()) {
			grumpySprite.draw(g2d, x - (width * 1 / 6), y - 6);
		}
		if (((Player) entity).isBadBoy()) {
			skullSprite.draw(g2d, x , y);
		}
	
	}

	//
	// EntityView
	//

	/**
	 * Perform an action.
	 * 
	 * @param at
	 *            The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		switch (at) {
		case ADD_BUDDY:
			at.send(at.fillTargetInfo(entity.getRPObject()));
			break;

		default:
			super.onAction(at);
			break;
		}
	}
}
