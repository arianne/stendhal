/***************************************************************************
 *                    Copyright © 2011-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
