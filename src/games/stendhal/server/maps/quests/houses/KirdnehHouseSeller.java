
package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.condition.AgeGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;
import games.stendhal.server.entity.npc.parser.JokerExprMatcher;

import java.util.Arrays;

final class KirdnehHouseSeller extends HouseSellerNPCBase {
	/** Cost to buy house in kirdneh. */
	private static final int COST_KIRDNEH = 120000;
	private static final String KIRDNEH_QUEST_SLOT = "weekly_item";

	KirdnehHouseSeller(final String name, final String location, final HouseTax houseTax) {
		super(name, location, houseTax);
		init();
	}

	private void init() {
		// Other than the condition that you must not already own a house, there are a number of conditions a player must satisfy. 
		// For definiteness we will check these conditions in a set order. 
		// So then the NPC doesn't have to choose which reason to reject the player for (appears as a WARN from engine if he has to choose)
		
		// player is not old enough
		add(ConversationStates.ATTENDING, 
				 Arrays.asList("cost", "house", "buy", "purchase"),
				 new NotCondition(new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE)),
				 ConversationStates.ATTENDING, 
				 "The cost of a new house in Kirdneh is "
						 + getCost()
				 + " money. But I am afraid I cannot trust you with house ownership just yet. Come back when you have spent at least " 
				 + Integer.toString((HouseSellerNPCBase.REQUIRED_AGE / 60)) + " hours on Faiumoni.",
				 null);
		
		// player is old enough and hasn't got a house but has not done required quest
		add(ConversationStates.ATTENDING, 
				 Arrays.asList("cost", "house", "buy", "purchase"),
				 new AndCondition(new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE), 
								  new QuestNotCompletedCondition(KirdnehHouseSeller.KIRDNEH_QUEST_SLOT), 
									 new QuestNotStartedCondition(HouseSellerNPCBase.QUEST_SLOT)),
				 ConversationStates.ATTENDING, 
				 "The cost of a new house in Kirdneh is "
				 + getCost()
				 + " money. But my principle is never to sell a house without establishing first the good #reputation of the prospective buyer.",
				 null);
		
		// player is eligible to buy a house
		add(ConversationStates.ATTENDING, 
				 Arrays.asList("cost", "house", "buy", "purchase"),
				 new AndCondition(new QuestNotStartedCondition(HouseSellerNPCBase.QUEST_SLOT), 
								  new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE), 
								  new QuestCompletedCondition(KirdnehHouseSeller.KIRDNEH_QUEST_SLOT)),
					ConversationStates.QUEST_OFFERED, 
				 "The cost of a new house is "
				 + getCost()
				 + " money.  Also, you must pay a house tax of " + HouseTax.BASE_TAX
				 + " money, every month. If you have a house in mind, please tell me the number now. I will check availability. "
				 + "Kirdneh Houses are numbered "
				 + getLowestHouseNumber() + " to " + getHighestHouseNumber() + ".",
				 null);
		
		// handle house numbers 26 to 49
		add(ConversationStates.QUEST_OFFERED,
				 // match for all numbers as trigger expression
				 "NUM", new JokerExprMatcher(),
				 new TextHasNumberCondition(getLowestHouseNumber(), getHighestHouseNumber()),
				 ConversationStates.ATTENDING, 
				 null,
				 new BuyHouseChatAction(getCost(), QUEST_SLOT));
		

		addJob("I'm an estate agent. In simple terms, I sell houses for the city of Kirdneh. Please ask about the #cost if you are interested. Our brochure is at #http://stendhalgame.org/wiki/index.php/StendhalHouses.");
		addReply("reputation", "I will ask Hazel about you. Provided you've finished any task she asked you to do for her recently, and haven't left anything unfinished, she will like you.");
		
		setDescription("You see a smart looking man.");
		setEntityClass("man_004_npc");
		setPosition(31, 4);
		initHP(100);
		
	}

	protected int getCost() {
		return KirdnehHouseSeller.COST_KIRDNEH;
	}

	@Override
	protected void createPath() {
		setPath(null);
	}

	@Override
	protected int getHighestHouseNumber() {
		return 49;
	}

	@Override
	protected int getLowestHouseNumber() {
		return 26;
	}
}
