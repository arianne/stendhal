/*
 * @(#) games/stendhal/client/entity/Creature2DView.java
 *
 * $Id$
 */

package games.stendhal.client.entity;

//
//

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import marauroa.common.game.RPObject;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.common.Debug;

/**
 * The 2D view of a creature.
 */
public class Creature2DView extends RPEntity2DView {
	/**
	 * The entity this view is for.
	 */
	private Creature	creature;

	/*
	 * The drawn height.
	 */
	private double		height;

	/*
	 * The drawn width.
	 */
	private double		width;



	/**
	 * Create a 2D view of a creature.
	 *
	 * @param	creature	The entity to render.
	 * @param	width		The drawn width in tile units.
	 * @param	height		The drawn height in tile units.
	 */
	public Creature2DView(final Creature creature, double width, double height) {
		super(creature);

		this.creature = creature;
		this.width = width;
		this.height = height;
	}


	//
	// Creature2DView
	//

	protected void drawPath(final GameScreen screen, final List<Creature.Node> path, final int delta) {
		Graphics g2d = screen.expose();
		Point2D p1 = screen.invtranslate(new Point.Double(getX(), getY()));

		for (Creature.Node node : path) {
			Point2D p2 = screen.invtranslate(new Point.Double(node.nodeX, node.nodeY));

			g2d.drawLine((int) p1.getX() + delta, (int) p1.getY() + delta, (int) p2.getX() + delta, (int) p2.getY()
			        + delta);
			p1 = p2;
		}
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
		String name;

		name = object.get("class");

		if (object.has("subclass")) {
			name += "/" + object.get("subclass");
		}

		return SpriteStore.get().getSprite(translate(name));
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
		return new Rectangle.Double(getX(), getY(), width, height);
	}


	protected static String translate(final String type) {
		return "data/sprites/monsters/" + type + ".png";
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
		List<Creature.Node>	path;


		super.draw(screen);

		if (Debug.CREATURES_DEBUG_CLIENT && !creature.isPathHidden()) {
			Graphics g2d = screen.expose();

			if ((path = creature.getTargetMovedPath()) != null) {
				int delta = GameScreen.SIZE_UNIT_PIXELS / 2;
				g2d.setColor(Color.red);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2);
			}

			if ((path = creature.getPatrolPath()) != null) {
				g2d.setColor(Color.green);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2 + 1);
			}

			if ((path = creature.getMoveToTargetPath()) != null) {
				g2d.setColor(Color.blue);
				drawPath(screen, path, GameScreen.SIZE_UNIT_PIXELS / 2 + 2);
			}
		}
	}
}
