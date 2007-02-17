/*
 * @(#) src/games/stendhal/server/entity/OnePlayerArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity;

//
//

import java.awt.geom.Rectangle2D;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.MovementListener;

/**
 * An area that only allows one play at a time to enter.
 * This currently does not account for teleporting into.
 */
public class OnePlayerArea extends Entity implements MovementListener {
	/**
	 * The logger instance.
	 */
	private static final Logger	logger =
					Log4J.getLogger(OnePlayerArea.class);

	/**
	 * The area height.
	 */
	protected int			height;

	/**
	 * The area width.
	 */
	protected int			width;

	/**
	 * The entity ID currently in the area (if any).
	 */
	protected Entity.ID		occupant;


	/**
	 * Create a one player area.
	 *
	 * @param	width		The area width.
	 * @param	height		The area height.
	 */
	public OnePlayerArea(int width, int height)
	 throws AttributeNotFoundException {
		put("type", "one_player_area");
		put("server-only", "");

		this.width = width;
		this.height = height;

		occupant = null;
	}


	//
	// OnePlayerArea
	//

	/**
	 * Verify the occupant is still there (just incase).
	 */
	protected void validateOccupant() {
		IRPZone		zone;
		Entity		entity;


		if(occupant != null) {
			zone = getZone();

			if(!zone.has(occupant)) {
				occupant = null;
			} else {
				entity = (Entity) zone.get(occupant);

				if(!getArea().intersects(entity.getArea())) {
					occupant = null;
				}
			}
		}
	}


	//
	// Entity
	//

	/**
	 * Get the entity's area.
	 *
	 * @param	rect		The rectangle to fill in.
	 * @param	x		The X coordinate.
	 * @param	y		The Y coordinate.
	 */
	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, width, height);
	}


	/**
	 * Checks whether players, NPC's, etc. can walk over this entity.
	 *
	 * @return	<code>true</code> if an RPEntity is given and it
	 *		is occupied by someone else.
	 */
	public boolean isObstacle(Entity entity) {
		/*
		 * Only applies to RPEntity's
		 */
		if(!(entity instanceof RPEntity))
			return false;

		/*
		 * Special exception for ghosts
		 * (should this method even get called for ghosts???)
		 */
		if(entity.isGhost())
			return false;

		validateOccupant();

		return ((occupant != null)
			&& !occupant.equals(entity.getID()));
	}


	/**
	 * Called when this object is added to a zone.
	 *
	 * @param	zone		The zone this was added to.
	 */
	public void onAdded(StendhalRPZone zone) {
		super.onAdded(zone);
		zone.addMovementListener(this);
	}


	/**
	 * Called when this object is being removed from a zone.
	 *
	 * @param	zone		The zone this will be removed from.
	 */
	public void onRemoved(StendhalRPZone zone) {
		zone.removeMovementListener(this);
		super.onRemoved(zone);
	}


	/**
	 * Attribute(s) updated.
	 */
	public void update() throws AttributeNotFoundException {
		StendhalRPZone	zone;


		super.update();

		/*
		 * Reregister incase coordinates changed (could be smarter)
		 */
		zone = getZone();
		zone.removeMovementListener(this);
		zone.addMovementListener(this);
	}


	//
	// MovementListener
	//

	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param	entity		The RPEntity who moved.
	 * @param	zone		The new zone.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onEntered(RPEntity entity, StendhalRPZone zone,
	 int newX, int newY) {
		/*
		 * Ghosts don't occupy normal space
		 */
		if(entity.isGhost())
			return;

		/*
		 * Just players (for now?)
		 */
		if(entity instanceof Player)
		{
			validateOccupant();

			/*
			 * Check to make sure things aren't buggy
			 */
			if((occupant != null)
			 && !occupant.equals(entity.getID())) {
				logger.warn("Existing occupant!");
			} else {
				occupant = entity.getID();
			}
		}
	}


	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param	entity		The RPEntity who entered.
	 * @param	zone		The old zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 *
	 */
	public void onExited(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY) {
		/*
		 * Check occupant incase a ghost or teleporter entered.
		 */
		if((occupant != null) && occupant.equals(entity.getID()))
			occupant = null;
	}


	/**
	 * Invoked when an entity moves while over the object area.
	 *
	 * @param	entity		The RPEntity who left.
	 * @param	zone		The zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onMoved(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY, int newX, int newY) {
	}
}
