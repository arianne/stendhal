package games.stendhal.server.maps.ados.magician_house;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

/**
 * Creates the items on the table in the magician house.
 *
 * @author hendrik
 */
public class ItemsOnTable implements ZoneConfigurator {
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildMagicianHouseArea(zone);
	}

	private void buildMagicianHouseArea(StendhalRPZone zone) {
		Item item = addPersistentItem("summon_scroll", zone, 7, 6);
		item.put("infostring", "giant_red_dragon");

		// Plant grower for poison
		PassiveEntityRespawnPoint plantGrower = new PassiveEntityRespawnPoint("poison", 1500);
		zone.assignRPObjectID(plantGrower);
		plantGrower.setX(3);
		plantGrower.setY(6);
		plantGrower.setDescription("Haizen tends to put his magic drinks here.");
		plantGrower.setToFullGrowth();

		zone.add(plantGrower);
		StendhalRPRuleProcessor.get().getPlantGrowers().add(plantGrower);
	}

	private Item addPersistentItem(String name, StendhalRPZone zone, int x, int y) {
		Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(name);
		zone.assignRPObjectID(item);
		item.setX(x);
		item.setY(y);
		item.put("persistent", 1);
		zone.add(item);
		return item;
	}
}
