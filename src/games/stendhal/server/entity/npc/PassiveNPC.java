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

import games.stendhal.common.Direction;

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
	
    protected int pauseTurns = 0;
	protected int pauseTurnsRemaining = 0;
	protected Direction pauseDirection;
	
	/**
	 * Creates a new PassiveNPC.
	 *
	 */
	public PassiveNPC() {
		baseSpeed = 0.2;
		createPath();
		
		put("title_type", "npc");
		
		// Entity name is not drawn because of "unnamed" attribute
        setName("PassiveNPC");
		put("unnamed", "");
		
		// Health bar drawing is supressed
		put("no_hpbar", "");
		
		setSize(1, 1);
		
		updateModifiedAttributes();
	}
	
	protected void createPath() {
		// sub classes can implement this method
	}

	@Override
	public void logic() {
        if (pauseTurnsRemaining == 0) {
    	    super.logic();
    	    
    		if (hasPath()) {
    			setSpeed(getBaseSpeed());
    		}
    		
		    applyMovement();
		} else {
		    if (!stopped()) {
		        stop();
	            if (pauseDirection != null) setDirection(pauseDirection);
		    }
		    
		    pauseTurnsRemaining -= 1;
		}
        
        notifyWorldAboutChanges();
	}
	
	@Override
	public void onFinishedPath() {
	    super.onFinishedPath();
	    
	    if (isMovingEntity() && usesRandomPath()) {
	        // FIXME: There is a pause when renewing path
            setRandomPathFrom(getX(), getY(), getMovementRange() / 2);
	    }
	    
	    pauseTurnsRemaining = pauseTurns;
	}
	
	/**
	 * Pause the entity when path is completed.
	 * Call setDirection() first to specify which
	 * way entity should face during pause.
	 * 
	 * @param pause
	 *         Number of turns entity should stay paused
	 */
	public void setFinishedPathPause(int pause) {
	    this.pauseTurns = pause;
	    this.pauseDirection = getDirection();
	}
}
