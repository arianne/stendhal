package games.stendhal.server.maps.semos.village;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Configure semos village rats not to be cowards. (For helping newbies kill them)
 */
public class RatsCreatures implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildVillage(zone, attributes);
	}
	
	private void buildVillage(final StendhalRPZone zone, final Map<String, String> attributes) {
		
		for(CreatureRespawnPoint p:zone.getRespawnPointList()) {
			if(p!=null) {
				if("rat".equals(p.getPrototypeCreature().getName())) {
					// it is a rat, we will remove ai profile of stupid coward
					final Creature creature = p.getPrototypeCreature();
					final Map<String, String> aiProfiles = new HashMap<String, String>(creature.getAiProfiles());
					aiProfiles.remove("stupid coward");
					creature.setAiProfiles(aiProfiles);
				}
			}
		}
	}
}
