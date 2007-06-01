/*
 * @(#) games/stendhal/client/entity/Corpse2DView.java
 *
 * $Id$
 */
package games.stendhal.client.entity;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.gui.wt.EntityContainer;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.Log4J;
import marauroa.common.Logger;

/**
 * The 2D view of a corpse.
 */
public class Corpse2DView extends Entity2DView {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Corpse2DView.class);

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
	 */
	@Override
	protected void buildRepresentation() {
		String clazz = corpse.getEntityClass();
		String corpseType = corpse.getType();

		if (clazz != null) {
			if (clazz.equals("player")) {
				corpseType = corpseType + "_player";
			} else if (clazz.equals("giant_animal")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("giant_human")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("huge_animal")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("mythical_animal")) {
				corpseType = corpseType + "_huge_animal";
			} else if (clazz.equals("boss")) {
				corpseType = corpseType + "_giantrat";
			} else if (clazz.equals("enormous_creature")) {
				corpseType = corpseType + "_enormous_creature";
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
	@Override
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
	@Override
	public int getZIndex() {
		return 5500;
	}
}
