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
package games.stendhal.server.entity.item;

import games.stendhal.server.entity.RPEntity;

import java.util.Map;

public class RingOfLife extends Ring {

	public RingOfLife(final String name, final String clazz, final String subclass, final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	public RingOfLife(final RingOfLife item) {
		super(item);
	}
	
	
	
	public RingOfLife() {
		super("emerald ring", "ring", "emerald-ring", null);
		put("amount", 1);
		  
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		return true;
	}
	
	public boolean isBroken() {
		return  getInt("amount") == 0;
	}

	public void damage() {
		put("amount", 0);
	}
	
	public void repair() {
		put("amount", 1);
	}
	

	/**
	 * Gets the description.
	 * 
	 * The description of RingOfLife depends on the ring's state.
	 * 
	 * @return The description text.
	 */
	@Override
	public String describe() {
		String text;
		if (isBroken()) {
			text = "You see an emerald ring, known as the ring of life. The gleam is lost from the stone and it has no powers.";
		} else {
			text = "You see an emerald ring, known as the ring of life. Wear it, and you risk less from death.";
		}
		
		if (isBound()) {
			text = text + " It is a special quest reward for " + getBoundTo()
					+ ", and cannot be used by others.";
		}
		return text;
	}
}
