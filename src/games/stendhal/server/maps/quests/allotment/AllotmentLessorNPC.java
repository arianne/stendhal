package games.stendhal.server.maps.quests.allotment;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.GateKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.Map;
 
/**
 * Builds an allotment lessor NPC for Semos. 
 *
 * @author kymara
 */
public class AllotmentLessorNPC implements ZoneConfigurator {
	private static String QUEST_SLOT = "allotment rental";
	private AllotmentUtilities rentHelper;
 
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		rentHelper = AllotmentUtilities.get();
		buildNPC(zone, attributes);
	}
 
	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
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
						new QuestActiveCondition(QUEST_SLOT),
						ConversationStates.QUEST_STARTED,
						"So what can I do you for? I hope the ground you rented is working well for you.",
						null);
				
				// if allotment not rented and there are available then offer one or say none available
				add(ConversationStates.ATTENDING,
					Arrays.asList("rent", "allotment"),
					new QuestNotActiveCondition("QUEST_SLOT"),
					ConversationStates.QUEST_OFFERED,
					null,
					new ChatAction() {
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							String reply = "";
							for (String s : rentHelper.getAvailableAllotments(zone.getName())) {
								reply += s + ", ";
							}
						
							if (reply.length() > 0) {
								npc.say("Would you like to rent one of the allotments? Allotments " + reply + "are available.");
							} else {
								npc.say("Unfortunately there aren't any allotments available at the moment. Please come back later.");
								npc.setCurrentState(ConversationStates.ATTENDING);
							}
						}
				});
				
				// if offer rejected
				add(ConversationStates.QUEST_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Ok, how else may I help you?",
					new SetQuestAction(QUEST_SLOT, 0, "rejected"));
				
				// if accepts to rent allotment
				add(ConversationStates.QUEST_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					null,
					ConversationStates.QUESTION_1,
					"Which one?",
					null);
				
				// do business
				add(ConversationStates.QUESTION_1,
					"",
					new TextHasNumberCondition(),
					ConversationStates.ATTENDING, 
					null,
					new ChatAction() {
						public void fire(Player player, Sentence sentence, EventRaiser npc) {
							final int number = sentence.getNumeral().getAmount();
							final String allotmentNumber = Integer.toString(number);
							
							if (!rentHelper.hasAllotment(zone.getName(), allotmentNumber)) {
								npc.say("I'm sorry, that allotment does not exist, which one would you like?");
							} else {
								if (rentHelper.getAvailableAllotments(zone.getName()).contains(allotmentNumber)) {
									if(rentHelper.setExpirationTime(zone.getName(), allotmentNumber)) {
										npc.say("Here's your key");
										player.equipToInventoryOnly(rentHelper.getKey(zone.getName(), allotmentNumber));
									} else {
										npc.say("Uh oh!");
										// error?
									}
								} else {
									npc.say("I'm sorry, that allotment is already taken.");
								}
							}
						}					
					}
				);
				
				//for testing purposes
				add(ConversationStates.ATTENDING, "key", null, ConversationStates.ATTENDING, null, new ChatAction() {
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						final Item key = SingletonRepository.getEntityManager().getItem("gate key");
						
						((GateKey) key).setup("Allotment 0", MathHelper.MILLISECONDS_IN_ONE_MINUTE + System.currentTimeMillis());
						if (player.equipToInventoryOnly(key)) {
							npc.say("Here's a key");
						} else {
							npc.say("Nope sorry");
						}

						rentHelper.setExpirationTime(zone.getName(), "0");
					}
				});
			}
		};
 
		npc.setEntityClass("kid6npc");
		npc.setPosition(85, 11);
		npc.initHP(100);
		npc.setDescription("You see jefs clone. He seems like waiting for someone.");
		zone.add(npc);
	}
}