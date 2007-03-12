package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AthosFerryService extends AbstractQuest {

	private static final int PRICE = 1; // TODO: raise to 25

	/**
	 * This class simulates the ferry going back and forth between the mainland
	 * and the island.
	 */
	private static class AthosFerry implements TurnListener {

		public static final int ANCHORED_AT_MAINLAND = 0;

		public static final int DRIVING_TO_ISLAND = 1;

		public static final int ANCHORED_AT_ISLAND = 2;

		public static final int DRIVING_TO_MAINLAND = 3;

		/** The Singleton instance. */
		private static AthosFerry instance;

		/**
		 * A list of non-player characters that get notice when the ferry
		 * arrives or departs, so that they can react accordingly, e.g.
		 * inform nearby players.
		 */
		private List<FerryAnnouncerNPC> listeners;
		
		private int state;

		private static Map<Integer, Integer> durations;

		private static Map<Integer, String> descriptions;

		private AthosFerry() {
			durations = new HashMap<Integer, Integer>();
			// ca. 2 minutes
			durations.put(ANCHORED_AT_MAINLAND, 120 * 3);
			// ca. 5 minutes
			durations.put(DRIVING_TO_ISLAND, 300 * 3);
			durations.put(ANCHORED_AT_ISLAND, 120 * 3);
			durations.put(DRIVING_TO_MAINLAND, 300 * 3);

			descriptions = new HashMap<Integer, String>();
			descriptions.put(ANCHORED_AT_MAINLAND,
					"The ferry is currently anchoring at the mainland.");
			descriptions.put(DRIVING_TO_ISLAND,
					"The ferry is currently driving to the island.");
			descriptions.put(ANCHORED_AT_ISLAND,
					"The ferry is currently anchoring at the island.");
			descriptions.put(DRIVING_TO_MAINLAND,
					"The ferry is currently driving to the mainland.");

			state = DRIVING_TO_MAINLAND;
			
			listeners = new LinkedList<FerryAnnouncerNPC>();
			// initiate the turn notification cycle
			TurnNotifier.get().notifyInTurns(1, this, null);
		}

		/**
		 * @return The Singleton instance.
		 */
		public static AthosFerry get() {
			if (instance == null) {
				instance = new AthosFerry();
			}
			return instance;
		}
		
		/**
		 * @return one of ANCHORED_AT_MAINLAND, DRIVING_TO_ISLAND,
		 *         ANCHORED_AT_ISLAND, and DRIVING_TO_MAINLAND.
		 */
		public int getState() {
			return state;
		}

		/**
		 * Gets a textual description of the ferry's status.
		 * @return A String representation of the ferry's current state.
		 */
		public String getCurrentDescription() {
			return descriptions.get(state);
		}

		/**
		 * Is called when the ferry has either arrived at or departed from
		 * a harbor.
		 */
		public void onTurnReached(int currentTurn, String message) {
			// cycle to the next state
			state = (state + 1) % 4;
			for (FerryAnnouncerNPC npc: listeners) {
				npc.onNewFerryState(state);
			}
			TurnNotifier.get().notifyInTurns(durations.get(state), this, null);
		}
		
		public void addListener(FerryAnnouncerNPC npc) {
			listeners.add(npc);
		}
	}

	private static abstract class FerryAnnouncerNPC extends SpeakerNPC {

		public FerryAnnouncerNPC(String name) {
			super(name);
		}

		public abstract void onNewFerryState(int status);
	}

	private void buildShipArea(final StendhalRPZone shipZone, final StendhalRPZone mainlandDocksZone, final StendhalRPZone islandDocksZone) {
		FerryAnnouncerNPC captain = new FerryAnnouncerNPC ("Captain") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				
				addGreeting("Ahoy!");
				
				add(ConversationStates.ANY,
						SpeakerNPC.GOODBYE_MESSAGES,
						ConversationStates.IDLE,
						"Goodbye",
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.setDirection(Direction.DOWN);
					}
				});
				// TODO
				addHelp("...");
				addJob("I'm the captain of this ferry.");

				add(ConversationStates.ATTENDING,
						"status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthosFerry.get()
										.getCurrentDescription());
							}
						});
			}


			public void onNewFerryState(int status) {
				if (status == AthosFerry.ANCHORED_AT_MAINLAND || status == AthosFerry.ANCHORED_AT_ISLAND) {
					say("Let go anchor! We have arrived.");
				} else  {
					say("Anchors aweigh! We're departing.");
				}
			}
		};
		NPCList.get().add(captain);
		shipZone.assignRPObjectID(captain);
		captain.put("class", "piratenpc");
		captain.set(22, 37);
		captain.setDirection(Direction.DOWN);
		captain.initHP(100);
		AthosFerry.get().addListener(captain);
		shipZone.addNPC(captain);
	

		FerryAnnouncerNPC jackie = new FerryAnnouncerNPC ("Jackie") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGoodbye("Goodbye!"); //TODO: sailor-style language
				addGreeting("Ahoy, Matey! How can I #help you?");
				addHelp("You can #deboard this ferry, but only when it's anchoring a harbor. Just ask me for the #status if you don't know where we are.");
				addJob("I'm taking passengers who want to #deboard to the coast with this rowing boat.");

				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthosFerry.get()
										.getCurrentDescription());
							}
						});

				add(ConversationStates.ATTENDING,
						"deboard",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								AthosFerry ferry = AthosFerry.get();
								if (ferry.getState() == AthosFerry.ANCHORED_AT_MAINLAND) {
									npc.say("Do you really want me to take you to the mainland?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else if (ferry.getState() == AthosFerry.ANCHORED_AT_ISLAND) {
									npc.say("Do you really want me to take you to the island?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else{
									npc.say(AthosFerry.get()
										.getCurrentDescription()
										+ " You can only deboard the ferry when it's anchoring near a harbor.");
								}
							}
						});
						
						
				add(ConversationStates.SERVICE_OFFERED,
						SpeakerNPC.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING, null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								AthosFerry ferry = AthosFerry.get();
								if (ferry.getState() == AthosFerry.ANCHORED_AT_MAINLAND) {
									player.teleport(mainlandDocksZone, 100, 100, Direction.LEFT, null);
									npc.setCurrentState(ConversationStates.IDLE);
								} else if (ferry.getState() == AthosFerry.ANCHORED_AT_ISLAND) {
									// TODO
									player.teleport(islandDocksZone, 100, 100, Direction.LEFT, null);
									npc.setCurrentState(ConversationStates.IDLE);
								} else {
									npc.say("Too bad! The ship has already taken off.");
								}
							}
						});

				add(ConversationStates.SERVICE_OFFERED,
						"no",
						null,
						ConversationStates.ATTENDING,
						"Aye, matey!", null);
			}

			public void onNewFerryState(int status) {
				if (status == AthosFerry.ANCHORED_AT_MAINLAND) {
					say("Attention: The ferry has arrived at the mainland! You can now #deboard the ship.");
				} else if (status == AthosFerry.ANCHORED_AT_ISLAND) {
					say("Attention: The ferry has arrived at the island! You can now #deboard the ship.");
				} else  {
					say("Attention: The ferry has taken off.");
				}
			}
		};
		NPCList.get().add(jackie);
		shipZone.assignRPObjectID(jackie);
		jackie.put("class", "pirate_sailornpc");
		jackie.set(29, 33);
		jackie.setDirection(Direction.LEFT);
		jackie.initHP(100);
		AthosFerry.get().addListener(jackie);
		shipZone.addNPC(jackie);
	}

	private void buildMainlandDocksArea(final StendhalRPZone mainlandDocksZone, final StendhalRPZone shipZone) {
		FerryAnnouncerNPC eliza = new FerryAnnouncerNPC ("Eliza") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGoodbye("Goodbye!");
				addGreeting("Welcome to the Athos #ferry service! How can I #help you?");
				addHelp("You can #board the #ferry for only "
						+ PRICE
						+ " gold, but only when it's anchoring near this harbor. Just ask me for the #status if you want to know where the ferry is.");
				addJob("If passengers want to #board the #ferry to Athos island, I take them to the ship with this rowing boat.");
				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthosFerry.get()
										.getCurrentDescription());
							}
						});

				add(ConversationStates.ATTENDING,
						"board",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								AthosFerry ferry = AthosFerry.get();
								if (ferry.getState() == AthosFerry.ANCHORED_AT_MAINLAND) {
									npc.say("In order to board the ferry, you have to pay " + PRICE
								+ " gold. Do you want to pay?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else {
									npc.say(AthosFerry.get()
										.getCurrentDescription()
										+ " You can only board the ferry when it's anchoring at the mainland.");
								}
							}
						});
						
						
				add(ConversationStates.SERVICE_OFFERED,
						SpeakerNPC.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING, null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								if (player.drop("money", PRICE)) {
									player.teleport(shipZone, 27, 33,
											Direction.LEFT, null);
								} else {
									npc.say("Hey! You don't have enough money!");
								}
							}
						});

				add(ConversationStates.SERVICE_OFFERED,
						"no",
						null,
						ConversationStates.ATTENDING,
						"You don't know what you're missing, landlubber!",
						null);

			}

			public void onNewFerryState(int status) {
				if (status == AthosFerry.ANCHORED_AT_MAINLAND) {
					say("Attention: The ferry has arrived at this coast! You can now #board the ship.");
				} else if (status == AthosFerry.DRIVING_TO_ISLAND) {
					say("Attention: The ferry has taken off. You can no longer board it.");
				}
			}
		};
		NPCList.get().add(eliza);
		mainlandDocksZone.assignRPObjectID(eliza);
		eliza.put("class", "woman_008_npc");
		eliza.set(101, 102);
		eliza.setDirection(Direction.LEFT);
		eliza.initHP(100);
		AthosFerry.get().addListener(eliza);
		mainlandDocksZone.addNPC(eliza);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addToWorld() {
		StendhalRPZone mainlandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_ados_coast_s_w2");
		StendhalRPZone shipZone = (StendhalRPZone) StendhalRPWorld.get()
				.getRPZone("0_athos_ship_w2");
		// TODO
		StendhalRPZone islandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_ados_coast_s_w2");
		buildMainlandDocksArea(mainlandDocksZone, shipZone);
		buildShipArea(shipZone, mainlandDocksZone, islandDocksZone);

	}

}
