/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2010-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement;

/**
 * categories of achievements
 *
 * @author madmetzger
 */
public enum Category {
	/** xp points */
	EXPERIENCE,
	/** fighting and killing creatures */
	FIGHTING,
	/** solving quests */
	QUEST,
	/** groups of achievements */
	META,
	/** visiting groups of outside zones */
	OUTSIDE_ZONE,
	/** visiting groups of underground zones */
	UNDERGROUND_ZONE,
	/** age of character */
	AGE,
	/** looting items */
	ITEM,
	/** getting items */
	OBTAIN, 
	/** helping others and being friendly */
	FRIEND, 
	/** producing items */
	PRODUCTION;
}