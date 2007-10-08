package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.pathfinder.FixedPath;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
/**
 * Controls house buying
 * 
 * @author kymara
 */

public class HouseBuying extends AbstractQuest {

	// constants
	private static final String QUEST_SLOT = "house";
    	private static final String PRINCESS_QUEST_SLOT = "imperial_princess";

	// Cost to buy house (lots!)
	private static final int COST = 100000;
        // Cost to buy spare keys
	private static final int COST2 = 1000;
        /* age required to buy a house. Note, age is in minutes, not seconds! 
	 * So this is 300 hours */
	private static final int REQUIRED_AGE = 300 * 60;
	

        /* This is the initial postman quest slot
	 * It would be around 50 long when all 25 houses full.
	 * If more houses get added (in other zones?) then we must 
	 * always make sure this postman quest slot stays under 255 characters
	 */
        private static final String POSTMAN_SLOT = ";";
	private static final String ZONE_NAME = "0_kalavan_city";
  
	protected SpeakerNPC npc;
	protected StendhalRPZone zone;

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void createNPC() {
		npc = new SpeakerNPC("Barrett Holmes") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(55, 94));
				nodes.add(new Node(93, 94));		 
				nodes.add(new Node(93, 73));
				nodes.add(new Node(107, 73));
				nodes.add(new Node(107, 35));
				nodes.add(new Node(84, 35));
				nodes.add(new Node(84, 20));
				nodes.add(new Node(17, 20));
				nodes.add(new Node(17, 82));
				nodes.add(new Node(43, 82));
				nodes.add(new Node(43, 94));
				setPath(new FixedPath(nodes, true));
		       	}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						      	String reply;
							if (player.hasQuest(QUEST_SLOT)) {
								reply = " At the cost of " + COST2 + " money you can purchase a spare key for your house. Do you want to buy one now?";
								engine.setCurrentState(ConversationStates.QUESTION_1);
							} else {
								reply = "";
							}
							engine.say("Hello, "
									+ player.getTitle() + "." + reply);
					
					}
				    }
				);
				addReply("cost", null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
					        if ( player.getAge()< REQUIRED_AGE){ 
						    engine.say("The cost of a new house is " + COST
									+ " money. But I am afraid I cannot trust you with house ownership just yet, as you have not been a part of this world long enough.");
					        }
						else if (!player.isQuestCompleted(PRINCESS_QUEST_SLOT)) {
						    engine.say("The cost of a new house is " + COST
							       + " money. But I am afraid I cannot sell you a house until your citizenship has been approved by the King, who you will find north of here in Kalavan Castle. try speaking to his daughter first, she is ... friendlier.");
						}

                                                else if (!player.hasQuest(QUEST_SLOT)) {
							engine.say("The cost of a new house is " + COST
									+ " money. If you have a house in mind, please tell me the number now. I will check availability.");
							engine.setCurrentState(ConversationStates.QUEST_OFFERED);
						} else {
							engine.say("As you already know, the cost of a new house is "
									+ COST + " money. But you cannot own more than one house, the market is too demanding for that!");
						}
					}
				    });
				// for house number, from 1 to 25:
				for (int house = 1; house < 26; house++) {
			        add(ConversationStates.QUEST_OFFERED, Integer.toString(house), null, ConversationStates.ATTENDING,
				null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
					    Player postman = StendhalRPRuleProcessor.get().getPlayer("postman");
					    // is postman online?
					    if (postman != null) {
						// First, check if anyone has bought a house from this npc yet
						if (!postman.hasQuest(QUEST_SLOT)){
						    postman.setQuest(QUEST_SLOT,POSTMAN_SLOT);
						}
						    String postmanslot = postman.getQuest(QUEST_SLOT);
						    String[] boughthouses = postmanslot.split(";");
						    List<String> doneList = Arrays.asList(boughthouses);
						    // now check if the house they said is free
						    if(!doneList.contains(text)){  
						    //it's available, so take money
						    if (player.drop("money", COST)) {
							        Item key = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("private_key_"+text);
						         	engine.say("Congratulations, here is your key to house " + text + "! Do you want to buy a spare key, at a price of " + COST2 + " money?");
							        player.equip(key);
								//remember what house they own
								player.setQuest(QUEST_SLOT, text);
								postman.setQuest(QUEST_SLOT, postmanslot + ";" + text);
								engine.setCurrentState(ConversationStates.QUESTION_1);
							} else {
								engine.say("You do not have enough money to buy a house!");
							}
						    }
						    else{
							 engine.say("Sorry, house " + text + " is sold, please give me the number of another.");
							 engine.setCurrentState(ConversationStates.QUEST_OFFERED);
						    }
					     } else {
						    //postman is offline!
						     engine.say("Oh dear, I've lost my records temporarily. I'm afraid I can't check anything for you. Please try again another time.");
					     }
					}
					
				});
				}
				// we need to warn people who buy spare keys about the chest being accessible to other players with a key
				add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES, null, ConversationStates.QUESTION_2, "Before we go on, I must warn you that anyone with a key to your house can access the chest in it, and take any items you left there. Of course they can also leave items there themselves. Do you still wish to buy a spare key?", null);
				// player wants spare keys and is ok with chest being accessible to other person.
				add(ConversationStates.QUESTION_2, ConversationPhrases.YES_MESSAGES, null, ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
					    if (player.drop("money", COST2)) {
						String house = player.getQuest(QUEST_SLOT);
						Item key = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("private_key_"+house);
					      	engine.say("Here you go, a spare key to your house. Please remember, only give spare keys to people you #really, #really, trust!");
						player.equip(key);
					    }
					    else {
					       	engine.say("You do not have enough money for another key!");
					    }
					}
				    }
				);
				add(ConversationStates.QUESTION_2, ConversationPhrases.NO_MESSAGES, null, ConversationStates.ATTENDING, "That is wise of you. It is certainly better to restrict use of your house to those you can really trust.", null);
				add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null, ConversationStates.ATTENDING, "No problem! If I can help you with anything else, just ask.", null);
				addJob("I'm an estate agent. In simple terms, I sell houses to anyone who wants to buy one. They #cost a lot, of course. Our brochure is at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses.");
				addReply("buy","You should really enquire the #cost before you ask to buy. And check our brochure, #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses.");
				addReply("really","That's right, really, really, really. Really.");
				addOffer("I sell houses, please look at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.");
				addHelp("You can buy a house if there are any available. If you can pay the #cost, I'll give you a key. As a house owner you can buy spare keys to give your friends. See #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses for pictures inside the houses and more details.");
				addQuest("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #http://arianne.sourceforge.net/wiki/index.php?title=StendhalHouses.");
				addGoodbye("Goodbye.");
			}
		};

		npc.setDescription("You see a smart looking man.");
		npc.setEntityClass("estateagentnpc");
		npc.setPosition(55, 94);
		npc.initHP(100);
		zone.add(npc);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		zone = StendhalRPWorld.get().getZone(ZONE_NAME);
       		createNPC();
	}
}
