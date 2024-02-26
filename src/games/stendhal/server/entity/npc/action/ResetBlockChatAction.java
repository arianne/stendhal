/***************************************************************************
 *                 Copyright © 2014-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Resets a block to its initial position
 *
 * @author madmetzger
 */
public class ResetBlockChatAction implements ChatAction {

	private final Block blockToReset;

	/**
	 * Create a new Action to reset the given Block
	 *
	 * @param block
	 *          The Block to reset
	 */
	public ResetBlockChatAction(Block block) {
		this.blockToReset = block;
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		this.blockToReset.reset();
	}

}
