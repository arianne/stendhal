package games.stendhal.server.maps.ados.outside;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnimalKeeperNPC implements ZoneConfigurator {

	private static class AdosAttackableCreature extends AttackableCreature implements TurnListener {

		private static long lastShoutTime;

		private String cryForHelp;

		/**
		 * An attackable creature that will cause Katinka to shout if it
		 * is killed by a monster.
		 *
		 * @param copy template creature
		 */
		public AdosAttackableCreature(final Creature copy) {
			super(copy);
		}

		@Override
		public void onDead(final Entity killer) {
			super.onDead(killer);

			if (!(killer instanceof Player)) {
				final long currentTime = System.currentTimeMillis();
				if (lastShoutTime + 5 * 60 * 1000 < currentTime) {
					lastShoutTime = currentTime;
					cryForHelp = "Katinka shouts: Help! " + Grammar.A_noun(killer.getTitle()) + " is eating our "
					        + Grammar.plural(getTitle()) + ".";
					// HACK: we need to wait a turn because the message is lost otherwise
					SingletonRepository.getTurnNotifier().notifyInTurns(0, this);
				}
			}
		}

		@Override
		public Creature getInstance() {
			return new AdosAttackableCreature(this);
		}

		public void onTurnReached(final int currentTurn) {
			// HACK: we need to wait a turn because the message is lost otherwise
			// sends the message to all players
			SingletonRepository.getRuleProcessor().tellAllPlayers(cryForHelp);
		}
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildZooArea(zone, attributes);
	}

	private void buildZooArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Katinka") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(41, 40));
				nodes.add(new Node(51, 40));
				nodes.add(new Node(51, 46));
				nodes.add(new Node(58, 46));
				nodes.add(new Node(58, 42));
				nodes.add(new Node(51, 42));
				nodes.add(new Node(51, 40));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addOffer("Can you keep a secret? Dr. Feelgood, our veterinarian, can sell you medicine that he doesn't need for the animals.");
				addJob("I'm the keeper of this animal refuge. I will take care of any abandoned pets I am brought.");
				add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(final Player player, final Sentence sentence,
											final SpeakerNPC engine) {
							return player.hasPet();
						}
					}, 
					ConversationStates.SERVICE_OFFERED, "Have you brought that pet to be taken care of here?",
					null);
				
				add(ConversationStates.SERVICE_OFFERED,
					ConversationPhrases.YES_MESSAGES, null,
					ConversationStates.ATTENDING, null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(final Player player, final Sentence sentence,
										 final SpeakerNPC npc) {
							String petName = player.getPet().getTitle();
							npc.say("Thank you for rescuing this " + petName + ", I will take good care of it.");
							player.removePet(player.getPet());
							player.addKarma(30.0);
						}
					});

				add(ConversationStates.SERVICE_OFFERED,
					ConversationPhrases.NO_MESSAGES, null,
					ConversationStates.ATTENDING, "Oh, it's so nice to see an owner and their pet happy together. Good luck both of you.", null);
				
				add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(final Player player, final Sentence sentence,
											final SpeakerNPC engine) {
							return !player.hasPet();
						}
					}, 
					ConversationStates.ATTENDING, "If you should ever find an abandoned pet, please bring it to me.",
					null);

				addGoodbye("Goodbye!");
				
			}
			// remaining behaviour is defined in maps.quests.ZooFood.
		};

		npc.setEntityClass("woman_007_npc");
		npc.setPosition(41, 40);
		//npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);

		// put special RespawnPoints
		// 65, 34 bear
		final EntityManager manager = SingletonRepository.getEntityManager();
		Creature creature = new AdosAttackableCreature(manager.getCreature("bear"));
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 65, 34, creature, 1);
		zone.add(point);

		// 67, 29 bear
		point = new CreatureRespawnPoint(zone, 67, 29, creature, 1);
		zone.add(point);

		// 67, 31 black_bear
		creature = new AdosAttackableCreature(manager.getCreature("black bear"));
		point = new CreatureRespawnPoint(zone, 67, 31, creature, 1);
		zone.add(point);

		// 67, 35 black_bear
		point = new CreatureRespawnPoint(zone, 67, 35, creature, 1);
		zone.add(point);
	}
}
