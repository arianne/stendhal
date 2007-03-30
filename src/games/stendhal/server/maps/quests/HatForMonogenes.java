package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Hat For Monogenes
 * PARTICIPANTS:
 * - Monogenes, an old man in Semos city. 
 *
 * STEPS:
 * - Monogenes asks you to buy a hat for him.
 * - Xin Blanca sells you a leather_helmet.
 * - Monogenes sees your leather_helmet and asks for it and then thanks you.
 *
 * REWARD:
 * - 10 XP
 *
 * REPETITIONS:
 * - None.
 */
public class HatForMonogenes extends AbstractQuest {
	private static final String QUEST_SLOT = "hat_monogenes";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public List<String> getHistory(Player player) {
		List<String> res = new ArrayList<String>();
		if (player.hasQuest("Monogenes")) {
			res.add("FIRST_CHAT");
		}
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("GET_HAT");
		if (player.isEquipped("leather_hat") || player.isQuestCompleted(QUEST_SLOT)) {
			res.add("GOT_HAT");
		}
		if (player.isQuestCompleted(QUEST_SLOT)) {
			res.add("DONE");
		}
		return res;
	}

	private void createRequestingStep() {
		SpeakerNPC monogenes = npcs.get("Monogenes");

		monogenes.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (!player.isQuestCompleted("hat_monogenes")) {
							npc.say("Could you bring me a #hat to cover my bald head? Brrrrr! The days here in Semos are really getting colder...");
						} else {
							npc.say("Thanks for the offer, good friend, but this hat will last me five winters at least, and it's not like I need more than one.");
							npc.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		monogenes.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thanks, my good friend. I'll be waiting here for your return!",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						player.setQuest("hat_monogenes", "start");
					}
				});

		monogenes.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"You surely have more importants things to do, and little time to do them in. I'll just stay here and freeze to death, I guess... *sniff*",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						player.setQuest("hat_monogenes", "rejected");
					}
				});

		monogenes.add(ConversationStates.QUEST_OFFERED,
				"hat",
				null,
				ConversationStates.QUEST_OFFERED,
				"You don't know what a hat is?! Anything light that can cover my head; like leather, for instance. Now, will you do it?",
				null);
	}

	private void createBringingStep() {
		SpeakerNPC monogenes = npcs.get("Monogenes");

		monogenes.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.hasQuest("hat_monogenes")
								&& player.getQuest("hat_monogenes").equals("start");
					}
				},
				ConversationStates.QUEST_ITEM_BROUGHT,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						if (player.isEquipped("leather_helmet")) {
							npc.say("Hey! Is that leather hat for me?");
						} else {
							npc.say("Hey, my good friend, remember that leather hat I asked you about before? It's still pretty chilly here...");
							npc.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		monogenes.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				// make sure the player isn't cheating by putting the helmet
				// away and then saying "yes"
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, String text, SpeakerNPC npc) {
						return player.isEquipped("leather_helmet");
					}
				}, 
				ConversationStates.ATTENDING,
				"Bless you, my good friend! Now my head will stay nice and warm.",
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						player.drop("leather_helmet");

						player.addXP(10);
						player.notifyWorldAboutChanges();
						player.setQuest("hat_monogenes", "done");
					}
				});

		monogenes.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"I guess someone more fortunate will get his hat today... *sneeze*",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		createRequestingStep();
		createBringingStep();
	}
}
