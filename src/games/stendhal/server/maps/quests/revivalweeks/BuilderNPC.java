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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.dbcommand.ReadGroupQuestCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.CollectingGroupQuestAdder;
import games.stendhal.server.entity.npc.behaviour.impl.CollectingGroupQuestBehaviour;
import games.stendhal.server.util.QuestUtils;
import marauroa.server.db.command.DBCommandQueue;

public class BuilderNPC implements LoadableContent, TurnListener {
	private SpeakerNPC npc = null;
	private static final String QUEST_SLOT = QuestUtils.evaluateQuestSlotName("minetown_construction_[year]");
	private ReadGroupQuestCommand command;
	private CollectingGroupQuestBehaviour behaviour;

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
		Map<String, Integer> progress = command.getProgress();
		setupCollectingGroupQuest(progress);
		addNPC();
	}

	private void setupCollectingGroupQuest(Map<String, Integer> progress) {
		Map<String, Integer> required = new LinkedHashMap<>();
		Map<String, Integer> chunkSize = new HashMap<>();
		Map<String, String> hints = new HashMap<>();

		required.put("old scythe", 1);
		chunkSize.put("old scythe", 1);
		hints.put("old scythe", "I am sure, that Xoderos in Semos will sell you an old scythe.");

		required.put("axe", 1);
		chunkSize.put("axe", 1);
		hints.put("axe", "I am sure, that Xoderos in Semos will sell you an axe.");

		required.put("hammer", 1);
		chunkSize.put("hammer", 1);
		hints.put("hammer", "I am sure, that Xoderos in Semos will sell you a hammer.");

		required.put("knife", 2);
		chunkSize.put("knife", 1);
		hints.put("knife", "I am sure, that Xin Blanca in Semos will sell you a knife.");

		required.put("lamp", 5);
		chunkSize.put("lamp", 1);
		hints.put("lamp", "You can probably buy a lamp from Jimbo in Deniran.");

		required.put("wood", 200);
		chunkSize.put("wood", 10);
		hints.put("wood", "You can find wood in the forests.");

		required.put("beer", 25);
		chunkSize.put("beer", 5);
		hints.put("beer", "You can probably find beer in any tavern.");

		behaviour = new CollectingGroupQuestBehaviour(QUEST_SLOT, required, chunkSize, hints, progress);
		behaviour.setProjectName("#Mine #Town #Revival #Weeks");
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
				addGreeting("Hi, there. Please be careful, we are building up #Mine #Town #Revival #Weeks.");
				addHelp("#Mine #Town #Revival #Weeks is an annual festival.");
				addQuest("We have run short of supplies and may not be able to finish contruction in time! What a desaster.");
				addJob("I am the construction manager responsible for setting up #Mine #Town #Revival #Weeks.");
				addGoodbye("Bye, come back soon.");

			}

		};

		npc.setOutfit("body=0,dress=55,head=2,mouth=0,eyes=18,mask=0,hair=25,hat=1");
		npc.setPosition(70, 118);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		npc.setDescription("You see Klaus. He is in charge of construction.");
		zone.add(npc);

		addQuestDialog(npc);
	}



	private void addQuestDialog(SpeakerNPC npc) {
		new CollectingGroupQuestAdder().add(npc, behaviour);
		npc.addReply(Arrays.asList("Mine", "Town", "Revival", "Weeks", "Mine Town",
				"Mine Town Revival", "Mine Town Revival Weeks", "Mine Town", "Revival Weeks"),
				"During the Revival Weeks we #celebrate the old and now mostly dead Mine Town north of Semos City. It has been a tradition for many years, but this year the #status of the build up is not looking well.",
				null);
		npc.addReply(Arrays.asList("project"),
				"I am responsible for setting up the #Mine #Town #Revival #Weeks. I have to get everything prepared for the festival to take place.",
				null);
		npc.addReply("celebrate", "There will be games and a lot to eat and drink. I even heard about a paper chase.");
	}

	@Override
	public boolean removeFromWorld() {
		if (npc != null) {
			npc.getZone().remove(npc);
		}
		return true;
	}

}
