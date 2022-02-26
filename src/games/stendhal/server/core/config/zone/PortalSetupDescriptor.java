/*
 * @(#) src/games/stendhal/server/config/zone/PortalSetupDescriptor.java
 *
 * $Id$
 */

package games.stendhal.server.core.config.zone;

import java.util.Arrays;

import org.apache.log4j.Logger;

//
//

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.EntityFactoryHelper;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;

/**
 * A portal setup descriptor.
 */
public class PortalSetupDescriptor extends EntitySetupDescriptor {
	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(PortalSetupDescriptor.class);

	/**
	 * The named portal identifier.
	 */
	protected Object identifier;

	/**
	 * The destination zone name (if any).
	 */
	protected String destinationZone;

	protected String associatedZones;

	/**
	 * The named destination portal (if any).
	 */
	protected Object destinationIdentifier;

	/**
	 * Whether replacing an existing portal at that location.
	 */
	protected boolean replacing;

	/**
	 * Create a portal setup descriptor.
	 *
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 * @param identifier
	 *            The identifier,
	 */
	public PortalSetupDescriptor(final int x, final int y,
			final Object identifier) {
		super(x, y);

		this.identifier = identifier;

		destinationZone = null;
		destinationIdentifier = null;
		replacing = false;
	}

	//
	// PortalSetupDescriptor
	//

	/**
	 * Get the destination identifier.
	 *
	 * @return An identifier.
	 */
	public Object getDestinationIdentifier() {
		return destinationIdentifier;
	}

	/**
	 * Get the destination zone.
	 *
	 * @return A zone name.
	 */
	public String getDestinationZone() {
		return destinationZone;
	}

	/**
	 * Get the identifier.
	 *
	 * @return An identifier.
	 */
	public Object getIdentifier() {
		return identifier;
	}

	/**
	 * Determine if existing portals are replaced.
	 *
	 * @return <code>true</code> if replacing an existing portal at that
	 *         location.
	 */
	public boolean isReplacing() {
		return replacing;
	}

	/**
	 * Set the destination zone/identifier.
	 *
	 * @param zone
	 *            The destination zone name.
	 * @param identifier
	 *            The named destination portal.
	 */
	public void setDestination(final String zone, final Object identifier) {
		this.destinationZone = zone;
		this.destinationIdentifier = identifier;
	}

	/**
	 * Set whether to replace any existing portal.
	 *
	 * @param replacing
	 *            Whether replacing an existing portal at that location.
	 */
	public void setReplacing(final boolean replacing) {
		this.replacing = replacing;
	}

	/**
	 * For house portals.
	 */
	public void setAssociatedZones(final String zones) {
		associatedZones = zones;
	}

	//
	// SetupDescriptor
	//

	/**
	 * Do appropriate zone setup.
	 *
	 * @param zone
	 *            The zone.
	 */
	@Override
	public void setup(final StendhalRPZone zone) {
		String className = getImplementation();

		if (className == null) {
			/*
			 * Default implementation
			 */
			className = Portal.class.getName();
		}

		try {
			final Portal portal = (Portal) EntityFactoryHelper.create(className,
					getParameters(), getAttributes());
			if (portal == null) {
				logger.warn("Unable to create portal: " + className);

				return;
			}

			portal.setPosition(getX(), getY());
			portal.setIdentifier(getIdentifier());

			final Object destIdentifier = getDestinationIdentifier();

			if (destIdentifier != null) {
				portal.setDestination(getDestinationZone(), destIdentifier);
			}

			if (portal instanceof HousePortal && associatedZones != null) {
				((HousePortal) portal).setAssociatedZones(associatedZones);
			}

			// Set facing direction for portal used as destination.
			if (portal.has("face")) {
				portal.setFaceDirection(portal.get("face"));
			}

			// Check for an existing portal at the location
			final Portal oportal = zone.getPortal(getX(), getY());
			if (oportal != null) {
				if (isReplacing()) {
					// copy owner attributes from old portal
					if (oportal instanceof HousePortal) {
						for (final String attr: Arrays.asList(
								"owner", "lock_number", "expires")) {
							final String atocopy = ((HousePortal) oportal).get(attr);
							if (atocopy != null) {
								((HousePortal) portal).put(attr, atocopy);
							}
						}
					}

					logger.debug("Replacing portal: " + oportal);
					zone.remove(oportal);
				} else {
					// reserved, and told not to replace it. just discard the portal
					return;
				}
			}

			zone.add(portal);
		} catch (final IllegalArgumentException ex) {
			logger.error("Error with portal factory", ex);
		}
	}
}
