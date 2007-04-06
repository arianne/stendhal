/*
 * @(#) games/stendhal/client/entity/Entity2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.stendhal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

//
//

/**
 * The 2D view of an entity.
 */
public class Entity2DView { // implements EntityView {
	/**
	 * The entity this view is for
	 */
	private Entity	entity;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public Entity2DView(final Entity entity) {
		this.entity = entity;
	}


	//
	// Entity2DView
	//

	/**
	 * Get the 2D drawn area.
	 *
	 * @return	The 2D area this draws in.
	 */
	public Rectangle2D getDrawnArea() {
		// XXX - Eventually abstract, but for transition
		return new Rectangle.Double(entity.getX(), entity.getY(), 1, 1);
        }


	/**
	 * Get the sprite image for this entity.
	 *
	 * @return	The image representation.
	 */
	protected Sprite getSprite() {
		// XXX - Eventually manage own sprite, but for now
		return entity.getSprite();
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
		// XXX - Eventually abstract, but for transition
		return 10000;
	}


	//
	// <EntityView>
	//

	public void draw(final GameScreen screen) {
		screen.draw(getSprite(), entity.getX(), entity.getY());

		if (stendhal.SHOW_COLLISION_DETECTION) {
			Graphics g2d = screen.expose();
			Rectangle2D rect = entity.getArea();
			g2d.setColor(Color.green);
			Point2D p = new Point.Double(rect.getX(), rect.getY());
			p = screen.invtranslate(p);
			g2d.drawRect((int) p.getX(), (int) p.getY(), (int) (rect.getWidth() * GameScreen.SIZE_UNIT_PIXELS),
			        (int) (rect.getHeight() * GameScreen.SIZE_UNIT_PIXELS));

			g2d = screen.expose();
			rect = getDrawnArea();
			g2d.setColor(Color.blue);
			p = new Point.Double(rect.getX(), rect.getY());
			p = screen.invtranslate(p);
			g2d.drawRect((int) p.getX(), (int) p.getY(), (int) (rect.getWidth() * GameScreen.SIZE_UNIT_PIXELS),
			        (int) (rect.getHeight() * GameScreen.SIZE_UNIT_PIXELS));
		}
	}
}
