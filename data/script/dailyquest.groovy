/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;
import java.util.Random;
import games.stendhal.common.Level;

// Complex sample script that creates a mayor who gives a daily
// quest to any player that is: kill a named creature

// game is a predefined variable of the current StendhalGroovyScript 
// environment. all world and zone operations should be accessed
// through this to support unloading of scripts:
//  boolean game.setZone(String name) 
//  game.add(NPC npc) 
//  game.add(RPObject object)
//  game.getItem(String name)
//  game.getCreatures()
//  game.getItems()
// logger is a predefined variable of the current StendhalGroovyScript 
logger.debug("Starting Daily Quest Groovy Script") 

class DailyQuestAction extends SpeakerNPC.ChatAction {
  StendhalGroovyScript game;
  List sortedcreatures;
  public DailyQuestAction ( StendhalGroovyScript game) {
    this.game = game;
    List creatures = game.getCreatures().toList();
    sortedcreatures = creatures.sort { it.getLevel() }
  }
  public void fire(Player player, String text, SpeakerNPC engine)
    {
    String questInfo = player.getQuest("daily");
    String questKill = null;
    String questCount = null;
    String questLast = null;
    long delay = 60 * 60 * 24 * 1000; // Miliseconds in a day
    if(questInfo != null) 
      {
      List tokens = (questInfo+";0;0;0").tokenize(";");
      questKill = tokens[0];
      questLast = tokens[1];
      questCount = tokens[2];
      }
     if(questKill!=null && !"done".equals(questKill))
      {
      engine.say("You're already on a quest to slay a " + questKill + ". Say #complete if you're done with it!");
      return;
      }
    if(questLast != null && (new Date()).getTime() - new Long( questLast) < delay )
      {
      engine.say("I can only give you a new quest once a day. Please check later.");
      return;
      }
    int current = 0;
    int start   = 0;
    int level   = player.getLevel();
    for (creature in sortedcreatures) 
      {
      if((start==0) && creature.getLevel()>0 && creature.getLevel()>=level-5) 
        {
        start=current;          
        }
      if(creature.getLevel()>level+5) 
        {
        current--;
        break;
        }
      current++;
      }
    if(start >= sortedcreatures.size()-1) 
      {
      start = sortedcreatures.size()-2;        
      }
    if(start < 0) 
      {
      start = 0;        
      }
    if(current == sortedcreatures.size())
      {
      current--;
      }
    if(current>=start)
      {
      int result = start + new Random().nextInt(current-start+1);
      String creatureName = sortedcreatures.get(result).getName();

      // don't ask level 0 players to kill a bat as this cannot be found
      // anywhere they have a change to survive.
      if ("bat".equals(creatureName)) {
          creatureName = "rat";
      }
      engine.say("Semos is in need of help. Go kill a " + creatureName + " and say #complete, once you're done.");
      player.removeKill(creatureName);
      questLast = ""+(new Date()).getTime();
      player.setQuest("daily",sortedcreatures.get(result).getName()+";"+questLast+";"+questCount);
      }
    else // shouldn't happen
      {
      engine.say("Thanks for asking, but there's nothing you can do for me now.");      
      }
    }
}

class DailyQuestCompleteAction extends SpeakerNPC.ChatAction {
  StendhalGroovyScript game;
  public DailyQuestCompleteAction ( StendhalGroovyScript game) {
    this.game = game;
  }
  public void fire(Player player, String text, SpeakerNPC engine)
    {
    String questInfo = player.getQuest("daily");
    String questKill = null;
    String questCount = null;
    String questLast = null;
    if(questInfo == null)
      {
      engine.say("I'm afraid I didn't send you on a #quest yet.");
      return;
      }
    List tokens = (questInfo+";0;0").tokenize(";");
    questKill = tokens[0];
    questLast = tokens[1];
    questCount =tokens[2];
    if(questCount.equals("null"))
      {
      questCount = "0";
      }
    if("done".equals(questKill))
      {
      engine.say("You already completed the last quest I had given to you.")
      return;
      }
    if(player.hasKilled(questKill))
      {
      int start = Level.getXP(player.getLevel());
      int next  = Level.getXP(player.getLevel()+1);
      int reward = (next - start) / 5;
      if(player.getLevel() >= Level.maxLevel())
        {
        reward = 0;
        }
      engine.say("Good work! Let me thank you in the name of the people of Semos!")
      player.addXP(reward);
      questCount = "" + (new Integer(questCount) + 1 );
      questLast = ""+(new Date()).getTime();
      player.setQuest("daily","done"+";"+questLast+";"+questCount);
      }
    else
      {
      engine.say("You didn't kill a " + questKill + " yet. Go and do it and say #complete only after you're done.");
      }
    }
}


// Adding a a Mayor to the townhall who gives out daily quests
myZone = "int_semos_townhall"
if(game.setZone(myZone))   // if zone exists
  {
  // We create an NPC
  npc=new ScriptingNPC("Mayor")
  
  // Set an outfit for this player
  npc.setClass("mayornpc")
  
  // Set the NPC at the mayor place
  node = {x,y | new Path.Node(x,y)}
  npc.setPath([node(13,2),node(19,2)])
  
  // Adds all the behaviour chat
  npc.behave("greet","Welcome citizen! Do you need #help?")
  npc.behave("job","I'm the mayor of Semos village.")
  npc.behave("help","You will find a lot of people in Semos that offer you help on different topics.")
  npc.behave("bye","Have a good day and enjoy your stay!")

  npc.add (1,[ "quest", "task" ],null,1,null, new DailyQuestAction(game));
  npc.add (1,[ "complete", "done" ],null,1,null, new DailyQuestCompleteAction(game));

  // Add our new NPC to the game world
  game.add(npc);
   
  }
  else
    {
   logger.error("Cannot set Zone " + myZone)
    }



logger.debug("Finished Daily Quest Groovy Script")
