package games.stendhal.server.maps.quests.houses;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Controls house buying.
 *
 * @author kymara
 */

public class HouseBuyingMain implements LoginListener {
	static HouseTax houseTax;

	/** the logger instance. */
	static final Logger logger = Logger.getLogger(HouseBuyingMain.class);

	// constants
	static final String QUEST_SLOT = "house";

	static final String PRINCESS_QUEST_SLOT = "imperial_princess";
	static final String ANNA_QUEST_SLOT = "toys_collector";
	static final String KEYRING_QUEST_SLOT = "hungry_joshua";
	static final String GHOSTS_QUEST_SLOT = "find_ghosts";
	static final String DAILY_ITEM_QUEST_SLOT = "daily_item";
	static final String FISHROD_QUEST_SLOT = "get_fishing_rod";
	static final String ZARA_QUEST_SLOT = "suntan_cream_zara";
	static final String KIRDNEH_QUEST_SLOT = "weekly_item";
	static final String FISHLICENSE2_QUEST_SLOT = "fishermans_license2";

	/** Cost to buy spare keys. */
	static final int COST_OF_SPARE_KEY = 1000;

	/** percentage of initial cost refunded when you resell a house.*/
	static final int DEPRECIATION_PERCENTAGE = 40;
	
	/**
	 * age required to buy a house. Note, age is in minutes, not seconds! So
	 * this is 300 hours.
	 */
	static final int REQUIRED_AGE = 300 * 60;

	/** Kalavan house seller Zone name. */
	private static final String KALAVAN_CITY = "0_kalavan_city";
	/** Athor house seller Zone name. */
	private static final String ATHOR_ISLAND = "0_athor_island";
	/** Ados house seller Zone name. */
	private static final String ADOS_TOWNHALL = "int_ados_town_hall_3";
	/** Kirdneh house seller Zone name. */
	private static final String KIRDNEH_TOWNHALL = "int_kirdneh_townhall";

	/** Kalavan house seller. */
	private StendhalRPZone kalavan_city_zone;
	/** Athor house seller Zone. */
	private StendhalRPZone athor_island_zone;
	/** Ados house seller Zone.  */
	private StendhalRPZone ados_townhall_zone;
	/** Kirdneh house seller Zone. */
	private StendhalRPZone kirdneh_townhall_zone;
	
	


	static void fillChest(final StoredChest chest) {
		Item item = SingletonRepository.getEntityManager().getItem("note");
		item.setDescription("WELCOME TO THE HOUSE OWNER\n"
				+ "1. If you do not pay your house taxes, the house and all the items in the chest will be confiscated.\n"
				+ "2. All people who can get in the house can use the chest.\n"
				+ "3. Remember to change your locks as soon as the security of your house is compromised.\n"
				+ "4. You can resell your house to the state if wished (please don't leave me)\n");
		chest.add(item);
		
		item = SingletonRepository.getEntityManager().getItem("wine");
		((StackableItem) item).setQuantity(2);
		chest.add(item);
		
		item = SingletonRepository.getEntityManager().getItem("chocolate bar");
		((StackableItem) item).setQuantity(2);
		chest.add(item);
	}

