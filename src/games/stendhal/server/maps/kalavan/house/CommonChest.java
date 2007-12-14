package games.stendhal.server.maps.kalavan.house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
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
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildKalavanHouseAreaChest(zone);
	}

	private void buildKalavanHouseAreaChest(StendhalRPZone zone) {
	    // load the stuff in the house with presents
		Chest chest = new Chest();
		chest.setPosition(22, 2);
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("wine"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("easter_egg"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("mega_potion"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("present"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("pie"));
		zone.add(chest);
	}
}
