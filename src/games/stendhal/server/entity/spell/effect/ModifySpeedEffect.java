package games.stendhal.server.entity.spell.effect;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.GuidedEntity;
import games.stendhal.server.entity.player.Player;
/**
 * Slow down an entity
 * @author madmetzger
 */
public class ModifySpeedEffect extends AbstractEffect {

	public ModifySpeedEffect(final Nature nature, final int amount, final int atk, final int def,
			final double lifesteal, final int rate, final int regen, double modifier) {
		super(nature, amount, atk, def, lifesteal, rate, regen, modifier);
	}

	@Override
	public void act(final Player caster, final Entity target) {
		actInternal(caster, (GuidedEntity) target);
	}

	private void actInternal(Player caster, GuidedEntity target) {

	}

}
