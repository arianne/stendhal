package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import java.util.Date;
/**
 * Boost an entity's def
 * 
 * @author madmetzger
 */
public class ModifyDefEffect extends AbstractEffect {

	public ModifyDefEffect(final Nature nature, final int amount, final int atk, final int def,
			final double lifesteal, final int rate, final int regen, double modifier) {
		super(nature, amount, atk, def, lifesteal, rate, regen, modifier);
	}

	public void act(final Player caster, final Entity target) {
		actInternal(caster, (RPEntity) target);
	}

	private void actInternal(Player caster, RPEntity target) {
		Date expire = new Date(System.currentTimeMillis() + getAmount()*1000);
		target.addDefModifier(expire, getModifier());
	}

}
