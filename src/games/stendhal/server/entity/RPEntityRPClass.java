/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003-2014 - Marauroa                 *
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

import static games.stendhal.common.constants.Common.PATHSET;

import games.stendhal.common.constants.Testing;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

/**
 * Handles the RPClass registration.
 */
public class RPEntityRPClass {

	/**
	 * Generates the RPClass and specifies slots and attributes.
	 *
	 * @param ATTR_TITLE title attribute name
	 */
    public static void generateRPClass(String ATTR_TITLE) {
        final RPClass entity = new RPClass("rpentity");

        entity.isA("active_entity");
        entity.addAttribute("name", Type.STRING);
        entity.addAttribute(ATTR_TITLE, Type.STRING);
        entity.addAttribute("level", Type.SHORT);
        entity.addAttribute("xp", Type.INT);
        entity.addAttribute("mana", Type.INT);
        entity.addAttribute("base_mana", Type.INT);

        entity.addAttribute("base_hp", Type.SHORT);
        entity.addAttribute("hp", Type.SHORT);

        entity.addAttribute("atk", Type.SHORT, Definition.PRIVATE);
        entity.addAttribute("atk_xp", Type.INT, Definition.PRIVATE);
        entity.addAttribute("atk_item", Type.INT,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));
        entity.addAttribute("def", Type.SHORT, Definition.PRIVATE);
        entity.addAttribute("def_xp", Type.INT, Definition.PRIVATE);
        entity.addAttribute("def_item", Type.INT,
                (byte) (Definition.PRIVATE | Definition.VOLATILE));
        /* TODO: Remove condition when ranged stat testing is finished. */
        if (Testing.COMBAT) {
        	// FIXME: remove VOLATILE when ranged stat is ready
//        	entity.addAttribute("ratk", Type.SHORT, Definition.PRIVATE);
//        	entity.addAttribute("ratk_xp", Type.INT, Definition.PRIVATE);
	        entity.addAttribute("ratk", Type.SHORT,
	        		(byte) (Definition.PRIVATE | Definition.VOLATILE));
	        entity.addAttribute("ratk_xp", Type.INT,
	        		(byte) (Definition.PRIVATE | Definition.VOLATILE));
	        entity.addAttribute("ratk_item", Type.INT,
	                (byte) (Definition.PRIVATE | Definition.VOLATILE));
        }

        entity.addAttribute("risk", Type.BYTE, Definition.VOLATILE); // obsolete, do not use
        entity.addAttribute("damage", Type.INT, Definition.VOLATILE); // obsolete, do not use
        entity.addAttribute("heal", Type.INT, Definition.VOLATILE);
        // TODO: check that the binary representation of old saved players is compatible when this is changed into a list.
        entity.addAttribute("target", Type.INT, Definition.VOLATILE);
        entity.addAttribute("title_type", Type.STRING, Definition.VOLATILE);
        entity.addAttribute("base_speed", Type.FLOAT, Definition.VOLATILE);

        entity.addAttribute("ignore_collision", Type.FLAG, Definition.VOLATILE);

        entity.addAttribute("unnamed", Type.FLAG, Definition.VOLATILE);
        entity.addAttribute("no_hpbar", Type.FLAG, Definition.VOLATILE);

        // Jobs
        entity.addAttribute("job_merchant", Type.FLAG, Definition.VOLATILE);
        entity.addAttribute("job_healer", Type.FLAG, Definition.VOLATILE);

        // Status effects
        entity.addAttribute("choking", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("status_confuse", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("eating", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("poisoned", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("status_shock", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("status_zombie", Type.SHORT, Definition.VOLATILE);
        entity.addAttribute("status_heavy", Type.SHORT, Definition.VOLATILE);

        //Status effects resistances
        entity.addAttribute("resist_confused", Type.FLOAT, Definition.VOLATILE);
        entity.addAttribute("resist_drunk", Type.FLOAT, Definition.VOLATILE);
        entity.addAttribute("resist_heavy", Type.FLOAT, Definition.VOLATILE);
        entity.addAttribute("resist_poisoned", Type.FLOAT, Definition.VOLATILE);
        entity.addAttribute("resist_shocked", Type.FLOAT, Definition.VOLATILE);
        entity.addAttribute("resist_zombie", Type.FLOAT, Definition.VOLATILE);

		/* Movement */
		entity.addAttribute(PATHSET, Type.STRING, Definition.VOLATILE);

        entity.addRPSlot("head", 1, Definition.PRIVATE);
        entity.addRPSlot("rhand", 1, Definition.PRIVATE);
        entity.addRPSlot("lhand", 1, Definition.PRIVATE);
        entity.addRPSlot("armor", 1, Definition.PRIVATE);
        entity.addRPSlot("finger", 1, Definition.PRIVATE);
        entity.addRPSlot("cloak", 1, Definition.PRIVATE);
        entity.addRPSlot("legs", 1, Definition.PRIVATE);
        entity.addRPSlot("feet", 1, Definition.PRIVATE);
        entity.addRPSlot("back", 1, Definition.PRIVATE);
        entity.addRPSlot("belt", 1, Definition.PRIVATE);

        entity.addRPSlot("bag", 12, Definition.PRIVATE);
        entity.addRPSlot("keyring", 8, Definition.PRIVATE);

        entity.addRPEvent("attack", Definition.VOLATILE);
    }

}
