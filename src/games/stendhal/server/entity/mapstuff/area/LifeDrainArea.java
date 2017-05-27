/*
 * @(#) src/games/stendhal/server/entity/area/LifeDrainArea.java
 *
 *$Id$
 */

package games.stendhal.server.entity.mapstuff.area;

import games.stendhal.server.entity.RPEntity;

/**
 * An area that drains an RPEntity of HP while over it.
 *
 */
public class LifeDrainArea extends OccupantArea {

	/**
	 * The minimum damage inflicted.
	 */
	protected int minimumDamage;

	/**
	 * The ratio of HP to inflicted.
	 */
	protected double damageRatio;

	/**
	 * Create a damaging area.
	 *
	 * @param width
	 *            Width of this area.
	 * @param height
	 *            Height of this area.
	 * @param interval
	 *            How often damage is given while stationary (in turns).
	 * @param damageRatio
	 *            The ratio of damage to inflict.
	 * @param minimumDamage
	 *            The minimum damage to inflict.
	 */
	public LifeDrainArea(final int width, final int height, final int interval,
			final double damageRatio, final int minimumDamage) {
		super(width, height, interval);

		this.damageRatio = damageRatio;
		this.minimumDamage = minimumDamage;

		setResistance(50);
	}

	//
	// LifeDrainArea
	//

	/**
	 * Inflict damage on an entity.
	 *
	 * @param entity
	 *            The entity to damage.
	 *
	 * @return <code>false</code> if this entity should be removed from
	 *         further processing, <code>true</code> otherwise.
	 */
	protected boolean doDamage(final RPEntity entity) {
		final int hp = entity.getHP();

		/*
		 * Don't beat a dead horse!
		 */
		if (hp == 0) {
			return false;
		}

		/*
		 * Can't touch a ghost
		 */
		if (entity.isGhost()) {
			return true;
		}

		int damage = (int) (hp * damageRatio);

		damage = Math.max(damage, minimumDamage);
		damage = Math.min(damage, hp);

		if (damage != 0) {
			entity.onDamaged(this, damage);
		}

		return true;
	}

	//
	// OccupantArea
	//

	/**
	 * An entity has entered the area. This should not apply any actions that
	 * <code>handleMovement()</code> does.
	 *
	 * @param entity
	 *            The RPEntity that was added.
	 *
	 * @return <code>false</code> if this entity should not be processed,
	 *         <code>true</code> otherwise.
	 */
	@Override
	protected boolean handleAdded(final RPEntity entity) {
		if (!super.handleAdded(entity)) {
			return false;
		}

		entity.rememberAttacker(this);
		return doDamage(entity);
	}

	/**
	 * Apply actions done at regular intervals.
	 *
	 * @param entity
	 *            The RPEntity occupant.
	 *
	 * @return <code>false</code> if this entity should be removed from
	 *         further processing, <code>true</code> otherwise.
	 */
	@Override
	protected boolean handleInterval(final RPEntity entity) {
		return doDamage(entity);
	}

	/**
	 * An entity has left the area. This should not apply any actions that
	 * <code>handleMovement()</code> does.
	 *
	 * @param entity
	 *            The RPEntity that was added.
	 */
	@Override
	protected void handleRemoved(final RPEntity entity) {
		entity.stopAttacking(this);
		super.handleRemoved(entity);
	}
}
