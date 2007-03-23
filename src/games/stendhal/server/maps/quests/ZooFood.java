package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Zoo Food
 * 
 * PARTICIPANTS:
 * - Katinka, the keeper at the Ados Wildlife Refuge
 * - Dr. Feelgood, the veterinary
 * 
 * STEPS:
 * - Katinka asks you for ham for the animals
 * - You get the ham, e.g. by killing other animals ;)
 * - You give the ham to Katinka.
 * - Katinka thanks you.
 * - You can then buy cheap medicine from Dr. Feelgood.
 * 
 * REWARD:
 * - 200 XP
 * - everlasting supply for cheap medicine
 * 
 * REPETITIONS:
 * - None.
 */
public class ZooFood extends AbstractQuest {

	private static final int REQUIRED_HAM = 10;

	private static final String QUEST_SLOT = "zoo_food";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
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
			return res;
		}
		res.add("QUEST_ACCEPTED");
		if ((player.isEquipped("ham", REQUIRED_HAM)) || isCompleted(player)) {
			res.add("FOUND_ITEM");
		}
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Katinka");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
		        null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (!player.isQuestCompleted(QUEST_SLOT)) {
					        engine
					                .say("Welcome to the Ados Wildlife Refuge! We rescue animals from being slaughtered by evil adventurers. But we need help... maybe you could do a #task for us?");
				        } else {
					        engine
					                .say("Welcome back to the Ados Wildlife Refuge! Thanks again for rescuing our animals!");
				        }
			        }
		        });

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
		        ConversationStates.QUEST_OFFERED, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (!player.isQuestCompleted(QUEST_SLOT)) {
					        engine.say("Our tigers, lions and bears are hungry. We need "
					                + Grammar.quantityplnoun(REQUIRED_HAM, "ham") + " to feed them. Can you help us?");
				        } else {
					        engine.say("Thank you, but I think we are out of trouble now.");
					        engine.setCurrentState(ConversationStates.ATTENDING);
				        }
			        }
		        });

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.ATTENDING,
		        "Okay, but please don't let the poor animals suffer too long! Bring me the "
		                + Grammar.plnoun(REQUIRED_HAM, "ham") + " as soon as you get " + Grammar.itthem(REQUIRED_HAM)
		                + ".", new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        player.setQuest(QUEST_SLOT, "start");
			        }
		        });

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.ATTENDING,
		        "Oh dear... I guess we're going to have to feed them with the deer...", new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        player.setQuest(QUEST_SLOT, "rejected");
			        }
		        });
	}

	private void step_2() {
		// Just find the ham somewhere. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Katinka");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new SpeakerNPC.ChatCondition() {

			@Override
			public boolean fire(Player player, String text, SpeakerNPC engine) {
				return player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT).equals("start");
			}
		}, ConversationStates.QUEST_ITEM_BROUGHT, "Welcome back! Have you brought the "
		        + Grammar.quantityplnoun(REQUIRED_HAM, "ham") + "?", null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (player.drop("ham", REQUIRED_HAM)) {
					        player.notifyWorldAboutChanges();
					        player.setQuest(QUEST_SLOT, "done");
					        player.addXP(200);
					        engine.say("Thank you! You have rescued our rare animals.");
				        } else {
					        engine.say("*sigh* I SPECIFICALLY said that we need "
					                + Grammar.quantityplnoun(REQUIRED_HAM, "ham") + "!");
				        }
			        }
		        });

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, "no", null, ConversationStates.ATTENDING,
		        "Well, hurry up! These rare animals are starving!", null);
	}

	private void step_4() {
		SpeakerNPC npc = npcs.get("Dr. Feelgood");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
		        null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (player.isQuestCompleted(QUEST_SLOT)) {
					        engine
					                .say("Hello! Now that the animals have enough food, they don't get sick that easily, and I have time for other things. How can I help you?");
				        } else {
					        engine
					                .say("Sorry, can't stop to chat. The animals are all sick because they don't have enough food. See yourself out, won't you?");
					        engine.setCurrentState(ConversationStates.IDLE);
				        }
			        }
		        });
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
		step_4();
	}
}
