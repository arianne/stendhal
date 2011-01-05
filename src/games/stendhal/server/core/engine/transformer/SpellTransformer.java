package games.stendhal.server.core.engine.transformer;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.engine.transformer.Transformer;
import games.stendhal.server.entity.spell.HealingSpell;
import games.stendhal.server.entity.spell.Spell;
/**
 * Transformer for spells
 * 
 * @author madmetzger
 */
public class SpellTransformer implements Transformer {

	public RPObject transform(RPObject object) {
		return new HealingSpell(object);
	}

}
