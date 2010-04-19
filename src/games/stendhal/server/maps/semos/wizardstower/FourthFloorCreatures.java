package games.stendhal.server.maps.semos.wizardstower;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;

import java.util.HashMap;
import java.util.Map;

public class FourthFloorCreatures implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildFourthFloor(zone, attributes);
	}

	private void buildFourthFloor(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();

		final Creature creature = manager.getCreature("water elemental");
		final Creature creature1 = manager.getCreature("ice elemental");
		final Creature creature2 = manager.getCreature("ice giant");
		final Creature creature3 = manager.getCreature("ice golem");

		creature.setAiProfiles(new HashMap<String, String>());
		creature1.setAiProfiles(new HashMap<String, String>());
		creature2.setAiProfiles(new HashMap<String, String>());
		creature3.setAiProfiles(new HashMap<String, String>());	

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
		creature3.setPosition(15,2);

		creature.setDirection(Direction.UP);
		creature1.setDirection(Direction.LEFT);
		creature2.setDirection(Direction.RIGHT);

		zone.add(creature);
		zone.add(creature1);
		zone.add(creature2);
		zone.add(creature3);
	}
}
