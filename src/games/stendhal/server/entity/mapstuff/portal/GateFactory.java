/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import games.stendhal.server.core.config.factory.ConfigurableFactory;
import games.stendhal.server.core.config.factory.ConfigurableFactoryContext;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * creates a gate
 */
public class GateFactory implements ConfigurableFactory {

	@Override
	public Object create(ConfigurableFactoryContext ctx) {
		final String orientation = ctx.getRequiredString("orientation");
		final String image = ctx.getRequiredString("image");
		final int autoclose = ctx.getInt("autoclose", 0);
		final String id = ctx.getString("identifier", null);
		final String message = ctx.getString("message", null);

		ChatCondition condition = null;
		final String condString = ctx.getString("condition", null);
		if (condString != null) {
			final GroovyShell interp = new GroovyShell(new Binding());
			String code = "import games.stendhal.server.entity.npc.condition.*;\r\n"
				+ condString;
			try {
				condition = (ChatCondition) interp.evaluate(code);
			} catch (CompilationFailedException e) {
				throw new IllegalArgumentException(e);
			}
		}

		final Gate gate = new Gate(orientation, image, condition);

		gate.setAutoCloseDelay(autoclose);
		gate.setRefuseMessage(message);
		gate.setIdentifier(id);
		return gate;
	}

}
