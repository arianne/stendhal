package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Snowballs
 * <p>
 * PARTICIPANTS:
 * <li> Mr. Yeti, a creature in a dungeon needs help
 * <p>
 * STEPS:
 * <li> Mr. Yeti ask for some snow, and wants you to get 25 snowballs.
 * <li> You collect 25 snowballs from ice golems.
 * <li> You give the snowballs to Mr. Yeti.
 * <li> Mr. Yeti gives you 20 cod or perch.
 * <p>
 * REWARD: <li> 20 cod or perch <li> 500 XP
 * <p>
 * REPETITIONS: <li> Unlimited, but 12960 turns (should be 12 hours) of waiting are
 * required between repetitions
 */
 
public class Snowballs extends AbstractQuest {

	private static final int REQUIRED_SNOWBALLS = 25;
	
	private static final int REQUIRED_MINUTES = 120;

	private static final String QUEST_SLOT = "snowballs";

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	@Override
	public boolean isCompleted(Player player) {
		return player.hasQuest(QUEST_SLOT)
				&& !player.getQuest(QUEST_SLOT).equals("start");
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
		if ((player.isEquipped("snowball", REQUIRED_SNOWBALLS)) || isCompleted(player)) {
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
			String lasttime = player.getQuest(QUEST_SLOT);
		   
		   long delay = REQUIRED_MINUTES * 60 * 1000;
		   
		   long timeRemaining = (Long.parseLong(lasttime) + delay) - System.currentTimeMillis();
		   
		   if (timeRemaining < 0) {
		   player.setQuest(QUEST_SLOT, "0");
		   return true;
		   } else {
		   return false;
		   }
		}
	}

	private void prepareRequestingStep() {
		SpeakerNPC npc = npcs.get("Mr. Yeti");

		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS)),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Greetings stranger! I see you have the snow i asked for. Are these snowballs for me?",
			null);

		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS))),
			ConversationStates.ATTENDING, 
			"You're back already? Don't forget that you promised to collect a bunch of snowballs for me!",
			null);

		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if (canStartQuestNow(npc, player)) {
						npc.say("Greetings stranger! Have you seen my snow sculptures? Could you do me a #favor?");
					} else {
						// TODO: say how many minutes are left.
						npc.say("I have enough snow for my new sculpture. Thank you for helping!");
					}
				}
			});

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.QUEST_OFFERED,
			"You already promised me to bring some snowballs! Twentyfive pieces, remember?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.QUEST_OFFERED, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if (canStartQuestNow(npc, player)) {
						npc.say("I like to make some snow sculptures but the snow in this cavern is not good enough. Would you help me and get some snowballs for me? I need twentyfive of them.");
					} else {
						npc.say("I have enough snow to finish my sculpture, but thanks for asking.");
						npc.setCurrentState(ConversationStates.ATTENDING);
					}
				}
			});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Fine. You can Get the snowballs from the ice golem in this cavern, but be careful there is something huge near them! Come back when you get twentyfive snowballs!",
			new SetQuestAction(QUEST_SLOT, "start"));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"So what are you doing her? Go away!",
			null);
	}

	private void prepareBringingStep() {
		SpeakerNPC npc = npcs.get("Mr. Yeti");
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS),
			ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						player.drop("snowball", REQUIRED_SNOWBALLS);
						player.setQuest(QUEST_SLOT, "" + System.currentTimeMillis());
						player.addXP(500);

						String rewardClass;
						if (Rand.throwCoin() == 1) {
							rewardClass = "cod";
						} else {
							rewardClass = "perch";
						}
						npc.say("Thank you! Here, take some " + rewardClass + "! I do not like to eat them,");
						EntityManager manager = StendhalRPWorld.get()
								.getRuleManager().getEntityManager();
						StackableItem reward = (StackableItem) manager.getItem(rewardClass);
						reward.setQuantity(20);
						player.equip(reward, true);
						player.notifyWorldAboutChanges();
					}
				});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new NotCondition(new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS)),
			ConversationStates.ATTENDING, 
			"Hey! Where did you put the snowballs?",
			null);

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh i hope you bring me them soon! I like to finish my sculpture!",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareRequestingStep();
		prepareBringingStep();
	}
}
