/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import org.apache.log4j.Logger;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;


/**
 * An entity that engages in combat fighting.
 */
public abstract class CombatEntity extends GuidedEntity {

	private static final Logger logger = Logger.getLogger(CombatEntity.class);

	public static final String RPCLASS_NAME = "combat_entity";


	/**
	 * Default constructor.
	 */
	public CombatEntity() {
		super();
	}

	/**
	 * Copy constructor.
	 *
	 * @param object
	 *     The entity to be copied.
	 */
	public CombatEntity(final RPObject object) {
		super(object);
	}

	/**
	 * Generates the RPClass & specifies attributes.
	 */
    public static void generateRPClass() {
        try {
			final RPClass rpclass = new RPClass(RPCLASS_NAME);
			rpclass.isA("active_entity");
        } catch (final Exception e) {
            logger.error("cannot generate RPClass", e);
        }
    }
}
