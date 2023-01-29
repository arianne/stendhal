/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
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

import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.player.Player;


public abstract class AbstractAdminScript extends ScriptImpl {

	/**
	 * Instructions to execute after parameter count sanity is checked.
	 *
	 * @param admin
	 *     The player executing the script.
	 * @param args
	 *     Parameters passed to the script.
	 */
	protected abstract void run(final Player admin, final List<String> args);

	@Override
	public void execute(final Player admin, final List<String> args) {
		final int minparams = getMinParams();
		final int maxparams = getMaxParams();
		if (maxparams > -1 && maxparams < minparams) {
			admin.sendPrivateText(NotificationType.ERROR, "Maximum parameters ("
					+ maxparams + ") is less than minimum parameters ("
					+ minparams + ").");
			return;
		}
		final int argc = args.size();
		if (argc < minparams) {
			admin.sendPrivateText(NotificationType.ERROR, "Missing parameter.");
			showUsage(admin);
			return;
		} else if (maxparams > -1 && argc > maxparams) {
			admin.sendPrivateText(NotificationType.ERROR, "Too many parameters.");
			showUsage(admin);
			return;
		}
		run(admin, args);
	}

	/**
	 * Retrieves minumum number of parameters required to run script.
	 */
	protected int getMinParams() {
		return 0;
	}

	/**
	 * Retrieves maximum number of parameters required to run script.
	 */
	protected int getMaxParams() {
		return -1;
	}

	/**
	 * Displays usage information.
	 *
	 * @param admin
	 *     Player to whom information is sent.
	 */
	public void showUsage(final Player admin) {
		admin.sendPrivateText(getUsage());
	}

	/**
	 * Retrieves a string representation of usage information.
	 */
	public String getUsage() {
		final String cmd = "/script " + getClass().getSimpleName() + ".class";
		String usage = "Usage:";
		final List<String> params = getParamStrings();
		int pcount = 0;
		if (params != null) {
			pcount = params.size();
		}
		if (pcount == 0) {
			usage += " " + cmd;
		} else if (pcount == 1) {
			usage += " " + cmd + " " + params.get(0);
		} else {
			for (final String paramset: params) {
				usage += "\n  " + cmd;
				if (paramset.length() > 0) {
					usage += " " + paramset;
				}
			}
		}
		List<String> details = getParamDetails();
		if (details != null && details.size() > 0) {
			usage += "\nParameters:";
			for (final String detail: details) {
				usage += "\n  " + detail;
			}
		}
		return usage;
	}

	/**
	 * Retrieves a list of parameter strings that this script accepts.
	 *
	 * An empty string can be included to show execution without
	 * parameters.
	 */
	protected List<String> getParamStrings() {
		return null;
	}

	/**
	 * Retrieves extended instructions on parameter usage.
	 */
	protected List<String> getParamDetails() {
		return null;
	}
}
