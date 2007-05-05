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
import games.stendhal.client.SpriteStore;
import games.stendhal.client.stendhal;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

/**
 * The 2D view of an entity.
 */
public abstract class Entity2DView { // implements EntityView {
	/**
	 * The entity this view is for
	 */
	private Entity	entity;

	/**
	 * The last entity change serial number.
	 */
	private int	changeSerial;

	/**
	 * The entity image (or current one at least).
	 */
	protected Sprite sprite;

	/**
	 * The entity drawing composite.
	 */
	protected Composite	entityComposite;

	/**
	 * Whether this view is contained.
	 */
	protected boolean	contained;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	entity		The entity to render.
	 */
	public Entity2DView(final Entity entity) {
		this.entity = entity;

		changeSerial = entity.getChangeSerial() - 1;
		entityComposite = AlphaComposite.SrcOver;
		contained = false;
	}


	//
	// Entity2DView
	//

	/**
	 * Rebuild the representation using the base entity.
	 */
	public void buildRepresentation() {
		buildRepresentation(entity.getRPObject());
	}


	/**
	 * Build the visual representation of this entity.
	 *
	 * @param	object		An entity object.
	 */
	protected void buildRepresentation(final RPObject object) {
		sprite = SpriteStore.get().getSprite(translate(getEntity().getType()));
	}


	/**
	 * Draw the base entity part.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	protected void drawEntity(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		getSprite().draw(g2d, x, y);
	}


	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	protected void draw(final GameScreen screen, Graphics2D g2d, int x, int y, int width, int height) {
		Composite oldComposite;


		oldComposite = g2d.getComposite();
		g2d.setComposite(entityComposite);
		drawEntity(screen, g2d, x, y, width, height);
		g2d.setComposite(oldComposite);

		if (stendhal.SHOW_COLLISION_DETECTION) {
			g2d.setColor(Color.blue);
			g2d.drawRect(x, y, width, height);

			g2d.setColor(Color.green);
			g2d.draw(screen.convertWorldToScreen(entity.getArea()));
		}
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	public abstract Rectangle2D getDrawnArea();


	/**
	 * Get the sprite image for this entity.
	 *
	 * @return	The image representation.
	 */
	public Sprite getSprite() {
		return sprite;
	}


	/**
	 * Get the entity's X coordinate.
	 *
	 * @return	The X coordinate.
	 */
	protected double getX() {
		return entity.getX();
	}


	/**
	 * Get the entity's Y coordinate.
	 *
	 * @return	The Y coordinate.
	 */
	protected double getY() {
		return entity.getY();
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


	/**
	 * Determine if this view is contained, and should render in a
	 * compressed (it's defined) area without clipping anything important.
	 *
	 * @return	<code>true</code> if contained.
	 */
	public boolean isContained() {
		return contained;
	}


	/**
	 * Set whether this view is contained, and should render in a
	 * compressed (it's defined) area without clipping anything important.
	 *
	 * @param	contained	<code>true</code> if contained.
	 */
	public void setContained(boolean contained) {
		this.contained = contained;
	}


	/**
	 * Get the resource path for a sprite type.
	 *
	 *
	 *
	 */
	protected static String translate(final String type) {
		return "data/sprites/" + type + ".png";
	}


	//
	// <EntityView>
	//

	/**
	 * Draw the entity.
	 *
	 * @param	screen		The screen to drawn on.
	 */
	public void draw(final GameScreen screen) {
		int	serial;


		/*
		 * Check for entity changes
		 */
		serial = entity.getChangeSerial();

		if(serial != changeSerial) {
			update();
			changeSerial = serial;
		}


		Rectangle r = screen.convertWorldToScreen(getDrawnArea());

		if(screen.isInScreen(r)) {
			draw(screen, screen.expose(), r.x, r.y, r.width, r.height);
		}
	}


	/**
	 * Get the entity this view represents.
	 *
	 * @return	The represented entity.
	 */
	protected Entity getEntity() {
		return entity;
	}


	/**
	 * Update representation.
	 */
	protected void update() {
		entityComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, entity.getVisibility() / 100.0f);
	}
}
