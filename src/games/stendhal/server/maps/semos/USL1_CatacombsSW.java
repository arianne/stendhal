package games.stendhal.server.maps.semos;

import java.util.Map;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ZoneConfigurator;
import marauroa.common.game.IRPZone;

public class USL1_CatacombsSW implements ZoneConfigurator {
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-1_semos_catacombs_sw")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosCatacombs1SWArea(zone);
	}


	private void buildSemosCatacombs1SWArea(StendhalRPZone zone) {
	}
}
