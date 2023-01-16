/***************************************************************************
 *                   (C) Copyright 2023 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.core.rp.DaylightUpdater;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Changes the global day time to specified phase. Intended for testing night
 * time map coloring. Calling with parameter "reset" restores the normal timed 
 * behavior.
 */
public class ChangeDaytime extends ScriptImpl {
	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() != 1) {
			usage(admin);
			return;
		}
	
		if (!setPhase(args.get(0))) {
			usage(admin);
		}
	}

	private void usage(Player admin) {
		String validTimes = Arrays.stream(DaylightPhase.values()).map(p -> p.toString().toLowerCase()).collect(Collectors.joining(", "));
		sandbox.privateText(admin, "Usage: /script ChangeDaytime.class {<daytime>|reset}\nValid values for <daytime> are: " + validTimes);
	}

	private boolean setPhase(String phaseString) {
		if ("reset".equalsIgnoreCase(phaseString)) {
			DaylightPhase.unsetTestingPhase();
		} else {
			boolean found = false;
			for (DaylightPhase phase : DaylightPhase.values()) {
				if (phaseString.equalsIgnoreCase(phase.toString())) {
					DaylightPhase.setTestingPhase(phase);
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		DaylightUpdater.get().updateDaytimeColor();
		return true;
	}
}
