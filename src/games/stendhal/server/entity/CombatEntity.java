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

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
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

			// hp/mana/etc.
			rpclass.addAttribute("base_hp", Type.SHORT);
			rpclass.addAttribute("hp", Type.SHORT);
			rpclass.addAttribute("base_mana", Type.INT);
			rpclass.addAttribute("mana", Type.INT);

			// stats
			rpclass.addAttribute("level", Type.SHORT);
			rpclass.addAttribute("xp", Type.INT);
			rpclass.addAttribute("atk", Type.SHORT, Definition.PRIVATE);
			rpclass.addAttribute("atk_xp", Type.INT, Definition.PRIVATE);
			// TODO: volatile flag should be removed when ready for main server
			rpclass.addAttribute("ratk", Type.SHORT, (byte) (Definition.PRIVATE | Definition.VOLATILE));
			rpclass.addAttribute("ratk_xp", Type.INT, (byte) (Definition.PRIVATE | Definition.VOLATILE));
			rpclass.addAttribute("def", Type.SHORT, Definition.PRIVATE);
			rpclass.addAttribute("def_xp", Type.INT, Definition.PRIVATE);

			rpclass.addAttribute("atk_item", Type.INT,
					(byte) (Definition.PRIVATE | Definition.VOLATILE));
			rpclass.addAttribute("ratk_item", Type.INT,
					(byte) (Definition.PRIVATE | Definition.VOLATILE));
			rpclass.addAttribute("def_item", Type.INT,
					(byte) (Definition.PRIVATE | Definition.VOLATILE));
			rpclass.addAttribute("heal", Type.INT, Definition.VOLATILE);
			// TODO: check that the binary representation of old saved players
			//       is compatible when this is changed into a list.
			rpclass.addAttribute("target", Type.INT, Definition.VOLATILE);

			// Status effects
			rpclass.addAttribute("choking", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("status_confuse", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("eating", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("poisoned", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("status_shock", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("status_zombie", Type.SHORT, Definition.VOLATILE);
			rpclass.addAttribute("status_heavy", Type.SHORT, Definition.VOLATILE);

			// status effects resistances
			rpclass.addAttribute("resist_confused", Type.FLOAT, Definition.VOLATILE);
			rpclass.addAttribute("resist_drunk", Type.FLOAT, Definition.VOLATILE);
			rpclass.addAttribute("resist_heavy", Type.FLOAT, Definition.VOLATILE);
			rpclass.addAttribute("resist_poisoned", Type.FLOAT, Definition.VOLATILE);
			rpclass.addAttribute("resist_shocked", Type.FLOAT, Definition.VOLATILE);
			rpclass.addAttribute("resist_zombie", Type.FLOAT, Definition.VOLATILE);

			// events
			rpclass.addRPEvent("attack", Definition.VOLATILE);

			// DO NOT USE, the following are obsolete
			rpclass.addAttribute("risk", Type.BYTE, Definition.VOLATILE);
			rpclass.addAttribute("damage", Type.INT, Definition.VOLATILE);
        } catch (final Exception e) {
            logger.error("cannot generate RPClass", e);
        }
    }
}
