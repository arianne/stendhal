/**
 *
 */
package games.stendhal.server.maps.quests.houses;

import java.util.Arrays;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.ExpressionType;
import games.stendhal.common.parser.JokerExprMatcher;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.condition.AgeGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TextHasNumberCondition;

final class AthorHouseSeller extends HouseSellerNPCBase {
	/** Cost to buy house in athor. */
	private static final int COST_ATHOR = 100000;
	private static final String FISHLICENSE2_QUEST_SLOT = "fishermans_license2";

	AthorHouseSeller(final String name, final String location, final HouseTax houseTax) {
		super(name, location, houseTax);
		init();
	}

	private void init() {
		// Other than the condition that you must not already own a house, there are a number of conditions a player must satisfy.
		// For definiteness we will check these conditions in a set order.
		// So then the NPC doesn't have to choose which reason to reject the player for (appears as a WARN from engine if he has to choose)

		// player is not old enough
		add(ConversationStates.ATTENDING,
				 Arrays.asList("cost", "house", "buy", "purchase", "apartment"),
				 new NotCondition(new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE)),
				 ConversationStates.ATTENDING,
				 "The cost of a new apartment in Athor is "
						 + getCost()
				 + " money. But, you'll have to come back when you have spent at least "
				 + Integer.toString((HouseSellerNPCBase.REQUIRED_AGE / 60)) + " hours on Faiumoni. Maybe I'll have managed to get a suntan by then.",
				 null);

		// player is old enough and hasn't got a house but has not done required quest
		add(ConversationStates.ATTENDING,
				 Arrays.asList("cost", "house", "buy", "purchase", "apartment"),
				 new AndCondition(new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE),
								  new QuestNotCompletedCondition(AthorHouseSeller.FISHLICENSE2_QUEST_SLOT),
								  new QuestNotStartedCondition(HouseSellerNPCBase.QUEST_SLOT)),
				 ConversationStates.ATTENDING,
				 "What do you want with an apartment on Athor when you're not even a good #fisherman? We are trying to attract owners who will spend time on the island. Come back when you have proved yourself a better fisherman.",
				 null);

		// player is eligible to buy a apartment
		add(ConversationStates.ATTENDING,
				 Arrays.asList("cost", "house", "buy", "purchase", "apartment"),
				 new AndCondition(new QuestNotStartedCondition(HouseSellerNPCBase.QUEST_SLOT),
								  new AgeGreaterThanCondition(HouseSellerNPCBase.REQUIRED_AGE),
								  new QuestCompletedCondition(AthorHouseSeller.FISHLICENSE2_QUEST_SLOT)),
					ConversationStates.QUEST_OFFERED,
				 "The cost of a new apartment is "
				 + getCost()
				 + " money.  Also, you must pay a monthly tax of " + HouseTax.BASE_TAX
				 + " money. If you have an apartment in mind, please tell me the number now. I will check availability. "
				 + "Athor Apartments are numbered "
				 + getLowestHouseNumber() + " to " + getHighestHouseNumber() + ".",
				 null);

		// handle house numbers 101 to 108
		addMatching(ConversationStates.QUEST_OFFERED,
				 // match for all numbers as trigger expression
				ExpressionType.NUMERAL, new JokerExprMatcher(),
				new TextHasNumberCondition(getLowestHouseNumber(), getHighestHouseNumber()),
				ConversationStates.ATTENDING,
				null,
				new BuyHouseChatAction(getCost(), QUEST_SLOT));


		addJob("Well, I'm actually trying to sunbathe here. But, since you ask, I sell apartments here on Athor. Our brochure is at #https://stendhalgame.org/wiki/StendhalHouses.");
		addReply("fisherman", "A fishing license from Santiago in Ados is the sign of a good fisherman, he sets two exams. Once you have passed both parts you are a good fisherman.");
		setDirection(Direction.DOWN);
		setDescription("You see a man trying to catch some sun.");
		setEntityClass("swimmer1npc");
		setPosition(44, 40);
		initHP(100);

	}

	@Override
	protected int getCost() {
		return AthorHouseSeller.COST_ATHOR;
	}

	@Override
	protected void createPath() {
		setPath(null);
	}

	@Override
	public void say(final String text) {
		// He doesn't move around because he's "lying" on his towel.
		say(text, false);
	}

	@Override
	protected int getHighestHouseNumber() {
		return 108;
	}

	@Override
	protected int getLowestHouseNumber() {
		return 101;
	}
}
