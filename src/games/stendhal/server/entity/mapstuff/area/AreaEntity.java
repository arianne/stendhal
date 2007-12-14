/*
 * @(#) src/games/stendhal/server/entity/area/AreaEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

//
//

import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * A base area entity.
 */
public abstract class AreaEntity extends Entity {
	/**
	 * The name attribute name.
	 */
	protected static final String ATTR_NAME = "name";

	// MAYBEDO (if Entity.RPCLASS added):
	// /**
	// * The RPClass.
	// */
	// public final static RPClass RPCLASS = createRPClass();

	/**
	 * Create an area entity.
	 * 
	 * @param width
	 *            Width of this area
	 * @param height
	 *            Height of this area
	 */
	public AreaEntity(int width, int height) {
		setRPClass("area");
		put("type", "area");

		setSize(width, height);
		setResistance(0);
	}

	//
	// AreaEntity
	//

	/**
	 * Define the RPClass.
	 * 
	 * @return The configured RPClass.
	 */
	private static RPClass createRPClass() {
		RPClass rpclass = new RPClass("area");

		// MAYBEDO: rpclass.isA(Entity.RPCLASS)
		rpclass.isA("entity");
		rpclass.addAttribute(ATTR_NAME, Type.STRING);

		return rpclass;
	}

	/**
	 * Generate the RPClass (compatible with manual init/order).
	 * 
	 * NOTE: This MUST be called during environment initialization.
	 */
	public static void generateRPClass() {
		createRPClass();
	}

	/**
	 * Get the entity name.
	 * 
	 * @return The entity's name, or <code>null</code> if undefined.
	 */
	public String getName() {
		if (has(ATTR_NAME)) {
			return get(ATTR_NAME);
		} else {
			return null;
		}
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *            The area name.
	 */
	public void setName(final String name) {
		put(ATTR_NAME, name);
	}

	//
	// Entity
	//

	/**
	 * Returns the name or something that can be used to identify the entity for
	 * the player
	 * 
	 * @param definite
	 *            <code>true</code> for "the", and <code>false</code> for
	 *            "a/an" in case the entity has no name.
	 * 
	 * @return The description name.
	 */
	@Override
	public String getDescriptionName(final boolean definite) {
		String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getDescriptionName(definite);
		}
	}

	/**
	 * Get the nicely formatted entity title/name.
	 * 
	 * @return The title, or <code>null</code> if unknown.
	 */
	@Override
	public String getTitle() {
		String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}
}
