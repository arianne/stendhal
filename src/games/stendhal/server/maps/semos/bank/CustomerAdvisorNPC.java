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
package games.stendhal.server.maps.semos.bank;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Map;
/**
 * ZoneConfigurator configuring the NPC (former known as Dagobert) in semos bank
 */
public class CustomerAdvisorNPC implements ZoneConfigurator {

	private static final class VaultChatAction implements ChatAction {
		
		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			final StendhalRPZone vaultzone = (StendhalRPZone) SingletonRepository
					.getRPWorld().getRPZone("int_vault");
			String zoneName = player.getName() + "_vault";
			
			final StendhalRPZone zone = new Vault(zoneName, vaultzone, player);
			
			
			SingletonRepository.getRPWorld().addRPZone(zone);
			player.teleport(zone, 4, 5, Direction.UP, player);
			((SpeakerNPC) npc.getEntity()).setDirection(Direction.DOWN);
		}
	}
	
	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Dagobert") {
			
			@Override
			public void createDialog() {
				addGreeting("Welcome to the bank of Semos! I am here to #help you manage your personal chest.");
				addHelp("Follow the corridor to the right, and you will find the magic chests. You can store your belongings in any of them, and nobody else will be able to touch them! A number of spells have been cast on the chest areas to ensure #safety.");
				addReply("safety", "When you are standing at a chest to organise your items, any other people or animals will not be able to come near you. A magical aura stops others from using scrolls to arrive near you. You will need to walk out. Lastly let me tell you about safe #trading.");
				addReply("trading", "To start a trade with another player, right-click on them and select 'Trade'. If they also want to trade with you, you'll see a window pop up where you can drag items to offer, and see what is being offered to you. Both click Offer, and then you both need to Accept the offer to complete the trade.");
				addJob("I'm the Customer Advisor here at Semos Bank.");
				addOffer("If you wish to access your personal chest in solitude, I can give you access to a private #vault. A guidebook inside will explain how it works.");		
				addGoodbye("It was a pleasure to serve you.");
				add(ConversationStates.ANY, "vault", new QuestCompletedCondition("armor_dagobert"), ConversationStates.IDLE, null, 
						new MultipleActions(new PlaySoundAction("keys-1", true), new VaultChatAction()));
				
				add(ConversationStates.ANY, "vault", new QuestNotCompletedCondition("armor_dagobert"), ConversationStates.ATTENDING, "Perhaps you could do a #favour for me, and then I will tell you more about the private banking vaults.", null);
				
				// remaining behaviour defined in games.stendhal.server.maps.quests.ArmorForDagobert	
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
			
		};
		npc.setPosition(9, 23);
		npc.setDirection(Direction.DOWN);
		npc.setDescription("You see Dagobert. He looks like a safe, dependable type.");
		npc.setHP(95);
		npc.setEntityClass("youngnpc");
		zone.add(npc);
	}

}