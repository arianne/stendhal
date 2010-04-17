package games.stendhal.server.entity.spell.effect;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

public interface Effect {
	
	public void act(Player caster, Entity target);

}
