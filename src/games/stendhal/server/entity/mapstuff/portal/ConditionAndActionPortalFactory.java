/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.portal;

import org.codehaus.groovy.control.CompilationFailedException;

import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * A factory for <code>ConditionCheckingPortal</code> objects.
 */
public class ConditionAndActionPortalFactory extends AccessCheckingPortalFactory {

	/**
	 * Extract the quest name from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The quest name.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected ChatCondition getCondition(final ConfigurableFactoryContext ctx) {
		String value = ctx.getString("condition", null);
		if (value == null) {
			return null;
		}
		Binding groovyBinding = new Binding();
		final GroovyShell interp = new GroovyShell(groovyBinding);
		try {
			String code = "import games.stendhal.server.entity.npc.condition.*;\r\n"
				+ value;
			return (ChatCondition) interp.evaluate(code);
		} catch (CompilationFailedException e) {
			throw new IllegalArgumentException(e);
		}
	}


	/**
	 * Extract the quest name from a context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The quest name.
	 * @throws IllegalArgumentException
	 *             If the quest attribute is missing.
	 */
	protected ChatAction getAction(final ConfigurableFactoryContext ctx) {
		String value = ctx.getString("action", null);
		if (value == null) {
			return null;
		}
		Binding groovyBinding = new Binding();
		final GroovyShell interp = new GroovyShell(groovyBinding);
		try {
			String code = "import games.stendhal.server.entity.npc.action.*;\r\n"
				+ value;
			return (ChatAction) interp.evaluate(code);
		} catch (CompilationFailedException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Create a condition checking portal.
	 *
	 * @param ctx
	 *            Configuration context.
	 * @return A ConditionCheckingPortal
	 * @throws IllegalArgumentException
	 *             If there is a problem with the attributes. The exception
	 *             message should be a value suitable for meaningful user
	 *             interpretation.
	 */
	@Override
	protected AccessCheckingPortal createPortal(final ConfigurableFactoryContext ctx) {
		String rejectedMessage = getStringValue(ctx, "rejected");
		ChatCondition condition = getCondition(ctx);
		ChatAction action = getAction(ctx);
		if (rejectedMessage != null) {
			return new ConditionAndActionPortal(condition, rejectedMessage, action);
		} else {
			return new ConditionAndActionPortal(condition, action);
		}
	}
}
