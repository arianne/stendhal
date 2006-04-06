package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.maps.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.*;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Speak with Zynn
 * PARTICIPANTS:
 * - Zynn
 *
 * STEPS:
 * - Talk to Zynn to activate the quest and keep speaking with Zynn.
 *
 * REWARD:
 * - 10 XP (check that user's level is lower than 5)
 * - 5 gold
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetZynn implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("int_semos_library"));

    SpeakerNPC npc=npcs.get("Zynn Iwuhos");

    /** Quest can always be started again. Just check that no reward is given for players higher than level 15. */

    npc.add(1,"history",null,1,"There are at present two powers at Faiumoni: Deniran empire and Blordrough's dark legion. Deniran still controls the central and north part of Faiumoni, and has under its control the mines of Gold and Mithril. On the other side the Blordrough's legion has conquered the south of the island and has under its control several mines of steel and an important gold mine.",null);

    npc.add(1,"news",null,1,"Deniran empire is seeking help from adventurers to take part in their army on the task of recovering the total control of Faiumoni, unfortunatelly they have found an awesome resistence. The empire army has been sent to defend the south part of the island that is suffering constants attacks.",null);

    npc.add(1,"geography",null,1,"I can tell you about the different #places in Faiumoni. I can teach you how to #use maps or where to #get them. I can recommend you a #SPS psychic",null);

    npc.add(1,"places",null,1,"I can talk to you about the island of #Faiumoni or about its most famous locations: #Semos city, #Ados city, #Or'ril castle, #Narwol city or #Deniran city.",null);

    npc.add(1,"Faiumoni",null,1,"It is the big island we are in, with mountains on its north part, a big desert on the middle and a long river that crosses it on its half.",null);

    npc.add(1,"Semos",null,1,"It is precisely our small city on the North of Faiumoni. It is populated by 40-50 persons.",null);

    npc.add(1,"Ados",null,1,"It is a coastal city with a very important port, where merchants arrive to sell their stuff. Ados is the end of an important commercial route that has its heart at Deniran, the capital of Faiumoni.",null);

    npc.add(1,"Or'ril",null,1,"It is one of a set of castles that were built around the imperial path to defend the route from Ados to Deniran. Now Or'ril is abandoned because the empire army has been sent to defend the south part of the empire that is suffering constants attacks. It used to be populated by a unit of Deniran's swordmen, around 60 of them, plus a set of helpers. It is not known if there is anyone any longer living in the castle.",null);

    npc.add(1,"Narwol",null,1," It is an ancient elven city, that legend says was built to defend Teruykeh, the elven capital, against an ancient evil. Narwol was built long years before the first human set his feet on Faiumoni. Elves dislike common treats with the rest of races, so their cities are generally hidden inside forests or deep valleys.",null);

    npc.add(1,"Deniran",null,1,"It is the jewel of the crown. Deniran is the center of Faiumoni and supports the army that tries to defeat enemies that want to conquer Faiumoni. Deniran has a very important commercial route with other empires that is a channel through the imperial path up to Ados on the north and with Sikhw on the south. Unfortunatelly Sikhw was conquered some time ago, and no one can reach it anymore.",null);

    npc.add(1,"use",null,1,"You have to become familiarized with map's #levels, map's zone #naming and map's #positioning. Once you've managed that, you can go to meet any other adventurer when you know his position.",null);

    npc.add(1,"levels",null,1,"Maps are classified by levels to indicate their height respect to the surface. Thus, surface is indicated by level 0, subsurface by level -1, subsubsurface by level -2. These level indexes appear as prefixes in the name of every map. Thus the map of the surface of Semos city is 0_semos_city and the first level of the dungeon under Semos city is -1_semos_dungeon. Interiors of buildings have levels as suffixes and INT as prefix.",null);

    npc.add(1,"naming",null,1,"Every set of zones of the map has a central zone wich is the reference point for other surrounding areas. These areas are named consequently after this central area with an added suffix that indicates the path taken from the central area to the area of interest. Thus,  0_semos_forest_n2_w is the zone in the neighborhood of the forest of semos that is two areas north and then one area west from the forest's central area.",null);

    npc.add(1,"positioning",null,1,"Position (X,Y) is relative to every zone of the map: X stands for horizontal position and increases to the right and Y stands for vertical position and increases to the bottom: this means that (0,0) is the top left corner.",null);

    npc.add(1,"get",null,1,"You can get an scaled-down aerial image of the known surface of stendhal at #http://arianne.sourceforge.net/wiki_images/atlas_world.png But BEWARE, while maps can be very useful they might also spoil a little the surprise of discovering for the first time",null);

    npc.add(1,"SPS",null,1,"#SPS stands for #S tendhal's #P ositioning #S ystem. You can ask #Io about how to know your own or other adventurers' position anytime.",null);

    npc.add(1,"Io",null,1,"Her full name is Io Flotto. She spends most of her time in the Temple... floating... Yes, I know it sounds weird but I have to admit all my sextants have never worked half as well as her \"intuitive skill\".",null);

    /** I still have to think of a way to reward a good amount of XP to the most interested player for this long reading...
        How about keeping a list of all the things the player has asked and reward him when the list is complete?*/
    npc.add(1,"bye",null,0,null,

      new SpeakerNPC.ChatAction()
        {
        public void fire(Player player, String text, SpeakerNPC engine)
          {

          int level=player.getLevel();
          if(level<15)
            {
            engine.say("Bye, if you're going to stay here remember to keep silence.");
            }

          else
            {
            engine.say("Bye, you should consider getting a library card...");
            }
          }
	});
    }


 public MeetZynn(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }