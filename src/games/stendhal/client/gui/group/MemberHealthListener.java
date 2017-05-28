/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.group;

import games.stendhal.client.entity.EntityChangeListener;
import games.stendhal.client.entity.RPEntity;

/**
 * Listener for entity HP ratio changes.
 */
class MemberHealthListener implements EntityChangeListener<RPEntity> {
	private final Member member;

	/**
	 * Create a new MemberHealthListener.
	 *
	 * @param member member whose hp ratio should be changed when the entity's
	 * 	hp ratio changes
	 */
	MemberHealthListener(final Member member) {
		this.member = member;
	}

	@Override
	public void entityChanged(RPEntity entity, Object property) {
		if (property == RPEntity.PROP_HP_RATIO) {
			member.setHpRatio(entity.getHpRatio());
		}
	}
}
