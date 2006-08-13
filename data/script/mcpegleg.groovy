/* $Id$ */

import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path

// Almost simple ;) sample script that creates NPC McPegleg
// Note: McPegleg is needed for a little side quest (see kanmararn.groovy)

// game is a predefined variable of the current StendhalGroovyScript 
// environment. all world and zone operations should be accessed
// through this to support unloading of scripts:
//  boolean game.setZone(String name) 
//  game.add(NPC npc) 
//  game.add(RPObject object)
//  game.add(ScriptCondition condition, ScriptAction action)
//  game.getItem(String name)
// logger is a predefined variable of the current StendhalGroovyScript 
logger.debug("Starting McPegleg Groovy Script") 

// We create now some shops. Split the shops as needed as they can be added later.
rareWeaponShop=["scimitar":65,"katana":70,"bardiche":75,"hammer_+3":80]
rareArmorShop=["chain_armor_+1":32,"chain_armor_+2":42,"chain_armor_+3":52,"plate_armor":62,"plate_shield":40,"lion_shield":50]

// We create a dialogue action for the NPC we gonna create            
 class IOUQuestCompleteAction extends SpeakerNPC.ChatAction {
   StendhalGroovyScript game;
   public IOUQuestCompleteAction ( StendhalGroovyScript game) {
     this.game = game;
   }
   public void fire(Player player, String text, SpeakerNPC engine) {
     Item note  = player.getEquipped("note");
     if((note!=null && "charles".equalsIgnoreCase(note.get("infostring")))) {
       engine.say("Where did you get that from? Anyways, here is the money *sighs*");
       StackableItem money=game.getItem("money");            
       player.drop("note");
       money.setQuantity(250);
       player.equip(money);
       player.setQuest("IOU","done");
       engine.setActualState(1);
       } else
       {
       engine.say("I can't see that you got a valid IOU with my signature!");
       }
   }
 }
                              
// Adding a new NPC that buys some of the stuff that Xin doesn't
myZone = "int_semos_tavern_1"
if(game.setZone(myZone))   // if zone exists
  {
  // We create an NPC
  npc=new ScriptingNPC("McPegleg")
  
  // Set an outfit for this player
  npc.setClass("pirate_sailornpc")
  
  // Set the NPC path with the help of a Groovy closure
  node = {x,y | new Path.Node(x,y)}
  npc.setPath([node(16,2),node(13,2),node(13,1),node(13,2)])
  
  // Adds all the behaviour chat
//  Behaviours.addGreeting(npc,"Yo matey! You look like you need #help.")
  npc.behave("greet","Yo matey! You look like you need #help.")
//  Behaviours.addJob(npc,"I'm a trader of ... let's say ... #rare things.")
  npc.behave("job","I'm a trader of ... let's say ... #rare things.")
//  Behaviours.addHelp(npc,"Not sure if I can trust you ....")
  npc.behave("help","Not sure if I can trust you ....")
//  Behaviours.addQuest(npc,"Perhaps if you find some #rare #armor or #weapon ...")
  npc.behave("quest","Perhaps if you find some #rare #armor or #weapon ...")
//  Behaviours.addGoodbye(npc,"I see you!")
  npc.behave("bye","I see you!")
//  Behaviours.addReply(npc,["weapon","armor","rare"],"Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer");
  npc.behave(["weapon","armor","rare"],"Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer");

  // Add shop function
  myShop = [:]    // too bad, plus() isn't defined for HashMaps
  myShop.putAll(rareWeaponShop)
  myShop.putAll(rareArmorShop)
//  Behaviours.addBuyer(npc,new Behaviours.BuyerBehaviour(myShop))    
  npc.behave("buy",myShop)      
  
  // Add some atmosphere
  npc.setDescription("You see a dubious man with a patched eye and a wooden leg.")  
  npc.behave(["eye","leg","wood","patch"],"Not every day is a lucky day ...");
  npc.behave("pirate","That's none of you business!");

  // Add a mini quest: Bring him back the IOU from dead Charles from the Kanmararn quest and get the cash
  npc.add(1,[ "iou","henry","charles","note" ],new ScriptingNPC.NotQuestCondition("IOU") ,1,null,new IOUQuestCompleteAction(game));
  npc.add(1,[ "iou","henry","charles","note" ],new ScriptingNPC.QuestCompletedCondition("IOU") ,1,"You already got cash for that damned IOU!",null);
  
  // Add our new NPC to the game world
  game.add(npc);
  
  // Add a blackboard with the shop offers
  board=new Blackboard(false);
  board.set(11,4);
  text = "-- Buying --\n";
  for (entry in myShop) { text += entry.key + " \t" + entry.value + "\n" } 
  board.setText(text);
  game.add(board);    
  
  }
  else
    {
   logger.error("Cannot set Zone " + myZone)
    }

logger.debug("Finished McPegleg Groovy Script")
