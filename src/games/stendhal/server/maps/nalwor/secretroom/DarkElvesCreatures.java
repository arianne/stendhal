package games.stendhal.server.maps.nalwor.secretroom;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.CircumstancesOfDeath;
import games.stendhal.server.entity.creature.KillNotificationCreature;
import games.stendhal.server.entity.mapstuff.spawner.KillNotificationCreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.magic.school.SpidersCreatures;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

/**
 * Configure secret room.
 */
public class DarkElvesCreatures implements ZoneConfigurator {
	private final String QUEST_SLOT = "kill_dark_elves";
	// be sure to have synchronized creatures lists with DrowCreatures.class
	private final List<String> creatures = 
		Arrays.asList("dark elf captain"
				      ,"dark elf general"
				     );
	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSecretRoomArea(zone, attributes);
	}
	
	/**
	 * function will fill information about victim to killer's quest slot.
	 * @param circ - information about victim,zone and killer.
	 */	
	private void updatePlayerQuest(final CircumstancesOfDeath circ) {
		final RPEntity killer = circ.getKiller();
		final String victim = circ.getVictim().getName();
		Logger.getLogger(SpidersCreatures.class).debug(
				"in "+circ.getZone().getName()+
				": "+circ.getVictim().getName()+
				" killed by "+circ.getKiller().getName());
		
		// check if was killed by other animal/pet
		if(!circ.getKiller().getClass().getName().equals(Player.class.getName()) ) {
			return;
		}
		final Player player = (Player) killer;
		// check if player started his quest already
		if(!player.getQuest(QUEST_SLOT, 0).equals("started")) {
			return;
		}
		int slot=creatures.indexOf(victim);
		if(slot!=-1) {
			player.setQuest(QUEST_SLOT, 1+slot, victim);
		}
	}
	
	class DrowObserver implements Observer {
		public void update(Observable o, Object arg) {
			updatePlayerQuest((CircumstancesOfDeath) arg);
		}
	}

	private void buildSecretRoomArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		Observer observer = new DrowObserver();
		KillNotificationCreature creature;
		KillNotificationCreatureRespawnPoint point;
		// drow captain
		creature = new KillNotificationCreature(manager.getCreature("dark elf captain"));
		point = new KillNotificationCreatureRespawnPoint(zone, 8, 7, creature, 1, observer);
		zone.add(point);
		// drow general
		creature = new KillNotificationCreature(manager.getCreature("dark elf general"));
		point = new KillNotificationCreatureRespawnPoint(zone, 16, 6, creature, 1, observer);
		zone.add(point);	
	}
}
