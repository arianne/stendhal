package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;
import games.stendhal.server.rule.defaultruleset.DefaultCreature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Experienced warrior knowing a lot about creatures (location semos_plains_s)
 * Original name: Starkad
 *
 * @author johnnnny
 */
public class ExperiencedWarriorNPC extends SpeakerNPCFactory {

	/**
	 * cost of the information for players. Final cost is: INFORMATION_BASE_COST +
	 * creatureLevel * INFORMATION_COST_LEVEL_FACTOR
	 */
	static final int INFORMATION_BASE_COST = 30;

	/**
	 * multiplier of the creature level for the information cost.
	 */
	static final double INFORMATION_COST_LEVEL_FACTOR = 2;

	/**
	 * literals for probabilities. %s is replaced with item description (name
	 * and amount)
	 */
	static Map<Double, String> probabilityLiterals;

	/**
	 * literal for item amounts %s is replaced with singular item name, %a with
	 * "a/an item name" depending on the item name
	 */
	static Map<Integer, String> amountLiterals;

	/**
	 * literal for how dangerous a creature is based on the percentual
	 * difference to player level %s is replaced with singular creature name, %S
	 * with plural
	 */
	static Map<Double, String> dangerLiterals;

	/**
	 * literals for line starts. %s is replaced with singular creature name, %S
	 * plural
	 */
	static final String[] LINE_STARTS = new String[] { "Oh yes I know %s!",
			"When I was your age, I killed many %S!",
			"Those %S are one of my favorites!",
			"Let me think...%s...now I remember!",
			"I was almost killed by %a once!",
			"I've had some nice battles with %S!", };

	static {
		probabilityLiterals = new LinkedHashMap<Double, String>();
		probabilityLiterals.put(100.0, "always %s");
		probabilityLiterals.put(99.99, "almost always %s");
		probabilityLiterals.put(75.0, "most of the time %s");
		probabilityLiterals.put(55.0, "over half the time %s");
		probabilityLiterals.put(40.0, "very often %s");
		probabilityLiterals.put(20.0, "often %s");
		probabilityLiterals.put(5.0, "sometimes %s");
		probabilityLiterals.put(1.0, "rarely %s");
		probabilityLiterals.put(0.1, "very rarely %s");
		probabilityLiterals.put(0.001, "extremely rarely %s");
		probabilityLiterals.put(0.0001, "very few of them carry %s");
		probabilityLiterals.put(0.00000001,
				"maybe %s too, but I've only heard about that");
		probabilityLiterals.put(0.0, "never %s");

		amountLiterals = new LinkedHashMap<Integer, String>();
		amountLiterals.put(2000, "thousands of %s");
		amountLiterals.put(200, "hundreds of %s");
		amountLiterals.put(100, "lots of %s");
		amountLiterals.put(10, "some %s");
		amountLiterals.put(2, "few %s");
		amountLiterals.put(1, "%a");

		dangerLiterals = new LinkedHashMap<Double, String>();
		dangerLiterals.put(40.0, "%s would kill you in a second!");
		dangerLiterals.put(15.0,
				"%s is probably lethal for you, don't try to kill one!");
		dangerLiterals.put(2.0, "%s is extremely dangerous for you, beware!");
		dangerLiterals.put(1.8, "%S are very dangerous for you, be careful!");
		dangerLiterals.put(1.7,
				"%S are dangerous for you, keep potions with you!");
		dangerLiterals.put(1.2,
				"It is possibly dangerous for you, keep an eye on your health!");
		dangerLiterals.put(0.8,
				"It may be a nice challenge for you to kill one!");
		dangerLiterals.put(0.5, "Killing %s should be trivial for you.");
		dangerLiterals.put(0.3, "Killing %s should be easy for you.");
		dangerLiterals.put(0.0, "%s is probably not enough challenge for you.");
	}

	/**
	 * %1 = time to respawn
	 */
	static final String[] RESPAWN_TEXTS = new String[] {
			"They seem to get back after %1.",
			"They're reborn %1 after their death." };

	/**
	 * %1 = list of items dropped
	 */
	static final String[] CARRY_TEXTS = new String[] { "They carry %1.",
			"Dead ones have %1.", "The corpses contain %1." };

