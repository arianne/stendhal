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
package games.stendhal.client.gui.wt;

import games.stendhal.client.actions.SlashActionRepository;

/**
 * This is a 'button' command list
 * 
 * @author kymara
 */
public class ButtonCommandList extends CommandList {
	
	private static final long serialVersionUID = -1607102841664745919L;
		
	/**
	 * Create an button command list.
	 * 
	 * @param name
	 *            The menu name (needed?).
	 * @param items
	 *            The action names.
	 */
	public ButtonCommandList(final String name, final String[] items) {
		super(name, items);
	}

	/** an action has been chosen. 
	 * @param command */
	@Override
	protected void doAction(String command) {
		// the commands from PopUpMenuOpener may have had spaces, strip these out
		command = command.replace(" ","");
		SlashActionRepository.get(command).execute(null, null);
	}

}
