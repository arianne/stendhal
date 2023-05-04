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

import games.stendhal.server.entity.RPEntity;


/**
 * Weapon implementation.
 */
public interface WeaponImpl {

  /**
   * Action when item is used as a weapon.
   *
   * @param target
   *   Entity targeted by attacker.
   * @param attacker
   *   Attacking entity.
   */
  void onAttackAttempt(final RPEntity target, final RPEntity attacker);

  /**
   * Action when item is used as a weapon.
   *
   * @param target
   *   Entity targeted by attacker.
   * @param attacker
   *   Attacking entity.
   * @param damage
   *   Amount of damage resulting from attack.
   */
  void onAttackSuccess(final RPEntity target, final RPEntity attacker, final int damage);
}
