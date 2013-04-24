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

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

public class GateConfigurator implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final int x = MathHelper.parseInt(attributes.get("x"));
		final int y = MathHelper.parseInt(attributes.get("y"));
		final String orientation = attributes.get("orientation");
		final String image = attributes.get("image");
		final int autoclose = MathHelper.parseInt(attributes.get("autoclose"));
		final String id = attributes.containsKey("identifier") ? attributes.get("identifier") : null;
		
		ChatCondition condition = null;
		final String condString = attributes.get("condition");
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
		final String message = attributes.get("message");

		buildGate(zone, x, y, orientation, image, condition, autoclose, message, id);
	}
	
	/**
	 * Create the gate
	 * 
	 * @param zone the zone to add the gate to
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param orientation gate orientation
	 * @param image the gate image to be used
	 * @param condition conditions for allowing use
	 * @param autoclose delay in seconds before shutting the gate automatically,
	 * 	or 0 if it should stay open
	 * @param message message to send to the player if opening is refused
	 * @param id identifier of the gate used to match to a key
	 */
	private void buildGate(final StendhalRPZone zone, final int x, final int y, 
			final String orientation, final String image, ChatCondition condition,
			int autoclose, String message, String id) {
		final Gate gate = new Gate(orientation, image, condition);
		
		gate.setPosition(x, y);
		gate.setAutoCloseDelay(autoclose);
		gate.setRefuseMessage(message);
		gate.setIdentifier(id);
		zone.add(gate);
	}
}
