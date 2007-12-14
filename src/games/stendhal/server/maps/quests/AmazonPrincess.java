package games.stendhal.server.maps.quests;

import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
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
	private static final List<String> triggers = Arrays.asList("drink",
			"pina_colada", "cocktail", "cheers", "pina");

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void offerQuestStep() {
		SpeakerNPC npc = npcs.get("Princess Esclara");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						if (!player.hasQuest(QUEST_SLOT)
								|| player.getQuest(QUEST_SLOT).equals(
										"rejected")) {
							npc.say("I'm looking for a drink, should be an exotic one. Can you bring me one?");
							npc.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else if (player.isQuestCompleted(QUEST_SLOT)) { // shouldn't
							// happen
							npc.say("I'm drunken now thank you!");
						} else if (player.getQuest(QUEST_SLOT).startsWith(
								"drinking;")) {
							// She is still drunk from her previous pina colada,
							// she doesn't want another yet
							String[] tokens = player.getQuest(QUEST_SLOT).split(
									";"); // this splits the time from the
							// word drinking
							// tokens now is like an array with 'drinking' in
							// tokens[0] and
							// the time is in tokens[1]. so we use just
							// tokens[1]

							long delay = REQUIRED_MINUTES * 60 * 1000; // minutes
							// ->
							// milliseconds
							// timeRemaining is ''time when quest was done +
							// delay - time now''
							// if this is > 0, she's still drunk!
							long timeRemaining = (Long.parseLong(tokens[1]) + delay)
									- System.currentTimeMillis();
							if (timeRemaining > 0) {
								npc.say("I'm sure I'll be too drunk to have another for at least "
										+ TimeUtil.timeUntil((int) (timeRemaining / 1000))
										+ "!");
								return;
								// note: it is also possible to make the npc
								// say an approx time but this sounded wrong
								// with the 'at least'
							}
							// She has recovered and is ready for another
							npc.say("The last cocktail you brought me was so lovely. Will you bring me another?");
							npc.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else {
							npc.say("I like these exotic drinks, I forget the name of my favourite one.");
						}
					}
				});
		// Player agrees to get the drink
		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 10.0));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, never mind. Bye then.", new SetQuestAndModifyKarmaAction(
						QUEST_SLOT, "start", 10.0));
	}

	/*
	 * Get Drink Step :
	 * src/games/stendhal/server/maps/athor/cocktail_bar/BarmanNPC.java he
	 * serves drinks to all, not just those with the quest
	 */
	private void bringCocktailStep() {
		SpeakerNPC npc = npcs.get("Princess Esclara");
		npc.add(ConversationStates.ATTENDING, triggers, new AndCondition(
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new PlayerHasItemWithHimCondition("pina_colada")),
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						player.drop("pina_colada");
						player.addKarma(15);
						StackableItem fishpies = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
								"fish_pie");
						int pieamount;
						// make it from 2 to 7 just to avoid dealing with
						// grammear of pie/pies
						pieamount = Rand.roll1D6() + 1;
						fishpies.setQuantity(pieamount);
						player.equip(fishpies, true);
						npc.say("Thank you!! Take these "
								+ Integer.toString(pieamount)
								+ " fish pies from my cook, and this kiss, from me.");
						// We set the slot to start with 'drinking'
						// and to also store the current time, split with a ';'
						player.setQuest(QUEST_SLOT, "drinking;"
								+ System.currentTimeMillis());
					}
				});

		npc.add(
				ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(new PlayerHasItemWithHimCondition(
								"pina_colada"))),
				ConversationStates.ATTENDING,
				"You don't have any drink I like yet. Go, and you better get an exotic one!",
				null);

		npc.add(ConversationStates.ATTENDING, triggers,
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

}
