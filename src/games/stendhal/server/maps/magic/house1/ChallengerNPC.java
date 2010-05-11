package games.stendhal.server.maps.magic.house1;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;

public class ChallengerNPC extends SpeakerNPCFactory {
 private static final int MINUTES_IN_DAYS = 24 * 60;
/** how many creatures will be spawned.*/
 private static final int NUMBER_OF_CREATURES = 5;
 /** lowest level allowed to island.*/
 private static final int MIN_LEVEL = 50;
 /** Cost multiplier for getting to island. */
 private static final int COST_FACTOR = 300;
  /** How long to wait before visiting island again. */
 private static final int DAYS_BEFORE_REPEAT = 3;
 /** The name of the quest slot where we store the time last visited. */
 private static final String QUEST_SLOT = "adventure_island";
 
 private static final Logger logger = Logger.getLogger(ChallengerNPC.class);
	private final class ChallengeChatAction implements ChatAction {

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			int cost = COST_FACTOR * player.getLevel();
			if (!player.isEquipped("money", cost)) {
				npc.say("You don't have enough money with you, the fee at your level is " + cost + " money.");
				npc.setCurrentState(ConversationStates.ATTENDING);
				return;
			}
			final StendhalRPZone challengezone = (StendhalRPZone) SingletonRepository
					.getRPWorld().getRPZone("int_adventure_island");
			String zoneName = player.getName() + "_adventure_island";
			
			final AdventureIsland zone = new AdventureIsland(zoneName, challengezone, player);

			SingletonRepository.getRPWorld().addRPZone(zone);

			player.setQuest(QUEST_SLOT, Long.toString(System.currentTimeMillis()));
			player.teleport(zone, 4, 4, Direction.DOWN, player);
			String message;
			int numCreatures = zone.getCreatures(); 
			if (zone.getCreatures() < AdventureIsland.NUMBER_OF_CREATURES) {
				// if we didn't manage to spawn NUMBER_OF_CREATURES they get a reduction
				cost =  (int) (cost * ((float) numCreatures / (float) NUMBER_OF_CREATURES));
				message = "Haastaja bellows from below: I could only fit " + numCreatures + " creatures on the island for you. You have therefore been charged less, a fee of only " + cost + " money. Good luck.";
				logger.info("Tried too many times to place creatures in adventure island so less than the required number have been spawned");
			} else { 
				message = "Haastaja bellows from below: I took the fee of " + cost + " money. Good luck up there.";
			}
			player.drop("money", cost);
			player.sendPrivateText(message);
			
			player.notifyWorldAboutChanges();
		}
	}

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("And so, the hero has come.");
		npc.addQuest("Pay the #fee and you can #fight my trained magical creatures. There will be " + NUMBER_OF_CREATURES 
					 + " in all, at a level to challenge you.");
		npc.addHelp("If you are strong enough and will pay the #fee, you can #fight " + NUMBER_OF_CREATURES 
					+ " of my animals on a private adventure island.");
		npc.addJob("I train magical animals for fighting and offer warriors the chance to #battle against them on a magical island.");
		npc.addOffer("To fight against " + NUMBER_OF_CREATURES + " of my trained creatures, chosen for your level, make the #challenge.");		
		npc.addGoodbye("Bye.");
		npc.add(ConversationStates.ANY, 
				"fee", 
				new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1), 
								 new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)), 
				ConversationStates.QUEST_OFFERED, 
				null,
				new ChatAction() {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						npc.say("The fee is your current level, multiplied by " + COST_FACTOR + " and payable in cash. At your level of " 
								+ player.getLevel() + " the fee is " + COST_FACTOR * player.getLevel() + " money. Do you want to fight?");			
				}
		});
			
		// player meets conditions, first remind them of the dangers and wait for a 'yes'
		npc.add(ConversationStates.ANY, 
				Arrays.asList("challenge", "fight", "battle"), 
				new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1), 
								 new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)), 
				ConversationStates.QUEST_OFFERED, 
				"I accept your challenge. If you can pay the #fee, I will summon an island with " + NUMBER_OF_CREATURES 
				+ " dangerous creatures for you to face. So, are you sure you want to enter the adventure island?", 
				null);
		// player returns within DAYS_BEFORE_REPEAT days, and his island has expired 
		npc.add(ConversationStates.ANY, 
				Arrays.asList("challenge", "fight", "battle", "fee"),
				new AndCondition(
								 new NotCondition(new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)), 
								 new NotCondition(new AdventureZoneExistsCondition())
								 ),
				ConversationStates.ATTENDING, 
				null, 
				new SayTimeRemainingAction(QUEST_SLOT, "Your life force will not support the island so soon after you last visited. You will be ready again in", DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS));

	// player returns within DAYS_BEFORE_REPEAT days, if the zone still exists that he was in before, send him straight up. 
		npc.add(ConversationStates.ANY, 
				Arrays.asList("challenge", "fight", "battle", "fee"),
				new AndCondition(
								 new NotCondition(new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)), 
								 new AdventureZoneExistsCondition()
								 ),
				ConversationStates.QUESTION_1, 
				"The island which I recently summoned for you, remains for you to visit at no extra cost. Do you wish to return to it?", 
				null);

		// player below MIN_LEVEL
		npc.add(ConversationStates.ANY, 
				Arrays.asList("challenge", "fight", "battle", "fee"), 
				new LevelLessThanCondition(MIN_LEVEL), 
				ConversationStates.ATTENDING, 
				"You are too weak to fight against " + NUMBER_OF_CREATURES  + " at once. Come back when you are at least Level " + MIN_LEVEL + ".",
				null);
		// all conditions are met and player says yes he wants to fight
		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.YES_MESSAGES, 
				new LevelGreaterThanCondition(MIN_LEVEL - 1), 
				ConversationStates.IDLE, 
				null, 
				new ChallengeChatAction());
		// player was reminded of dangers and he doesn't want to fight
		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null, 
				ConversationStates.ATTENDING, 
				"Fair enough.", 
				null);	

		// player wishes to return to an existing adventure island
		npc.add(ConversationStates.QUESTION_1, 
				ConversationPhrases.YES_MESSAGES, 
				// check again it does exist
				new AdventureZoneExistsCondition(),
				ConversationStates.IDLE, 
				null, 
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						final String zoneName = player.getName() + "_adventure_island";
						final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneName);
						player.teleport(zone, 4, 4, Direction.DOWN, null);
						player.notifyWorldAboutChanges();
					}
				});
				
		// player wished to return to an existing adventure island but it's now gone
		npc.add(ConversationStates.QUESTION_1, 
				ConversationPhrases.YES_MESSAGES, 
				// check again it does exist
				new NotCondition(new AdventureZoneExistsCondition()),
				ConversationStates.ATTENDING,
				"Sorry, but the island vanished between the offer I just made you and you saying 'yes'. You cannot visit it now.",
				null);


		// player declined to return to an existing adventure island
		npc.add(ConversationStates.QUESTION_1, 
				ConversationPhrases.NO_MESSAGES, 
				null, 
				ConversationStates.ATTENDING, 
				"Very well.", 
				null);
	}

	// Not made as an entity.npc.condition. file because the zone name depends on player here. 
	class AdventureZoneExistsCondition implements ChatCondition {
		public boolean fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String zoneName = player.getName() + "_adventure_island";
			final IRPZone.ID zoneid = new IRPZone.ID(zoneName);
			return SingletonRepository.getRPWorld().hasRPZone(zoneid);
		}
	}
}
