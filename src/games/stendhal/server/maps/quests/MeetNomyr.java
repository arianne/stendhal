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
 * QUEST: Speak with Nomyr
 * PARTICIPANTS:
 * - Nomyr
 *
 * STEPS:
 * - Talk to Nomyr to activate the quest and keep speaking with Nomyr.
 *
 * REWARD:
 * - No XP
 * - No money
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetNomyr implements IQuest
  {
  private StendhalRPWorld world;
  private NPCList npcs;

  private void step_1()
    {
    StendhalRPZone zone=(StendhalRPZone)world.getRPZone(new IRPZone.ID("0_semos_city"));

    SpeakerNPC npc=npcs.get("Nomyr Ahba");

    npc.add(1,"yes",null,50,"The young people have joined the Beniran army to fight in the South, so the city has been left unprotected from hordes of monsters coming from the dungeons. Can you help us?",null);

    npc.add(1,"no",null,0,"Oh, fine. Then help me at the other window... I'm trying to find out what's happening inside",null);

    npc.add(50,"yes",null,0,"First of all you should talk to Hayunn Naratha. He's a former great hero and our only defense here and he will gladly give you good advices. Good luck.",null);

    npc.add(50,"no",null,0,"Awww... I didn't know you were such a coward! What about me? Hey, I'm about to save a damsel in distress! Bye!",null);
    }

 public MeetNomyr(StendhalRPWorld w, StendhalRPRuleProcessor rules)
    {
    this.npcs=NPCList.get();
    this.world=w;

    step_1();
    }
  }
