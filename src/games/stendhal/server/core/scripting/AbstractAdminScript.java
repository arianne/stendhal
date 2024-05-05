/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import games.stendhal.common.NotificationType;
import games.stendhal.server.constants.StandardMessages;
import games.stendhal.server.entity.player.Player;


public abstract class AbstractAdminScript extends ScriptImpl {

	/** Admin player executing script. */
	protected Player admin;


	/**
	 * Instructions to execute after parameter count sanity is checked.
	 *
	 * @param args
	 *   Parameters passed to the script.
	 */
	protected abstract void run(final List<String> args);

	@Override
	public void execute(final Player admin, final List<String> args) {
		this.admin = checkNotNull(admin);
		final int minparams = getMinParams();
		final int maxparams = getMaxParams();
		if (maxparams > -1 && maxparams < minparams) {
			admin.sendPrivateText(NotificationType.ERROR, "Maximum parameters ("
					+ maxparams + ") is less than minimum parameters ("
					+ minparams + ").");
			return;
		}
		final int argc = args.size();
		if (argc > 0 && Arrays.asList("-?", "-h", "-help", "--help")
				.contains(args.get(0).toLowerCase(Locale.ENGLISH))) {
			showUsage();
			return;
		}
		if (argc < minparams) {
			StandardMessages.missingParameter(admin);
			showUsage();
			return;
		} else if (maxparams > -1 && argc > maxparams) {
			StandardMessages.excessParameter(admin);
			showUsage();
			return;
		}
		run(args);
	}

	/**
	 * Retrieves minumum number of parameters required to run script.
	 */
	protected int getMinParams() {
		return 0;
	}

	/**
	 * Retrieves maximum number of parameters required to run script. An indefinite amount can be
	 * specified with {@code -1}.
	 */
	protected int getMaxParams() {
		return -1;
	}

	/**
	 * The script is assumed to have help info if it accepts any number of parameters.
	 */
	private boolean hasHelp() {
		return getMaxParams() != 0;
	}

	/**
	 * Displays usage information to admin.
	 */
	public void showUsage() {
		admin.sendPrivateText(getUsage());
	}

	/**
	 * Retrieves a string representation of usage information.
	 */
	public String getUsage() {
		final boolean includeHelp = hasHelp();
		final String cmd = "/script " + getClass().getSimpleName() + ".class";
		String usage = "Usage:";
		final List<String> params = new LinkedList<>();
		// make a copy in case returned value is immutable
		params.addAll(getParamStrings());
		if (includeHelp) {
			params.add("-?");
		}
		final int pcount =  params.size();
		if (pcount == 0) {
			usage += " " + cmd;
		} else if (pcount == 1) {
			usage += " " + cmd + " " + params.get(0);
		} else {
			for (final String paramset: params) {
				usage += "\n&nbsp;&nbsp;" + cmd;
				if (paramset.length() > 0) {
					usage += " " + paramset;
				}
			}
		}
		final List<String> details = new LinkedList<>();
		// make a copy in case returned value is immutable
		details.addAll(getParamDetails());
		if (includeHelp) {
			details.add("-?: Show this help information.");
		}
		if (details.size() > 0) {
			usage += "\nParameters:";
			for (final String detail: details) {
				usage += "\n&nbsp;&nbsp;" + detail;
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
		return new LinkedList<String>();
	}

	/**
	 * Retrieves extended instructions on parameter usage.
	 */
	protected List<String> getParamDetails() {
		return new LinkedList<String>();
	}
}
