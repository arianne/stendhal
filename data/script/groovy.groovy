/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path

// Simple sample script that creates a sign prooving Groovy is active

// game is a predefined variable of the current StendhalGroovyScript 
// environment. all world and zone operations should be accessed
// through this to support unloading of scripts:
//  boolean game.setZone(String name) 
//  game.add(NPC npc) 
//  game.add(RPObject object)
//  game.add(ScriptCondition condition, ScriptAction action)
//  game.getItem(String name)
// logger is a predefined variable of the current StendhalGroovyScript 
logger.debug("Starting Stendhal Groovy Script") 
                              
// Adding a sign to the game world that shows Groovy is active
myZone = "0_semos_city"
if(game.setZone(myZone))   // if zone exists
  {  
  // We create now a sign and place it on position 31,50 with some text
  sign=new Sign()
  sign.setx(31)
  sign.sety(50)
  sign.setText("Groovy objects can be found!")
  // Add our new Object to the game world
  game.add(sign)
  }
else
  {
 logger.error("Cannot set Zone " + myZone)
  }

logger.debug("Finished Stendhal Groovy Script")
