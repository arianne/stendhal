/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;

public class DressedEntityRPClass {

	/**
	 * Generates the RPClass and specifies slots and attributes.
	 */
	public static void generateRPClass() {
		final RPClass entity = new RPClass("dressed_entity");

		entity.isA("rpentity");
		entity.addAttribute("outfit_ext", Type.STRING);
		entity.addAttribute("outfit_ext_orig", Type.STRING, Definition.PRIVATE);
		entity.addAttribute("outfit", Type.INT);
		entity.addAttribute("outfit_org", Type.INT, Definition.PRIVATE);
		entity.addAttribute("outfit_colors", Type.MAP);
		entity.addAttribute("outfit_expire_age", Type.INT, Definition.HIDDEN);

		/* unused */
		entity.addAttribute("outfit_mouth", Type.INT, Definition.VOLATILE);
		entity.addAttribute("outfit_mouth_org", Type.INT, (byte) (Definition.PRIVATE|Definition.VOLATILE));
		entity.addAttribute("outfit_eyes", Type.INT, Definition.VOLATILE);
		entity.addAttribute("outfit_eyes_org", Type.INT, (byte) (Definition.PRIVATE|Definition.VOLATILE));
		entity.addAttribute("outfit_mask", Type.INT, Definition.VOLATILE);
		entity.addAttribute("outfit_mask_org", Type.INT, (byte) (Definition.PRIVATE|Definition.VOLATILE));
		entity.addAttribute("outfit_hat", Type.INT, Definition.VOLATILE);
		entity.addAttribute("outfit_hat_org", Type.INT, (byte) (Definition.PRIVATE|Definition.VOLATILE));
	}
}
