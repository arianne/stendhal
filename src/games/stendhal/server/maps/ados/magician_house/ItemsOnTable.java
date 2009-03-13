package games.stendhal.server.maps.ados.magician_house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

import java.util.Map;

/**
 * Creates the items on the table in the magician house.
 *
 * @author hendrik
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicianHouseArea(zone);
	}

	private void buildMagicianHouseArea(final StendhalRPZone zone) {
		final Item item = addPersistentItem("summon scroll", zone, 7, 6);
		item.setInfoString("giant_red_dragon");

		// Plant grower for poison
		final PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("poison", 1500);
		plantGrower.setPosition(3, 6);
		plantGrower.setDescription("Haizen tends to put his magic drinks here.");
		zone.add(plantGrower);

		plantGrower.setToFullGrowth();
	}

	private Item addPersistentItem(final String name, final StendhalRPZone zone, final int x, final int y) {
		final Item item = SingletonRepository.getEntityManager().getItem(name);
		item.setPosition(x, y);
		zone.add(item, false);
		
		return item;
	}
}
