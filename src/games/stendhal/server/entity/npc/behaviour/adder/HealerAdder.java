package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

public class HealerAdder {

	public void addHealer(final SpeakerNPC npc, final int cost) {
		final HealerBehaviour healerBehaviour = new HealerBehaviour(cost);
		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES, null,
				ConversationStates.ATTENDING, "I can #heal you.", null);

		engine.add(ConversationStates.ATTENDING, "heal", null,
				ConversationStates.HEAL_OFFERED, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC engine) {
						healerBehaviour.setChosenItemName("heal");
						healerBehaviour.setAmount(1);
						final int cost = healerBehaviour.getCharge(engine, player);

						if (cost > 0) {
							engine.say("Healing costs " + cost
									+ ". Do you have that much?");
						} else {
							if ((player.getATK() > 35) || (player.getDEF() > 35)) {
								engine.say("Sorry, I cannot heal you because you are way too strong for my limited powers");
							} else if (!player.isNew()
									&& (player.getLastPVPActionTime() > System.currentTimeMillis()
											- 2 * MathHelper.MILLISECONDS_IN_ONE_HOUR)) {
								// ignore the PVP flag for very young characters
								// (low atk, low def AND low level)
								engine.say("Sorry, but you have a bad aura, so that I am unable to heal you right now.");
							} else {
								engine.say("There, you are healed. How else may I help you?");
								healerBehaviour.heal(player);
							}
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final SpeakerNPC engine) {
						if (player.drop("money",
								healerBehaviour.getCharge(engine, player))) {
							healerBehaviour.heal(player);
							engine.say("There, you are healed. How else may I help you?");
						} else {
							engine.say("I'm sorry, but it looks like you can't afford it.");
						}
					}
				});

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "OK, how else may I help you?",
				null);
	}

}