	/** The sale of a spare key has been agreed, player meets conditions, 
	 * here is the action to simply sell it. */
	static final class BuySpareKeyChatAction implements ChatAction {
	

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			if (player.isEquipped("money", COST_OF_SPARE_KEY)) {

				final String housenumber = player.getQuest(QUEST_SLOT);
				final Item key = SingletonRepository.getEntityManager().getItem(
																				"house key");
				final int number = MathHelper.parseInt(housenumber);
				final HousePortal houseportal = HouseUtilities.getHousePortal(number);

				if (houseportal == null) {
					// something bad happened
					engine.say("Sorry something bad happened. I'm terribly embarassed.");
					return;
				}
				
				final int locknumber = houseportal.getLockNumber();
				final String doorId = houseportal.getDoorId();
				((HouseKey) key).setup(doorId, locknumber, player.getName());

				if (player.equipToInventoryOnly(key)) {
					player.drop("money", COST_OF_SPARE_KEY);
					engine.say("Here you go, a spare key to your house. Please remember, only give spare keys to people you #really, #really, trust! Anyone with a spare key can access your chest, and tell anyone that you give a key to, to let you know if they lose it. If that happens, you should #change your locks.");
				} else {
					engine.say("Sorry, you can't carry more keys!");
				}
			} else {
				engine.say("You do not have enough money for another key!");
			}
		}
	}

	static final class ChangeLockAction implements ChatAction {

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			if (player.isEquipped("money", COST_OF_SPARE_KEY)) {
				// we need to find out which this houseportal is so we can change lock
				final String claimedHouse = player.getQuest(QUEST_SLOT);
				
				try {
					final int id = Integer.parseInt(claimedHouse);
					final HousePortal portal = HouseUtilities.getHousePortal(id);
					// change the lock
					portal.changeLock();
					// make a new key for the player, with the new locknumber
					final String doorId = portal.getDoorId();
					final Item key = SingletonRepository.getEntityManager().getItem("house key");
					final int locknumber = portal.getLockNumber();

					((HouseKey) key).setup(doorId, locknumber, player.getName());
					if (player.equipToInventoryOnly(key)) {
						player.drop("money", COST_OF_SPARE_KEY);
						engine.say("The locks have been changed for " + doorId + ", here is your new key. Do you want to buy a spare key, at a price of "
								   + COST_OF_SPARE_KEY + " money?");
						engine.setCurrentState(ConversationStates.QUESTION_1);
					} else {
						// if the player doesn't have the space for the key, change the locks anyway as a security measure, but don't charge.
						engine.say("The locks have been changed for " 
								   + doorId + ", but you do not have space to carry the new key. I haven't charged you for this service. "
								   + "If you want to go away and make space, come back and I will offer you the chance to buy a spare key. Goodbye.");
						engine.setCurrentState(ConversationStates.IDLE);
					}
				} catch (final NumberFormatException e) {
					logger.error("Invalid number in house slot", e);
					engine.say("Sorry, something bad happened. I'm terribly embarassed.");
					return;
				}
			} else { 
				engine.say("You need to pay " + COST_OF_SPARE_KEY + " money to change the lock and get a new key for your house.");
			}
		}
	}

	/** House owners are offered the chance to buy a spare key when the seller greets them. Others are just greeted with their name. */
	static final class HouseSellerGreetingAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			String reply = "";
			
			if (HouseUtilities.playerOwnsHouse(player)) {
				reply = " At the cost of "
					+ COST_OF_SPARE_KEY
					+ " money you can purchase a spare key for your house. Do you want to buy one now?";
				engine.setCurrentState(ConversationStates.QUESTION_1);
			} else if (player.hasQuest(QUEST_SLOT)) {
				// the player has lost the house. clear the slot so that he can buy a new one if he wants
				player.removeQuest(QUEST_SLOT);
			}
			
			engine.say("Hello, " + player.getTitle() + "." + reply);
		}
		
	}

	static class PlayerOwnsHouseCondition implements ChatCondition {
		public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			return HouseUtilities.playerOwnsHouse(player);
		}
	}

	/** The NPC for Kalavan Houses. */
	private void createNPC() {
		final SpeakerNPC npc = new KalavanHouseseller("Barrett Holmes", "kalavan");
		

		kalavan_city_zone.add(npc);
	}

	/** The NPC for Ados Houses. */
	private void createNPC2() {
		final SpeakerNPC npc2 = new AdosHouseSeller("Reg Denson", "ados");


		ados_townhall_zone.add(npc2);
	}

	/** The NPC for Kirdneh Houses. */
	private void createNPC3() {
		final SpeakerNPC npc3 = new KirdnehHouseSeller("Roger Frampton", "kirdneh");

		kirdneh_townhall_zone.add(npc3);
	}

	/** The NPC for Athor Apartments. */
	private void createNPC4() {
		final SpeakerNPC npc4 = new AthorHouseSeller("Cyk", "athor");

		athor_island_zone.add(npc4);
	}

	// we'd like to update houses sold before release of 0.73 with the owner name
	// when a player logs in we see if they own a house and we get the number from the house slot
	// this can be removed after all previously owned portals would have expired unless player has logged in to pay tax 
	// as by then unclaimed houses will be reclaimed by state
	// this will be 6? months after release of 0.73
	public void onLoggedIn(final Player player) {
		final String name = player.getName();
		if (player.hasQuest(QUEST_SLOT) && !"postman".equals(name)) {
			
			// note we default to a DIFFERENT value than the default house number incase neither found for some bad reasons
			final int id = MathHelper.parseIntDefault(player.getQuest(QUEST_SLOT), -1);
			logger.debug("Found that " + name + " owns house " + Integer.toString(id));
			// Now look for the house portal which matches this and update it to have the player name on it
			final List<HousePortal> portals =  HouseUtilities.getHousePortals();
			for (final HousePortal houseportal : portals) {
				final String owner = houseportal.getOwner();
				if ("an unknown owner".equals(owner)) {
					final int number = houseportal.getPortalNumber();
					if (number == id) {
						houseportal.setOwner(name);
						logger.debug(name + " owns house " + Integer.toString(id) + " and we labelled the house");
						return;
					}
				}
			}
		}
	}

	public void addToWorld() {

		kalavan_city_zone = SingletonRepository.getRPWorld().getZone(KALAVAN_CITY);
		createNPC();

		ados_townhall_zone = SingletonRepository.getRPWorld().getZone(ADOS_TOWNHALL);
		createNPC2();

		kirdneh_townhall_zone = SingletonRepository.getRPWorld().getZone(KIRDNEH_TOWNHALL);
		createNPC3();

		athor_island_zone = SingletonRepository.getRPWorld().getZone(ATHOR_ISLAND);
		createNPC4();

		SingletonRepository.getLoginNotifier().addListener(this);
		
		// Start collecting taxes as well
		houseTax = new HouseTax();
	}
}
