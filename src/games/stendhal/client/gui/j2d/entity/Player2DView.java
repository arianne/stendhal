/*
 * @(#) games/stendhal/client/gui/j2d/entity/Player2DView.java
 *
 * $Id$
 */

package games.stendhal.client.gui.j2d.entity;

//
//

import games.stendhal.client.OutfitStore;
import games.stendhal.client.entity.ActionType;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.User;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

import java.awt.Graphics2D;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPAction;

/**
 * The 2D view of a player.
 */
public class Player2DView extends RPEntity2DView {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(Player2DView.class);

	/**
	 * Sprite representing away.
	 */
	private static Sprite	awaySprite;

	/**
	 * The player entity.
	 */
	private Player player;


	static {
		awaySprite = SpriteStore.get().getAnimatedSprite("data/sprites/ideas/away.png", 0, 4, 1.0, 1.0, 2000L, true);
	}


	/**
	 * Create a 2D view of a player.
	 *
	 * @param	player		The entity to render.
	 */
	public Player2DView(final Player player) {
		super(player);

		this.player = player;
	}


	//
	// RPEntity2DView
	//

	/**
	 * Draw the entity status bar.
	 *
	 * @param	g2d		The graphics context.
	 * @param	x		The drawn X coordinate.
	 * @param	y		The drawn Y coordinate.
	 * @param	width		The drawn width.
	 */
	@Override
	protected void drawStatusBar(final Graphics2D g2d, final int x, final int y, final int width) {
		/*
		 * Shift bar slightly to avoid overlap with smaller entities
		 */
		super.drawStatusBar(g2d, x, y + 6, width);
	}


	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
		OutfitStore store = OutfitStore.get();

		try {
			return store.getOutfit(player.getOutfit());
		} catch (Exception e) {
			logger.warn("Cannot build outfit. Setting failsafe outfit.", e);
			return store.getFailsafeOutfit();
		}
	}


	/**
	 * Determine is the user can see this entity while in ghostmode.
	 *
	 * @return	<code>true</code> if the client user can see this
	 *		entity while in ghostmode.
	 */
	@Override
	protected boolean isVisibleGhost() {
		/*
		 * Admins see all
		 */
		if(User.isAdmin()) {
			return true;
		}

		return false;
	}


	//
	// Entity2DView
	//

	/**
	 * Build a list of entity specific actions.
	 * <strong>NOTE: The first entry should be the default.</strong>
	 *
	 * @param	list		The list to populate.
	 */
	@Override
	protected void buildActions(final List<String> list) {
		super.buildActions(list);

		// TODO: If practical, only add this if not already a buddy
		list.add(ActionType.ADD_BUDDY.getRepresentation());
	}


	/**
	 * Draw the entity.
	 *
	 * @param	g2d		The graphics to drawn on.
	 */
	@Override
	protected void draw(Graphics2D g2d, int x, int y, int width, int height) {
		super.draw(g2d, x, y, width, height);

		if(player.isAway()) {
			awaySprite.draw(g2d, x + (width * 3 / 4), y - 10);
		}
	}


	//
	// EntityView
	//

	/**
	 * Perform an action.
	 *
	 * @param	at		The action.
	 */
	@Override
	public void onAction(final ActionType at) {
		RPAction rpaction;


		switch (at) {
			case ADD_BUDDY:
				rpaction = new RPAction();

				rpaction.put("type", at.toString());
				rpaction.put("target", player.getName());

				at.send(rpaction);
				break;

			default:
				super.onAction(at);
				break;
		}
	}
}
