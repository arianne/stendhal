/***************************************************************************
 *                   (C) Copyright 2016 - Faiumoni e. V.                   *
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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.sign.PopupImage;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;
import marauroa.common.Configuration;
import marauroa.common.crypto.Hash;

public class PhotographerChatAction implements ChatAction {
	private static Logger logger = Logger.getLogger(PhotographerChatAction.class);

	private StendhalRPZone zone;
	private Map<String, Entity> playerSigns = new HashMap<String, Entity>();

	/**
	 * creates a PhotographerChatAction
	 *
	 * @param zone StendhalRPZone
	 */
	public PhotographerChatAction(StendhalRPZone zone) {
		this.zone = zone;
	}

	private static final String[] CAPTIONS = new String[] {
		" meeting Balduin",
		" starting the adventure",
		" exploring Semos Dungeon",
		" visiting the Semos Temple",
		" meeting Jenny",
		" discovering the Gnome village",
		" visiting Ados",
		" discovering a huge tower",
		" sneaking into Ados Wildlife Refuge",
		" looking out of the wizzard tower",
		" providing ice cream",
		" visiting hell",
		" looking around",
		" getting to the top of the tower",
		" visiting elves",
		" visiting oni",
		" relaxing at a camp fire"
	};

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		String outfit = player.getOutfit().getData(player.getOutfitColors());
		int i = determinePhoto(player);

		String url = generateUrl(outfit, i);
		String caption = player.getName() + CAPTIONS[i];
		addSign(player.getName(), url, "Picture", caption);
		player.addEvent(new ExamineEvent(url, "Picture", caption));
		player.notifyWorldAboutChanges();
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
		sign.setEntityClass("notice");
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

	/**
	 * generates the images url
	 *
	 * @param outfit outfit of player
	 * @param i background index
	 * @return
	 */
	private String generateUrl(String outfit, int i) {
		try {
			String hash = hmac(i + "_" + outfit, Configuration.getConfiguration().get("stendhal.secret"));
			StringBuilder sb = new StringBuilder();
			sb.append("https://stendhalgame.org/content/game/photo.php?outfit=");
			sb.append(outfit);
			sb.append("&i=");
			sb.append(i);
			sb.append("&h=");
			sb.append(hash.toLowerCase(Locale.ENGLISH));
			return sb.toString();
		} catch (Exception e) {
			logger.error(e, e);
			return "";
		}
	}

	/**
	 * calculates the hamc
	 *
	 * @param data data
	 * @param key key
	 * @return hmac
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static String hmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException	{
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		return Hash.toHexString(mac.doFinal(data.getBytes("UTF-8")));
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
