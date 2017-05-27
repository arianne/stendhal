/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.block;

import org.codehaus.groovy.control.CompilationFailedException;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * a factory for movable block targets
 *
 * @author hendrik
 */
public class BlockTargetFactory implements ConfigurableFactory{

	@Override
	public Object create(ConfigurableFactoryContext ctx) {
		final String description = ctx.getString("description", "");
		final String shape = ctx.getString("shape", null);
		BlockTarget target = new BlockTarget(shape);

		if (description != null) {
			target.setDescription(description);
		}

		try {
			String condition = ctx.getString("condition", null);
			if (condition != null) {
				ChatCondition created = createCondition(condition);
				if(created != null) {
					target.setCondition(created);
				}
			}
			String action = ctx.getString("action", null);
			if (action != null) {
				ChatAction created = createAction(action);
				if(created != null) {
					target.setAction(created);
				}
			}
		} catch (CompilationFailedException e) {
			throw new IllegalArgumentException(e);
		}

		return target;
	}

	/**
	 * Create a ChatAction
	 *
	 * @param action the configuration String
	 * @return the action or null
	 * @throws CompilationFailedException
	 */
	private ChatAction createAction(String action)
			throws CompilationFailedException {
		final GroovyShell interp = createGroovyShell();
		String code = "import games.stendhal.server.entity.npc.action.*;\r\n"
			+ action;
		ChatAction created = (ChatAction) interp.evaluate(code);
		return created;
	}

	/**
	 * Create a ChatCondtion
	 *
	 * @param condition the configuration String
	 * @return the condition or null
	 * @throws CompilationFailedException
	 */
	private ChatCondition createCondition(String condition)
			throws CompilationFailedException {
		final GroovyShell interp = createGroovyShell();
		String code = "import games.stendhal.server.entity.npc.condition.*;\r\n"
			+ condition;
		ChatCondition created = (ChatCondition) interp.evaluate(code);
		return created;
	}

	/**
	 * Create a GroovyShell
	 *
	 * @return a fresh GroovyShell
	 */
	private GroovyShell createGroovyShell() {
		Binding groovyBinding = new Binding();
		final GroovyShell interp = new GroovyShell(groovyBinding);
		return interp;
	}

}
