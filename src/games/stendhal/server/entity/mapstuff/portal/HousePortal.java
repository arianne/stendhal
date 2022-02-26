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
package games.stendhal.server.entity.mapstuff.portal;

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.slot.Slots;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * A portal that can be used with a matching <code>HouseKey</code>.
 * The requirements are saved with the portal.
 */
public class HousePortal extends AccessCheckingPortal {
	private static final String RPCLASS_NAME = "house_portal";

	/** attribute name for the door identifier string. */
	private static final String DOOR_ID = "door_id";
	/** attribute name for the door owner string. */
	private static final String OWNER = "owner";
	/** attribute name for the lock number. */
	private static final String LOCK_NUMBER = "lock_number";
	/** attribute name for the time when the house would be confiscated. */
	private static final String EXPIRES = "expires";

	// these 3 are needed to save the data for linking the portals to each other
	/** attribute name for storing the destination zone name .*/
	private static final String DESTINATION_ZONE = "destination_zone";
	/** attribute name for storing the referance name of the destination portal. */
	private static final String DESTINATION_ID = "destination_id";
	/** the attribute name for the map reference id of this portal. */
	private static final String PORTAL_REFERENCE = "reference";

	private boolean needsSaving = false;

	public static void generateRPClass() {
		if (!RPClass.hasRPClass(RPCLASS_NAME)) {
			final RPClass entity = new RPClass(RPCLASS_NAME);
			entity.isA("portal");
			entity.addAttribute(DOOR_ID, Type.STRING);
			entity.addAttribute(OWNER, Type.STRING);
			entity.addAttribute(LOCK_NUMBER, Type.INT);
			entity.addAttribute(EXPIRES, Type.STRING);
			entity.addAttribute(DESTINATION_ZONE, Type.STRING);
			entity.addAttribute(DESTINATION_ID, Type.STRING);
			entity.addAttribute(PORTAL_REFERENCE, Type.STRING);
			entity.addAttribute("associated_zones", Type.STRING, Definition.VOLATILE);
		}
	}

	/**
	 * Create a HousePortal.
	 *
	 * @param doorId the door identifier
	 */
	public HousePortal(final String doorId) {
		super("The door is locked.");
		setRPClass(RPCLASS_NAME);
		put("type", "house_portal");
		put(DOOR_ID, doorId);
		put(OWNER, "");
		put(LOCK_NUMBER, 0);
		store();
	}

	/**
	 * Create a HousePortal from an <code>RPObject</code>. Used when restoring from the database.
	 *
	 * @param object the corresponding <code>RPObject</code>
	 */
	public HousePortal(final RPObject object) {
		super(object);
		setRPClass(RPCLASS_NAME);
		put("type", "house_portal");
		super.setDestination(get(DESTINATION_ZONE), idToObject(DESTINATION_ID));
		super.setIdentifier(idToObject(PORTAL_REFERENCE));
		setRejectedMessage("The door is locked.");

	   	store();
	}

	/**
	 * Sets the owner of this portal.
	 *
	 * @param owner name of the owner.
	 */
	public void setOwner(final String owner) {
		put(OWNER, owner);
		requestSave();
	}

	// treating the portal references the same way as PortalSetupXMLReader
	/**
	 * Get the door reference as an <code>Object</code>.
	 *
	 * @param attribute name of the attribute to fetch the reference from
	 *
	 * @return portal reference in the form that <code>Portal</code> accepts it
	 */
	private Object idToObject(final String attribute) {
		final String idString = get(attribute);
		Object id;
		try {
			id = Integer.valueOf(idString);
		} catch (final NumberFormatException ex) {
			id = idString;
		}

		return id;
	}

	@Override
	public void setDestination(final String zone, final Object id) {
		super.setDestination(zone, id);
		// Save zone and id information. They are needed when restoring from db
		put(DESTINATION_ZONE, zone);
		put(DESTINATION_ID, id.toString());
	}

	@Override
	public void setIdentifier(final Object id) {
		super.setIdentifier(id);
		// Save the reference name of this portal. Needed when restoring from the db.
		put(PORTAL_REFERENCE, id.toString());
	}

