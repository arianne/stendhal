package games.stendhal.server.entity.spell;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.ModifySpeedEffect;
import marauroa.common.game.RPObject;
/**
 * Slow down spell
 *
 * @author madmetzger
 */
public class SlowDownSpell extends Spell {

	public SlowDownSpell(final String name, final Nature nature, final int amount, final int atk,
			final int cooldown, final int def, final double lifesteal, final int mana,
			final int minimumlevel, final int range, final int rate, final int regen, double modifier) {
		super(name, nature, amount, atk, cooldown, def, lifesteal, mana, minimumlevel,
				range, rate, regen, modifier);
	}

	public SlowDownSpell(final RPObject object) {
		super(object);
	}

	@Override
	protected void doEffects(final Player caster, final Entity target) {
		new ModifySpeedEffect(getNature(), getAmount(), getAtk(), getDef(), getLifesteal(), getRate(), getRegen(), getModifier()).act(caster, target);
	}

}
