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
package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.SokobanBoard;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/**
 * A Tic Tac Toe game for two players
 *
 * @author hendrik
 */
public class SokobanGame implements LoadableContent {
	private StendhalRPZone zone = null;
	private SokobanBoard board;
	private SpeakerNPC npc;

	/**
	 * creates TicTacToeBoard and adds it to the world.
	 */
	private void addBoard() {
		board = new SokobanBoard();
		board.setPosition(26, 107);
		zone.add(board);
	}

	/**
	 * adds the NPC which moderates the game to the world.
	 */
	private void addNPC() {
		npc = new SpeakerNPC("TODO") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
						Arrays.asList("level"),
						ConversationStates.IDLE,
						null,
						new PlayAction(board));
			}
		};
		npc.setEntityClass("paulnpc"); 
		npc.setPosition(46, 125);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

	private static class PlayAction implements ChatAction {
		private SokobanBoard board;

		public PlayAction(SokobanBoard board) {
			this.board = board;
		}

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			board.setPlayer(player);
			board.loadLevel(MathHelper.parseIntDefault(sentence.getNormalized().substring("level ".length()), 1));
		}
	}

	@Override
	public void addToWorld() {
		zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");

		addBoard();
		addNPC();
	}

	/**
	 * try to remove the content from the world-
	 *
	 * @return <code>true</code>
	 */
	@Override
	public boolean removeFromWorld() {
		NPCList.get().remove("TODO");
		zone.remove(npc);
		board.clear();
		zone.remove(board);
		return true;
	}
}
