/*
 * DefaultRuleMamager.java
 *
 * Created on 20. August 2005, 12:25
 *
 */

package games.stendhal.server.rule.defaultruleset;

import games.stendhal.server.rule.EntityManager;
import games.stendhal.server.rule.RuleManager;

/**
 * RuleSet-Manager for the default ruleset
 * @author Matthias totz
 */
public class DefaultRuleManager implements RuleManager
{
  public DefaultRuleManager()
  {
  }

  /** returns the EntityManager for the default ruleset */
  public EntityManager getEntityManager()
  {
    return DefaultEntityManager.getInstance();
  }
  
}
