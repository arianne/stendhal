package games.stendhal.server.entity.npc.condition;

import java.util.Collection;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
/**
 * Checks if player has visited certain zones
 *  
 * @author madmetzger
 */
public class PlayerVisitedZonesCondition implements ChatCondition {

	private final String region;
	
	private final Boolean exterior;
	
	private final Boolean aboveGround;
	
	private final Boolean accessible;
	
	/**
	 * Creates a new PlayerVisitedZonesCondition
	 * 
	 * @param region the name of the region to consider
	 * @param exterior 
	 * @param aboveGround
	 * @param accessible
	 */
	public PlayerVisitedZonesCondition(String region, Boolean exterior,
			Boolean aboveGround, Boolean accessible) {
		this.region = region;
		this.exterior = exterior;
		this.aboveGround = aboveGround;
		this.accessible = accessible;
	}
	
	/**
	 * Creates a new PlayerVisitedZonesCondition
	 * 
	 * @param region the name of the region to consider
	 * @param exterior 
	 * @param aboveGround
	 */
	public PlayerVisitedZonesCondition(String region, Boolean exterior,
			Boolean aboveGround) {
		this(region, exterior, aboveGround, Boolean.TRUE);
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		Collection<StendhalRPZone> zones = SingletonRepository.getRPWorld().getAllZonesFromRegion(region, exterior, aboveGround, accessible);
		for(StendhalRPZone zone : zones) {
			if(!player.hasVisitedZone(zone)) {
				return false;
			}
		}
		return true;
	}

}
