package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;

import java.util.Map;

public class SixthFloorCreatures implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSixthFloor(zone, attributes);
	}

	private void buildSixthFloor(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();

		final Creature creature = manager.getCreature("littlefairy");
		final Creature creature1 = manager.getCreature("pegasus");
		final Creature creature2 = manager.getCreature("unicorn");
		final Creature creature3 = manager.getCreature("archangel");

		creature.clearDropItemList();
		creature1.clearDropItemList();
		creature2.clearDropItemList();
		creature3.clearDropItemList();

		creature.setXP(0);
		creature1.setXP(0);
		creature2.setXP(0);
		creature3.setXP(0);
		
		creature.setPosition(15,28);
		creature1.setPosition(29,15);
		creature2.setPosition(1,15);
		creature3.setPosition(14,2);

		zone.add(creature);
		zone.add(creature1);
		zone.add(creature2);
		zone.add(creature3);
	}
}
