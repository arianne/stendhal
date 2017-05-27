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

import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;


/**
 * A sign rented by a player for a fixed amount of time.
 *
 * @author hendrik
 */
public class RentedSign extends Sign implements StorableEntity {
	public static final String RPCLASS_NAME = "rented_sign";
	private static final String RENTER = "renter";
	private static final String TIMESTAMP = "timestamp";

	public static void generateRPClass() {
		final RPClass clazz = new RPClass(RPCLASS_NAME);
		clazz.isA("sign");
		clazz.addAttribute(RENTER, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(TIMESTAMP, Type.FLOAT, Definition.HIDDEN);
	}

	/**
	 * Creates a RentedSign.
	 *
	 * @param renter player who rented this sign
	 * @param text text to display on this sign
	 */
	public RentedSign(final Player renter, final String text) {
		setRPClass(RPCLASS_NAME);
		store();
		put(RENTER, renter.getName());
		put(TIMESTAMP, System.currentTimeMillis());
		super.setText(text);
	}

	/**
	 * Creates a RentedSign based on an existing RPObject. This is just for loading
	 * a sign from the database, use the other constructors.
	 * @param rpobject
	 */
	public RentedSign(final RPObject rpobject) {
	    super(rpobject);
	    store();
    }

	/**
	 * returns the owner.
	 *
	 * @return name of owner
	 */
    public String getRenter() {
    	return get(RENTER);
    }

    /**
     * Returns the timestamp when this entity was created.
     *
     * @return timestamp in milliseconds
     */
	public long getTimestamp() {
		return (long) Float.parseFloat(get(TIMESTAMP));
	}

	@Override
	public String describe() {
		String text = super.describe();

		// add renter and age to the text. We use a relative time because
		// a fixed timestamp is meaningless for players from
		// other timezones.
		final int seconds = (int) ((System.currentTimeMillis() - getTimestamp()) / 1000);
		text = text + "\nThis sign was rented by " + get(RENTER) + " " + TimeUtil.approxTimeUntil(seconds) + " ago.";
		return text;
	}

}
