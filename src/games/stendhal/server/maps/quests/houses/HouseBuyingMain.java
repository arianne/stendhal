package games.stendhal.server.maps.quests.houses;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Controls house buying.
 *
 * @author kymara
 */

public class HouseBuyingMain implements LoginListener {
	static HouseTax houseTax = new HouseTax();

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(HouseBuyingMain.class);

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
	
	


	

	/** The NPC for Kalavan Houses. */
	private void createNPC() {
		final SpeakerNPC npc = new KalavanHouseseller("Barrett Holmes", "kalavan", houseTax);
		

		kalavan_city_zone.add(npc);
	}

	/** The NPC for Ados Houses. */
	private void createNPC2() {
		final SpeakerNPC npc2 = new AdosHouseSeller("Reg Denson", "ados", houseTax);


		ados_townhall_zone.add(npc2);
	}

	/** The NPC for Kirdneh Houses. */
	private void createNPC3() {
		final SpeakerNPC npc3 = new KirdnehHouseSeller("Roger Frampton", "kirdneh", houseTax);

		kirdneh_townhall_zone.add(npc3);
	}

	/** The NPC for Athor Apartments. */
	private void createNPC4() {
		final SpeakerNPC npc4 = new AthorHouseSeller("Cyk", "athor", houseTax);

		athor_island_zone.add(npc4);
	}

	// we'd like to update houses sold before release of 0.73 with the owner name
	// when a player logs in we see if they own a house and we get the number from the house slot
	// this can be removed after all previously owned portals would have expired unless player has logged in to pay tax 
	// as by then unclaimed houses will be reclaimed by state
	// this will be 6? months after release of 0.73
	public void onLoggedIn(final Player player) {
		final String name = player.getName();
		if (player.hasQuest(HouseSellerNPCBase.QUEST_SLOT) && !"postman".equals(name)) {
			
			// note we default to a DIFFERENT value than the default house number incase neither found for some bad reasons
			final int id = MathHelper.parseIntDefault(player.getQuest(HouseSellerNPCBase.QUEST_SLOT), -1);
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
		
	}
}
