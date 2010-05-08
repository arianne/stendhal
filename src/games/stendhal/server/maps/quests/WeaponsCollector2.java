package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.TriggerList;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: The Weapons Collector Part 2
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Balduin, a hermit living on a mountain between Semos and Ados
 * </ul>
 * <p>
 * STEPS:
 * <ul>
 * <li> Balduin asks you for some new weapons.
 * <li> You get one of the weapons somehow, e.g. by killing a monster.
 * <li> You bring the weapon up the mountain and give it to Balduin.
 * <li> Repeat until Balduin received all weapons. (Of course you can bring up
 * several weapons at the same time.)
 * <li> Balduin gives you a pair of swords in exchange.
 * </ul>
 * REWARD:
 * <ul>
 * <li> rhand sword and lhand sword
 * <li> 3000 XP
 * </ul>
 * REPETITIONS:
 * <ul>
 * <li> None.
 * </ul>
 */
public class WeaponsCollector2 extends AbstractQuest {

	private static final String QUEST_SLOT = "weapons_collector2";

	
	private static final List<String> neededWeapons = Arrays.asList(
			// fairly rare from glow_monster in haunted house
			"morning star",
			// rare from monk on mountain
			"staff", 
			// rare from devil_queen on mountain
			"great sword" 
	);

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	public List<String> getNeededItems() {
		return neededWeapons;
	}

	public SpeakerNPC getNPC() {
		return npcs.get("Balduin");
	}

	/**
	 * Returns a list of the names of all weapons that the given player still
	 * has to bring to fulfill the quest.
	 * 
	 * @param player
	 *            The player doing the quest
	 * @param hash
	 *            If true, sets a # character in front of every name
	 * @return A list of weapon names
	 */
	private List<String> missingWeapons(final Player player, final boolean hash) {
		final List<String> result = new LinkedList<String>();

		String doneText = player.getQuest(QUEST_SLOT);
		if (doneText == null) {
			doneText = "";
		}
		final List<String> done = Arrays.asList(doneText.split(";"));
		for (String weapon : neededWeapons) {
			if (!done.contains(weapon)) {
				if (hash) {
					weapon = "#" + weapon;
				}
				result.add(weapon);
			}
		}
		return result;
	}

	private void step_1() {
		final SpeakerNPC npc = getNPC();

		// player says hi before starting the quest
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestCompletedCondition("weapons_collector"), new QuestNotStartedCondition(QUEST_SLOT)),
			    ConversationStates.ATTENDING,
			    "Greetings, old friend. If you are willing, I have another #quest for you.",
			    null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestCompletedCondition("weapons_collector"), new QuestNotStartedCondition(QUEST_SLOT)),
				ConversationStates.QUEST_2_OFFERED, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
							if (player.isQuestCompleted(QUEST_SLOT)) {
								engine.say("My collection is now complete! Thanks again.");
								engine.setCurrentState(ConversationStates.ATTENDING);
							} else {
								engine.say("Recent adventurers to these parts describe strange new creatures with weapons I have never seen. "
										+ "Would you fight these creatures and bring their weapons to me?");
							}
						}
				});

		// player is willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						engine.say("Wonderful. Now, the #list is small but the risk may be great. "
								+ "If you return safely, I have another reward for you.");
						player.setQuest(QUEST_SLOT, "");
					}
				});

		// player is not willing to help
		npc.add(ConversationStates.QUEST_2_OFFERED,
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"Well, maybe someone else will happen by and help me.", 
				null);

		// player asks what exactly is missing
		npc.add(ConversationStates.ATTENDING, 
				"list", 
				new QuestActiveCondition(QUEST_SLOT), 
				ConversationStates.QUESTION_2, 
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final List<String> needed = missingWeapons(player, true);
						engine.say("There "
								+ Grammar.isare(needed.size())
								+ " "
								+ Grammar.quantityplnoun(needed.size(), "weapon")
								+ " still missing from my newest collection: "
								+ Grammar.enumerateCollection(needed)
								+ ". Do you have anything like that with you?");
					}
				});

		// player says he doesn't have required weapons with him
		npc.add(ConversationStates.QUESTION_2, 
				ConversationPhrases.NO_MESSAGES,
				null, 
				ConversationStates.IDLE, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final List<String> missing = missingWeapons(player, false);
						engine.say("Let me know as soon as you find "
								+ Grammar.itthem(missing.size())
								+ ". Farewell.");
					}
				});

		// player says he has a required weapon with him
		npc.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.QUESTION_2, 
				"What did you find?", 
				null);

		npc.add(ConversationStates.QUESTION_2, 
				neededWeapons, 
				null,
				ConversationStates.QUESTION_2, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
						final Expression item = sentence.getTriggerExpression();

						TriggerList missing = new TriggerList(missingWeapons(player, false));

						final Expression found = missing.find(item);
						if (found != null) {
							final String itemName = found.getOriginal();

							if (player.drop(itemName)) {
								// register weapon as done
								final String doneText = player.getQuest(QUEST_SLOT);
								player.setQuest(QUEST_SLOT, doneText + ";" + itemName);

								// check if the player has brought all weapons
								missing = new TriggerList(missingWeapons(player, true));

								if (missing.size() > 0) {
									engine.say("Thank you very much! Do you have anything more for me?");
								} else {
									final Item lhandsword = SingletonRepository.getEntityManager().getItem(
											"l hand sword");
									lhandsword.setBoundTo(player.getName());
									player.equipOrPutOnGround(lhandsword);
									final Item rhandsword = SingletonRepository.getEntityManager().getItem(
											"r hand sword");
									rhandsword.setBoundTo(player.getName());
									player.equipOrPutOnGround(rhandsword);
									player.addXP(3000);
									engine.say("At last, my collection is complete! Thank you very much; here, take this pair of swords in exchange!");
									player.setQuest(QUEST_SLOT, "done");
									player.notifyWorldAboutChanges();
									engine.setCurrentState(ConversationStates.ATTENDING);
								}
							} else {
								engine.say("I may be old, but I'm not senile, and you clearly don't have "
										+ Grammar.a_noun(itemName)
										+ ". What do you really have for me?");
							}
						} else {
							engine.say("I already have that one. Do you have any other weapon for me?");
						}
					}
				});
	}

	private void step_2() {
		// Just find some of the weapons somewhere and bring them to Balduin.
	}

	private void step_3() {
		final SpeakerNPC npc = getNPC();

		// player returns while quest is still active
		playerReturnsWhileQuestIsActive(npc);

		// player returns after finishing the quest
	//	playerReturnsAfterFinishingQuest(npc);
	}

	private void playerReturnsWhileQuestIsActive(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Welcome back. I hope you have come to help me with my latest #list of weapons.",
				null);
	}

/*	private void playerReturnsAfterFinishingQuest(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Welcome! Thanks again for extending my collection.",
				null);
	} */

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}

	@Override
	public String getName() {
		return "WeaponsCollector2";
	}
	
	// it can be a long quest so they can always start it before they can necessarily finish all
	@Override
	public int getMinLevel() {
		return 60;
	}
}