	@Override
	public String describe() {
		final String owner = getOwner();
		final String description;
		if (owner.length() > 0) {
			description = "Here lives " + owner + ".";
		} else {
			description = "For sale!";
		}

		return description;
	}

	@Override
	protected boolean isAllowed(final RPEntity user) {
		// check if the player is carrying a matching HouseKey
		// HoukeKeys can not be found with getAllEquipped, because they override
		// getName()
		for (RPSlot slot : user.slots(Slots.CARRYING)) {

			for (final RPObject object : slot) {
				if (keyMatches(object)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check if an RPObject, or any of the objects it contains is a HouseKey
	 * matching this portal.
	 *
	 * @param obj checked object
	 * @return <code>true</code> if a match was found, <code>false<code>
	 * 	otherwise
	 */
	private boolean keyMatches(RPObject obj) {
		if (obj instanceof HouseKey) {
			final HouseKey key = (HouseKey) obj;
			if (key.matches(getDoorId(), getLockNumber())) {
				/*
				 * Key renaming support. Before the stored portals the house
				 * keys did not have owner names, so the names are added when
				 * the correct portal is found. There likely aren't many such
				 * keys left, but keeping the support does not cost much.
				 */
				key.setup(getDoorId(), getLockNumber(), getOwner());
				return true;
			} else {
				return false;
			}
		}
		for (RPSlot slot : obj.slots()) {
			for (RPObject subobj : slot) {
				if (keyMatches(subobj)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Get the identification string of this portal.
	 *
	 * @return the identification string
	 */
	public String getDoorId() {
		return get(DOOR_ID);
	}


	/**
	 * Get the house number of this portal.
	 *
	 * @return the house number, or 0 if house number not found
	 */
	public int getPortalNumber() {
		final String doorId = getDoorId();
		final String[] parts = doorId.split(" ");
		int id = 0;
		if (parts.length > 2) {
			id = MathHelper.parseIntDefault(parts[2], 0);
		}
		return id;
	}

	/**
	 * Get the name of the owner.
	 *
	 * @return owner
	 */
	public String getOwner() {
		return get(OWNER);
	}

	/**
	 * Get the number of the lock.
	 *
	 * @return number of the lock
	 */
	public int getLockNumber() {
		return this.getInt(LOCK_NUMBER);
	}

	/**
	 * Increase the lock number by one.
	 */
	public void changeLock() {
		put(LOCK_NUMBER, getInt(LOCK_NUMBER) + 1);

		requestSave();
	}

	/**
	 * Get the expiration time of the portal.
	 *
	 * @return time in milliseconds
	 */
	public long getExpireTime() {
		return Long.parseLong(get(EXPIRES));
	}

	/**
	 * Set the expiration time of the portal.
	 *
	 * @param time time in milliseconds
	 */
	public void setExpireTime(final long time) {
		put(EXPIRES, Long.toString(time));

		requestSave();
	}

	/**
	 * Request saving the zone where the portal is located.
	 * Multiple requests within a turn get merged to one.
	 */
	private void requestSave() {
		/*
		 * Avoid saving the zone three times when a new house is bought
		 */
		needsSaving = true;
		SingletonRepository.getTurnNotifier().notifyInTurns(1, new TurnListener() {
			@Override
			public void onTurnReached(int turn) {
				if (needsSaving) {
					needsSaving = false;
					saveToDatabase();
				}
			}
		});
	}

	/**
	 * Save the zone (and the portal along it)
	 */
	private void saveToDatabase() {
		StendhalRPZone zone = this.getZone();
		if (zone != null) {
			zone.storeToDatabase();
		}
	}

	/**
	 * Sets other zones that should hear knocking on door.
	 *
	 * @param zones
	 *     Comma-separated string of zone names.
	 */
	public void setAssociatedZones(final String zones) {
		put("associated_zones", zones);
	}

	/**
	 * Gets other zones that should hear knocking on door.
	 */
	public String getAssociatedZones() {
		return get("associated_zones");
	}

	/**
	 * Gets other zones that should hear knocking on door.
	 */
	public List<String> getAssociatedZonesList() {
		return Arrays.asList(getAssociatedZones().split(","));
	}
}
