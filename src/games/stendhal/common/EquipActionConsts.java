/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;

/**
 * Constants using during the procession of an Equipment action.
 * TODO everywhere use this constants instead of plain strings
 */
public interface EquipActionConsts {
	String SOURCE_PATH = "source_path";

	// Compatibility object addressing
	String BASE_ITEM = "baseitem";
	String BASE_SLOT = "baseslot";
	String BASE_OBJECT = "baseobject";

	String TYPE = "type";

	// Compatibility object addressing
	String TARGET_OBJECT = "targetobject";
	String TARGET_SLOT = "targetslot";

	String GROUND_X = "x";
	String GROUND_Y = "y";

	String QUANTITY = "quantity";

	double MAXDISTANCE = 0.25;
	int MAX_CONTAINED_DEPTH = 25;

	String CLICKED = "clicked";

	String SOURCE_NAME = "source_name";

}
