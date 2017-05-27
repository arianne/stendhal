package games.stendhal.server.entity.spell;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.ModifyDefEffect;
import marauroa.common.game.RPObject;
/**
 * Spell to modify a target's def value
 *
 * @author madmetzger
 */
public class ModifyDefSpell extends Spell {

	/**
	 * Create a new ModifyDefSpell
	 *
	 * @param name
	 * @param nature
	 * @param amount
	 * @param atk
	 * @param cooldown
	 * @param def
	 * @param lifesteal
	 * @param mana
	 * @param minimumlevel
	 * @param range
	 * @param rate
	 * @param regen
	 * @param modifier
	 */
	public ModifyDefSpell(String name, Nature nature, int amount, int atk,
			int cooldown, int def, double lifesteal, int mana,
			int minimumlevel, int range, int rate, int regen, double modifier) {
		super(name, nature, amount, atk, cooldown, def, lifesteal, mana, minimumlevel,
				range, rate, regen, modifier);
	}

	/**
	 * Create a ModifyDefSpell from a RPObject
	 *
	 * @param object
	 */
	public ModifyDefSpell(RPObject object) {
		super(object);
	}

	@Override
	protected void doEffects(Player caster, Entity target) {
		new ModifyDefEffect(getNature(), getAmount(), getAtk(), getDef(), getLifesteal(), getRate(), getRegen(), getModifier()).act(caster, target);
	}

	@Override
	protected boolean isTargetValid(Entity caster, Entity target) {
		return target instanceof RPEntity;
	}

}
