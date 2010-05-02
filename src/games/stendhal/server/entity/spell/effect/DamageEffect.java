package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

public class DamageEffect extends AbstractEffect {

	public DamageEffect(Nature nature, int amount, int atk, int def,
			double lifesteal, int rate, int regen) {
		super(nature, amount, atk, def, lifesteal, rate, regen);
	}

	public void act(Player caster, Entity target) {
		actInternal(caster, (RPEntity) target);
	}
	
	private void actInternal(Player caster, RPEntity target) {
		
	}

}
