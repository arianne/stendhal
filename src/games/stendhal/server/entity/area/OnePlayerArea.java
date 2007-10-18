/*
 * @(#) src/games/stendhal/server/entity/OnePlayerArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.events.MovementListener;

import java.awt.geom.Rectangle2D;

/**
 * An area that only allows one play at a time to enter. This currently does not
 * account for being "placed" into the zone.
 *
 *
 * XXX Changed to full zone scan to assure this is secure. XXX Will improve
 * later once all ways to enter a zone are accounted for. XXX Commented out code
 * is that used for fast (but unreliable) check.
 *
 */
public class OnePlayerArea extends AreaEntity implements LoginListener,
		MovementListener {
	// /**
	// * The entity ID currently in the area (if any).
	// */
	// protected Entity.ID occupant;

	/**
	 * Create a one player area.
	 *
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 */
	public OnePlayerArea(int width, int height) {
		super(width, height);

		put("server-only", "");
		LoginNotifier.get().addListener(this);
		// occupant = null;
	}

	//
	// OnePlayerArea
	//

	// /**
	// * Verify the occupant is still there (just incase).
	// */
	// protected void validateOccupant() {
	// IRPZone zone;
	// Entity entity;
	//
	//
	// if(occupant != null) {
	// zone = getZone();
	//
	// if(!zone.has(occupant)) {
	// occupant = null;
	// } else {
	// entity = (Entity) zone.get(occupant);
	//
	// if(!getArea().intersects(entity.getArea())) {
	// occupant = null;
	// }
	// }
	// }
	// }

	/**
	 * Slow occupant check (until all area entry methods are handled).
	 */
	protected boolean slowObstacleCheck(Entity entity) {
		Rectangle2D area;

		area = getArea();

		for (RPEntity zentity : getZone().getPlayerAndFriends()) {
			/*
			 * Ignore same entity
			 */
			if (zentity == entity) {
				continue;
			}

			/*
			 * Only players (ignore friends)
			 */
			if (!(zentity instanceof Player)) {
				continue;
			}

			/*
			 * Ghosts don't count
			 */
			if (zentity.isGhost()) {
				continue;
			}

			/*
			 * In area?
			 */
			if (area.intersects(zentity.getArea())) {
				return true;
			}
		}

		return false;
	}

	//
	// Entity
	//

	/**
	 * Checks whether players, NPC's, etc. can walk over this entity.
	 *
	 * @param entity
	 *            The entity trying to enter.
	 *
	 * @return <code>true</code> if an RPEntity is given and it is occupied by
	 *         someone else.
	 */
	@Override
	public boolean isObstacle(Entity entity) {
		/*
		 * Only applies to RPEntity's
		 */
		if (!(entity instanceof RPEntity)) {
			return super.isObstacle(entity);
		}

		/*
		 * Special exception for ghosts (should this method even get called for
		 * ghosts???)
		 */
		if (entity.isGhost()) {
			return false;
		}

		return slowObstacleCheck(entity);

		// validateOccupant();
		//
		// return ((occupant != null)
		// && !occupant.equals(entity.getID()));
	}

	// /**
	// * Called when this object is added to a zone.
	// *
	// * @param zone The zone this was added to.
	// */
	// public void onAdded(StendhalRPZone zone) {
	// super.onAdded(zone);
	// zone.addMovementListener(this);
	// }

	// /**
	// * Called when this object is being removed from a zone.
	// *
	// * @param zone The zone this will be removed from.
	// */
	// public void onRemoved(StendhalRPZone zone) {
	// zone.removeMovementListener(this);
	// super.onRemoved(zone);
	// }

	// /**
	// * Attribute(s) updated.
	// */
	// public void update() {
	// StendhalRPZone zone;
	//
	//
	// super.update();
	//
	// /*
	// * Reregister incase coordinates changed (could be smarter)
	// */
	// zone = getZone();
	// zone.removeMovementListener(this);
	// zone.addMovementListener(this);
	// }

	//
	// MovementListener
	//

	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param entity
	 *            The entity that moved.
	 * @param zone
	 *            The new zone.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	public void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX,
			int newY) {
		// /*
		// * Ghosts don't occupy normal space
		// */
		// if(entity.isGhost())
		// return;
		//
		// /*
		// * Just players (for now?)
		// */
		// if(entity instanceof Player)
		// {
		// validateOccupant();
		//
		// /*
		// * Check to make sure things aren't buggy
		// */
		// if((occupant != null)
		// && !occupant.equals(entity.getID())) {
		// logger.warn("Existing occupant!");
		// } else {
		// occupant = entity.getID();
		// }
		// }
	}

	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param entity
	 *            The entity that entered.
	 * @param zone
	 *            The old zone.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 *
	 */
	public void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY) {
		// /*
		// * Check occupant incase a ghost or teleporter entered.
		// */
		// if((occupant != null) && occupant.equals(entity.getID()))
		// occupant = null;
	}

	/**
	 * Invoked when an entity moves while over the object area.
	 *
	 * @param entity
	 *            The entity that left.
	 * @param zone
	 *            The zone.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	public void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX,
			int oldY, int newX, int newY) {
	}

	public void onLoggedIn(Player player) {
		if (this.getArea().contains(player.getX(), player.getY())) {
			player.teleport(StendhalRPWorld.get().getZone("0_semos_city"), 30,
					40, null, null);
		}
	}
}
