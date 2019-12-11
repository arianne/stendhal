/***************************************************************************
 *                   (C) Copyright 2003-2015 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common.constants;

/**
 * Constants defined for testing purposes.
 *
 * @author AntumDeluge
 */
public class Testing {
	/** Switch for general testing */
	public static final boolean GENERAL =
			(System.getProperty("testing") != null);

	/** Debugging */
	public static final boolean DEBUG =
			(System.getProperty("DEBUG") != null);

	/** Testing switch for player actions */
	public static final boolean ACTION =
			(System.getProperty("testing.action") != null);

	/** Testing switch for chat system */
	public static final boolean CHAT =
			(System.getProperty("testing.chat") != null);

	/** Testing switch for combat system */
	public static final boolean COMBAT =
			(System.getProperty("testing.combat") != null);

	/** Testing switch for entity movement */
	public static final boolean MOVEMENT =
			(System.getProperty("testing.movement") != null);

	/** Testing switch for outfit system */
	public static final boolean OUTFIT =
			(System.getProperty("testing.outfit") != null);

	/** Testing switch for quests **/
	public static final boolean QUEST =
			(System.getProperty("testing.quest") != null);
}
