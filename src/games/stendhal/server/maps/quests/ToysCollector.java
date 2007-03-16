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
 * QUEST: Toys Collector
 * 
 * PARTICIPANTS:
 * - Anna, a girl who live in Ados
 * 
 * STEPS:
 * - Anna asks for some toys
 * - You guess she might like a teddy, dice or dress
 * - You bring the toy to Anna
 * - Repeat until Anna received all toys. (Of course you can
 *   bring several toys at the same time.)
 * - Anna gives you a reward
 * 
 * REWARD:
 * - ? some pies? 
 * - 100 XP
 * 
 * REPETITIONS:
 * - None.
 */
public class ToysCollector extends AbstractQuest {

	private static final List<String> neededToys = Arrays.asList("teddy", "dice", "dress");

	/**
	 * Returns a list of the names of all toys that the given player
	 * still has to bring to fulfil the quest.
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of toy names
	 */
	private List<String> missingToys(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();

		String doneText = player.getQuest("toys_collector");
		if (doneText == null) {
			doneText = "";
		}
		List<String> done = Arrays.asList(doneText.split(";"));
		for (String toy : neededToys) {
			if (!done.contains(toy)) {
				if (hash) {
					toy = "#" + toy;
				}
				result.add(toy);
			}
		}
		return result;
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Anna");

		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, 
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.hasQuest("toys_collector");
				}
			},
			ConversationStates.ATTENDING,
			"Mummy said, we are not allowed to talk to strangers. She is worried about that lost girl. But I'm bored. I want some #toys!",
			null);

		npc.add(ConversationStates.ATTENDING, "toys",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.hasQuest("toys_collector");
				}
			},
			ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					if (!player.isQuestCompleted("toys_collector")) {
						engine.say("I'm not sure what toys, but whatever would be fun for me to play with! Will you bring me some please?");
					} else { // to be honest i don't understand when this would be implemented. i put the text i want down in stage 3 and it works fine.
						engine.say("The toys are great! Thanks!");
						engine.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		// player says yes
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.IDLE, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					engine.say("Hooray! How exciting. See you soon.");
					player.setQuest("toys_collector", "");
				}
			});


		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.ATTENDING,
			"Oh ... you're mean.", null);
	}

	private void step_2() {
		// Just find some of the toys somewhere and bring them to Anna.
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Anna");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return player.hasQuest("toys_collector") && !player.isQuestCompleted("toys_collector");
				}
			},
			ConversationStates.ATTENDING, "Hello! I'm still bored. Did you bring me toys?", null);

		// player says he has a required toy with him
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_1, "What did you bring?!", null);

		for (String toy : neededToys) {
			npc.add(ConversationStates.QUESTION_1, toy, null, ConversationStates.QUESTION_1, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, String text, SpeakerNPC engine) {
					List<String> missing = missingToys(player, false);
					if (missing.contains(text)) {
						if (player.drop(text)) {
							// register toy as done
							String doneText = player.getQuest("toys_collector");
							player.setQuest("toys_collector", doneText + ";" + text);
							// check if the player has brought all toys
							missing = missingToys(player, true);
							if (missing.size() > 0) {
								engine.say("Thank you very much! What else did you bring?");
							} else {
								StackableItem pie = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("pie");
								pie.setQuantity(3);
								player.equip(pie, true);
								player.addXP(100);
								engine.say("These toys will keep me happy for ages! Please take these pies. Arlindo baked them for us but I think you should have them.");
								player.setQuest("toys_collector", "done");
								player.notifyWorldAboutChanges();
							}
						} else {
							engine.say("Hey! It's bad to lie! You don't have " + Grammar.a_noun(text) + " with you.");
						}
					} else {
						engine.say("I already have that toy!");
					}
				}
			});
		}
	
		npc.add(ConversationStates.QUESTION_1, "",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !neededToys.contains(text);
				}
			},
			ConversationStates.QUESTION_1, "That's not a good toy!", null);

		npc.add(ConversationStates.ATTENDING, "no",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.isQuestCompleted("toys_collector");
				}
			},
			ConversationStates.ATTENDING,
			"Then you should go away before I get in trouble for talking to you.", null);

		// player says he didn't bring any toys to different question
		npc.add(ConversationStates.QUESTION_1, "no",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return !player.isQuestCompleted("toys_collector");
				}
			},
			ConversationStates.ATTENDING, "Okay then. Come back later.", null);


		// player returns after finishing the quest
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, String text, SpeakerNPC engine) {
					return player.isQuestCompleted("toys_collector");
				}
			}, 
			ConversationStates.ATTENDING, "Hi! I'm busy playing with my toys, no grown ups allowed.", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
}
