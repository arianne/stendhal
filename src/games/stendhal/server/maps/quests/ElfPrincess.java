package games.stendhal.server.maps.quests;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The Elf Princess
 * 
 * PARTICIPANTS:
 * <ul>
 *  <li>Tywysoga, the Elf Princess in Nalwor Tower</li>
 *  <li>Rose Leigh, the wandering flower seller.</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 *  <li>The princess asks you for a rare flower</li>
 *  <li>Find the wandering flower seller</li>
 *  <li>You are given the flower, provided you've already been asked to fetch it</li>
 *  <li>Take flower back to princess</li>
 *  <li>Princess gives you a reward</li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 *  <li>5000 XP</li>
 *  <li>Some gold bars</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *  <li>Unlimited, provided you've activated the quest by asking the princess for a task again</li>
 * </ul>
 */
public class ElfPrincess extends AbstractQuest {
	private static final int GOLD_AMOUNT = 5;
	private static final String QUEST_SLOT = "elf_princess";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void offerQuestStep() {
		SpeakerNPC npc = npcs.get("Tywysoga");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
		        ConversationStates.ATTENDING,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        if (!player.hasQuest(QUEST_SLOT) || player.getQuest(QUEST_SLOT).equals("rejected")) {
					        npc.say("Will you find the wandering flower seller, Rose Leigh, and get from her my favourite flower, the Rhosyd?");
						npc.setCurrentState(ConversationStates.QUEST_OFFERED);
				        }
					//shouldn't happen
					else if (player.isQuestCompleted(QUEST_SLOT)) {
					        npc.say("I have plenty of blooms now thank you.");
				        } else if (player.hasQuest(QUEST_SLOT)&&player.getQuest(QUEST_SLOT).equals("flower_brought")){
				        	npc.say("The last Rhosyd you brought me was so lovely. Will you find me another from Rose Leigh?");
						npc.setCurrentState(ConversationStates.QUEST_OFFERED);
				        }
				        else {
					        npc.say("I do so love those pretty flowers from Rose Leigh ...");
				        }
			        }
		        });
		//Player agrees to collect flower
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
			        null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
					        npc.say("Thank you! Once you find it, say #flower to me so I know you have it. I'll be sure to give you a nice reward.");
						player.setQuest(QUEST_SLOT, "start");
						player.addKarma(10.0);
					}
				});
		//Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
                ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.IDLE,
                "Oh, never mind. Bye then.",
                new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						player.addKarma(-10.0);
						player.setQuest(QUEST_SLOT, "rejected");
					}
				});
	}


    
    private void getFlowerStep() {
	SpeakerNPC rose = npcs.get("Rose Leigh");

				rose.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				    new SpeakerNPC.ChatCondition() {
					@Override
					    public boolean fire(Player player, String text, SpeakerNPC npc) {
					    return player.hasQuest(QUEST_SLOT)&& 
						   player.getQuest(QUEST_SLOT).equals("start");  
						}
				    },
					ConversationStates.ATTENDING, null,
					new SpeakerNPC.ChatAction() {

						@Override
						public void fire(Player player, String text, SpeakerNPC engine) {
							Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("rhosyd");
							engine.say("Hello dearie. My far sight tells me you need a pretty flower for some fair maiden. Here ye arr.");
							item.put("bound", player.getName());
							player.equip(item, true);
							player.setQuest(QUEST_SLOT, "got_flower");
						}
				});


    }	


private void bringFlowerStep() {
	SpeakerNPC npc = npcs.get("Tywysoga");
	npc.add(ConversationStates.ATTENDING,
		Arrays.asList("rhosyd", "flower", "Rhosyd"),		        
	        new SpeakerNPC.ChatCondition() {
		        @Override
		        public boolean fire(Player player, String text, SpeakerNPC npc) {
			        return player.hasQuest(QUEST_SLOT)
				    && player.getQuest(QUEST_SLOT).equals("got_flower")  
			        	    && player.isEquipped("rhosyd");
		        }
	        },
	        ConversationStates.ATTENDING,
	        null,
	        new SpeakerNPC.ChatAction() {
	        	@Override
		        public void fire(Player player, String text, SpeakerNPC npc) {
	        	 player.drop("rhosyd");    
		         npc.say("Thank you! Take this gold, I have plenty. If you'd ever like to get me another, be sure to ask me first. Rose Leigh is superstitious, she won't give the bloom unless she senses you need it.");
		         player.addXP(5000);
			 StackableItem goldbars = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("gold_bar");
			 goldbars.setQuantity(GOLD_AMOUNT);
			 //	 goldbars.put("bound", player.getName()); <- not sure if these should get bound or not.
			 player.equip(goldbars, true);
	        	 player.setQuest(QUEST_SLOT, "flower_brought");
		        }
	         }
	        );
	npc.add(ConversationStates.ATTENDING,
			Arrays.asList("rhosyd", "flower", "Rhosyd"),
	        new SpeakerNPC.ChatCondition() {
		        @Override
		        public boolean fire(Player player, String text, SpeakerNPC npc) {
			    return !player.isEquipped("rhosyd");
		        }
	        },
	        ConversationStates.ATTENDING,
 "You don't seem to have a rhosyd bloom with you. But Rose Leigh wanders all over the island, I'm sure you'll find her one day!", 
			    null);
	        

	
}



	@Override
	public void addToWorld() {
		super.addToWorld();

		offerQuestStep();
		getFlowerStep();
		bringFlowerStep();
	}

}
