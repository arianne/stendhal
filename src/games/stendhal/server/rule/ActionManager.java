/*
 * ActionManager.java
 *
 * Created on 20. August 2005, 11:27
 *
 */

package games.stendhal.server.rule;

import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Equipable;

/**
 * Ruleset Interface for processing actions in Stendhal.
 *
 * @author Matthias Totz
 */
public interface ActionManager
{
  boolean onEquip(RPEntity entity, Equipable item);

  void onAttack();
  void onTalk();
}
