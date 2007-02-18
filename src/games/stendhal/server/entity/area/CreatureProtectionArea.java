/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.area;

//
//

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.LinkedList;
import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;

/**
 * An area prevents creatures from entering. This allows a layered chain
 * of criteria. Think of this as a creature firewall.
 */
public class CreatureProtectionArea extends Entity {
	/**
	 * The logger instance.
	 */
	private static final Logger	logger =
				Log4J.getLogger(CreatureProtectionArea.class);

	/**
	 * Whether to block on no match.
	 */
	protected boolean		defaultBlocked;

	/**
	 * The list (if any specific) of blocked creatues.
	 */
	protected List<Entry>		entries;

	/**
	 * The area height.
	 */
	protected int			height;

	/**
	 * The area width.
	 */
	protected int			width;


	/**
	 * Create a 1x1 creature protection area.
	 */
	public CreatureProtectionArea() throws AttributeNotFoundException {
		this(1, 1);
	}


	/**
	 * Create a creature protection area.
	 *
	 * @param	width		The area width.
	 * @param	height		The area height.
	 */
	public CreatureProtectionArea(int width, int height)
	 throws AttributeNotFoundException {
		this(width, height, true);
	}


	/**
	 * Create a creature protection area.
	 *
	 * @param	width		The area width.
	 * @param	height		The area height.
	 * @param	defaultBlocked	Whether blocked on no match.
	 */
	public CreatureProtectionArea(int width, int height,
	 boolean defaultBlocked) throws AttributeNotFoundException {
		put("type", "creature_protection_area");
		put("server-only", "");

		this.width = width;
		this.height = height;
		this.defaultBlocked = defaultBlocked;

		entries = new LinkedList<Entry>();
	}


	//
	// CreatureProtectionArea
	//

	/**
	 * Add a blocked criteria entry.
	 *
	 * @param	clazz		A creature class to match
	 *				(or <code>null</code> for any).
	 */
	public void add(String clazz) {
		add(clazz, null);
	}


	/**
	 * Add a blocked criteria entry.
	 *
	 * @param	clazz		A creature class to match
	 *				(or <code>null</code> for any).
	 * @param	subclazz	A creature subclass to match
	 *				(or <code>null</code> for any).
	 */
	public void add(String clazz, String subclazz) {
		add(clazz, subclazz, true);
	}


	/**
	 * Add a criteria entry.
	 *
	 * @param	clazz		A creature class to match
	 *				(or <code>null</code> for any).
	 * @param	subclazz	A creature subclass to match
	 *				(or <code>null</code> for any).
	 * @param	blocked		Whether to block.
	 */
	public void add(String clazz, String subclazz, boolean blocked) {
		entries.add(new Entry(clazz, subclazz, blocked));
	}


	/**
	 * Does a creature match a criteria entry.
	 *
	 * @param	creature	The creature to compare.
	 * @param	defaultAnswer	The answer if no match is found.
	 *
	 * @return	The matching criteria, or default response.
	 */
	protected boolean matchesCriteria(Creature creature,
	 boolean defaultAnswer) {
		String	clazz;
		String	subclazz;


		/**
		 * No class/subclass defined?
		 */
		if(!creature.has("class") || !creature.has("subclass"))
			return false;

		clazz = creature.get("class");
		subclazz = creature.get("subclass");

		for(Entry entry : entries) {
			if(entry.matches(clazz, subclazz))
				return entry.isBlocked();
		}

		return defaultAnswer;
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
	 * Checks whether a creature can enter.
	 *
	 * @return	<code>true</code> if a matching creature is given.
	 */
	public boolean isObstacle(Entity entity) {
		/*
		 * Only applies to Creature's
		 */
		if(!(entity instanceof Creature))
			return false;

		return matchesCriteria((Creature) entity, defaultBlocked);
	}

	//
	//

	/**
	 * An entry representing creature criteria.
	 */
	protected static class Entry {
		/**
		 * Whether it should be blocked.
		 */
		protected boolean	blocked;

		/**
		 * The creature class to match.
		 */
		protected String	clazz;

		/**
		 * The creature subclass to match.
		 */
		protected String	subclazz;


		/**
		 * Create a criteria entry.
		 *
		 * @param	clazz		A creature class to match
		 *				(or <code>null</code> for any).
		 * @param	subclazz	A creature subclass to match
		 *				(or <code>null</code> for any).
		 * @param	blocked		Whether it should be blocked.
		 */
		public Entry(String clazz, String subclazz, boolean blocked) {
			this.clazz = clazz;
			this.subclazz = subclazz;
			this.blocked = blocked;
		}


		//
		// Entry
		//

		/**
		 * Determine if a creature matching this criteria is blocked.
		 *
		 * @return	<code>true</code> if it should be blocked.
		 */
		public boolean isBlocked() {
			return blocked;
		}


		/**
		 * Check if a class/subclass matches.
		 *
		 *
		 *
		 */
		public boolean matches(String clazz, String subclazz) {
			if((this.clazz != null)
			 && !clazz.equals(this.clazz)) {
				return false;
			}

			if((this.subclazz != null) &&
			 !subclazz.equals(this.subclazz)) {
				return false;
			}

			return true;
		}
	}
}
