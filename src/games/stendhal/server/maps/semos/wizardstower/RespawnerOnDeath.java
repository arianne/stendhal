/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.Creature;

import java.util.Observable;
import java.util.Observer;

/** 
 * class for spawning important creatures immediately after killing, 
 * so they will turn undead.
 * 
 * TODO:  need to check types during castings 
 *  
 * @author yoriy
 *
 */
public class RespawnerOnDeath implements Observer {
 	public void update(Observable o, Object arg) {
       	CircumstancesOfDeath circ = (CircumstancesOfDeath) arg;
        Creature monster = (Creature) circ.getVictim();
   		//Logger.getLogger(RespawnerOnDeath.class).info("Killed "+monster.getName()+" in "+monster.getZone().getName()+"!");
   		monster.getZone().remove(monster);
   		monster.getRespawnPoint().notifyDead(monster);
        monster.getRespawnPoint().spawnNow();
   	}    		
}
