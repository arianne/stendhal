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
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Sets NPC to reverse path on collision
 *
 * @author AntumDeluge
 */
public class SetNPCPathReversible extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		String reverseOnCollision = args.get(1).toLowerCase();
		super.execute(admin, args);
		if (args.size() != 2) {
			admin.sendPrivateText(NotificationType.ERROR,
					"/script SetNPCPathReversible npc true|false");
			return;
		}
		SpeakerNPC npc = NPCList.get().get(args.get(0));
		/* TODO: merge with NPC's current collision action rather than replace
		 *       it.
		 */
		if (reverseOnCollision.equals("true")) {
			npc.setCollisionAction(CollisionAction.REVERSE);
		} else if (reverseOnCollision.equals("false")) {
			npc.setCollisionAction(null);
		} else {
			admin.sendPrivateText(NotificationType.ERROR, "Unknown argument \""
					+ reverseOnCollision + "\". Please declare using \"true\""
					+ " or \"false\".");
		}
	}
}
