package games.stendhal.server.maps.athor;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;

import java.util.Map;

public class USL1_Island implements ZoneConfigurator {
	DefaultEntityManager manager = (DefaultEntityManager)
	StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 * 
	 * @param 	zone		The zone to be configured.
	 * @param 	attributes	Configuration attributes.
	 */
	
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildLabyrinth(zone, attributes);
	}

	private void buildLabyrinth(StendhalRPZone zone,
			Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(
				manager.getCreature("minotaur_king"), "kokuda");

				CreatureRespawnPoint point =
					new CreatureRespawnPoint(zone, 83, 103, creature, 1);
				
				zone.addRespawnPoint(point);
		
		}
}