	/**
	 * no attributes
	 */
	static final String[] CARRY_NOTHING_TEXTS = new String[] {
			"I don't know if they carry anything.",
			"None of the ones I've seen carried anything." };

	/**
	 * %1 = list of locations
	 */
	static final String[] LOCATION_TEXTS = new String[] {
			"I have seen them %1.", "You should be able to find them %1.",
			"I have killed few of those %1." };

	/**
	 * %1 = name of the creature
	 */
	static final String[] LOCATION_UNKNOWN_TEXTS = new String[] { "I don't know of any place where you could find %1." };

	static CreatureInfo creatureInfo = new CreatureInfo(probabilityLiterals,
			amountLiterals, dangerLiterals, LINE_STARTS, RESPAWN_TEXTS,
			CARRY_TEXTS, CARRY_NOTHING_TEXTS, LOCATION_TEXTS,
			LOCATION_UNKNOWN_TEXTS);

	@Override
	public void createDialog(SpeakerNPC npc) {

		class StateInfo {
			String creatureName;

			int informationCost;

			void setCreatureName(String creatureName) {
				this.creatureName = creatureName;
			}

			String getCreatureName() {
				return creatureName;
			}

			void setInformationCost(int informationCost) {
				this.informationCost = informationCost;
			}

			int getInformationCost() {
				return informationCost;
			}
		}

		final StateInfo stateInfo = new StateInfo();

		npc.addGreeting();
		npc.setLevel(368);
		npc.setDescription("You see " + npc.getName()
				+ ", the mighty warrior and defender of Semos.");

		npc.addJob("My job? I'm a well known warrior, strange that you haven't heard of me!");
		npc.addQuest("Thanks, but I don't need any help at the moment.");
		npc.addHelp("If you want, I can tell you about the #creatures I have encountered.");
		npc.addOffer("I offer you information on #creatures I've seen for a reasonable fee.");

		npc.add(ConversationStates.ATTENDING, "creature", null,
				ConversationStates.QUESTION_1,
				"Which creature you would like to hear more about?", null);

		npc.add(ConversationStates.QUESTION_1, "", null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC speakerNPC) {
						EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
						String creatureName = sentence.toString();
						DefaultCreature creature = manager.getDefaultCreature(creatureName);
						if (creature == null) {
							speakerNPC.say("I have never heard of such a creature! Please tell the name again.");
							speakerNPC.setCurrentState(ConversationStates.QUESTION_1);
						} else {
							stateInfo.setCreatureName(creatureName);
							if (INFORMATION_BASE_COST > 0) {
								int informationCost = getCost(player, creature);
								stateInfo.setInformationCost(informationCost);
								speakerNPC.say("This information costs "
										+ informationCost
										+ ". Are you still interested?");
								speakerNPC.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
							} else {
								speakerNPC.say(getCreatureInfo(player,
										stateInfo.getCreatureName())
										+ " If you want to hear about another creature, just tell me which.");
								speakerNPC.setCurrentState(ConversationStates.QUESTION_1);
							}
						}
					}

					private int getCost(Player player, DefaultCreature creature) {
						return (int) (INFORMATION_BASE_COST + INFORMATION_COST_LEVEL_FACTOR
								* creature.getLevel());
					}
				});

		npc.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC speakerNPC) {
						if (stateInfo.getCreatureName() != null) {
							if (player.drop("money",
									stateInfo.getInformationCost())) {
								String infoString = getCreatureInfo(player,
										stateInfo.getCreatureName());
								infoString += " If you want to hear about another creature, just tell me which.";
								speakerNPC.say(infoString);
								speakerNPC.setCurrentState(ConversationStates.QUESTION_1);
							} else {
								speakerNPC.say("You don't have enough money with you.");
							}
						}
					}
				});

		npc.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Ok, come back if you're interested later.", null);

		npc.addGoodbye("Farewell and godspeed!");
	}

	private String getCreatureInfo(final Player player,
			final String creatureName) {
		String result = null;
		DefaultCreature creature;
		EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
		creature = manager.getDefaultCreature(creatureName);
		if (creature != null) {
			result = creatureInfo.getCreatureInfo(player, creature, 3, 8, true);
		} else {
			result = "I have never heard of such a creature!";
		}
		return result;
	}
}
