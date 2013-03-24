/***************************************************************************
 *						(C) Copyright 2013 - Marauroa					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/

package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.PassiveEntity;

import org.apache.log4j.Logger;

/**
 * @author AntumDeluge
 * 
 * A passive NPC is an entity that acts like an NPC/Pet/Creature but is non-interactive.
 */
public abstract class PassiveNPC extends PassiveEntity {
	
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(PassiveNPC.class);
	
	/**
	 * The range in which the NPC will search for movement paths.
	 */
	private int movementRange = 20;
}