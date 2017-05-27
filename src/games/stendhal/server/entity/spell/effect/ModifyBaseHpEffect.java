package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
/**
 * An Effect that temporarily modifies a player's base_hp
 *
 * @author madmetzger
 */
public class ModifyBaseHpEffect extends AbstractEffect {

	public ModifyBaseHpEffect(Nature nature, int amount, int atk, int def,
			double lifesteal, int rate, int regen, double modifier) {
		super(nature, amount, atk, def, lifesteal, rate, regen, modifier);
	}

	@Override
	public void act(Player caster, Entity target) {
		actInternal(caster, (RPEntity) target);
	}

	private void actInternal(Player caster, RPEntity target) {
	}

}
