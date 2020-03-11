/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;


public class FlyOverArea extends InvisibleEntity {

	@Override
	public boolean isObstacle(final IEntity entity) {
		if (entity instanceof ActiveEntity) {
			if (((ActiveEntity) entity).isFlying()) {
				return false;
			}
		}

		return super.isObstacle(entity);
	}
}
