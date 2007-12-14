/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.rule;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;

/**
 * Ruleset Interface for processing actions in Stendhal.
 * 
 * @author Matthias Totz
 */
public interface ActionManager {

	/** PRE 0.40 */
	String getSlotNameToEquip(RPEntity entity, Item item);

	boolean onEquip(RPEntity entity, String slotName, Item item);
}
