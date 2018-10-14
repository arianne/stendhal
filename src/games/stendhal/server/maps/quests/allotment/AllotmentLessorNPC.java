package games.stendhal.server.maps.quests.allotment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.GateKey;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

/**
 * Builds an allotment lessor NPC for Semos.
 *
 * @author kymara, filipe
 */
public class AllotmentLessorNPC implements ZoneConfigurator {
	private static String QUEST_SLOT = AllotmentUtilities.QUEST_SLOT;
	private AllotmentUtilities rentHelper;

	/**
	 * Configure a zone.
	 *
	 * @param zone The zone to be configured.
	 * @param attributes Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		rentHelper = AllotmentUtilities.get();
		buildNPC(zone);
	}

	/**
	 * Creates the NPC and sets the quest dialog
	 *
	 * @param zone The zone to be configured.
	 * @param attributes Configuration attributes.
	 */
	private void buildNPC(final StendhalRPZone zone) {
		// condition to check if there are any allotments available
		final ChatCondition hasAllotments = new ChatCondition() {
			@Override
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				return rentHelper.getAvailableAllotments(zone.getName()).size() > 0;
			}
		};

		/**
		 * condition to check if the player already has an allotment rented
		 * note: this is used instead QuestActiveCondition because it relies on
		 * the time that the player speaks to the NPC
		 */
		final ChatCondition questActive = new ChatCondition() {
			@Override
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				return new QuestStateGreaterThanCondition(QUEST_SLOT, 1, (int) System.currentTimeMillis()).fire(player,  sentence, npc);
			}
		};

		// create the new NPC
		final SpeakerNPC npc = new SpeakerNPC("Jef's_twin") {

			@Override
			protected void createPath() {
				/*final List<Node> nodes = new LinkedList<Node>();

				nodes.add(new Node(70, 19));
                nodes.add(new Node(86,19));
                nodes.add(new Node(86,3));
                nodes.add(new Node(87,3));
                nodes.add(new Node(87,19));
                nodes.add(new Node(106,19));
                nodes.add(new Node(106,3));
                nodes.add(new Node(107,3));
                nodes.add(new Node(107,19));
                nodes.add(new Node(69,19));

				setPath(new FixedPath(nodes, true));*/
				setPath(null);
			}

			@Override
			protected void createDialog() {
				// TODO: this was copy pasted change as needed
				addGreeting("Heya!");
				addJob("Um, not sure what you mean. Right now I'm waiting for my mum to get back from the #shops.");
				addHelp("I have some #news about the bazaar over there.");
				addOffer("I don't sell stuff, I'm just waiting for my mum. But I have some #news if you wanna hear it.");
				// quest: FindJefsMom , quest sentence given there
				addReply("news", "Some more shopkeepers will be at the market soon! It'll be cool, it's kind of empty round here at the moment.");
				addReply("shops", "Yeah she's had to go out of town. All we have here is that flower seller! There's #news about our bazaar, though ...");
				addGoodbye("See you around.");

				// if player already has one rented ask how may help
				add(ConversationStates.ATTENDING,
					Arrays.asList("rent", "allotment"),
					questActive,
					ConversationStates.QUEST_STARTED,
					"So what can I do you for? Did you lose your #key or want another one or would you like to know how much #time is left until your allotment expires?",
					null);

				// if allotment not rented and there are available then ask if player wants to rent
				add(ConversationStates.ATTENDING,
					Arrays.asList("rent", "allotment"),
					new AndCondition(
							new NotCondition(questActive),
							hasAllotments),
					ConversationStates.QUEST_OFFERED,
					"Would you like to rent an allotment?",
					null);

				// if allotment not rented and there are none available then tell player
				add(ConversationStates.ATTENDING,
					Arrays.asList("rent", "allotment"),
					new AndCondition(
							new NotCondition(questActive),
							new NotCondition(hasAllotments)),
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							long diff = rentHelper.getNextExpiryTime(zone.getName()) - System.currentTimeMillis();

							npc.say("I'm sorry, there aren't any available at the moment. Please come back in about " + TimeUtil.approxTimeUntil((int) (diff / 1000L)) + ".");
						}
				});

				// if offer rejected
				add(ConversationStates.QUEST_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Ok, how else may I help you?",
					new SetQuestAction(QUEST_SLOT, 1, "0"));

				// if accepts to rent allotment
				add(ConversationStates.QUEST_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					null,
					ConversationStates.QUESTION_1,
					null,
					new ChatAction() {
						//say which ones are available
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							List<String> allotments = rentHelper.getAvailableAllotments(zone.getName());
							String reply = Grammar.enumerateCollection(allotments);

							npc.say("Which one would you like? Let's see... " + Grammar.plnoun(allotments.size(), "allotment") + " "
									+ reply + " are available, or perhaps #none if you've changed your mind.");
						}
					});

				// to exit renting/choosing an allotment
				add(ConversationStates.ANY,
					"none",
					null,
					ConversationStates.ATTENDING,
					"Ok.",
					null);

				// do business
				add(ConversationStates.QUESTION_1,
					"",
					new TextHasNumberCondition(),
					ConversationStates.ATTENDING,
					null,
					new ChatAction() {
						// does the transaction if possible
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							final int number = sentence.getNumeral().getAmount();
							final String allotmentNumber = Integer.toString(number);

							//TODO: get payment
							if (!rentHelper.isValidAllotment(zone.getName(), allotmentNumber)) {
								npc.say("I'm afraid that allotment does not exist.");
							} else {
								if (rentHelper.getAvailableAllotments(zone.getName()).contains(allotmentNumber)) {
									if(rentHelper.setExpirationTime(zone.getName(), allotmentNumber, player.getName())) {
										npc.say("Here's your key to allotment " + allotmentNumber + ". You are allowed to use the allotment for the next "
												+ TimeUtil.approxTimeUntil((int) (AllotmentUtilities.RENTAL_TIME / 1000L)) + ".");

										if (!player.equipToInventoryOnly(rentHelper.getKey(zone.getName(), player.getName()))) {
											npc.say("Oh, you look a bit overloaded there. I'll keep it safe here until you come back. Just ask about your #allotment.");
										}

										new SetQuestAction(QUEST_SLOT, 1, Long.toString(AllotmentUtilities.RENTAL_TIME + System.currentTimeMillis())).fire(player, sentence, npc);
									} else {
										// error? shouldn't happen
										npc.say("Uh oh! There appears to be a problem in the paperwork. Please give me some time to sort it out.");
									}
								} else {
									npc.say("I'm sorry, that allotment is already taken.");
								}
							}
						}
					});

				// if player asked about key
				add(ConversationStates.QUEST_STARTED,
					"key",
					null,
					ConversationStates.ATTENDING,
					"",
					// gives player a new key to give to friends to use
					new ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							GateKey key = rentHelper.getKey(zone.getName(), player.getName());

							if (key != null) {
								if (player.equipToInventoryOnly(key)) {
									npc.say("Here's your key, happy planting.");
								} else {
									npc.say("You can't carry that right now. Ask me again when you're not carrying so much.");
								}
							} else {
								npc.say("There must have been a mixup in the paperwork. It appears you haven't rented out an allotment.");
							}
						}
					});

				// if player asked about remaining time
				add(ConversationStates.QUEST_STARTED,
					"time",
					null,
					ConversationStates.ATTENDING,
					null,
					// gets a new key
					new ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							npc.say("The time remaining on your rent is " + rentHelper.getTimeLeftPlayer(zone.getName(), player.getName()) + ".");
						}
					});

			}
		};

		//TODO: also copy-pasted change as needed
		npc.setEntityClass("kid6npc");
		npc.setPosition(85, 11);
		npc.initHP(100);
		npc.setDescription("You see jefs clone. He seems like waiting for someone.");
		zone.add(npc);
	}
}
