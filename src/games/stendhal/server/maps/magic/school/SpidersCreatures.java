package games.stendhal.server.maps.magic.school;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.KillNotificationCreature;
import games.stendhal.server.entity.mapstuff.spawner.KillNotificationCreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * Configure Magic School Cellar.
 */
public class SpidersCreatures implements ZoneConfigurator {
	private final List<String> creatures = 
		Arrays.asList("spider","poisonous spider","giant spider");
	

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicSchoolCellarArea(zone, attributes);
	}
	
	public void updatePlayerQuest(final CircumstancesOfDeath circ) {
		final Player player = (Player) circ.getKiller();
		final String victim = circ.getVictim().getName();
		player.setQuest("kill_all_spiders", 1+creatures.indexOf(victim), victim);
		
		Logger.getLogger(SpidersCreatures.class).debug(
				"in "+circ.getZone().getName()+
				": "+victim+
				" killed by "+player.getName());		
	}

	class SpidersObserver implements Observer {
		public void update(Observable o, Object arg) {
			updatePlayerQuest((CircumstancesOfDeath) arg);			
		}
	}
	
	private void buildMagicSchoolCellarArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		final SpidersObserver observer = new SpidersObserver();
		KillNotificationCreature creature;
		KillNotificationCreatureRespawnPoint point;
		
		// spider
		creature = new KillNotificationCreature(manager.getCreature("spider"));
		point = new KillNotificationCreatureRespawnPoint(
				zone, 15, 16, creature, 1, observer);
		zone.add(point);
		// poisonous spider
		creature = new KillNotificationCreature(manager.getCreature("poisonous spider"));
		point = new KillNotificationCreatureRespawnPoint(
				zone, 13, 4, creature, 1, observer);
		zone.add(point);
		// giant spider
		creature = new KillNotificationCreature(manager.getCreature("giant spider"));
		point = new KillNotificationCreatureRespawnPoint(
				zone, 9, 9, creature, 1, observer);
		zone.add(point);
		
	}
}
