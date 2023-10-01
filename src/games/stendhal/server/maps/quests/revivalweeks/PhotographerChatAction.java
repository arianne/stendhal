/***************************************************************************
 *                (C) Copyright 2016-2023 - Faiumoni e. V.                 *
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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.sign.PopupImage;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;

public class PhotographerChatAction implements ChatAction {

	private StendhalRPZone zone;
	private String questSlot;
	private Map<String, Entity> playerSigns = new HashMap<String, Entity>();

	/**
	 * creates a PhotographerChatAction
	 *
	 * @param zone StendhalRPZone
	 */
	public PhotographerChatAction(StendhalRPZone zone, String questSlot) {
		this.zone = zone;
		this.questSlot = questSlot;
	}


	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		TurnNotifier.get().notifyInSeconds(1, new TurnListener() {

			@Override
			public void onTurnReached(int currentTurn) {

				int i = determinePhoto(player);

				String url = PhotographerNPC.generateUrl(player, i);
				String caption = player.getName() + PhotographerNPC.CAPTIONS[i];
				addSign(player.getName(), url, "Picture", caption);

				player.addEvent(new ExamineEvent(url, "Picture", caption));
				player.notifyWorldAboutChanges();

				player.setQuest(questSlot, 0, Integer.toString(i));
				npc.say("Do you want to buy this picture for 1000 money?");
			}
		});
	}

	/**
	 * position the sign at an empty spot
	 *
	 * @param sign Sign to position
	 * @return true, if there was an empty spot
	 */
	private boolean positionSign(Entity sign) {
		for (int y = 125; y <= 127; y++) {
			for (int x = 80; x > 51; x--) {
				if (!zone.collides(sign, x, y)) {
					sign.setPosition(x, y);
					zone.add(sign);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * adds a sign with the photo
	 *
	 * @param name player name
	 * @param url url of image
	 * @param title title of image
	 * @param caption caption of image
	 */
	private void addSign(String name, String url, String title, String caption) {
		Entity oldSign = playerSigns.get(name);
		if (oldSign != null) {
			zone.remove(oldSign);
		}

		PopupImage sign = new PopupImage(url, title, caption);
		sign.setEntityClass("notice_sign");
		positionSign(sign);
		playerSigns.put(name, sign);
	}

	/**
	 * determine the background based on the players experience in the world
	 *
	 * @param player Player
	 * @return background index
	 */
	private int determinePhoto(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();
		List<Integer> photos = new LinkedList<Integer>();
		if (player.hasQuest("weapons_collector")) {
			photos.add(Integer.valueOf(0));
		}
		if (player.getLevel() < 50) {
			photos.add(Integer.valueOf(1));
		}
		if (player.hasVisitedZone(world.getZone("-1_semos_dungeon"))) {
			photos.add(Integer.valueOf(2));
		}
		if (player.hasVisitedZone(world.getZone("int_afterlife"))) {
			photos.add(Integer.valueOf(3));
		}
		if (player.hasQuest("jenny_mill_flour")) {
			photos.add(Integer.valueOf(4));
		}
		if (player.hasVisitedZone(world.getZone("0_semos_mountain_n_w2"))) {
			photos.add(Integer.valueOf(5));
		}
		if (player.hasVisitedZone(world.getZone("0_ados_wall_n"))) {
			photos.add(Integer.valueOf(6));
		}
		if (player.hasVisitedZone(world.getZone("int_semos_wizards_tower_basement"))) {
			photos.add(Integer.valueOf(7));
		}
		if (player.hasVisitedZone(world.getZone("-1_ados_outside_nw"))) {
			photos.add(Integer.valueOf(8));
		}
		if (player.hasVisitedZone(world.getZone("int_semos_wizards_tower_9"))) {
			photos.add(Integer.valueOf(9));
		}
		if (player.hasQuest("icecream_for_annie")) {
			photos.add(Integer.valueOf(10));
		}
		if (player.hasQuest("solve_riddles")) {
			photos.add(Integer.valueOf(11));
		}
		if (player.hasVisitedZone(world.getZone("int_imorgens_house"))) {
			photos.add(Integer.valueOf(12));
		}
		/*
		if (player.hasVisitedZone(world.getZone("int_semos_wizards_tower_9"))) {
			photos.add(Integer.valueOf(13));
		}
		*/
		if (player.hasVisitedZone(world.getZone("0_nalwor_city"))) {
			photos.add(Integer.valueOf(14));
		}
		if (player.hasVisitedZone(world.getZone("int_oni_palace_2"))) {
			photos.add(Integer.valueOf(15));
		}
		if (player.hasQuest("campfire")) {
			photos.add(Integer.valueOf(16));
		}
		return Rand.rand(photos).intValue();
	}

	@Override
	public String toString() {
		return "PhotographerChatAction";
	}

	@Override
	public int hashCode() {
		return 970201;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		return obj instanceof PhotographerChatAction;
	}
}
