package games.stendhal.server.maps.kirdneh.museum;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

import java.util.Map;

/**
 * Creates the items on the table in the museum.
 *
 * @author kymara
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBasementArea(zone);
	}

	private void buildBasementArea(StendhalRPZone zone) {

		PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("emerald", 4000);
		plantGrower.setPosition(26, 38);
		plantGrower.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower);

		plantGrower.setToFullGrowth();

		PassiveEntityRespawnPoint plantGrower2 = new PassiveEntityRespawnPoint("sapphire", 4000);
		plantGrower2.setPosition(26, 39);
		plantGrower2.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower2);

		plantGrower2.setToFullGrowth();

		PassiveEntityRespawnPoint plantGrower3 = new PassiveEntityRespawnPoint("carbuncle", 4000);
		plantGrower3.setPosition(26, 40);
		plantGrower3.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower3);

		plantGrower3.setToFullGrowth();

		PassiveEntityRespawnPoint plantGrower4 = new PassiveEntityRespawnPoint("obsidian", 4000);
		plantGrower4.setPosition(26, 41);
		plantGrower4.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower4);

		plantGrower4.setToFullGrowth();

		PassiveEntityRespawnPoint plantGrower5 = new PassiveEntityRespawnPoint("diamond", 4000);
		plantGrower5.setPosition(26, 42);
		plantGrower5.setDescription("A space for a gem to be displayed is here.");
		zone.add(plantGrower5);

		plantGrower5.setToFullGrowth();
	}

}
