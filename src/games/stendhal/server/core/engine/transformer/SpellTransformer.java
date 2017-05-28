package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.spell.Spell;
import marauroa.common.game.RPObject;
/**
 * Transformer for spells
 *
 * @author madmetzger
 */
public class SpellTransformer implements Transformer {

	@Override
	public RPObject transform(RPObject object) {
		Spell spell = SingletonRepository.getEntityManager().getSpell(object.get("subclass"));
		if(spell != null) {
			//preserve the id of the transformed spell
			spell.setID(object.getID());
		}
		return spell;
	}

}
