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

import games.stendhal.server.entity.Entity;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * are persistent arrest warrant.
 *
 * @author hendrik
 */
public class ArrestWarrant extends Entity implements StorableEntity {
	public static final String RPCLASS_NAME = "arrest_warrant";
	private static final String CRIMINAL = "criminal";
	private static final String POLICE_OFFICER = "police_officer";
	private static final String MINUTES = "minutes";
	private static final String REASON = "reason";
	private static final String TIMESTAMP = "timestamp";
	private static final String STARTED = "started";

	public static void generateRPClass() {
		final RPClass clazz = new RPClass(RPCLASS_NAME);
		clazz.isA("entity");
		clazz.addAttribute(CRIMINAL, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(POLICE_OFFICER, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(MINUTES, Type.INT, Definition.HIDDEN);
		clazz.addAttribute(REASON, Type.LONG_STRING, Definition.HIDDEN);
		clazz.addAttribute(TIMESTAMP, Type.FLOAT, Definition.HIDDEN);
		clazz.addAttribute(STARTED, Type.FLAG, Definition.HIDDEN);
	}

	/**
	 * Creates an ArrestWarrant.
	 *
	 * @param criminalName  name of criminal to be jailed
	 * @param policeOfficer name of police officer who issued the /jail command
	 * @param minutes time of sentence
	 * @param reason reason
	 */
	public ArrestWarrant(final String criminalName, final String policeOfficer, final int minutes, final String reason) {
		setRPClass(RPCLASS_NAME);
		store();
		hide();
		put(CRIMINAL, criminalName);
		put(POLICE_OFFICER, policeOfficer);
		put(MINUTES, minutes);
		put(REASON, reason);
		put(TIMESTAMP, System.currentTimeMillis());
	}

	/**
	 * creates an ArrestWarrant based on a deserialized RPObject;
	 * use the other constructor.
	 *
	 * @param rpobject RPObject
	 */
	public ArrestWarrant(final RPObject rpobject) {
		super(rpobject);
		store();
		hide();
	}

	/**
	 * Gets the name of the criminal.
	 *
	 * @return name of criminal
	 */
	public String getCriminal() {
		return get(CRIMINAL);
	}

	/**
	 * has the criminal started his jail time?
	 *
	 * @return true iff started
	 */
	public boolean isStarted() {
		return has(STARTED);
	}

	/**
	 * The criminal has started his jail time.
	 */
	public void setStarted() {
		put(STARTED, "");
	}

	/**
	 * Returns the time of the sentence.
	 *
	 * @return time in minutes
	 */
	public int getMinutes() {
		return getInt(MINUTES);
	}

	/**
	 * Returns the name of the police officer.
	 *
	 * @return name of player who issued /jail
	 */
	public String getPoliceOfficer() {
		return get(POLICE_OFFICER);
	}

	/**
	 * Returns the reason text.
	 * @return reason
	 */
	public String getReason() {
		return get(REASON);
	}

	/**
	 * Returns the timestamp of the sentence.
	 *
	 * @return timestamp
	 */
	public long getTimestamp() {
		return (long) Float.parseFloat(get(TIMESTAMP));
	}
}
