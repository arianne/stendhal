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

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Configuration;
import marauroa.common.crypto.Hash;

/**
 * NPCs who creates photos
 */
public class PhotographerNPC implements LoadableContent {
	private static Logger logger = Logger.getLogger(PhotographerNPC.class);

	public static final String[] CAPTIONS = new String[] {
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

	private String QUEST_SLOT = "photograph";


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
	private static String hmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException	{
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(keySpec);
		return Hash.toHexString(mac.doFinal(data.getBytes("UTF-8")));
	}

	/**
	 * generates the images url
	 *
	 * @param player outfit of player
	 * @param i background index
	 * @return
	 */
	public static String generateUrl(Player player, int i) {
		String outfit = player.getOutfit().getData(player.getOutfitColors());

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


	private void createNPC() {
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		final SpeakerNPC npc1 = new SpeakerNPC("Kirla") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("#Pictures! Good #Pictures! Memory #Pictures! Buy #Pictures!");
				addJob("I create #pictures from your memories.");
				addGoodbye("Take care.");

				add(ConversationStates.ATTENDING,
						"picture",
						new TimePassedCondition(QUEST_SLOT, 1, 5),
						ConversationStates.SELL_PRICE_OFFERED,
						"Ohmmmmm, I see blury mist, Ohmmmmm. The picture is getting clearer, Ohmmmmm. Just a view more seconds...",
						new PhotographerChatAction(zone, QUEST_SLOT));

				add(ConversationStates.ATTENDING,
						"picture",
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, 5)),
						ConversationStates.ATTENDING,
						null,
						new SayTimeRemainingAction(QUEST_SLOT, 1, 5, "I am sorry, I just created a picture for you. Please return in about"));


				add(ConversationStates.SELL_PRICE_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						null,
						new PhotographerSellChatAction(QUEST_SLOT));

				add(ConversationStates.SELL_PRICE_OFFERED,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.SELL_PRICE_OFFERED,
						"I could try to create another #picture.",
						null);
				add(ConversationStates.SELL_PRICE_OFFERED,
						"picture",
						null,
						ConversationStates.SELL_PRICE_OFFERED,
						"Ohmmmmm, I see blury mist, Ohmmmmm. The picture is getting clearer, Ohmmmmm. Just a view more seconds...",
						new PhotographerChatAction(zone, QUEST_SLOT));
			}
		};

		npc1.setPosition(68, 119);
		npc1.setEntityClass("photographernpc");
		npc1.setDirection(Direction.DOWN);
		npc1.setDescription("You see Kirla. She creates pictures.");
		npc1.initHP(100);
		zone.add(npc1);
	}


	/**
	 * removes an NPC from the world and NPC list
	 *
	 * @param name name of NPC
	 */
	private void removeNPC(String name) {
		SpeakerNPC npc = NPCList.get().get(name);
		if (npc == null) {
			return;
		}
		npc.getZone().remove(npc);
	}

	@Override
	public void addToWorld() {
		createNPC();
	}


	/**
	 * removes NPC from the Mine Town
	 *
	 * @return <code>true</code>, if the content was removed, <code>false</code> otherwise
	 */
	@Override
	public boolean removeFromWorld() {
		removeNPC("Kirla");
		return true;
	}
}
