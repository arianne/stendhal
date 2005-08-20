/*
 * RuleSetFactory.java
 *
 * Created on 20. August 2005, 12:27
 *
 */

package games.stendhal.server.rule;

import games.stendhal.server.rule.defaultruleset.DefaultRuleManager;

/**
 * Factory class for retrieving a ruleset.
 *
 * @author Matthias Totz
 */
public class RuleSetFactory
{
  
  /** Creates a new instance of RuleSetFactory */
  private RuleSetFactory()
  {
  }
  
  /** returns the ruleset with the given name */
  public static RuleManager getRuleSet(String name)
  {
    return new DefaultRuleManager();
  }
  
  
}
