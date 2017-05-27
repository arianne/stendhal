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
package games.stendhal.server.entity.trade;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
/**
 * Represents the earned sum of money for a sold {@link Offer} in the {@link Market}
 *
 * @author madmetzger
 */
public class Earning extends Entity implements Dateable {

	/**
	 * RPClass name for an Earning
	 */
	public static final String EARNING_RPCLASS_NAME = "earning";

	private static final String VALUE_ATTRIBUTE = "value";
	private static final String REWARD_ATTRIBUTE = "reward";
	private static final String NAME_ATTRIBUTE = "sellerName";
	private static final String TIMESTAMP_ATTRIBUTE = "timestamp";

	private final Integer value;
	private final String sellerName;

	public static void generateRPClass() {
		final RPClass earningClass = new RPClass(EARNING_RPCLASS_NAME);
		earningClass.isA("entity");
		earningClass.addAttribute(VALUE_ATTRIBUTE, Type.INT);
		earningClass.addAttribute(NAME_ATTRIBUTE, Type.STRING);
		earningClass.addAttribute(REWARD_ATTRIBUTE, Type.INT);
		earningClass.addAttribute(TIMESTAMP_ATTRIBUTE, Type.STRING);
	}

	/**
	 * Constructs Earning for sold price.
	 * @param value	Earned money
	 * @param sellerName	Name of the selling player
	 * @param shouldReward	True if the trade should be rewarded in trade score
	 */
	public Earning(final Integer value, final String sellerName, final boolean shouldReward) {
		super();
		setRPClass(EARNING_RPCLASS_NAME);
		hide();
		put(VALUE_ATTRIBUTE, value);
		this.value = value;
		this.sellerName = sellerName;
		put(NAME_ATTRIBUTE, sellerName);
		put(REWARD_ATTRIBUTE, shouldReward ? 1 : 0);
		put(TIMESTAMP_ATTRIBUTE, Long.toString(System.currentTimeMillis()));
	}

	/**
	 * Create an Earning from a RPObject
	 *
	 * @param object
	 */
	public Earning(final RPObject object) {
		super(object);
		setRPClass(EARNING_RPCLASS_NAME);
		value = getInt(VALUE_ATTRIBUTE);
		sellerName = get(NAME_ATTRIBUTE);
	}

	/**
	 * @return the earned money
	 */
	public Integer getValue() {
		return this.value;
	}

	/**
	 * @return the name of the selling player
	 */
	public String getSeller() {
		return this.sellerName;
	}

	/**
	 * @return true iff the earning player should get the trading bonus for this earning
	 */
	public boolean shouldReward() {
		return (getInt(REWARD_ATTRIBUTE) != 0);
	}

	@Override
	public long getTimestamp() {
		long timeStamp = 0;
		try {
			timeStamp = Long.parseLong(get(TIMESTAMP_ATTRIBUTE));
		} catch (final NumberFormatException e) {
			Logger.getLogger(Earning.class).error("Invalid timestamp: " + get(TIMESTAMP_ATTRIBUTE), e);
		}
		return timeStamp;
	}

}
