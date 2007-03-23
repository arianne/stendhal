package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;

/**
 * QUEST: Campfire
 * 
 * PARTICIPANTS:
 * - Sally, a scout sitting next to a campfire near Or'rill
 * 
 * STEPS:
 * - Sally asks you for wood for her campfire
 * - You collect 10 pieces of wood in the forest
 * - You give the wood to Sally.
 * - Katinka gives you 10 meat or ham in return.
 * 
 * REWARD:
 * - 10 meat or ham
 * - 50 XP
 * 
 * REPETITIONS:
 * - Unlimited, but 1000 turns (ca. 5 minutes) of waiting are required
 *   between repetitions
 */
public class Campfire extends AbstractQuest {

	private static final int REQUIRED_WOOD = 10;

	private static final String QUEST_SLOT = "campfire";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public boolean isCompleted(Player player) {
		return player.hasQuest(QUEST_SLOT) && !player.getQuest(QUEST_SLOT).equals("start");
	}

	@Override
	public boolean isRepeatable(Player player) {
		return true;
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
		if ((player.isEquipped("wood", REQUIRED_WOOD)) || isCompleted(player)) {
			res.add("FOUND_ITEM");
		}
		if (isCompleted(player)) {
			res.add("DONE");
		}
		return res;
	}

	private boolean canStartQuestNow(SpeakerNPC npc, Player player) {
		if (!player.hasQuest(QUEST_SLOT)) {
			return true;
		} else if (player.getQuest(QUEST_SLOT).equals("start")) {
			return false;
		} else {
			int turnWhenLastBroughtWood;
			try {
				turnWhenLastBroughtWood = Integer.parseInt(player.getQuest(QUEST_SLOT));
			} catch (NumberFormatException e) {
				// compatibility: Old Stendhal version stored "done" on completed quest
				return true;
			}
			int turnsSinceLastBroughtWood = StendhalRPRuleProcessor.get().getTurn() - turnWhenLastBroughtWood;
			if (turnsSinceLastBroughtWood < 0) {
				// The server was restarted since last doing the quest.
				// Make sure the player can repeat the quest.
				turnsSinceLastBroughtWood = 0;
				player.setQuest(QUEST_SLOT, "0");
			}
			return turnsSinceLastBroughtWood >= 1000;
		}
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Sally");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, null, ConversationStates.ATTENDING,
		        null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (canStartQuestNow(engine, player)) {
					        engine.say("Hi! Could you do me a #favor?");
				        } else if (player.getQuest(QUEST_SLOT).equals("start")) {
					        if (player.isEquipped("wood", REQUIRED_WOOD)) {
						        engine
						                .say("Hi again! You've got wood, I see; do you have those 10 pieces of wood I asked about earlier?");
						        engine.setCurrentState(ConversationStates.QUEST_ITEM_BROUGHT);
					        } else {
						        engine
						                .say("You're back already? Don't forget that you promised to collect ten pieces of wood for me!");
					        }
				        } else {
					        engine
					                .say("Oh, I still have plenty of wood from the last time you helped me. Thank you for helping!");
				        }
			        }
		        });

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
		        ConversationStates.QUEST_OFFERED, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (canStartQuestNow(engine, player)) {
					        engine
					                .say("I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.");
				        } else if (player.getQuest(QUEST_SLOT).equals("start")) {
					        engine.say("You already promised me to bring me some wood! Ten pieces, remember?");
				        } else {
					        engine.say("I don't need any more wood at the moment, but thanks for asking.");
					        engine.setCurrentState(ConversationStates.ATTENDING);
				        }
			        }
		        });

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.ATTENDING,
		        "Okay. You can find wood in the forest north of here. Come back when you get ten pieces of wood!",
		        new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        player.setQuest(QUEST_SLOT, "start");
			        }
		        });

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED, "no", null, ConversationStates.ATTENDING,
		        "Oh dear, how am I going to cook all this meat? Perhaps I'll just have to feed it to the animals...",
		        null);
	}

	private void step_2() {
		// Just find the wood somewhere. It isn't a quest
	}

	private void step_3() {
		SpeakerNPC npc = npcs.get("Sally");

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC engine) {
				        if (player.drop("wood", REQUIRED_WOOD)) {
					        player.setQuest(QUEST_SLOT, Integer.toString(StendhalRPRuleProcessor.get().getTurn()));
					        player.addXP(50);

					        String rewardClass;
					        if (Rand.throwCoin() == 1) {
						        rewardClass = "meat";
					        } else {
						        rewardClass = "ham";
					        }
					        engine.say("Thank you! Here, take some " + rewardClass + "!");
					        EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
					        StackableItem reward = (StackableItem) manager.getItem(rewardClass);
					        reward.setQuantity(REQUIRED_WOOD);
					        player.equip(reward, true);
					        player.notifyWorldAboutChanges();
				        } else {
					        engine.say("Hey! Where did you put the wood?");
				        }
			        }
		        });

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT, "no", null, ConversationStates.ATTENDING,
		        "Oh... well, I hope you find some quickly; this fire's going to burn out soon!", null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
	}
}