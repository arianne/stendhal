package games.stendhal.server.entity.spell;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.ModifyBaseHpEffect;
import marauroa.common.game.RPObject;
/**
 * A spell to modify an RPEntity's base hp
 *
 * @author madmetzger
 */
public class ModifyHpSpell extends Spell {

	/**
	 * Create a new ModifyHpSpell
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
	public ModifyHpSpell(String name, Nature nature, int amount, int atk,
			int cooldown, int def, double lifesteal, int mana,
			int minimumlevel, int range, int rate, int regen, double modifier) {
		super(name, nature, amount, atk, cooldown, def, lifesteal, mana, minimumlevel,
				range, rate, regen, modifier);
	}

	/**
	 * Create a new Spell from a RPObject
	 *
	 * @param object
	 */
	public ModifyHpSpell(RPObject object) {
		super(object);
	}

	@Override
	protected void doEffects(Player caster, Entity target) {
		new ModifyBaseHpEffect(getNature(), getAmount(), getAtk(), getDef(), getLifesteal(), getRate(), getRegen(), getModifier()).act(caster, target);
	}

}
