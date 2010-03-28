package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;

import java.util.Map;

public class SecondFloorCreatures implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSecondFloor(zone, attributes);
	}

	private void buildSecondFloor(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();

		final Creature creature = manager.getCreature("air elemental");
		final Creature creature1 = manager.getCreature("madaram windwalker");
		final Creature creature2 = manager.getCreature("djinn");
		final Creature creature3 = manager.getCreature("invisible man");

		creature1.setName("cloudwalker");
		creature2.setName("djinn windmaster");
		creature3.setName("incorporeal man");

		creature.clearDropItemList();
		creature1.clearDropItemList();
		creature2.clearDropItemList();
		creature3.clearDropItemList();

		creature.setXP(0);
		creature1.setXP(0);
		creature2.setXP(0);
		creature3.setXP(0);
		
		creature.setPosition(29,15);
		creature1.setPosition(1,15);
		creature2.setPosition(15,28);
		creature3.setPosition(15,2);

		zone.add(creature);
		zone.add(creature1);
		zone.add(creature2);
		zone.add(creature3);
	}
}
