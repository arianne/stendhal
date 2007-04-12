package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * QUEST: The Magic Sword forging
 * 
 * PARTICIPANTS:
 * - Vulcanus, son of Zeus itself, will forge for you the god's sword.
 * 
 * STEPS:
 * - Vulcanus tells you about the sword.
 * - He offers to forge a magic sword for you if you bring him what it needs. 
 * - You give him all what he ask you. 
 * - Vulcanus forges the magic sword for you
 * 
 * REWARD:
 * - Magic Sword
 * - 15000 XP
 * 
 *
 * REPETITIONS:
 * - None.
 */
public class StuffForVulcanus extends AbstractQuest {
	private static final int REQUIRED_IRON = 15;
	private static final int REQUIRED_GOLD_BAR = 12;
	private static final int REQUIRED_WOOD = 26;
	private static final int REQUIRED_GIANT_HEART = 6;
	private static final int REQUIRED_TIME = 10;
	private static final String QUEST_SLOT = "magicsword_quest";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (!player.hasQuest(QUEST_SLOT)) {
					engine.say("I once forged the most powerful of swords. I can do it again for you. Are you interested?");
				} else if (player.isQuestCompleted(QUEST_SLOT)) {
					engine.say("Oh! I am so tired. Look for me later. I need a few years of relax.");	  
				} else { 
					engine.say("Why are you bothering me when you haven't completed your quest yet?");
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
				engine.say("I will need several things: "+REQUIRED_IRON+" iron, "+REQUIRED_WOOD+" wood logs, "+REQUIRED_GOLD_BAR+" gold bars and "+REQUIRED_GIANT_HEART+" giant hearts. Come back when you have them.");
				player.setQuest(QUEST_SLOT, "start;0;0;0;0");
			}
		});
		npc.add(ConversationStates.QUEST_OFFERED,
				"no",
				null,
				ConversationStates.IDLE,
				"Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.",
				null
		);
	}

	private void step_2() {
		/* Get the stuff.*/
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Vulcanus");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.hasQuest(QUEST_SLOT)
				&& player.getQuest(QUEST_SLOT).startsWith("start");		    
			}
		},
		ConversationStates.ATTENDING,
		null,
		new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				String[] tokens = player.getQuest(QUEST_SLOT).split(";");
				
				int neededIron=REQUIRED_IRON-Integer.parseInt(tokens[1]);
				int neededWoodLogs=REQUIRED_WOOD-Integer.parseInt(tokens[2]);
				int neededGoldBars=REQUIRED_GOLD_BAR-Integer.parseInt(tokens[3]);
				int neededGiantHearts=REQUIRED_GIANT_HEART-Integer.parseInt(tokens[4]);
				boolean missingSomething=false;

				if(!missingSomething && neededIron>0) {
					if(!player.isEquipped("iron",neededIron)) {
						int amount=player.getNumberOfEquipped("iron");	
						if(amount>0) {
							player.drop("iron",amount);
							neededIron-=amount;
						}

						engine.say("How do you expect me to #forge it without missing "+Grammar.quantityplnoun(neededIron, "iron bar")+"?");
						missingSomething=true;
					} else {
						player.drop("iron",neededIron);
						neededIron=0;
					}
				}

				if(!missingSomething && neededWoodLogs>0) {
					if(!player.isEquipped("wood",neededWoodLogs)) {
						int amount=player.getNumberOfEquipped("wood");						
						if(amount>0) {
							player.drop("wood",amount);
							neededWoodLogs-=amount;
						}
						
						engine.say("How do you expect me to #forge it without missing "+Grammar.quantityplnoun(neededWoodLogs, "wood log")+" for the fire?");
						missingSomething=true;
					} else {
						player.drop("wood",neededWoodLogs);
						neededWoodLogs=0;
					}
				}

				if(!missingSomething && neededGoldBars>0) {
					if(!player.isEquipped("gold_bar",neededGoldBars)) {
						int amount=player.getNumberOfEquipped("gold_bar");						
						if(amount>0) {
							player.drop("gold_bar",amount);
							neededGoldBars-=amount;
						}
						engine.say("I must pay a bill to spirits in other to cast the enchantment over the sword. I need "+Grammar.quantityplnoun(neededGoldBars, "gold bar")+" more.");
						missingSomething=true;
					} else {
						player.drop("gold_bar",neededGoldBars);
						neededGoldBars=0;
					}
				}

				if(!missingSomething && neededGiantHearts>0) {
					if(!player.isEquipped("giant_heart",neededGiantHearts)) {
						int amount=player.getNumberOfEquipped("giant_heart");						
						if(amount>0) {
							player.drop("giant_heart",amount);
							neededGiantHearts-=amount;
						}
						engine.say("It is the base element of the enchantment. I do really need some "+Grammar.quantityplnoun(neededGiantHearts, "giant heart")+" more.");
						missingSomething=true;
					} else {
						player.drop("giant_heart",neededGiantHearts);
						neededGiantHearts=0;
					}
				}

				if(!missingSomething) {
					engine.say("You've brought everything I need to make the magic sword. Come back in "+
							REQUIRED_TIME + " minutes and it will be ready");
					player.setQuest(QUEST_SLOT,
							"forging;" + System.currentTimeMillis());
				} else {
					player.setQuest(QUEST_SLOT, "start;"+
							(REQUIRED_IRON-neededIron)+";"+
							(REQUIRED_WOOD-neededWoodLogs)+";"+
							(REQUIRED_GOLD_BAR-neededGoldBars)+";"+
							(REQUIRED_GIANT_HEART-neededGiantHearts));
				}
			}
		});	


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
				long timeRemaining = (Long.parseLong(tokens[1]) + delay) - System.currentTimeMillis();

				if ( timeRemaining > 0L ){
					engine.say("I haven't finished forging the sword. Please check back in " +
							TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))+
					".");
					return;
				}

				engine.say("I have finished forging the mighty magic sword. You deserve this. Now i'm going back to work, goodbye!");
				player.addXP(15000);
				Item magicSword = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("magic_sword");
				magicSword.put("bound", player.getName());
				player.equip(magicSword, true);
				player.notifyWorldAboutChanges();			
				player.setQuest(QUEST_SLOT, "done");
			}
		});

		npc.add(ConversationStates.ATTENDING,
				"forge",			
				null, 
				ConversationStates.ATTENDING,
				"I will need "+REQUIRED_IRON+" #iron, "+REQUIRED_WOOD+" #wood logs, "+REQUIRED_GOLD_BAR+" #gold bars and "+REQUIRED_GIANT_HEART+" #giant hearts",
				null);

		npc.add(ConversationStates.ANY,
				"iron",			
				null, 
				ConversationStates.ATTENDING,
				"You know, collect the iron ore lying around and get it cast! Bye!",
				null);
		npc.add(ConversationStates.ANY,
				"wood",			
				null, 
				ConversationStates.ATTENDING,
				"The forest is full of wood logs.",
				null);
		npc.add(ConversationStates.ANY,
				"gold",			
				null, 
				ConversationStates.ATTENDING,
				"Someone in Ados would forge the gold into gold bars for you.",
				null);
		npc.add(ConversationStates.ANY,
				"giant",			
				null, 
				ConversationStates.ATTENDING,
				"Long time ago forgotten histories talked about giants on the mountains at the north of Semos.",
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
