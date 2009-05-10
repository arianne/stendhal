package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain.BuySpareKeyChatAction;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain.ChangeLockAction;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain.HouseSellerGreetingAction;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain.PlayerOwnsHouseCondition;

import java.util.Arrays;

/**
 * Base class for dialogue shared by all houseseller NPCs.
 * 
 */
abstract class HouseSellerNPCBase extends SpeakerNPC {

	private final String location;
	/**	
	 *	Creates NPC dialog for house sellers.
	 * @param name
	 *            the name of the NPC
	 * @param location
	 *            where are the houses?
	*/
	HouseSellerNPCBase(final String name, final String location) {
		super(name);			
		this.location = location;
		createDialogNowWeKnowLocation();
	}
	
	@Override
	protected abstract void createPath();
	
	private void createDialogNowWeKnowLocation() {
		addGreeting(null, new HouseSellerGreetingAction());
		
			// quest slot 'house' is started so player owns a house
		add(ConversationStates.ATTENDING, 
			Arrays.asList("cost", "house", "buy", "purchase"),
			new PlayerOwnsHouseCondition(),
			ConversationStates.ATTENDING, 
			"As you already know, the cost of a new house is "
				+ getCost()
			+ " money. But you cannot own more than one house, the market is too demanding for that! You cannot own another house until you #resell the one you already own.",
			null);
		
		// we need to warn people who buy spare keys about the house
		// being accessible to other players with a key
		add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.QUESTION_2,
			"Before we go on, I must warn you that anyone with a key to your house can enter it, and access the items in the chest in your house. Do you still wish to buy a spare key?",
			null);

		// player wants spare keys and is OK with house being accessible
		// to other person.
		add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES, 
			null,
			ConversationStates.ATTENDING, 
			null,
			new BuySpareKeyChatAction());
			
		// refused offer to buy spare key for security reasons
		add(ConversationStates.QUESTION_2,
			ConversationPhrases.NO_MESSAGES,
			null,
				ConversationStates.ATTENDING,
			"That is wise of you. It is certainly better to restrict use of your house to those you can really trust.",
			null);
		
		// refused offer to buy spare key 
		add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"No problem! Just so you know, if you need to #change your locks, I can do that, and you can also #resell your house to me if you want to.",
			null);

		// player is eligible to resell a house
		add(ConversationStates.ATTENDING, 
			Arrays.asList("resell", "sell"),
			new PlayerOwnsHouseCondition(),
				ConversationStates.QUESTION_3, 
			"The state will pay you "
			+ Integer.toString(HouseBuyingMain.DEPRECIATION_PERCENTAGE)
			+ " percent of the price you paid for your house, minus any taxes you owe. You should remember to collect any belongings from your house before you sell it. Do you really want to sell your house to the state?",
			null);
		
		// player is not eligible to resell a house
		add(ConversationStates.ATTENDING, 
			Arrays.asList("resell", "sell"),
			new NotCondition(new PlayerOwnsHouseCondition()),
			ConversationStates.ATTENDING, 
			"You don't own any house at the moment. If you want to buy one please ask about the #cost.",
			null);
		
		// accepted offer to resell a house
		add(ConversationStates.QUESTION_3,
			ConversationPhrases.YES_MESSAGES,
			null,
				ConversationStates.ATTENDING,
			null,
			new ResellHouseAction(getCost()));
		
		// refused offer to resell a house
		add(ConversationStates.QUESTION_3,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Well, I'm glad you changed your mind.",
			null);
		
		// player is eligible to change locks
		add(ConversationStates.ATTENDING, 
			"change",
			new PlayerOwnsHouseCondition(),
			ConversationStates.SERVICE_OFFERED, 
			"If you are at all worried about the security of your house or, don't trust anyone you gave a spare key to, "
			+ "it is wise to change your locks. Do you want me to change your house lock and give you a new key now?",
			null);

		// player is not eligible to change locks
		add(ConversationStates.ATTENDING, 
			"change",
			new NotCondition(new PlayerOwnsHouseCondition()),
			ConversationStates.ATTENDING, 
			"You don't own any house at the moment. If you want to buy one please ask about the #cost.",
			null);

		// accepted offer to change locks
		add(ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			null,
			new ChangeLockAction());

		// refused offer to change locks 
		add(ConversationStates.SERVICE_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"OK, if you're really sure. Please let me know if I can help with anything else.",
			null);

		add(ConversationStates.ANY,
			Arrays.asList("available", "unbought", "unsold"), 
			null, 
			ConversationStates.ATTENDING,
			null,
			new ListUnboughtHousesAction(location));

		addReply(
				 "buy",
				 "You should really enquire the #cost before you ask to buy. And check our brochure, #http://stendhal.game-host.org/wiki/index.php/StendhalHouses.");
		addReply("really",
				 "That's right, really, really, really. Really.");
		addOffer("I sell houses, please look at #http://stendhal.game-host.org/wiki/index.php/StendhalHouses for examples of how they look inside. Then ask about the #cost when you are ready.");
		addHelp("You may be eligible to buy a house if there are any #available. If you can pay the #cost, I'll give you a key. As a house owner you can buy spare keys to give your friends. See #http://stendhal.game-host.org/wiki/index.php/StendhalHouses for pictures inside the houses and more details.");
		addQuest("You may buy houses from me, please ask the #cost if you are interested. Perhaps you would first like to view our brochure, #http://stendhal.game-host.org/wiki/index.php/StendhalHouses.");
		addGoodbye("Goodbye.");
	}

	protected abstract int getCost();

	protected abstract int getLowestHouseNumber();
	protected abstract int getHighestHouseNumber();
}
