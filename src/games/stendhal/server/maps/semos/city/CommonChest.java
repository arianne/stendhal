package games.stendhal.server.maps.semos.city;

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
		buildSemosCityAreaChest(zone);
	}

	private void buildSemosCityAreaChest(final StendhalRPZone zone) {

		final Chest chest = new Chest();
		chest.setPosition(44, 60);
		chest.add(SingletonRepository.getEntityManager().getItem("knife"));
		chest.add(SingletonRepository.getEntityManager().getItem("wooden shield"));
		chest.add(SingletonRepository.getEntityManager().getItem("leather armor"));
		chest.add(SingletonRepository.getEntityManager().getItem("money"));
		zone.add(chest);
	}
}
