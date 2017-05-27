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
package games.stendhal.server.core.scripting;

import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

public class ScriptingNPC extends SpeakerNPC {

	public ScriptingNPC(final String name) {
		super(name);
		initHP(100);
	}

	// TODO: use message constants from Behaviours.java
	public void behave(final String method, final String reply) {
		if ("greet".equalsIgnoreCase(method)) {
			addGreeting(reply);
		} else if ("job".equalsIgnoreCase(method)) {
			addJob(reply);
		} else if ("help".equalsIgnoreCase(method)) {
			addHelp(reply);
		} else if ("quest".equalsIgnoreCase(method)) {
			addQuest(reply);
		} else if ("bye".equalsIgnoreCase(method)) {
			addGoodbye(reply);
		} else {
			addReply(method, reply);
		}
	}

	public void behave(final String method, final List<String> triggers, final String reply)
			throws NoSuchMethodException {
		if ("reply".equalsIgnoreCase(method)) {
			addReply(triggers, reply);
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method
					+ ") not supported.");
		}
	}

	public void behave(final List<String> triggers, final String reply) {
		addReply(triggers, reply);
	}

	public void behave(final String method, final Map<String, Integer> items)
			throws NoSuchMethodException {
		if ("buy".equalsIgnoreCase(method)) {
			new BuyerAdder().addBuyer(this, new BuyerBehaviour(items), true);
		} else if ("sell".equalsIgnoreCase(method)) {
			new SellerAdder().addSeller(this, new SellerBehaviour(items));
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method
					+ ") not supported.");
		}
	}

	public void behave(final String method, final int cost) throws NoSuchMethodException {
		if ("heal".equalsIgnoreCase(method)) {
			new HealerAdder().addHealer(this, cost);
		} else {
			throw new NoSuchMethodException("Behaviour.add(" + method
					+ ") not supported.");
		}
	}

	@Override
	protected void createPath() {
		// do nothing
	}

	@Override
	protected void createDialog() {
		// do nothing
	}

}
