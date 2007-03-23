package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Special Soup
 * 
 * PARTICIPANTS:
 * - Mother Helena in Fado tavern
 * 
 * STEPS:
 * - OldLady tells you the ingredients of a special soup
 * - You collect the ingredients 
 * - You bring the ingredients to the taven and pay a small fee
 * - Eating the soup (must be at table) heals you fully and gives you a base HP/mana bonus
 *  
 *  
 * REWARD:
 * - heal
 * - base hp/mana bonus
 * - 100 XP
 * 
 * REPETITIONS:
 * - None.
 * 
 * @author kymara
 */
public class Soup extends AbstractQuest {

	private static final List<String> neededFood = Arrays.asList("carrot", "spinach", "courgette", "cabbage", "salad",
	        "onion", "cauliflower", "broccoli", "leek");

	/**
	 * Returns a list of the names of all food that the given player
	 * still has to bring to fulfil the quest.
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of toy names
	 */
	private List<String> missingFood(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();

		String doneText = player.getQuest("soup_maker");
		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		for (String ingredient : neededFood) {
			if (!done.contains(ingredient)) {
				if (hash) {
					ingredient = "#" + ingredient;
				}
				result.add(ingredient);
			}
		}
		return result;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Mother Helena");

		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.hasQuest("soup_maker");
			}
		}, ConversationStates.ATTENDING,
		        "Hello, stranger. You look weary from your travels. I know what would #help you.", null);

		npc.add(ConversationStates.ATTENDING, "help", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.hasQuest("soup_maker");
			}
		}, ConversationStates.QUEST_OFFERED, null, new SpeakerNPC.ChatAction() {

			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (!player.isQuestCompleted("soup_maker")) {
					engine.say("My special soup will revive you. I need you to bring me the #ingredients.");
				} else { // to be honest i don't understand when this would be implemented. i put the text i want down in stage 3 and it works fine.
					engine.say("I have everything for the recipe now.");
					engine.setCurrentState(ConversationStates.ATTENDING);
				}
			}
		});

		//		 player asks what exactly is missing
		npc.add(ConversationStates.QUEST_OFFERED, "ingredients", null, ConversationStates.QUEST_OFFERED, null,
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        List<String> needed = missingFood(player, true);
				        engine.say("I need " + Grammar.quantityplnoun(needed.size(), "ingredient")
				                + " before I make the soup: " + Grammar.enumerateCollection(needed)
				                + ". Will you collect them?");
			        }
		        });
		// player is willing to collect
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.IDLE,
		        null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        engine.say("You made a wise choice. Farewell, traveller.");
				        player.setQuest("soup_maker", "");
			        }
		        });
		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.ATTENDING,
		        "Well, maybe someone else will happen by and help me.", null);
		// players asks about the vegetables individually
		npc.add(ConversationStates.QUEST_OFFERED, Arrays.asList("spinach", "courgette", "cabbage", "onion",
		        "cauliflower", "broccoli", "leek"), null, ConversationStates.QUEST_OFFERED,
		        "You will find that in allotments close to fado.", null);
		npc.add(ConversationStates.QUEST_OFFERED, Arrays.asList("salad", "carrot"), null,
		        ConversationStates.QUEST_OFFERED, "I usually have to get them imported from Semos.", null);
	}

	private void step_2() {
		// Fetch the ingredients and bring them back to Helena.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Mother Helena");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.hasQuest("soup_maker") && !player.isQuestCompleted("soup_maker");
			}
		}, ConversationStates.QUESTION_1, "Welcome back! I hope you collected some #ingredients for the soup.", null);

		//		 player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_1, "ingredients", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.hasQuest("soup_maker") && !player.isQuestCompleted("soup_maker");
			}
		}, ConversationStates.QUESTION_1, null, new SpeakerNPC.ChatAction() {

			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				List<String> needed = missingFood(player, true);
				engine.say("I still need " + Grammar.quantityplnoun(needed.size(), "ingredient") + ": "
				        + Grammar.enumerateCollection(needed) + ". Did you bring anything I need?");
			}
		});

		// player says he has a required ingredient with him
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES, null, ConversationStates.QUESTION_1,
		        "What did you bring?", null);

		for (String ingredient : neededFood) {
			npc.add(ConversationStates.QUESTION_1, ingredient, null, ConversationStates.QUESTION_1, null,
			        new SpeakerNPC.ChatAction() {

				        @Override
				        public void fire(Player player, String text, SpeakerNPC engine) {
					        List<String> missing = missingFood(player, false);
					        if (missing.contains(text)) {
						        if (player.drop(text)) {
							        // register ingredient as done
							        String doneText = player.getQuest("soup_maker");
							        player.setQuest("soup_maker", doneText + ";" + text);
							        // check if the player has brought all Food
							        missing = missingFood(player, true);
							        if (missing.size() > 0) {
								        engine.say("Thank you very much! What else did you bring?");
							        } else {
								        StackableItem pie = (StackableItem) StendhalRPWorld.get().getRuleManager()
								                .getEntityManager().getItem("pie");
								        pie.setQuantity(3);
								        player.equip(pie, true);
								        player.addXP(100);
								        player.setHP(player.getBaseHP());
								        player.healPoison();
								        engine
								                .say("Ok so I lied, you can't have soup yet because the image isn't drawn. Have some pies and a full heal instead. Sorry about that.");
								        player.setQuest("soup_maker", "done");
								        player.notifyWorldAboutChanges();
								        engine.setCurrentState(ConversationStates.ATTENDING);
							        }
						        } else {
							        engine.say("Don't take me for a fool, traveller. You don't have "
							                + Grammar.a_noun(text) + " with you.");
						        }
					        } else {
						        engine.say("You brought me that ingredient already.");
					        }
				        }
			        });
		}

		npc.add(ConversationStates.QUESTION_1, "", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !neededFood.contains(text);
			}
		}, ConversationStates.QUESTION_1, "I won't put that in your soup.", null);

		npc.add(ConversationStates.ATTENDING, "no", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.isQuestCompleted("soup_maker");
			}
		}, ConversationStates.ATTENDING, "I'm not sure what you want from me, then.", null);

		// player says he didn't bring any Food to different question
		npc.add(ConversationStates.QUESTION_1, "no", new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return !player.isQuestCompleted("soup_maker");
			}
		}, ConversationStates.ATTENDING, "Okay then. Come back later.", null);

		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.isQuestCompleted("soup_maker");
			}
		}, ConversationStates.ATTENDING,
		        "Hi! Did you want more soup? This quest will be repeatable when Katie learns how. But it's not yet.",
		        null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
}
