package games.stendhal.server.entity.spell.effect;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
/**
 * Interface for effects that can be applied between a player and any entity
 *  
 * @author madmetzger
 */
public interface Effect {
	
	/**
	 * applies the effect
	 * 
	 * @param caster
	 * @param target
	 */
	public void act(Player caster, Entity target);

}
