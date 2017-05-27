/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.spell.effect;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
/**
 * Interface for effects that can be applied between a player and any entity
 *
 * @author madmetzger
 */
public interface Effect {

	/**
	 * applies the effect
	 *
	 * @param caster
	 * @param target
	 */
	public void act(Player caster, Entity target);

}
