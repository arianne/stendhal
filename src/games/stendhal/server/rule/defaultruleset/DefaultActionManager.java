/*
 * DefaultActionManager.java
 *
 * Created on 21. August 2005, 14:16
 *
 */

package games.stendhal.server.rule.defaultruleset;

import games.stendhal.server.rule.ActionManager;

/**
 *
 * @author Matthias Totz
 */
public class DefaultActionManager implements ActionManager
{
  
  /** Creates a new instance of DefaultActionManager */
  public DefaultActionManager()
  {
  }

  public boolean onEquip(games.stendhal.server.entity.RPEntity entity, games.stendhal.server.entity.item.Equipable item)
  {
    return false;
  }

  public void onTalk()
  {
  }

  public void onAttack()
  {
  }
  
}
