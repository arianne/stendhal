/*
 * @(#) src/games/stendhal/server/entity/area/AreaEntity.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.server.entity.Entity;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * A base area entity.
 */
public abstract class AreaEntity extends Entity {
// MAYBEDO (if Entity.RPCLASS added):
//	/**
//	 * The RPClass.
//	 */
//	public final static RPClass	RPCLASS		= createRPClass();


	/**
	 * Create an area entity.
	 *
	 * @param	width		Width of this area
	 * @param	height		Height of this area
	 */
	public AreaEntity(int width, int height) {
		setRPClass("area");
		put("type", "area");

		setWidth(width);
		setHeight(height);

		setObstacle(false);
	}


	//
	// AreaEntity
	//

	/**
	 * Define the RPClass.
	 *
	 * @return	The configured RPClass.
	 */
	private static RPClass createRPClass() {
		RPClass rpclass = new RPClass("area");

		// MAYBEDO: rpclass.isA(Entity.RPCLASS)
		rpclass.isA("entity");
		rpclass.addAttribute("name", Type.STRING);

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
}
