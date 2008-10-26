package games.stendhal.server.maps.ados.rosshouse;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;

import java.util.Map;

/**
 * Creates the items on the tables and ground in the Ross' house.
 *
 * @author kymara
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildRossHouseArea(zone);
	}

	private void buildRossHouseArea(final StendhalRPZone zone) {
		final Item item = addPersistentItem("teddy", zone, 9, 9);
		final Item item = addPersistentItem("dice", zone, 11, 9);
	}

	private Item addPersistentItem(final String name, final StendhalRPZone zone, final int x, final int y) {
		final Item item = SingletonRepository.getEntityManager().getItem(name);
		item.setPosition(x, y);
		item.setPersistent(true);
		zone.add(item);
		return item;
	}
}
