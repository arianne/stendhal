/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rule.defaultruleset;

import games.stendhal.server.core.rule.ActionManager;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.RuleManager;

/**
 * RuleSet-Manager for the default ruleset
 * 
 * @author Matthias totz
 */
public class DefaultRuleManager implements RuleManager {

	/** returns the EntityManager for the default ruleset */
	public EntityManager getEntityManager() {
		return DefaultEntityManager.getInstance();
	}

	/** returns the EntityManager for the default ruleset */
	public ActionManager getActionManager() {
		return DefaultActionManager.getInstance();
	}
}
