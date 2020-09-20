/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
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

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.dbcommand.ReadGroupQuestCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.util.QuestUtils;
import marauroa.server.db.command.DBCommandQueue;

public class BuilderNPC implements LoadableContent, TurnListener {
	private SpeakerNPC npc = null;
	private static final String QUEST_SLOT = QuestUtils.evaluateQuestSlotName("minetown_construction_[year]");
	private ReadGroupQuestCommand command;
	private Map<String, Integer> progress;

	@Override
	public void addToWorld() {
		this.command = new ReadGroupQuestCommand(QUEST_SLOT);
		DBCommandQueue.get().enqueue(command);
		TurnNotifier.get().notifyInTurns(0, this);
	}
			
	@Override
	public void onTurnReached(int currentTurn) {
		if (command.getProgress() == null) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}
		this.progress = command.getProgress();
		addNPC();
	}

	private void addNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final SpeakerNPC npc = new SpeakerNPC("Klaus") {

			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, there.");
				addHelp("Mine Town Revival Weeks is an annual festival.");
				addQuest("We have run short of supplies and may not be able to finish contruction in time! What a desaster.");
				addJob("I am the construction manager responsible for setting up Mine Town Revival Weeks.");
				addGoodbye("Bye, come back soon.");

			}

		};

		npc.setOutfit("body=0,dress=55,head=2,mouth=0,eyes=18,mask=0,hair=25,hat=1");
		npc.setPosition(90, 111);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setDescription("You see Klaus. He is in charge of construction.");
		zone.add(npc);

	}

	@Override
	public boolean removeFromWorld() {
		if (npc != null) {
			npc.getZone().remove(npc);
		}
		return true;
	}

}
