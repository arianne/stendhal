package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The Vampire Sword
 * 
 * PARTICIPANTS:
 * - Hogart, a retired master dwarf smith, forgotten below the dwarf mines in Orril.
 * 
 * STEPS:
 * - Hogart tells you the story of the Vampire Lord.
 * - He offers to forge a Vampire Sword for you if you bring him what it needs.
 * - You get some items from the Catacombs and kill the Vampire Lord.
 * - You get the iron needed in the usual way by collecting iron ore and casting in Semos. 
 * - Hogart forges the Vampire Sword for you
 * 
 * REWARD:
 * - Vampire Sword
 * - 5000 XP
 * 
 *
 * REPETITIONS:
 * - None.
 */
public class VampireSword extends AbstractQuest {
    private static final int REQUIRED_IRON = 10;
    private static final int REQUIRED_TIME = 10;
    private static final String QUEST_SLOT = "vs_quest";
    
    @Override
	public void init(String name) {
	super.init(name, QUEST_SLOT);
    }
    
    private void step_1() {
	SpeakerNPC npc = npcs.get("Hogart");
	
	npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		null,
		ConversationStates.QUEST_OFFERED,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {
			if (!player.hasQuest(QUEST_SLOT)) {
			    engine.say("I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?");
			} else if (player.isQuestCompleted(QUEST_SLOT)) {
			    engine.say("What are you bothering me for now? You've got your sword, go and use it!");	  
			}
			else { engine.say("Why are you bothering me when you haven't completed your quest yet?");
			     }
		    }
		});
	
	npc.add(ConversationStates.QUEST_OFFERED,
		ConversationPhrases.YES_MESSAGES,
		null,
		ConversationStates.ATTENDING,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text,
					 SpeakerNPC engine) {
			engine.say("Then you need this #goblet. Take it to Semos #Catacombs.");
			Item emptygoblet =
			    StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("empty_goblet");
			player.equip(emptygoblet, true);
			player.setQuest(QUEST_SLOT, "start");
		    }
		});
	npc.add(ConversationStates.QUEST_OFFERED,
		"no",
		null,
		ConversationStates.IDLE,
		"Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.",
		null
		);
	
	npc.add(ConversationStates.ATTENDING,
		"Catacombs",
		null,
		ConversationStates.ATTENDING,
		"The Catacombs of North Semos of the ancient #stories.",
		null);

	npc.add(ConversationStates.ATTENDING,
		"goblet",
		null,
		ConversationStates.ATTENDING,
		"Go fill it with the blood of the enemies you meet in the #Catacombs.",
		null);
	
	
    }
    
    private void step_2() {
	/* Go to the catacombs, kill 7 vampirettes to get to the 3rd level,
	   kill 7 killer bats and the vampire lord to get the required
	   items to fill the goblet. Fill the goblet and come back. Phew!*/
    }
    
    private void step_3() {
	
	SpeakerNPC npc = npcs.get("Hogart");
	
	npc.add(ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		new SpeakerNPC.ChatCondition() {
		    @Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
			return player.hasQuest(QUEST_SLOT)
			    && player.getQuest(QUEST_SLOT).equals("start")
			    && player.isEquipped("goblet");
		    }
		},
		ConversationStates.QUEST_ITEM_BROUGHT,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {
			
			if (!player.isEquipped("iron",REQUIRED_IRON)) { 
			    engine.say("You have battled hard to bring that goblet. I will use it to #forge the vampire sword");
					}
			else {
			    player.drop("goblet");
			    player.drop("iron",REQUIRED_IRON);
				engine.say("You've brought everything I need to make the vampire sword. Come back in "+
					   REQUIRED_TIME + " minutes and it will be ready");
				/* and set a state correctly
				   -ConversationStates.STARTED_FORGE?
				   and take the stuff from the player!!! player.drop?
				*/
				player.setQuest(QUEST_SLOT,
						"forging;" + System.currentTimeMillis());
			}}});	

	npc.add(ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		new SpeakerNPC.ChatCondition() {
		    @Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
			return player.hasQuest(QUEST_SLOT)
			    && player.getQuest(QUEST_SLOT).equals("start")
			    && !player.isEquipped("goblet")
			    && player.isEquipped("empty_goblet");
		    }
		},
		ConversationStates.IDLE,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {
                engine.say("Did you lose your way? The Catacombs are in North Semos. Don't come back without a full goblet! Bye!");
			}
		});
		
	npc.add(ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		new SpeakerNPC.ChatCondition() {
		    @Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
			return player.hasQuest(QUEST_SLOT)
			    && player.getQuest(QUEST_SLOT).equals("start")
			    && !player.isEquipped("goblet")
			    && !player.isEquipped("empty_goblet");
		    }
		},
		ConversationStates.QUESTION_1,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {
               engine.say("I hope you didn't lose your goblet! Do you need another?");
			}
		});
		
	npc.add(ConversationStates.QUESTION_1,
	    ConversationPhrases.YES_MESSAGES,
	    null,
	    ConversationStates.IDLE,
	    null,
	    new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {

			    engine.say("You stupid ..... Be more careful next time. Bye!");
			    Item emptygoblet =
			    StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("empty_goblet");
			    player.equip(emptygoblet, true);
		    }
		});
		
    npc.add(ConversationStates.QUESTION_1,
	    "no",
	    null,
	    ConversationStates.IDLE,
	    "Then why are you back here? Go slay some vampires! Bye!",
	    null);
	    
	npc.add(ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		new SpeakerNPC.ChatCondition() {
		    @Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
			return player.hasQuest(QUEST_SLOT)
			    && player.getQuest(QUEST_SLOT).startsWith("forging;");
		    }
		},
		ConversationStates.IDLE,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {

			String[] tokens = player.getQuest(QUEST_SLOT).split(";");
			long delay = REQUIRED_TIME * 60 * 1000; // minutes -> milliseconds
			long timeRemaining = (Long.parseLong(tokens[1]) + delay) -
			    System.currentTimeMillis();

			if ( timeRemaining > 0L ){
			    engine.say("I haven't finished forging the sword. Please check back in " +
				       TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))+
				       ".");
			    return;
			}

			engine.say("I have finished forging the mighty Vampire Sword. You deserve this. Now i'm going back to work, goodbye!");
			player.addXP(5000);
			Item vampireSword = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("vampire_sword");
			vampireSword.put("bound", player.getName());
       			player.equip(vampireSword, true);
 			player.notifyWorldAboutChanges();			
			player.setQuest(QUEST_SLOT, "done");
		    }
		});
    	
	npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
		"forge",			
		null, 
		ConversationStates.QUEST_ITEM_BROUGHT,
		"Bring me 10 #iron bars to forge the sword with. Don't forget to bring the goblet too.",
		null);
	
	npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
		"iron",			
		null, 
		ConversationStates.IDLE,
		"You know, collect the iron ore lying around and get it cast! Bye!",
		null);



    }
    @Override
	public void addToWorld() {
	super.addToWorld();
	
	step_1();
	step_2();
	step_3();
    }

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("QUEST_REJECTED");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("QUEST_ACCEPTED");
		}
		if ((questState.equals("start") && player.isEquipped("goblet")) || questState.equals("done")) {
			res.add("FOUND_ITEM");
		}
		if (player.getQuest(QUEST_SLOT).startsWith("forging;")){
		    res.add("FORGING");
		}
		if (questState.equals("done")) {
			res.add("DONE");
		}
		return res;
	}
}
