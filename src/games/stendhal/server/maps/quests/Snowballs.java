package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
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
 * <li> Mr. Yeti asks for some snow, and wants you to get 25 snowballs.
 * <li> You collect 25 snowballs from ice golems.
 * <li> You give the snowballs to Mr. Yeti.
 * <li> Mr. Yeti gives you 20 cod or perch.
 * <p>
 * REWARD: <li> 20 cod or perch <li> 500 XP <li> 20 karma in total
 * <p>
 * REPETITIONS: <li> Unlimited, but 12 hours of waiting is
 * required between repetitions
 */
 
public class Snowballs extends AbstractQuest {

	private static final int REQUIRED_SNOWBALLS = 25;
	
	private static final int REQUIRED_MINUTES = 120;

	private static final String QUEST_SLOT = "snowballs";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT)
				&& !player.getQuest(QUEST_SLOT).equals("start");
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return true;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
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

	private boolean canStartQuestNow(final Player player) {
		if (!player.hasQuest(QUEST_SLOT)) {
			return true;
		} else if (player.getQuest(QUEST_SLOT).equals("start")) {
			return false;
		} else {
			final long timeRemaining = calculateRemainingTime(player);
			if (timeRemaining < 0) {
				player.setQuest(QUEST_SLOT, "0");
				return true;
			} else {
				return false;
			}
		}
	}

	private long calculateRemainingTime(final Player player) {
		final String lasttime = player.getQuest(QUEST_SLOT);
		final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
		final long timeRemaining = (MathHelper.parseLongDefault(lasttime, 0) + delay) - System.currentTimeMillis();
		return timeRemaining;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Mr. Yeti");

		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS)),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?",
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
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (canStartQuestNow(player)) {
						npc.say("Greetings stranger! Have you seen my snow sculptures? Could you do me a #favor?");
					} else {
						int seconds = (int) (calculateRemainingTime(player) / 1000);
						npc.say("I have enough snow for my new sculpture. Thank you for helping! " 
								+ "I might start a new one in " + TimeUtil.approxTimeUntil(seconds));
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
			new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					if (canStartQuestNow(player)) {
						npc.say("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.");
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
			"Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"So what are you doing here? Go away!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Mr. Yeti");
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new PlayerHasItemWithHimCondition("snowball", REQUIRED_SNOWBALLS),
			ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						player.drop("snowball", REQUIRED_SNOWBALLS);
						player.setQuest(QUEST_SLOT, "" + System.currentTimeMillis());
						player.addXP(500);
						player.addKarma(15);

						String rewardClass;
						if (Rand.throwCoin() == 1) {
							rewardClass = "cod";
						} else {
							rewardClass = "perch";
						}
						npc.say("Thank you! Here, take some " + rewardClass + "! I do not like to eat them.");
						final StackableItem reward = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
						reward.setQuantity(20);
						player.equipOrPutOnGround(reward);
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
			"Oh I hope you bring me them soon! I would like to finish my sculpture!",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getName() {
		return "Snowballs";
	}
	
	// the djinns, ice golems and ice elementals on the way to yeti caves are quite dangerous
	@Override
	public int getMinLevel() {
		return 60;
	}
	
}
