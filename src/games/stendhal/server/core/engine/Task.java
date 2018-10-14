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
package games.stendhal.server.core.engine;

/**
 * a task that can be execute with a parameter (similar to a command in the command-pattern).
 *
 * @author durkham
 * @param <T> type of the parameter of the execute method
 */
public interface Task<T> {

	/**
	 * execute the task.
	 *
	 * @param object a parameter used by the type
	 */
	void execute(T object);
}
