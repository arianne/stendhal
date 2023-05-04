/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item;

import java.util.Map;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.status.PoisonAttacker;
import games.stendhal.server.entity.status.StatusAttacker;


public class Projectile extends StackableItem implements WeaponImpl {

  /**
   * Creates a new projectile item.
   *
   * @param name
   *   Name of the item.
   * @param clazz
   *   Class of item.
   * @param subclass
   *   Subclass of item.
   * @param attributes
   *   Projectile attributes (such as attack). May be empty or `null`.
   */
  public Projectile(final String name, final String clazz, final String subclass,
      final Map<String, String> attributes) {
    super(name, clazz, subclass, attributes);
  }

  /**
   * Copy constructor.
   *
   * @param item
   *   Item to be copied.
   */
  public Projectile(final Projectile item) {
    super(item);
  }

  @Override
  public void onAttackAttempt(final RPEntity target, final RPEntity attacker) {
    // do nothing
  }

  @Override
  public void onAttackSuccess(final RPEntity target, final RPEntity attacker, final int damage) {
    // try to inflict status effects
    for (final StatusAttacker sa: getStatusAttackers()) {
      // poison is handled different than other status attackers
      if (sa instanceof PoisonAttacker) {
        sa.onAttackAttempt(target, attacker);
      } else {
        sa.onHit(target, attacker, damage);
      }
    }
  }
}
