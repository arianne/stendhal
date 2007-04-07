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


	/**
	 * Create a 2D view of a creature.
	 *
	 * @param	creature	The entity to render.
	 */
	public Creature2DView(final Creature creature) {
		super(creature);

		this.creature = creature;
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
	// Entity2DView
	//

	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	@Override
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), 1.0, 1.0);
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
