package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.ItemTools;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.TriggerList;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Cloak Collector 2
 * <p>
 * PARTICIPANTS: - Josephine, a young woman who live in Fado
 * <p>
 * STEPS:
 * <ul>
 * <li> Josephine asks you to bring her a cloak in colours she didn't get already from you
 * <li> You bring cloaks to Josephine 
 * <li> Repeat until Josephine
 * received all cloaks. (Of course you can bring several cloaks at the same
 * time.) 
 * <li> Josephine gives you a reward
 * </ul>
 * <p>
 * REWARD: - 105 Karma in all - 100000 XP - scent (when ready)
 * <p>
 * REPETITIONS: - None.
 */
public class CloakCollector2 extends AbstractQuest {

    private static final List<String> NEEDEDCLOAKS2 = Arrays.asList("red cloak", "shadow cloak", "xeno cloak",
								       "elvish cloak", "chaos cloak", "mainio cloak",
								       "golden cloak", "black dragon cloak");
    private static final String OLD_QUEST = "cloaks_collector";
    private static final String QUEST_SLOT = "cloaks_collector_2";   
    
    @Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	/**
	 * Returns a list of the names of all cloaks that the given player still has
	 * to bring to fulfill the quest.
	 *
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of cloak names
	 */
	private List<String> missingcloaks2(final Player player, final boolean hash) {

		String doneText2 = player.getQuest(QUEST_SLOT);
		final List<String> neededCopy2 = new LinkedList<String>(NEEDEDCLOAKS2);

		if (doneText2 == null) {
			doneText2 = "";
		}
		final List<String> done2 = Arrays.asList(doneText2.split(";"));
		neededCopy2.removeAll(done2);
		if (hash) {
			final List<String> result2 = new LinkedList<String>();
			for (final String cloak : neededCopy2) {
				result2.add("#" + cloak);
			}
			return result2;
		}

		return neededCopy2;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Josephine");

		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
						return !player.hasQuest(QUEST_SLOT) && player.isQuestCompleted(OLD_QUEST);
					}
				},
				ConversationStates.QUEST_2_OFFERED,
				"Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...",
				null);

		// player asks what cloaks are needed
		npc.add(ConversationStates.QUEST_2_OFFERED, 
				"collection", 
				null,
				ConversationStates.QUEST_2_OFFERED, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						final List<String> needed2 = missingcloaks2(player, true);
						entity.say("It's missing "
								+ Grammar.quantityplnoun(needed2.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed2)
								+ ". Will you find them?");
					}

					@Override
					public String toString() {
						return "list missingcloaks2";
					}
				});
		// player says yes
		npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.IDLE, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						entity.say("Brilliant! I'm all excited again! Bye!");
						player.setQuest(QUEST_SLOT, "");
						player.addKarma(5.0);
					}

					@Override
					public String toString() {
						return "answer offer2";
					}
				});

		// player is not willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.QUEST_2_OFFERED, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						entity.say("Oh ... you're not very friendly. Please say yes?");
						player.addKarma(-5.0);
					}

					@Override
					public String toString() {
						return "answer refuse2";
					}
				});

		// player asks about an individual cloak. We used the trick before that all cloaks were named by colour 
		// (their subclass) - so she would tell them what colour it was. In this case it fails for elvish,
		// xeno and shadow which are not named by colour. So, this time she'll say, e.g.
		// It's a shadow cloak, sorry if that's not much help, so will you find them all?
		// rather than say for elf cloak she'd said 'It's a white cloak, so will you find them all?'
		// it will still work for red (red_spotted is the subclass), black dragon (black), 
		// golden, mainio (primary coloured), chaos (multicoloured).
		npc.add(ConversationStates.QUEST_2_OFFERED, 
				NEEDEDCLOAKS2, 
				null,
				ConversationStates.QUEST_2_OFFERED, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						final String itemName = sentence.getTriggerExpression().getNormalized();
						final Item item = SingletonRepository.getEntityManager().getItem(itemName);
						StringBuilder stringBuilder = new StringBuilder();
						stringBuilder.append("You haven't seen one before? Well, it's a ");
						if (item == null) {
							stringBuilder.append(itemName);
						} else {
							stringBuilder.append(ItemTools.itemNameToDisplayName(item.getItemSubclass()));
						}
						
						stringBuilder.append(". Sorry if that's not much help, it's all I know! So, will you find them all?");
						entity.say(stringBuilder.toString());
					}

					@Override
					public String toString() {
						return "describe item";
					}
		});
	}

	private void step_2() {
		// Just find the cloaks and bring them to Josephine.
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Josephine");

		// player returns while quest is still active
		npc.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_2,
				"Welcome back! Have you brought any #cloaks with you?", null);
		// player asks what exactly is missing
		npc.add(ConversationStates.QUESTION_2, 
				"cloaks", 
				null,
				ConversationStates.QUESTION_2, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						final List<String> needed2 = missingcloaks2(player, true);
						entity.say("I want "
								+ Grammar.quantityplnoun(needed2.size(), "cloak")
								+ ". That's "
								+ Grammar.enumerateCollection(needed2)
								+ ". Did you bring any?");
					}

					@Override
					public String toString() {
						return "enumerate missingcloaks2";
					}
				});
		// player says he has a required cloak with him
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.QUESTION_2,
				"Woo! What #cloaks did you bring?", 
				null);

		npc.add(ConversationStates.QUESTION_2, 
				NEEDEDCLOAKS2, 
				null,
				ConversationStates.QUESTION_2, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC entity) {
						TriggerList missing = new TriggerList(missingcloaks2(player, false));
						final Expression item = sentence.getTriggerExpression();

						final Expression found = missing.find(item);
						if (found != null) {
							final String itemName = found.getOriginal();

							if (player.drop(itemName)) {
								// register cloak as done
								final String doneText = player.getQuest(QUEST_SLOT);
								player.setQuest(QUEST_SLOT, doneText + ";" + itemName);

								// check if the player has brought all cloaks
								missing = new TriggerList(missingcloaks2(player, true));

								if (missing.isEmpty()) {
									rewardPlayer(player);
									// TODO: Make speech mention scent reward if applicable.
									entity.say("Oh, yay! You're so kind, I bet you'll have great Karma now! Here, take these killer boots. I think they're gorgeous but they don't fit me!");
									player.setQuest(QUEST_SLOT, "done;rewarded");
									final Item boots = SingletonRepository.getEntityManager().getItem("killer boots");
									boots.setBoundTo(player.getName());
									player.equipOrPutOnGround(boots);
									player.notifyWorldAboutChanges();
									entity.setCurrentState(ConversationStates.ATTENDING);
								} else {
									entity.say("Wow, thank you! What else did you bring?");
								}
							} else {
								entity.say("Oh, I'm disappointed. You don't really have "
												+ Grammar.a_noun(itemName)
												+ " with you.");
							}
						} else {
							entity.say("You're terribly forgetful, you already brought that one to me.");
						}
					}

					@Override
					public String toString() {
						return "answer neededcloaks2";
					}
		});

		npc.add(ConversationStates.ATTENDING, 
				ConversationPhrases.NO_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
						return !player.isQuestCompleted(QUEST_SLOT);
					}
				}, 
				ConversationStates.ATTENDING,
				"Ok. If you want help, just say.", 
				null);

		// player says he didn't bring any cloaks to different question
		npc.add(ConversationStates.QUESTION_2, 
				ConversationPhrases.NO_MESSAGES,
				new ChatCondition() {
					public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
						return !player.isQuestCompleted(QUEST_SLOT);
					}
				}, ConversationStates.ATTENDING, "Okay then. Come back later.",
				null);

		// player returns after finishing the quest but not rewarded
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "done"), 
				ConversationStates.ATTENDING,
				"Oh! I didnt' reward you for helping me again! Here, take this boots. I think they're gorgeous but they don't fit me :(", 
				new MultipleActions(new EquipItemAction("killer boots", 1, true), new SetQuestAction(QUEST_SLOT, "done;rewarded")));
	
		//		 player returns after finishing the quest and was rewarded
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "done;rewarded"), 
				ConversationStates.ATTENDING,
				"Thanks again for helping me! The cloaks look great!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
	private static void rewardPlayer(final Player player) {
	    //  TODO: Once scent is done, add this as reward. Note it might be stackable, so check! 
	    //	Item scent = StendhalRPWorld.get()
	    //			.getRuleManager()
	    //			.getEntityManager().getItem("scent");
	    //	scent.setBoundTo(player.getName());
	    //	player.equip(scent, true);
		player.addKarma(100.0);
		player.addXP(100000);
      	}
	@Override
	public String getName() {
		return "CloakCollector2";
	}
	
	// The previous quest likely requires at least this level. 
	// When adding the hint check, remember to check if the CloakCollector quest is completed.
	@Override
	public int getMinLevel() {
		return 100;
	}
}
