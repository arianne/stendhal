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
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ExamineEvent;
import marauroa.common.Configuration;
import marauroa.common.crypto.Hash;

public class PhotographerChatAction implements ChatAction {
	private static Logger logger = Logger.getLogger(PhotographerChatAction.class);

	private static final String[] CAPTIONS = new String[] {
		" meeting Balduin",
		" starting the adventure",
		" exploring Semos Dungeon",
		" visiting the Semos Temple",
		" meeting Jenny",
		" discovering the Gnome village",
		" visiting Ados",
		" discovering a huge tower",
		" sneaking into Ados Wildlife Refuge"
	};

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		String outfit = Integer.toString(player.getOutfit().getCode());
		int i = Rand.rand(9);
		player.addEvent(new ExamineEvent(generateUrl(outfit, i), "Picture", player.getName() + CAPTIONS[i]));
		player.notifyWorldAboutChanges();
	}

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
