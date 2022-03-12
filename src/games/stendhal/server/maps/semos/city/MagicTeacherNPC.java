/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.city;

import java.util.Collection;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.config.annotations.TestServerOnly;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import marauroa.common.game.RPSlot;

/**
 * An NPC for testing purposes, that easily enables a player to play around with magic
 *
 * @author madmetzger
 */
@TestServerOnly
public class MagicTeacherNPC implements ZoneConfigurator {

	/**
	 * This ChatAction prepares a player with all he needs to play around with magic
	 *
	 * @author madmetzger
	 */
	private final class TeachMagicAction implements ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			enableSpellsFeature(player);
			boostMana(player);
			equipSpells(player);
			equipManaPotions(player);
		}

		private void equipManaPotions(Player player) {
			StackableItem potion = (StackableItem) SingletonRepository.getEntityManager().getItem("mana");
			potion.setQuantity(1000);
			player.equipOrPutOnGround(potion);
		}

		private void equipSpells(Player player) {
			EntityManager em = SingletonRepository.getEntityManager();
			RPSlot slot = player.getSlot("spells");
			Collection<String> spells = em.getConfiguredSpells();
			for (String spellName : spells) {
				Spell s = em.getSpell(spellName);
				slot.add(s);
			}
		}

		private void boostMana(Player player) {
			player.setBaseMana(1000);
			player.setMana(1000);
		}

		private void enableSpellsFeature(Player player) {
			player.setFeature("spells", true);
		}

	}

	@Override
	public void configureZone(StendhalRPZone zone,
		Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Mirlen") {

			@Override
			protected void createDialog() {
				add(ConversationStates.ATTENDING, "teach", null, ConversationStates.SERVICE_OFFERED, "Do you want to learn about magic?", null);
				add(ConversationStates.SERVICE_OFFERED, ConversationPhrases.YES_MESSAGES, null, ConversationStates.ATTENDING, null, new TeachMagicAction());
			}

		};
		npc.addGreeting("Hello, I am the magic teacher! I can #teach you about magic.");
		npc.addJob("My job is to #teach you a bit about magic, so you can try it out here.");
		npc.addHelp("If you need further help #https://stendhalgame.org/wiki/Ideas_for_Stendhal/Magic or you can ask in #'#arianne' for help.");
		npc.addOffer("I can offer to #teach you about magic.");
		npc.addGoodbye("Stay magical!");
		npc.setPosition(20, 26);
		npc.setEntityClass("blueoldwizardnpc");
		zone.add(npc);
	}

}
