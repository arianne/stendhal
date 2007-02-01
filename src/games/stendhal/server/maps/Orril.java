package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.orril.IL0_Castle;
import games.stendhal.server.maps.orril.IL0_JynathHouse;
import games.stendhal.server.maps.orril.OL0_Castle;
import games.stendhal.server.maps.orril.OL0_JynathHouse;
import games.stendhal.server.maps.orril.OL0_RiverSouthCampfire;
import games.stendhal.server.maps.orril.USL1_Castle;
import games.stendhal.server.maps.orril.USL1_CastleWest;
import games.stendhal.server.maps.orril.USL2_DwarfMine;
import games.stendhal.server.maps.orril.USL2_LichPalace;
import games.stendhal.server.maps.orril.USL3_DwarfBlacksmith;

import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Configure Orril zones.
 */
public class Orril implements ZoneConfigurator, IContent {
	public Orril() {
	}


	public void build() {
		configureZone(null, java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		ZoneConfigurator	configurator;
		StendhalRPWorld world = StendhalRPWorld.get();


		configurator = new IL0_Castle();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_orril_castle_0")),
			attributes);


		configurator = new IL0_JynathHouse();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_orril_jynath_house")),
			attributes);


		configurator = new OL0_Castle();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_orril_castle")),
			attributes);


		configurator = new OL0_JynathHouse();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_orril_river_s")),
			attributes);


		configurator = new OL0_RiverSouthCampfire();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_orril_river_s")),
			attributes);


		configurator = new USL1_Castle();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-1_orril_castle")),
			attributes);


		configurator = new USL1_CastleWest();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-1_orril_castle_w")),
			attributes);


		configurator = new USL2_DwarfMine();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-2_orril_dwarf_mine")),
			attributes);


		configurator = new USL2_LichPalace();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-2_orril_lich_palace")),
			attributes);


		configurator = new USL3_DwarfBlacksmith();

		configurator.configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-3_orril_dwarf_blacksmith")),
			attributes);
	}
}
