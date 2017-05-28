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
package games.stendhal.client.scripting;

import games.stendhal.client.actions.SlashAction;
import games.stendhal.common.ErrorBuffer;

/**
 * Command line parser for the Stendhal client.
 *
 * @author Martin Fuchs
 */
class SlashActionCommand extends ErrorBuffer {

	private String name;
	private SlashAction action;

	private String[] params;
	private String remainder;

	/**
	 * @return action object
	 */
	SlashAction getAction() {
		return action;
	}

	/**
	 * sets the action to be parsed.
	 *
	 * @param action
	 *            the action to be parsed
	 */
	void setAction(final SlashAction action) {
		this.action = action;
	}

	/**
	 * return command name.
	 *
	 * @return command name
	 */
	public String getName() {
		return name;
	}

	/**
	 * return command parameters.
	 *
	 * @return parameter array
	 */
	String[] getParams() {
		return params;
	}

	/**
	 * set command parameters.
	 *
	 * @param params
	 *            parameter array
	 */
	void setParams(final String[] params) {
		this.params = params;
	}

	/**
	 * return trailing parameter text.
	 *
	 * @return remainder
	 */
	public String getRemainder() {
		return remainder;
	}

	/**
	 * sets the trailing text.
	 *
	 * @param remainder
	 *            the trailing text
	 */
	void setRemainder(final String remainder) {
		this.remainder = remainder;
	}

	/**
	 * set command name.
	 *
	 * @param name
	 *            the command name
	 */
	void setName(final String name) {
		this.name = name;
	}

}
