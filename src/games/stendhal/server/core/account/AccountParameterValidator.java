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
package games.stendhal.server.core.account;

import marauroa.common.game.Result;

/**
 * validates a parameter used during account creation.
 *
 * @author hendrik
 */
public interface AccountParameterValidator {

	/**
	 * validates a parameter provided for account creation.
	 *
	 * @return <code>null</code> in case the parameter is valid, or an error
	 *         otherwise
	 */
	Result validate();
}
