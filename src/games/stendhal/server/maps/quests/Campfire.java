package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * QUEST: Campfire
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Sally, a scout sitting next to a campfire near Or'ril</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Sally asks you for wood for her campfire</li>
 * <li> You collect 10 pieces of wood in the forest</li>
 * <li> You give the wood to Sally.</li>
 * <li> Sally gives you 10 meat or ham in return.<li>
 * </ul>
 * 
 * REWARD:
 * <ul> 
 * <li> 10 meat or ham</li>
 * <li> 50 XP</li>
 * <li> Karma: 10</li>
 * </ul>
 * 
 * REPETITIONS: 
 * <ul>
 * <li> Unlimited, but 5 minutes of waiting are required between repetitions</li>
 * </ul>
 */
public class Campfire extends AbstractQuest {

	private static final int REQUIRED_WOOD = 10;
	
	private static final int REQUIRED_MINUTES = 5;

	private static final String QUEST_SLOT = "campfire";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT) && !"start".equals(player.getQuest(QUEST_SLOT)) && !"rejected".equals(player.getQuest(QUEST_SLOT));
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
		res.add("I have met Sally");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Sally");
			return res;
		}
		res.add("I do want to help Sally");
		if ((player.isEquipped("wood", REQUIRED_WOOD)) || isCompleted(player)) {
			res.add("I have found the 10 wood needed to start the fire");
		}
		if (isCompleted(player)) {
			res.add("I have given Sally the wood. She gave me some food in return. I also gained 50 xp");
		}
		return res;
	}

	private void respondToQuestMessage(final SpeakerNPC npc, final Player player) {
		// first time player asks 
		if (!player.hasQuest(QUEST_SLOT)) {
			npc.say("I need more wood to keep my campfire running, But I can't leave it unattended to go get some! Could you please get some from the forest for me? I need ten pieces.");
			return;
		} else {
			// quest can't be in state 'start' by condition. so it must be a number (the previous time) or 'rejected' or something else
		   long lastTime; 
			try {
 				lastTime = Long.parseLong(player.getQuest(QUEST_SLOT));
			} catch (final NumberFormatException e) {
				// it wasn't a number.
 				// compatibility: Old Stendhal version stored "done" on
 				// completed quest or state might be 'rejected'
				npc.say("My campfire needs wood again! Could you please get some from the forest for me? I need ten pieces.");
 				return;
 			}

		   
		   final long delay = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
		   
		   final long timeRemaining = (lastTime + delay) - System.currentTimeMillis();
		   
		   if (timeRemaining < 0) {
			   // it's been at least 5 minutes and sally wants wood again
			   npc.say("My campfire needs wood again! Could you please get some from the forest for me? I need ten pieces.");
		   return;
		   } else {
			   // sally's not ready for wood yet
			   npc.say("Thanks, but I think the wood you brought me already will last me " + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000)) + " more.");
			   npc.setCurrentState(ConversationStates.ATTENDING);
		   return;
		   }
		}
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Sally");

		// player returns with the promised wood
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("wood", REQUIRED_WOOD)),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Hi again! You've got wood, I see; do you have those 10 pieces of wood I asked about earlier?",
			null);

		//player returns without promised wood
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("wood", REQUIRED_WOOD))),
			ConversationStates.ATTENDING, 
			"You're back already? Don't forget that you promised to collect ten pieces of wood for me!",
			null);

		// first chat of player with sally
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, "Hi! I need a little #favor ... ",
			null);

		// player who is rejected or 'done' but waiting to start again, returns
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Hi again!", 
			null);
		
		// if they ask for quest while on it, remind them
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"You already promised me to bring me some wood! Ten pieces, remember?",
			null);
		
		// if they ask for quest while not already supposed to be collecting wood, deal with it correctly
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.QUEST_OFFERED, null,
			new ChatAction() {
			   	public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					respondToQuestMessage(npc, player);
				}
			});

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Okay. You can find wood in the forest north of here. Come back when you get ten pieces of wood!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5.0));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh dear, how am I going to cook all this meat? Perhaps I'll just have to feed it to the animals...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Sally");
		// player has wood and tells sally, yes, it is for her
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new PlayerHasItemWithHimCondition("wood", REQUIRED_WOOD),
			ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						player.drop("wood", REQUIRED_WOOD);
						player.setQuest(QUEST_SLOT,  "" + System.currentTimeMillis());
						player.addXP(50);

						String rewardClass;
						if (Rand.throwCoin() == 1) {
							rewardClass = "meat";
						} else {
							rewardClass = "ham";
						}
						npc.say("Thank you! Here, take some " + rewardClass + "!");
						final StackableItem reward = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
						reward.setQuantity(REQUIRED_WOOD);
						player.equipOrPutOnGround(reward);
						player.addKarma(10);
						player.notifyWorldAboutChanges();
					}
				});

		//player said the wood was for her but has dropped it from his bag or hands
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new NotCondition(new PlayerHasItemWithHimCondition("wood", REQUIRED_WOOD)),
			ConversationStates.ATTENDING, 
			"Hey! Where did you put the wood?",
			null);

		// player had wood but said it is not for sally
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh... well, I hope you find some quickly; this fire's going to burn out soon!",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Campfire", 
				"Sally wants to build a campfire, but she doesn't have any wood.", 
				false);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getName() {
		return "Campfire";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}
}
