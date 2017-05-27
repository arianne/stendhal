/***************************************************************************
 *                    (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.captureflag;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.player.Player;

/**
 * lets the player join a CTF game
 *
 * @author hendrik, sjtsp
 */
public class JoinCaptureFlagAction implements ChatAction {

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {

		// TODO: set some rpentity slot

		player.setUseListener("Tag", new CaptureFlagUseListener(player));

		// only if player does not have bow already
		new EquipItemAction("ctf bow").fire(player,  sentence, npc);

		new ProvideArrowsAction().fire(player, sentence, npc);
	}
}
