/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

public class CrystalNPCBase extends SpeakerNPC {

	public CrystalNPCBase(final String name) {
		super(name);

		// hide location from website
		put("hidezone", "");
	}

	@Override
	protected void createPath() {
		// doesn't move
		setPath(null);
	}
}
