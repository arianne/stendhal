package games.stendhal.server.maps.kalavan.house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.chest.Chest;

import java.util.Map;

public class CommonChest implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildKalavanHouseAreaChest(zone);
	}

	private void buildKalavanHouseAreaChest(final StendhalRPZone zone) {
	    // load the stuff in the house with presents
		final Chest chest = new Chest();
		chest.setPosition(22, 2);
		chest.add(SingletonRepository.getEntityManager().getItem("wine"));
		chest.add(SingletonRepository.getEntityManager().getItem("easter egg"));
		chest.add(SingletonRepository.getEntityManager().getItem("mega potion"));
		chest.add(SingletonRepository.getEntityManager().getItem("present"));
		chest.add(SingletonRepository.getEntityManager().getItem("pie"));
		zone.add(chest);
	}
}
