package games.stendhal.server.maps.quests.houses;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Controls house buying.
 *
 * @author kymara
 */

public class HouseBuyingMain {
	static HouseTax houseTax;

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

	public void addToWorld() {
		// Start collecting taxes as well
		houseTax = new HouseTax();
		
		kalavan_city_zone = SingletonRepository.getRPWorld().getZone(KALAVAN_CITY);
		createNPC();

		ados_townhall_zone = SingletonRepository.getRPWorld().getZone(ADOS_TOWNHALL);
		createNPC2();

		kirdneh_townhall_zone = SingletonRepository.getRPWorld().getZone(KIRDNEH_TOWNHALL);
		createNPC3();

		athor_island_zone = SingletonRepository.getRPWorld().getZone(ATHOR_ISLAND);
		createNPC4();
	}
}
