/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import org.codehaus.groovy.control.CompilationFailedException;

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * A base factory for <code>ConditionAndActionArea</code> objects.
 */
public class ConditionAndActionAreaFactory implements ConfigurableFactory {

	/**
	 * Extracts the height from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The height, 1 if unspecified.
	 */
	protected int getHeight(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("height", 1);
	}

	/**
	 * Extracts the width from context.
	 *
	 * @param ctx
	 *            The configuration context.
	 * @return The width, 1 if unspecified.
	 */
	protected int getWidth(final ConfigurableFactoryContext ctx) {
		return ctx.getInt("width", 1);
	}

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
	 * Create a damaging area.
	 *
	 * @param ctx
	 *            Configuration context.
	 *
	 * @return A OnePlayerArea.
	 *
	 * @see OnePlayerArea
	 */
	@Override
	public Object create(final ConfigurableFactoryContext ctx) {
		return new ConditionAndActionArea(getCondition(ctx), getAction(ctx), getWidth(ctx), getHeight(ctx));
	}
}
