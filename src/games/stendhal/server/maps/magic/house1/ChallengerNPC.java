package games.stendhal.server.maps.magic.house1;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.MovementListener;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.portal.Teleporter;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deathmatch.CreatureSpawner;
import games.stendhal.server.maps.deathmatch.Spot;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class ChallengerNPC extends SpeakerNPCFactory {
	// assign here all the hardcoded constants
 private static final int NUMBER_OF_CREATURES = 5;
 private static final int MIN_LEVEL = 50;
 private static final int COST = 50000;
 private static final int MIN_X = 10;
 private static final int MIN_Y = 10;
 private static final int MAX_X = 25;
 private static final int MAX_Y = 25;
 private static final double LEVEL_RATIO = 0.75;
 //private static final int DAYS_BEFORE_REPEAT = 3;
 
 static final Logger logger = Logger.getLogger(ChallengerNPC.class);
	private final class ChallengeChatAction implements ChatAction {

		private final class ChallengeMovementListener implements
				MovementListener {
			public Rectangle2D getArea() {
				return new Rectangle2D.Double(0, 0, 100, 100);
			}

			public void onEntered(final ActiveEntity entity, final StendhalRPZone zone, final int newX,
					final int newY) {

			}

			public void onExited(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
					final int oldY) {
				if (!(entity instanceof Player)){
					return;
				}
			    if(zone.getPlayers().size() == 1) {
			    	// since we are about to destroy the arena, change the player zoneid to semos bank so that 
			    	// if they are relogging, 
			    	// they can enter back to the bank (not the default zone of PlayerRPClass). 
			    	// If they are scrolling out or walking out the portal it works as before.
			    	entity.put("zoneid","int_magic_house1");
					entity.put("x","12");
					entity.put("y","3");
					// TODO: iterate through all items left in the zone and for the listeners, call
					/*if (inspected instanceof TurnListener) {
						*TurnListener listener = (TurnListener) inspected;
						*TurnNotifier.get().dontNotify(listener);
					}*/
					SingletonRepository.getRPWorld().removeZone(zone);

			    }
			}

			public void onMoved(final ActiveEntity entity, final StendhalRPZone zone, final int oldX,
					final int oldY, final int newX, final int newY) {

			}
		}

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			final StendhalRPZone Challengezone = (StendhalRPZone) SingletonRepository
					.getRPWorld().getRPZone("int_adventure_island");
			String zoneName = player.getName() + "_adventure_island";
			
			final StendhalRPZone zone = new StendhalRPZone(zoneName, Challengezone);
			//final StendhalRPZone zone = StendhalRPZone.fillContent(zoneName, Challengezone);
			zone.addMovementListener(new ChallengeMovementListener());
			Portal portal = new Teleporter(new Spot(player.getZone(), player.getX(), player.getY()));
			portal.setPosition(6, 3);
			zone.add(portal);
			int i = 0;
			int count = 0;
			// max 20 fails to place all creatures before we give up
			while (i < NUMBER_OF_CREATURES && count < 20) {
				int level = Rand.randUniform((int) (player.getLevel()*LEVEL_RATIO),player.getLevel()); 
				CreatureSpawner creatureSpawner = new CreatureSpawner();
				Creature creature = new Creature(creatureSpawner.calculateNextCreature(level));
				if (StendhalRPAction.placeat(zone, creature, Rand.randUniform(MIN_X,MAX_X), Rand.randUniform(MIN_Y, MAX_Y))) {
					i++;
				} else {
					logger.info(" could not add a creature to adventure island: " + creature);
					count++;	
				}
			}
			if (count>=20) {
				// consider refunding player some of the money :P or at least telling them
				logger.info("Tried too many times to place creatures in adventure island so less than the required number have been spawned");
			}
			zone.disallowIn();
			SingletonRepository.getRPWorld().addRPZone(zone);
			player.drop("money", COST);
			player.teleport(zone, 4, 6, Direction.DOWN, player);
			
		}
	}

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("And so, the hero has come.");
		npc.addQuest("Pay the fee of " + COST + " money and you can #fight my trained magical creatures. There will be " + NUMBER_OF_CREATURES + " in all, at a level to challenge you. Your life force is the power holding the spell - if you die, the island and your corpse and items are all lost." );
		npc.addHelp("If you are strong enough and will pay, you can #fight " + NUMBER_OF_CREATURES + " of my animals on a private adventure island. I summon it magically and it is held there by your energy; if you die, it vanishes and your corpse and items will be lost there, forever.");
		npc.addJob("I train magical animals for fighting and offer warriors the chance to #battle against them on a magical island.");
		npc.addOffer("To fight against " + NUMBER_OF_CREATURES + " of my trained creatures, chosen for your level, make the #challenge. Just know that if you die inside while on the island your corpse and items are lost forever.");		
		npc.addGoodbye("Bye.");
		npc.add(ConversationStates.ANY, Arrays.asList("challenge", "fight", "battle"), new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1),new PlayerHasItemWithHimCondition("money", COST)) , ConversationStates.QUEST_OFFERED, 
				"I accept your challenge. Remember if you die inside you CANNOT return to your corpse to retrieve any items you lost. Likewise if you leave and have left items on the ground, you lose them. Are you still sure you want to enter the adventure island?", 
				null);
		npc.add(ConversationStates.ANY, Arrays.asList("challenge", "fight", "battle"), new NotCondition(new PlayerHasItemWithHimCondition("money", COST)), ConversationStates.ATTENDING, "You don't have the fee, it is " + COST + " money.", 
				null);
		
		npc.add(ConversationStates.ANY, Arrays.asList("challenge", "fight", "battle"), new LevelLessThanCondition(MIN_LEVEL), ConversationStates.ATTENDING, "You are too weak to fight against " + NUMBER_OF_CREATURES  + " at once. Come back when you are at least Level " + MIN_LEVEL + ".", null);
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1),new PlayerHasItemWithHimCondition("money", COST)) , ConversationStates.IDLE, null, 
				new ChallengeChatAction());
				// incase player says challenge then drops the money before they say yes
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.YES_MESSAGES, new NotCondition(new PlayerHasItemWithHimCondition("money", COST)), ConversationStates.ATTENDING, "You don't have the fee, it is " + COST + " money.", 
						null);
		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null, ConversationStates.ATTENDING, "Fair enough.",null);	
	}

	
}
