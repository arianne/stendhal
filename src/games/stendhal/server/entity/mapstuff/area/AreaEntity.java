/*
 * @(#) src/games/stendhal/server/entity/area/AreaEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import java.util.LinkedList;
import java.util.List;

//
//

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * A base area entity.
 */
public class AreaEntity extends Entity {

	/**
	 * The name attribute name.
	 */
	protected static final String ATTR_NAME = "name";
	private List<AreaBehaviour> behaviours = new LinkedList<AreaBehaviour>();

	/**
	 * Creates a one by one area entity.
	 */
	public AreaEntity() {
		this(1, 1);
	}

	/**
	 * Create an area entity.
	 *
	 * @param width
	 *            Width of this area
	 * @param height
	 *            Height of this area
	 */
	public AreaEntity(final int width, final int height) {
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
		final RPClass rpclass = new RPClass("area");

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
	@Override
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
	 * the player.
	 *
	 * @param definite
	 *            <code>true</code> for "the", and <code>false</code> for
	 *            "a/an" in case the entity has no name.
	 *
	 * @return The description name.
	 */
	@Override
	public String getDescriptionName(final boolean definite) {
		final String name = getName();

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
		final String name = getName();

		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}

	/**
	 * adds a Behaviour to this area
	 *
	 * @param behaviour behaviour to add
	 */
	public void addBehaviour(AreaBehaviour behaviour) {
		this.behaviours.add(behaviour);
	}

	@Override
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		for (AreaBehaviour behaviour : behaviours ) {
			behaviour.addToWorld(this);
		}
	}

	@Override
	public void onRemoved(StendhalRPZone zone) {
		for (AreaBehaviour behaviour : behaviours) {
			behaviour.removeFromWorld();
		}
		super.onRemoved(zone);
	}

}
