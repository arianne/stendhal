/*
 * @(#) src/games/stendhal/server/entity/area/CreatureProtectionArea.java
 *
 * $Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;

/**
 * An area prevents creatures from entering. This allows a layered chain of
 * criteria. Think of this as a creature firewall.
 */
public class CreatureProtectionArea extends AreaEntity {

	/**
	 * Whether to block on no match.
	 */
	protected boolean defaultBlocked;

	/**
	 * The list (if any specific) of blocked creatures.
	 */
	protected List<Entry> entries;

	/**
	 * Create a 1x1 creature protection area.
	 */
	public CreatureProtectionArea() {
		this(1, 1);
	}

	/**
	 * Create a creature protection area.
	 *
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 */
	public CreatureProtectionArea(final int width, final int height) {
		this(width, height, true);
	}

	/**
	 * Create a creature protection area.
	 *
	 * @param width
	 *            The area width.
	 * @param height
	 *            The area height.
	 * @param defaultBlocked
	 *            Whether blocked on no match.
	 */
	public CreatureProtectionArea(final int width, final int height, final boolean defaultBlocked) {
		super(width, height);

		hide();
		setResistance(0);

		this.defaultBlocked = defaultBlocked;

		entries = new LinkedList<Entry>();
	}

	//
	// CreatureProtectionArea
	//

	/**
	 * Add a blocked criteria entry.
	 *
	 * @param clazz
	 *            A creature class to match (or <code>null</code> for any).
	 */
	public void add(final String clazz) {
		add(clazz, null);
	}

	/**
	 * Add a blocked criteria entry.
	 *
	 * @param clazz
	 *            A creature class to match (or <code>null</code> for any).
	 * @param subclazz
	 *            A creature subclass to match (or <code>null</code> for any).
	 */
	public void add(final String clazz, final String subclazz) {
		add(clazz, subclazz, true);
	}

	/**
	 * Add a criteria entry.
	 *
	 * @param clazz
	 *            A creature class to match (or <code>null</code> for any).
	 * @param subclazz
	 *            A creature subclass to match (or <code>null</code> for any).
	 * @param blocked
	 *            Whether to block.
	 */
	public void add(final String clazz, final String subclazz, final boolean blocked) {
		entries.add(new Entry(clazz, subclazz, blocked));
	}

	/**
	 * Does a creature match a criteria entry.
	 *
	 * @param creature
	 *            The creature to compare.
	 * @param defaultAnswer
	 *            The answer if no match is found.
	 *
	 * @return The matching criteria, or default response.
	 */
	protected boolean matchesCriteria(final Creature creature, final boolean defaultAnswer) {
		String clazz;
		String subclazz;

		/*
		 * Allow for optional class data. Technically all creatures should at
		 * least have an entity class type.
		 */
		if (creature.has("class")) {
			clazz = creature.get("class");
		} else {
			clazz = "";
		}

		if (creature.has("subclass")) {
			subclazz = creature.get("subclass");
		} else {
			subclazz = "";
		}

		for (final Entry entry : entries) {
			if (entry.matches(clazz, subclazz)) {
				return entry.isBlocked();
			}
		}

		return defaultAnswer;
	}

	//
	// Entity
	//

	/**
	 * Checks whether a creature can enter.
	 *
	 * @return <code>true</code> if a matching creature is given.
	 */
	@Override
	public boolean isObstacle(final Entity entity) {
		/*
		 * Only applies to Creature's
		 */
		if (!(entity instanceof Creature)) {
			return super.isObstacle(entity);
		}

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
		protected boolean blocked;

		/**
		 * The creature class to match.
		 */
		protected String clazz;

		/**
		 * The creature subclass to match.
		 */
		protected String subclazz;

		/**
		 * Create a criteria entry.
		 *
		 * @param clazz
		 *            A creature class to match (or <code>null</code> for
		 *            any).
		 * @param subclazz
		 *            A creature subclass to match (or <code>null</code> for
		 *            any).
		 * @param blocked
		 *            Whether it should be blocked.
		 */
		public Entry(final String clazz, final String subclazz, final boolean blocked) {
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
		 * @return <code>true</code> if it should be blocked.
		 */
		public boolean isBlocked() {
			return blocked;
		}

		/**
		 * Check if a class/subclass matches.
		 * @param clazz
		 * @param subclazz
		 * @return true if both are equals to fields
		 *
		 *
		 *
		 */
		public boolean matches(final String clazz, final String subclazz) {
			if ((this.clazz != null) && !clazz.equals(this.clazz)) {
				return false;
			}

			if ((this.subclazz != null) && !subclazz.equals(this.subclazz)) {
				return false;
			}

			return true;
		}
	}
}
