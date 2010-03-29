package games.stendhal.server.maps.nalwor.tunnel;

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
 * Configure Drow Tunnel -1 to include a Thing Creature who carries an amulet. 
 * Then it should give an amulet that is bound to the player.
 */
public class DrowCreatures implements ZoneConfigurator {

	
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
		point = new KillNotificationCreatureRespawnPoint(zone, 39, 67, creature, 1, observer);
		zone.add(point);
		// drow capitan
		creature = new KillNotificationCreature(manager.getCreature("dark elf captain"));
		point = new KillNotificationCreatureRespawnPoint(zone, 35, 52, creature, 1, observer);
		zone.add(point);
		// drow archer
		creature = new KillNotificationCreature(manager.getCreature("dark elf archer"));
		point = new KillNotificationCreatureRespawnPoint(zone, 38, 59, creature, 1, observer);
		zone.add(point);	
		// drow archer
		creature = new KillNotificationCreature(manager.getCreature("dark elf archer"));
		point = new KillNotificationCreatureRespawnPoint(zone, 38, 45, creature, 1, observer);
		zone.add(point);
		// drow archer
		creature = new KillNotificationCreature(manager.getCreature("dark elf archer"));
		point = new KillNotificationCreatureRespawnPoint(zone, 34, 31, creature, 1, observer);
		zone.add(point);
		// drow elite archer
		creature = new KillNotificationCreature(manager.getCreature("dark elf elite archer"));
		point = new KillNotificationCreatureRespawnPoint(zone, 29, 28, creature, 1, observer);
		zone.add(point);
		// drow knight
		creature = new KillNotificationCreature(manager.getCreature("dark elf knight"));
		point = new KillNotificationCreatureRespawnPoint(zone, 33, 45, creature, 1, observer);
		zone.add(point);
		// drow knight
		creature = new KillNotificationCreature(manager.getCreature("dark elf knight"));
		point = new KillNotificationCreatureRespawnPoint(zone, 39, 41, creature, 1, observer);
		zone.add(point);
		// drow wizard
		creature = new KillNotificationCreature(manager.getCreature("dark elf wizard"));
		point = new KillNotificationCreatureRespawnPoint(zone, 30, 38, creature, 1, observer);
		zone.add(point);
		// drow sacerdotist
		creature = new KillNotificationCreature(manager.getCreature("dark elf sacerdotist"));
		point = new KillNotificationCreatureRespawnPoint(zone, 24, 28, creature, 1, observer);
		zone.add(point);
		// drow viceroy
		creature = new KillNotificationCreature(manager.getCreature("dark elf viceroy"));
		point = new KillNotificationCreatureRespawnPoint(zone, 39, 28, creature, 1, observer);
		zone.add(point);
		// drow matronmother
		creature = new KillNotificationCreature(manager.getCreature("dark elf matronmother"));
		point = new KillNotificationCreatureRespawnPoint(zone, 31, 16, creature, 1, observer);
		zone.add(point);
	}
}
