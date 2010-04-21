package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.DamageType;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
/**
 * Effect for healing a player
 * 
 * @author madmetzger
 */
public class HealingEffect extends AbstractEffect {

	/**
	 * Creates a new {@link HealingEffect}
	 * 
	 * @param nature
	 * @param amount
	 * @param atk
	 * @param def
	 * @param lifesteal
	 * @param rate
	 * @param regen
	 */
	public HealingEffect(DamageType nature, int amount, int atk, int def, double lifesteal, int rate,
			int regen) {
		super(nature, amount, atk, def, lifesteal, rate, regen);
	}

	public void act(Player caster, Entity target) {
		int oldHP = caster.getHP();
		int baseHP = caster.getBaseHP();
		caster.setHP(Math.max(oldHP + getAmount(), baseHP));
	}

}
