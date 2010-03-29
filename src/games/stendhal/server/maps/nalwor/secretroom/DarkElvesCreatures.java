package games.stendhal.server.maps.nalwor.secretroom;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.KillNotificationCreature;
import games.stendhal.server.entity.mapstuff.spawner.KillNotificationCreatureRespawnPoint;
import games.stendhal.server.maps.magic.school.SpidersCreatures;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * Configure secret room.
 */
public class DarkElvesCreatures implements ZoneConfigurator {

	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSecretRoomArea(zone, attributes);
	}
	
	private void updatePlayerQuest(final CircumstancesOfDeath circ) {
		Logger.getLogger(SpidersCreatures.class).debug(
				"in "+circ.getZone().getName()+
				": "+circ.getVictim().getName()+
				" killed by "+circ.getKiller().getName());
	}
	
	class DrowObserver implements Observer {
		@Override
		public void update(Observable o, Object arg) {
			updatePlayerQuest((CircumstancesOfDeath) arg);
		}
	}

	private void buildSecretRoomArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		Observer observer = new DrowObserver();
		KillNotificationCreature creature;
		KillNotificationCreatureRespawnPoint point;
		// drow capitan
		creature = new KillNotificationCreature(manager.getCreature("dark elf captain"));
		point = new KillNotificationCreatureRespawnPoint(zone, 8, 7, creature, 1, observer);
		zone.add(point);
		// drow general
		creature = new KillNotificationCreature(manager.getCreature("dark elf general"));
		point = new KillNotificationCreatureRespawnPoint(zone, 16, 6, creature, 1, observer);
		zone.add(point);	
	}
}
