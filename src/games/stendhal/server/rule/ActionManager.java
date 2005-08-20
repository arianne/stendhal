/*
 * ActionManager.java
 *
 * Created on 20. August 2005, 11:27
 *
 */

package games.stendhal.server.rule;

/**
 * Ruleset Interface for processing actions in Stendhal.
 *
 * @author Matthias Totz
 */
public interface ActionManager
{
  void onAttack();
  void onHit();
  void onDead();
  void onEquip();
  void onTalk();
}
