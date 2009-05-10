/**
 * 
 */
package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.condition.AgeGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.npc.parser.JokerExprMatcher;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

final class AdosHouseSeller extends HouseSellerNPCBase {
	/** Cost to buy house in ados. */
	private static final int COST_ADOS = 120000;

	AdosHouseSeller(final String name, final String location) {
		super(name, location);
		init();
	}

	private void init() {
		// Other than the condition that you must not already own a house, there are a number of conditions a player must satisfy. 
		// For definiteness we will check these conditions in a set order. 
		// So then the NPC doesn't have to choose which reason to reject the player for (appears as a WARN from engine if he has to choose)
		
		// player is not old enough
		add(ConversationStates.ATTENDING, 
				 Arrays.asList("cost", "house", "buy", "purchase"),
				 new NotCondition(new AgeGreaterThanCondition(HouseBuyingMain.REQUIRED_AGE)),
				 ConversationStates.ATTENDING,
				 "The cost of a new house in Ados is "
				 + getCost()
				 + " money. But I am afraid I cannot trust you with house ownership just yet, come back when you have spent at least " 
				 + Integer.toString((HouseBuyingMain.REQUIRED_AGE / 60)) + " hours on Faiumoni.",
					null);
		
		
		// player doesn't have a house and is old enough but has not done required quests
		add(ConversationStates.ATTENDING, 
				 Arrays.asList("cost", "house", "buy", "purchase"),
				 new AndCondition(new AgeGreaterThanCondition(HouseBuyingMain.REQUIRED_AGE), 
								  new QuestNotStartedCondition(HouseBuyingMain.QUEST_SLOT),
								  new NotCondition(
													  new AndCondition(
																	   new QuestCompletedCondition(HouseBuyingMain.DAILY_ITEM_QUEST_SLOT),
																	   new QuestCompletedCondition(HouseBuyingMain.ANNA_QUEST_SLOT),
																	   new QuestCompletedCondition(HouseBuyingMain.KEYRING_QUEST_SLOT),
																	   new QuestCompletedCondition(HouseBuyingMain.FISHROD_QUEST_SLOT),
																	   new QuestCompletedCondition(HouseBuyingMain.GHOSTS_QUEST_SLOT),
																	   new QuestCompletedCondition(HouseBuyingMain.ZARA_QUEST_SLOT)))),
				 ConversationStates.ATTENDING, 
				 "The cost of a new house in Ados is "
				 + getCost()
				 + " money. But I am afraid I cannot sell you a house yet as you must first prove yourself a worthy #citizen.",
				 null);
		
		// player is eligible to buy a house
		add(ConversationStates.ATTENDING, 
					Arrays.asList("cost", "house", "buy", "purchase"),
				 new AndCondition(new QuestNotStartedCondition(HouseBuyingMain.QUEST_SLOT), 
								  new AgeGreaterThanCondition(HouseBuyingMain.REQUIRED_AGE), 
								  new QuestCompletedCondition(HouseBuyingMain.DAILY_ITEM_QUEST_SLOT),
								  new QuestCompletedCondition(HouseBuyingMain.ANNA_QUEST_SLOT),
								  new QuestCompletedCondition(HouseBuyingMain.KEYRING_QUEST_SLOT),
								  new QuestCompletedCondition(HouseBuyingMain.FISHROD_QUEST_SLOT),
								  new QuestCompletedCondition(HouseBuyingMain.GHOSTS_QUEST_SLOT),
								  new QuestCompletedCondition(HouseBuyingMain.ZARA_QUEST_SLOT)),
				 ConversationStates.QUEST_OFFERED, 
				 "The cost of a new house in Ados is "
				 + getCost()
				 + " money. Also, you must pay a house tax of " + HouseTax.BASE_TAX
				 + " money, every month. If you have a house in mind, please tell me the number now. I will check availability. "
				 + "The Ados houses are numbered from 50 to 73.",
				 null);
		
		// handle house numbers 50 to 73
		add(ConversationStates.QUEST_OFFERED,
				 // match for all numbers as trigger expression
					"NUM", new JokerExprMatcher(),
				 new TextHasNumberCondition(getLowestHouseNumber(), getHighestHouseNumber()),
				 ConversationStates.ATTENDING, 
				 null,
				 new BuyHouseChatAction(getCost()));
		
		addJob("I'm an estate agent. In simple terms, I sell houses for the city of Ados. Please ask about the #cost if you are interested. Our brochure is at #http://stendhal.game-host.org/wiki/index.php/StendhalHouses.");
		addReply("citizen", "I conduct an informal survey amongst the Ados residents. If you have helped everyone in Ados, I see no reason why they shouldn't recommend you. I speak with my friend Joshua, the Mayor, the little girl Anna, Pequod the fisherman, Zara, and I even commune with Carena, of the spirit world. Together they give a reliable opinion.");

		setDescription("You see a smart looking man.");
		setEntityClass("estateagent2npc");
		setPosition(37, 13);
		initHP(100);
		
	}

	protected int getCost() {
		return AdosHouseSeller.COST_ADOS;
	}

	@Override
	protected void createPath() {
		final List<Node> nodes = new LinkedList<Node>();
		nodes.add(new Node(37, 13));
		nodes.add(new Node(31, 13));
		nodes.add(new Node(31, 10));
		nodes.add(new Node(35, 10));
		nodes.add(new Node(35, 4));
		nodes.add(new Node(25, 4));
		nodes.add(new Node(25, 15));
		nodes.add(new Node(15, 15));
		nodes.add(new Node(15, 9));
		nodes.add(new Node(18, 9));
		nodes.add(new Node(18, 4));
		nodes.add(new Node(18, 10));
		nodes.add(new Node(15, 10));
		nodes.add(new Node(15, 16));
		nodes.add(new Node(25, 16));
		nodes.add(new Node(25, 3));
		nodes.add(new Node(35, 3));
		nodes.add(new Node(35, 10));
		nodes.add(new Node(37, 10));
		setPath(new FixedPath(nodes, true));
	}

	@Override
	protected int getHighestHouseNumber() {
		return 73;
	}

	@Override
	protected int getLowestHouseNumber() {
		return 50;
	}

}
