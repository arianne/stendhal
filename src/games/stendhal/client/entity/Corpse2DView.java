/*
 * @(#) games/stendhal/client/entity/Corpse2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * The 2D view of a corpse.
 */
public class Corpse2DView extends Entity2DView {
	/**
	 * The RP entity this view is for.
	 */
	private Corpse		corpse;

	private double		height;

	private double		width;


	/**
	 * Create a 2D view of an entity.
	 *
	 * @param	corpse		The entity to render.
	 */
	public Corpse2DView(final Corpse corpse) {
		super(corpse);

		this.corpse = corpse;

		height = 1.0;
		width = 1.0;
	}


	//
	// Corpse2DView
	//

	public double getHeight() {
		return height;
	}


	public double getWidth() {
		return width;
	}


	//
	// Entity2DView
	//

	/**
	 * Build the visual representation of this entity.
	 *
	 * @param	object		An entity object.
	 */
	protected void buildRepresentation(final RPObject object) {
		String clazz = object.get("class");
		String corpseType = object.get("type");

		if (clazz != null) {
			if (clazz.equals("player")) {
				corpseType = corpseType + "_player";
			} else if (clazz.equals("giant_animal")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("huge_animal")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("mythical_animal")) {
				corpseType = corpseType + "_giantrat";
			}
		}

		sprite = SpriteStore.get().getSprite(translate(corpseType));

		width = (double) sprite.getWidth() / GameScreen.SIZE_UNIT_PIXELS;
		height = (double) sprite.getHeight() / GameScreen.SIZE_UNIT_PIXELS;
	}


	/**
	 * Get the 2D area that is drawn in.
	 *
	 * @return	The 2D area this draws in.
	 */
	public Rectangle2D getDrawnArea() {
		return new Rectangle.Double(getX(), getY(), getWidth(), getHeight());
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
		return 5500;
	}
}
