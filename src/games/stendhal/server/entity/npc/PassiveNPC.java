/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * A stripped down SpeakerNPC that does not interact with players
 * 
 * @author AntumDeluge
 *
 */
public class PassiveNPC extends NPC {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(PassiveNPC.class);

	/**
	 * Creates a new PassiveNPC.
	 *
	 */
	public PassiveNPC() {
		baseSpeed = 0.2;
		createPath();
		
		//TODO: Make name not required
		setName("");
		put("title_type", "npc");
		
		setSize(1, 1);
		
		// set the default movement range
		//setMovementRange(20);
		
		updateModifiedAttributes();
	}
	
	protected void createPath() {
		// sub classes can implement this method
	}

	@Override
	protected void handleObjectCollision() {
		stop();
	}

	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		stop();
	}
	
	public void logic() {
		if (hasPath()) {
			setSpeed(getBaseSpeed());
		}
		applyMovement();
	}

}
