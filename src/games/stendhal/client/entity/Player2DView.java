/*
 * @(#) games/stendhal/client/entity/Player2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;

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


	static {
		SpriteStore st = SpriteStore.get();

		awaySprite = new AnimatedSprite(st.getSprites("data/sprites/ideas/away.png", 0, 4, 1.0, 1.0), 2000L);
	}


	/**
	 * Create a 2D view of a player.
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
	 * @return	A tile sprite containing all animation images.
	 */
	@Override
	protected Sprite getAnimationSprite() {
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
			outfitCode = 0;
			outfit = getOutfitSprite(store, 0);
		}

		return outfit;
	}


	//
	// AnimatedStateEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 */
	@Override
	protected void buildSprites(Map<String, AnimatedSprite> map) {
		buildSprites(map, 1.5, 2.0);
	}


	//
	// Entity2DView
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	protected void draw(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		super.draw(screen, g2d, x, y, width, height);

		if(player.isAway()) {
			awaySprite.draw(g2d, x + (width * 3 / 4), y - 10);
		}
	}


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
	@Override
	protected void update() {
		buildRepresentation();

		super.update();
	}
}
