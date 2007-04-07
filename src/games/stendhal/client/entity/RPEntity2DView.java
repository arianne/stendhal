/*
 * @(#) games/stendhal/client/entity/RPEntity2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

//
//

/**
 * The 2D view of an RP entity.
 */
public class RPEntity2DView extends AnimatedEntity2DView {
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
	// Entity2DView
	//

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


	//
	// <EntityView>
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	@Override
	public void draw(final GameScreen screen) {
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
				screen.draw(bladeStrikeSprites.get(entity.getAnimation())[frameBladeStrike], sx - 1.5, sy - 3.3);
			} else {
				entity.doneStriking();
				frameBladeStrike = 0;
			}

			frameBladeStrike++;
		}

		super.draw(screen);

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
				case HITTED:
					screen.draw(hitSprite, sx - 0.25, sy - 0.25);
					break;
			}
		}
	}
}
