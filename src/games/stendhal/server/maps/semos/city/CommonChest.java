package games.stendhal.server.maps.semos.city;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.mapstuff.chest.Chest;

import java.util.Map;

public class CommonChest implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildSemosCityAreaChest(zone);
	}

	private void buildSemosCityAreaChest(StendhalRPZone zone) {

		Chest chest = new Chest();
		chest.setPosition(44, 60);
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"knife"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"wooden_shield"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"leather_armor"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"money"));
		zone.add(chest);
	}
}
