/*
 * @(#) games/stendhal/client/entity/RPEntity2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of an RP entity.
 */
public abstract class RPEntity2DView extends AnimatedStateEntity2DView {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(RPEntity2DView.class);

	private static Map<String, Sprite[]> bladeStrikeSprites;

	private static Sprite	eatingSprite;

	private static Sprite	poisonedSprite;

	private static Sprite	hitSprite;

	private static Sprite	blockedSprite;

	private static Sprite	missedSprite;

	/**
	 * The RP entity this view is for.
	 */
	private RPEntity	entity;

	/**
	 * Blade strike frame.
	 */
	private int		frameBladeStrike;


	static {
		SpriteStore st = SpriteStore.get();

		bladeStrikeSprites = new HashMap<String, Sprite[]>();
		bladeStrikeSprites.put("move_up", st.getAnimatedSprite("data/sprites/combat/blade_strike.png", 0, 3, 3, 4));
		bladeStrikeSprites.put("move_right", st.getAnimatedSprite("data/sprites/combat/blade_strike.png", 1, 3, 3, 4));
		bladeStrikeSprites.put("move_down", st.getAnimatedSprite("data/sprites/combat/blade_strike.png", 2, 3, 3, 4));
		bladeStrikeSprites.put("move_left", st.getAnimatedSprite("data/sprites/combat/blade_strike.png", 3, 3, 3, 4));

		hitSprite = st.getSprite("data/sprites/combat/hitted.png");
		blockedSprite = st.getSprite("data/sprites/combat/blocked.png");
		missedSprite = st.getSprite("data/sprites/combat/missed.png");
		eatingSprite = st.getSprite("data/sprites/ideas/eat.png");
		poisonedSprite = st.getSprite("data/sprites/ideas/poisoned.png");
	}


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public RPEntity2DView(final RPEntity entity) {
		super(entity);

		this.entity = entity;
	}


	//
	// RPEntity2DView
	//

	/**
	 * Populate named animations.
	 *
	 * @param	map		The map to populate.
	 * @param	object		The entity to load animations for.
	 * @param	width		The image width in tile units.
	 * @param	height		The image height in tile units.
	 */
	public void buildAnimations(Map<String, Sprite []> map, final RPObject object, double width, double height) {
		SpriteStore store = SpriteStore.get();


		Sprite tiles = getAnimationSprite(object);

		map.put("move_up",
			store.getAnimatedSprite(tiles, 0, 4, width, height));

		map.put("move_right",
			store.getAnimatedSprite(tiles, 1, 4, width, height));

		map.put("move_down",
			store.getAnimatedSprite(tiles, 2, 4, width, height));

		map.put("move_left",
			store.getAnimatedSprite(tiles, 3, 4, width, height));

		map.get("move_up")[3] = map.get("move_up")[1];
		map.get("move_right")[3] = map.get("move_right")[1];
		map.get("move_down")[3] = map.get("move_down")[1];
		map.get("move_left")[3] = map.get("move_left")[1];
	}


	/**
	 * Get the full directional animation tile set for this entity.
	 *
	 * @param	object		The object to get animations for.
	 *
	 * @return	A tile sprite containing all animation images.
	 */
	protected Sprite getAnimationSprite(final RPObject object) {
		return SpriteStore.get().getSprite(translate(object.get("type")));
	}


	/**
	 * Gets the outfit sprite for the given object.
	 * 
	 * The outfit is described by the object's "outfit" attribute.
	 * It is an 8-digit integer of the form RRHHDDBB where RR is the
	 * number of the hair graphics, HH for the head, DD for the dress,
	 * and BB for the base.
	 * 
	 * @param	store		The sprite store
	 * @param	object		The object. The outfit attribute needs
	 *				to be set.
	 *
	 * @return	A sprite for the object
	 */
	protected Sprite getOutfitSprite(final SpriteStore store, final RPObject object) {
		int outfit = object.getInt("outfit");

		Sprite sprite = store.getSprite("data/sprites/outfit/player_base_" + outfit % 100 + ".png");
		sprite = sprite.copy();
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
	 * This method gets the default image.
	 * <strong>All sub-classes MUST provide a <code>move_up</code>
	 * named animation, or override this method</strong>.
	 *
	 * @return	The default sprite, or <code>null</code>.
	 */
	@Override
	protected Sprite getDefaultSprite() {
		return getAnimation("move_up")[0];
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
	protected void drawImpl(final GameScreen screen) {
		if (entity.isBeingAttacked()) {
			// Draw red box around
			Graphics g2d = screen.expose();
			Rectangle2D rect = entity.getArea();

			g2d.setColor(Color.red);
			Point2D p = new Point.Double(rect.getX(), rect.getY());
			p = screen.invtranslate(p);
			g2d.drawRect((int) p.getX(), (int) p.getY(), (int) (rect.getWidth() * GameScreen.SIZE_UNIT_PIXELS),
				(int) (rect.getHeight() * GameScreen.SIZE_UNIT_PIXELS));
			g2d.setColor(Color.black);
			g2d.drawRect((int) p.getX() - 1, (int) p.getY() - 1,
				(int) (rect.getWidth() * GameScreen.SIZE_UNIT_PIXELS) + 2,
				(int) (rect.getHeight() * GameScreen.SIZE_UNIT_PIXELS) + 2);
		}

		if (entity.isAttackingUser()) {
			// Draw orange box around
			Graphics g2d = screen.expose();
			Rectangle2D rect = entity.getArea();

			Point2D p = new Point.Double(rect.getX(), rect.getY());
			p = screen.invtranslate(p);

			g2d.setColor(Color.orange);
			g2d.drawRect((int) p.getX() + 1, (int) p.getY() + 1,
				(int) (rect.getWidth() * GameScreen.SIZE_UNIT_PIXELS) - 2,
				(int) (rect.getHeight() * GameScreen.SIZE_UNIT_PIXELS) - 2);
		}

		if (entity.isAttacking() && entity.isBeingStruck()) {
			Rectangle2D rect = entity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();

			if (frameBladeStrike < 3) {
				screen.draw(bladeStrikeSprites.get(getState())[frameBladeStrike], sx - 1.5, sy - 3.3);
			} else {
				entity.doneStriking();
				frameBladeStrike = 0;
			}

			frameBladeStrike++;
		}

		super.drawImpl(screen);

		if (entity.isEating()) {
			Rectangle2D rect = entity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();
			screen.draw(eatingSprite, sx - 0.75, sy - 0.25);
		}

		if (entity.isPoisoned()) {
			Rectangle2D rect = entity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();
			screen.draw(poisonedSprite, sx - 1.25, sy - 0.25);
		}

		if (entity.isDefending()) {
			// Draw bottom right combat icon
			Rectangle2D rect = entity.getArea();
			double sx = rect.getMaxX();
			double sy = rect.getMaxY();

			switch (entity.getResolution()) {
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
	public int getZIndex() {
		return 8000;
	}
}
