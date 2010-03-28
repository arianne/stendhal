package games.stendhal.server.maps.quests;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.List;

/**
 * QUEST: The Amazon Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Princess Esclara, the Amazon Princess in a Hut on Amazon Island</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for an exotic drink</li>
 * <li>Find someone who serves exotic drinks</li>
 * <li>Take exotic drink back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +25 in all</li>
 * <li>Some fish pie, random between 2 and 7.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it once an hour.</li>
 * </ul>
 */
public class AmazonPrincess extends AbstractQuest {

	private static final String QUEST_SLOT = "amazon_princess";

	// The delay between repeating quests is 60 minutes
	private static final int REQUIRED_MINUTES = 60;
	private static final List<String> triggers = Arrays.asList("drink", "pina colada", "cocktail", "cheers", "pina");


	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Princess Esclara");
npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I'm looking for a drink, should be an exotic one. Can you bring me one?",
				null);
npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		new QuestCompletedCondition(QUEST_SLOT),
		ConversationStates.ATTENDING,
		"I'm drunken now thank you!",
		null);

npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		new QuestStateStartsWithCondition(QUEST_SLOT, "drinking;"),
		ConversationStates.ATTENDING,
		null,
		new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						final String[] tokens = player.getQuest(QUEST_SLOT).split(";");
						// tokens[0]=="drinking" see condition
						long timeWhenQuestWasDone = Long.parseLong(tokens[1]);
						
						final long delay = REQUIRED_MINUTES	* MathHelper.MILLISECONDS_IN_ONE_MINUTE;
						// if this is > 0, she's still drunk!
						final long timeRemaining = (timeWhenQuestWasDone + delay)
								- System.currentTimeMillis();
						if (timeRemaining > 0) {
							String timeWhenSober = TimeUtil.timeUntil((int) (timeRemaining / 1000));
							npc.say("I'm sure I'll be too drunk to have another for at least "
											+ timeWhenSober
											+ "!");
							return;
							
						}
						// She has recovered and is ready for another
						npc.say("The last cocktail you brought me was so lovely. Will you bring me another?");
						npc.setCurrentState(ConversationStates.QUEST_OFFERED);
					}

				});
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING,
				"I like these exotic drinks, I forget the name of my favourite one.",
				null);

// Player agrees to get the drink
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, never mind. Bye then.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	/*
	 * Get Drink Step :
	 * src/games/stendhal/server/maps/athor/cocktail_bar/BarmanNPC.java he
	 * serves drinks to all, not just those with the quest
	 */
	private void bringCocktailStep() {
		final SpeakerNPC npc = npcs.get("Princess Esclara");
		npc.add(
			ConversationStates.ATTENDING, triggers,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("pina colada")),
			ConversationStates.ATTENDING, 
			null,
			new MultipleActions(
						new DropItemAction("pina colada"), 
						new ChatAction() {
							public void fire(final Player player,
									final Sentence sentence,
									final SpeakerNPC npc) {
								int pieAmount = Rand.roll1D6() + 1;
								new EquipItemAction("fish pie", pieAmount, true).fire(player, sentence, npc);
								npc.say("Thank you!! Take these "
												+ pieAmount
												+ " fish pies from my cook, and this kiss, from me.");
								new SetQuestAndModifyKarmaAction(getSlotName(), "drinking;" 
																 + System.currentTimeMillis(), 15.0).fire(player, sentence, npc);

							}
						}));

		npc.add(
			ConversationStates.ATTENDING, triggers,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("pina colada"))),
			ConversationStates.ATTENDING,
			"You don't have any drink I like yet. Go, and you better get an exotic one!",
			null);

		npc.add(
			ConversationStates.ATTENDING, triggers,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"Sometime you could do me a #favour ...", null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		offerQuestStep();
		bringCocktailStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AmazonPrincess";
	}
	
	// Amazon is dangerous below this level - don't hint to go there
	@Override
	public int getMinLevel() {
		return 70;
	}
}
