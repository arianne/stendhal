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
package games.stendhal.server.maps.semos.townhall;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

public class LeaderNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] text = {"Super Trainer, listen to me. Your skills are excellent but as you rarely hunt any creatures you are lacking in XP. Since your level is a factor in how hard you can hit, you are not reaching your full potential.", "XP Hunter, I have teaching for you too. Your habit of always letting another soldier defend against creatures means that your skills are never increasing. Yes, you have good level but your skills matter too!", "Well Rounded, I must commend you. You have a good level and good skills, both are needed for you to be able to hit creatures hard, and so that you can defend yourself. Well done!"};
		new MonologueBehaviour(buildSemosTownhallAreaLeader(zone), text, 1);
	}

	/**
	 * A leader of three cadets. He has an information giving role.
	 * @param zone zone to be configured with this npc
	 * @return the built NPC
	 */
	private SpeakerNPC buildSemosTownhallAreaLeader(final StendhalRPZone zone) {
		// We create an NPC
		final SpeakerNPC npc = new SpeakerNPC("Lieutenant Drilenun") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh hi, we're just taking a break here. My three cadets just got a reward from the Mayor for helping defend Semos.");
				addJob("I'm in charge of these three cadets. They need a lot of instruction, which I will have to go back to soon. Feel free to listen in, you may learn something!");
				addHelp("I can give you advice on your #weapon.");
				addQuest("Let me advise you on your #weapon.");
				addOffer("I'd like to comment on your #weapon, if I may.");
				addGoodbye("Don't forget to listen in on my teachings to these cadets, you may find it helpful!");
				add(ConversationStates.ATTENDING, "weapon", null, ConversationStates.ATTENDING,
				        null, new ChatAction() {

					        @Override
							public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					        	final Item weapon = player.getWeapon();
					        	if (weapon != null) {
					        		String comment;
					        		// this is, loosely, the formula used for damage of a weapon (not taking level into account here)
					        		final float damage = (weapon.getAttack() + 1) / weapon.getAttackRate();
					        		if (weapon.getName().endsWith(" hand sword")) {
					        			// this is a special case, we deal with explicitly
					        			comment = "I see you use twin swords. They have a superb damage capability but as you cannot wear a shield with them, you will find it harder to defend yourself if attacked.";
					        		} else if (damage >= 5) {
					        			comment = "That " + weapon.getName() + " is a powerful weapon, it has a good damage to rate ratio.";
					        			if (weapon.getAttackRate() < 3) {
					        				comment += " Despite the fast rate being useful, the low attack will not help you against strong creatures. Something heavier would be better then.";
					        			} else if (weapon.getAttackRate() > 6) {
					        				comment +=  " It should be useful against strong creatures. Remember though that something weaker but faster may suffice against lower level creatures.";
					        			}
					        		} else {
					        			comment = "Well, your " + weapon.getName() + " has quite low damage capability, doesn't it? You should look for something with a better attack to rate ratio.";
					        			if (weapon.getAttackRate() < 3) {
					        				comment += " At least you can hit fast with it, so it may be good enough against creatures weaker than you.";
					        			}
						       		}
					        		// simple damage doesn't take into account lifesteal. this is a decision the player must make, so inform them about the stats
					        		if (weapon.has("lifesteal")) {
					        			double lifesteal = weapon.getDouble("lifesteal");
					        			if (lifesteal > 0) {
					        				comment += " The positive lifesteal of " + lifesteal + " will increase your health as you use it.";
					        			} else {
					        				comment += " The negative lifesteal of " + lifesteal + " will drain your health as you use it.";
					        			}
					        		}
					        		raiser.say(comment);
					        	} else {
					        		// player didn't have a weapon, as getWeapon returned null.
					        		raiser.say("Oh, I can't comment on your weapon, as you have none equipped. That's not very wise in these dangerous times!");
					        	}
							} 
					    }
				);
			}
		};
		npc.setLevel(150);
		npc.setDescription("You see Lieutenant Drilenun who talks to his three cadets.");
		npc.setEntityClass("bossmannpc");
		npc.setPosition(23, 15);
		npc.initHP(100);
		zone.add(npc);
		
		return npc;
	}
}
