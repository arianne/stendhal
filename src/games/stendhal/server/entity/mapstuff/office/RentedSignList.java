/* $Id$ */
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
package games.stendhal.server.entity.mapstuff.office;

import java.awt.Shape;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;

/**
 * A list of RentedSign as frontend for the zone storage.
 *
 * @author hendrik
 */
public class RentedSignList extends StorableEntityList<RentedSign> {
	private static final long EXPIRE_TIMEOUT = MathHelper.MILLISECONDS_IN_ONE_DAY;

	/**
	 * Creates a new RentedSignList.
	 *
	 * @param zone  zone to store the rented signs in
	 * @param shape
	 */
	public RentedSignList(final StendhalRPZone zone, final Shape shape) {
		super(zone, shape, RentedSign.class);
		setupTurnNotifier(60 * 60);
	}

	@Override
    public String getName(final RentedSign rentedSign) {
		return rentedSign.getRenter();
    }

	@Override
	protected boolean shouldExpire(final RentedSign entity) {
		return entity.getTimestamp() + EXPIRE_TIMEOUT < System.currentTimeMillis();
	}

}
