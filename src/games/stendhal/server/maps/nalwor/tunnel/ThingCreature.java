package games.stendhal.server.maps.nalwor.tunnel;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure Drow Tunnel -1 to include a Thing Creature who carries an amulet. 
 * Then it should give an amulet that is bound to the player.
 */
public class ThingCreature implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildDrowTunnelArea(zone, attributes);
	}

	private void buildDrowTunnelArea(StendhalRPZone zone, Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(manager.getCreature("thing"), "amulet");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 32, 5, creature, 1);
		zone.add(point);
	}
}
