/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

logger.debug("Starting Deathmatch Entry Script") 
                   

class JumpToDeathmatchAction extends SpeakerNPC.ChatAction {
  StendhalGroovyScript game;
  public JumpToDeathmatchAction ( StendhalGroovyScript game) {
    this.game = game;
  }
  public void fire(Player player, String text, SpeakerNPC engine) {
    game.transferPlayer(player,  "int_semos_deathmatch", 17, 8);
    player.setDirection(Direction.DOWN);
    return;
  }
}


// Adding a sign to the game world that shows Groovy is active
myZone = "0_semos_plains_n"
if(game.setZone(myZone))   // if zone exists
  {  
  // We create now a sign and place it on position 100, 112 with some text
  sign=new Sign()
  sign.setx(102)
  sign.sety(117)
  sign.setText("Semos Deathmatch season!\nNow hiring! Amateurs need not apply!\n\nI am in Ados Swamp.\n\t\t -- Deathmatch Recruiter")
  game.add(sign)

  game.setZone("0_ados_swamp")
  npc=new ScriptingNPC("Deathmatch Recruiter")
  npc.setClass("youngsoldiernpc")
  node = {x,y | new Path.Node(x,y)}
  npc.setPath([node(40,35), node(40,84), node(53,84), node(53,80), node(84,80), 
               node(84,56), node(89,56), node(89,37), node(72,37), node(72,32), node(50,32), node(50,35)])
  game.add(npc)
  
 
  npc.behave("greet","Hey there. You look like a reasonable fighter.")
  npc.behave("job","I'm recruiter for the Semos #deathmatch.")
  npc.behave("help","Have you ever heard of the Semos #deathmatch.")
  npc.behave("deathmatch","The deathmatch is the ultimate challenge for true #heroes.")
  npc.behave("heroes","Are you such a hero? I can take you to the #challenge.")  
  npc.behave("bye","I hope you will enjoy the Semos #Deathmatch!")  
  
  npc.add (1,"challenge",null,1,null, new JumpToDeathmatchAction(game));
  
  
  
  }
else
  {
 logger.error("Cannot set Zone " + myZone)
  }

logger.debug("Finished Stendhal Groovy Script")
