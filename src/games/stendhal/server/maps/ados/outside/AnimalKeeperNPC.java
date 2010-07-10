package games.stendhal.server.maps.ados.outside;

import games.stendhal.common.Rand;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnimalKeeperNPC implements ZoneConfigurator {

	private static final String ZONE_NAME = "int_ados_pet_sanctuary";

	private static class AdosAttackableCreature extends AttackableCreature implements TurnListener {


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

		/*
		 disabled, see https://sourceforge.net/tracker/?func=detail&aid=2806268&group_id=1111&atid=101111

		private static long lastShoutTime;
		@Override
		public void onDead(final Entity killer, final boolean remove) {
			super.onDead(killer, remove);

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
		}*/

		@Override
		public Creature getNewInstance() {
			return new AdosAttackableCreature(this);
		}

		public void onTurnReached(final int currentTurn) {
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
					new ChatCondition() {
						public boolean fire(final Player player, final Sentence sentence,
											final Entity engine) {
							return player.hasPet();
						}
					}, 
					ConversationStates.SERVICE_OFFERED, "Have you brought that pet to be taken care of here?",
					null);
				
				add(ConversationStates.SERVICE_OFFERED,
					ConversationPhrases.YES_MESSAGES, null,
					ConversationStates.ATTENDING, null,
					new ChatAction() {
						public void fire(final Player player, final Sentence sentence,
										 final EventRaiser npc) {
							Pet pet = player.getPet();
							String petName = pet.getTitle();
							// these numbers are hardcoded, they're the area in the pet sanctuary which is for pets. It has food spawners.
							int x = Rand.randUniform(2, 12);
							int y = Rand.randUniform(7, 29);
							StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(ZONE_NAME);
							if (StendhalRPAction.placeat(zone, pet, x, y)) {
								player.removePet(pet);
								// reward with some karma but limit abuse
								if (player.getKarma() < 60.0) {
									player.addKarma(30.0);
								}
								npc.say("Thank you for rescuing this " + petName + ", I will take good care of it. Remember you can come back "
								+ "and visit the pet sanctuary any time you like!");
								notifyWorldAboutChanges();
							} else {
								// there was no room for the pet
								npc.say("It looks like we don't have any space left in our pet sanctuary! I hope you can look after this " + petName + " a little longer.");  
							}
						}
					});

				add(ConversationStates.SERVICE_OFFERED,
					ConversationPhrases.NO_MESSAGES, null,
					ConversationStates.ATTENDING, "Oh, it's so nice to see an owner and their pet happy together. Good luck both of you.", null);
				
				add(ConversationStates.ATTENDING,
					ConversationPhrases.HELP_MESSAGES,
					new ChatCondition() {
						public boolean fire(final Player player, final Sentence sentence,
											final Entity engine) {
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
