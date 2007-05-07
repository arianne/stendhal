/*
 * @(#) games/stendhal/client/entity/RPEntity2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.AnimatedSprite;
import games.stendhal.client.GameScreen;
import games.stendhal.client.ImageSprite;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;


/**
 * The 2D view of an RP entity.
 */
public abstract class RPEntity2DView extends ActiveEntity2DView {

	private static Map<Object, Sprite[]> bladeStrikeSprites;

	private static Sprite	eatingSprite;

	private static Sprite	poisonedSprite;

	private static Sprite	hitSprite;

	private static Sprite	blockedSprite;

	private static Sprite	missedSprite;

	/**
	 * The RP entity this view is for.
	 */
	protected RPEntity	rpentity;

	/**
	 * Blade strike frame.
	 */
	private int		frameBladeStrike;


	static {
		SpriteStore st = SpriteStore.get();

		bladeStrikeSprites = new HashMap<Object, Sprite[]>();
		bladeStrikeSprites.put(ActiveEntity.STATE_UP, st.getSprites("data/sprites/combat/blade_strike.png", 0, 3, 3, 4));
		bladeStrikeSprites.put(ActiveEntity.STATE_RIGHT, st.getSprites("data/sprites/combat/blade_strike.png", 1, 3, 3, 4));
		bladeStrikeSprites.put(ActiveEntity.STATE_DOWN, st.getSprites("data/sprites/combat/blade_strike.png", 2, 3, 3, 4));
		bladeStrikeSprites.put(ActiveEntity.STATE_LEFT, st.getSprites("data/sprites/combat/blade_strike.png", 3, 3, 3, 4));

		hitSprite = st.getSprite("data/sprites/combat/hitted.png");
		blockedSprite = st.getSprite("data/sprites/combat/blocked.png");
		missedSprite = st.getSprite("data/sprites/combat/missed.png");
		eatingSprite = st.getSprite("data/sprites/ideas/eat.png");
		poisonedSprite = st.getSprite("data/sprites/ideas/poisoned.png");
	}


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	rpentity	The entity to render.
	 */
	public RPEntity2DView(final RPEntity rpentity) {
		super(rpentity);

		this.rpentity = rpentity;
	}


	//
	// RPEntity2DView
	//

	/**
	 * Populate named state sprites.
	 *
	 * @param	map		The map to populate.
	 * @param	width		The image width in tile units.
	 * @param	height		The image height in tile units.
	 */
	protected void buildSprites(Map<Object, AnimatedSprite> map, double width, double height) {
		Sprite tiles = getAnimationSprite();

		map.put(ActiveEntity.STATE_UP, getAnimatedWalk(tiles, 0, width, height));
		map.put(ActiveEntity.STATE_RIGHT, getAnimatedWalk(tiles, 1, width, height));
		map.put(ActiveEntity.STATE_DOWN, getAnimatedWalk(tiles, 2, width, height));
		map.put(ActiveEntity.STATE_LEFT, getAnimatedWalk(tiles, 3, width, height));
	}


	/**
	 * Extract a walking animation for a specific row. The source sprite
	 * contains 3 animation tiles, but this is converted to 4 frames.
	 *
	 *
	 *
	 * @return	An animated sprite.
	 */
	protected AnimatedSprite getAnimatedWalk(Sprite tiles, int row, double width, double height) {
		Sprite [] frames = SpriteStore.get().getSprites(tiles, row, 4, width, height);

		frames[3] = frames[1];

		return new AnimatedSprite(frames, 100L, false);
	}


	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	protected Sprite getAnimationSprite() {
		return SpriteStore.get().getSprite(translate(entity.getType()));
	}


	/**
	 * Gets the outfit sprite.
	 * 
	 * The outfit is described by an "outfit code".
	 * It is an 8-digit integer of the form RRHHDDBB where RR is the
	 * number of the hair graphics, HH for the head, DD for the dress,
	 * and BB for the base.
	 * 
	 * @param	store		The sprite store
	 * @param	outfit		The outfit code.
	 *
	 * @return	A sprite for the object
	 */
	protected Sprite getOutfitSprite(final SpriteStore store, int outfit) {
		Sprite base = store.getSprite("data/sprites/outfit/player_base_" + outfit % 100 + ".png");
		ImageSprite sprite = new ImageSprite(base);
		outfit /= 100;
		if (outfit % 100 != 0) {
			int dressIdx = outfit % 100;
			Sprite dress = store.getSprite("data/sprites/outfit/dress_" + dressIdx + ".png");
			dress.draw(sprite.getGraphics(), 0, 0);
		}
		outfit /= 100;

		Sprite head = store.getSprite("data/sprites/outfit/head_" + outfit % 100 + ".png");
		head.draw(sprite.getGraphics(), 0, 0);
		outfit /= 100;

		if (outfit % 100 != 0) {
			Sprite hair = store.getSprite("data/sprites/outfit/hair_" + outfit % 100 + ".png");
			hair.draw(sprite.getGraphics(), 0, 0);
		}

		return sprite;
	}


	//
	// AnimatedEntity2DView
	//

	/**
	 * Get the default state name.
	 * <strong>All sub-classes MUST provide a
	 * <code><strong>ActiveEntity.STATE_UP</strong></code> named sprite,
	 * or override this method</strong>.
	 */
	@Override
	protected Object getDefaultState() {
		return ActiveEntity.STATE_UP;
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
		Rectangle srect = screen.convertWorldToScreen(entity.getArea());

		if (rpentity.isBeingAttacked()) {
			// Draw red box around

			g2d.setColor(Color.red);
			g2d.drawRect(srect.x, srect.y, srect.width, srect.height);

			g2d.setColor(Color.black);
			g2d.drawRect(srect.x - 1, srect.y - 1, srect.width + 2, srect.height + 2);
		}

		if (rpentity.isAttackingUser()) {
			// Draw orange box around
			g2d.setColor(Color.orange);
			g2d.drawRect(srect.x + 1, srect.y + 1, srect.width - 2, srect.height - 2);
		}

		if (rpentity.isAttacking() && rpentity.isBeingStruck()) {
			Rectangle2D rect = rpentity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();

			if (frameBladeStrike < 3) {
				screen.draw(bladeStrikeSprites.get(getState())[frameBladeStrike], sx - 1.5, sy - 3.3);
			} else {
				rpentity.doneStriking();
				frameBladeStrike = 0;
			}

			frameBladeStrike++;
		}

		super.draw(screen, g2d, x, y, width, height);

		if (rpentity.isEating()) {
			Rectangle2D rect = rpentity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();
			screen.draw(eatingSprite, sx - 0.75, sy - 0.25);
		}

		if (rpentity.isPoisoned()) {
			Rectangle2D rect = rpentity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();
			screen.draw(poisonedSprite, sx - 1.25, sy - 0.25);
		}

		if (rpentity.isDefending()) {
			// Draw bottom right combat icon
			Rectangle2D rect = rpentity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();

			switch (rpentity.getResolution()) {
				case BLOCKED:
					screen.draw(blockedSprite, sx - 0.25, sy - 0.25);
					break;
				case MISSED:
					screen.draw(missedSprite, sx - 0.25, sy - 0.25);
					break;
				case HIT:
					screen.draw(hitSprite, sx - 0.25, sy - 0.25);
					break;
			}
		}
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
	@Override
	public int getZIndex() {
		return 8000;
	}
}
