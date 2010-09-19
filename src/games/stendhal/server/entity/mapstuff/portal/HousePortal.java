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

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.Type;

import games.stendhal.common.Constants;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.HouseKey;

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
		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = user.getSlot(slotName);

			for (final RPObject object : slot) {
				if (!(object instanceof HouseKey)) {
					continue;
				}

				final HouseKey key = (HouseKey) object;
				if (key.matches(getDoorId(), getLockNumber())) {
					// TODO: Remove the key naming support, and just return.  
					// The renaming code does not need to be here forever, just some
					// months, so that most of the player's keys get tagged with
					// an owner. Comment added 2009-03-13 
					key.setup(getDoorId(), getLockNumber(), getOwner());
					
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
}
