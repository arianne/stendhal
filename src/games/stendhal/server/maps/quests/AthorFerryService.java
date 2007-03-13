package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is not a real quest, but rather a ferry service that brings players
 * from the mainland to Athor island and back.
 * 
 * NPCs that have to do with the ferry:
 * * Eliza   - brings players from the mainland docks to the ferry.
 * * Jessica - brings players from the island docks to the ferry.
 * * Jackie  - brings players from the ferry to the docks.
 * * Captain - the ship captain.
 * * Laura   - the ship galley maid.
 * 
 * @author daniel
 *
 */
public class AthorFerryService extends AbstractQuest {

	private static final int PRICE = 1; // TODO: raise to 25

	/**
	 * This class simulates the ferry going back and forth between the mainland
	 * and the island.
	 */
	private static class AthorFerry implements TurnListener {

		public static final int ANCHORED_AT_MAINLAND = 0;

		public static final int DRIVING_TO_ISLAND = 1;

		public static final int ANCHORED_AT_ISLAND = 2;

		public static final int DRIVING_TO_MAINLAND = 3;

		/** The Singleton instance. */
		private static AthorFerry instance;

		/**
		 * A list of non-player characters that get notice when the ferry
		 * arrives or departs, so that they can react accordingly, e.g.
		 * inform nearby players.
		 */
		private List<FerryAnnouncerNPC> listeners;
		
		private int state;

		private static Map<Integer, Integer> durations;

		private static Map<Integer, String> descriptions;

		private AthorFerry() {
			durations = new HashMap<Integer, Integer>();
			// ca. 2 minutes
			durations.put(ANCHORED_AT_MAINLAND, 120 * 3);
			// ca. 5 minutes
			durations.put(DRIVING_TO_ISLAND, 300 * 3);
			durations.put(ANCHORED_AT_ISLAND, 120 * 3);
			durations.put(DRIVING_TO_MAINLAND, 300 * 3);

			descriptions = new HashMap<Integer, String>();
			descriptions.put(ANCHORED_AT_MAINLAND,
					"The ferry is currently anchored at the mainland.");
			descriptions.put(DRIVING_TO_ISLAND,
					"The ferry is currently sailing to the island.");
			descriptions.put(ANCHORED_AT_ISLAND,
					"The ferry is currently anchored at the island.");
			descriptions.put(DRIVING_TO_MAINLAND,
					"The ferry is currently sailing to the mainland.");

			state = DRIVING_TO_MAINLAND;
			
			listeners = new LinkedList<FerryAnnouncerNPC>();
			// initiate the turn notification cycle
			TurnNotifier.get().notifyInTurns(1, this, null);
		}

		/**
		 * @return The Singleton instance.
		 */
		public static AthorFerry get() {
			if (instance == null) {
				instance = new AthorFerry();
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

	protected static abstract class FerryAnnouncerNPC extends SpeakerNPC {

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
				
				addGreeting("Yo-ho-ho, me bucko!");
				addGoodbye("So long...");
			
				// TODO
				addHelp("...");
				addJob("I'm th' captain of me boat.");

				add(ConversationStates.ATTENDING,
						"status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthorFerry.get()
										.getCurrentDescription());
							}
						});
			}
			
			@Override
			protected void onGoodbye(Player player) {
				System.out.println("GOODBYE");
				setDirection(Direction.DOWN);
			}


			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_MAINLAND || status == AthorFerry.ANCHORED_AT_ISLAND) {
					// capital letters symbolize screaming
					say("LET GO ANCHOR!");
				} else  {
					say("ANCHORS AWEIGH! SET SAIL!");
				}
				// Turn back to the wheel
				setDirection(Direction.DOWN);
			}
		};
		
		NPCList.get().add(captain);
		shipZone.assignRPObjectID(captain);
		captain.put("class", "piratenpc");
		captain.set(22, 37);
		captain.setDirection(Direction.DOWN);
		captain.initHP(100);
		AthorFerry.get().addListener(captain);
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
				addHelp("Ye can #disembark, but only when we're anchored a harbor. Just ask me for the #status if ye have no idea where we are.");
				addJob("I'm taking passengers who want to #disembark to the coast with me rowing boat.");

				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthorFerry.get()
										.getCurrentDescription());
							}
						});

				add(ConversationStates.ATTENDING,
						Arrays.asList("disembark", "leave"),
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								AthorFerry ferry = AthorFerry.get();
								if (ferry.getState() == AthorFerry.ANCHORED_AT_MAINLAND) {
									npc.say("Do ye really want me to take ye to the mainland with me skiff?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else if (ferry.getState() == AthorFerry.ANCHORED_AT_ISLAND) {
									npc.say("Do ye really want me to take ye to the island with me skiff?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else{
									npc.say(AthorFerry.get()
										.getCurrentDescription()
										+ " Ye can only get off the boat when it's anchored near a harbor.");
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
								AthorFerry ferry = AthorFerry.get();
								if (ferry.getState() == AthorFerry.ANCHORED_AT_MAINLAND) {
									player.teleport(mainlandDocksZone, 100, 100, Direction.LEFT, null);
									npc.setCurrentState(ConversationStates.IDLE);
								} else if (ferry.getState() == AthorFerry.ANCHORED_AT_ISLAND) {
									player.teleport(islandDocksZone, 144, 89, Direction.LEFT, null);
									npc.setCurrentState(ConversationStates.IDLE);
								} else {
									npc.say("Too bad! The ship has already set sail.");
								}
							}
						});

				add(ConversationStates.SERVICE_OFFERED,
						SpeakerNPC.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Aye, matey!", null);
			}

			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_MAINLAND) {
					say("Attention: The ferry has arrived at the mainland! You can now #disembark.");
				} else if (status == AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: The ferry has arrived at the island! You can now #disembark.");
				} else  {
					say("Attention: The ferry has set sail.");
				}
			}
		};
		NPCList.get().add(jackie);
		shipZone.assignRPObjectID(jackie);
		jackie.put("class", "pirate_sailor2npc");
		jackie.set(29, 33);
		jackie.setDirection(Direction.LEFT);
		jackie.initHP(100);
		AthorFerry.get().addListener(jackie);
		shipZone.addNPC(jackie);
	}

	private void buildShipUnderDeck1Area(final StendhalRPZone shipUnderDeck1Zone) {
		FerryAnnouncerNPC laura = new FerryAnnouncerNPC("Laura") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the oven
				nodes.add(new Path.Node(27, 27));
				// to the table
				nodes.add(new Path.Node(27, 30));
				// to the dining room
				nodes.add(new Path.Node(18, 30));
				// to the barrel
				nodes.add(new Path.Node(28, 30));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Ahoy! Welcome to the galley!");
				addJob("I'm running the galley on this ship. I #offer fine foods for the passengers and alcohol for the crew.");
				addHelp("The crew mates drink beer and grog all day. But if you want some more exclusive drinks, go to the cocktail bar at Athor beach.");
				Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("beer", 10);
				offerings.put("wine", 15);
				// more expensive than in normal taverns 
				offerings.put("ham", 100);
				offerings.put("pie", 150);
				addSeller(new SellerBehaviour(offerings));
				addGoodbye();
			}
			
			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_MAINLAND || status == AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: We have arrived!");
				} else  {
					say("Attention: We have set sail!");
				}
			}

		};
		NPCList.get().add(laura);
		shipUnderDeck1Zone.assignRPObjectID(laura);
		laura.put("class", "tavernbarmaidnpc");
		laura.set(27, 27);
		laura.initHP(100);
		AthorFerry.get().addListener(laura);
		shipUnderDeck1Zone.addNPC(laura);
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
				addGreeting("Welcome to the Athor #ferry service! How can I #help you?");
				addHelp("You can #board the #ferry for only "
						+ PRICE
						+ " gold, but only when it's anchored near this harbor. Just ask me for the #status if you want to know where the ferry is.");
				addJob("If passengers want to #board the #ferry to Athor island, I take them to the ship with this rowing boat.");
				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthorFerry.get()
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
								AthorFerry ferry = AthorFerry.get();
								if (ferry.getState() == AthorFerry.ANCHORED_AT_MAINLAND) {
									npc.say("In order to board the ferry, you have to pay " + PRICE
								+ " gold. Do you want to pay?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else {
									npc.say(AthorFerry.get()
										.getCurrentDescription()
										+ " You can only board the ferry when it's anchored at the mainland.");
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
				if (status == AthorFerry.ANCHORED_AT_MAINLAND) {
					say("Attention: The ferry has arrived at this coast! You can now #board the ship.");
				} else if (status == AthorFerry.DRIVING_TO_ISLAND) {
					say("Attention: The ferry has set sail. You can no longer board it.");
				}
			}
		};
		NPCList.get().add(eliza);
		mainlandDocksZone.assignRPObjectID(eliza);
		eliza.put("class", "woman_008_npc");
		eliza.set(101, 102);
		eliza.setDirection(Direction.LEFT);
		eliza.initHP(100);
		AthorFerry.get().addListener(eliza);
		mainlandDocksZone.addNPC(eliza);
	}

	private void buildIslandDocksArea(final StendhalRPZone islandDocksZone, final StendhalRPZone shipZone) {
		FerryAnnouncerNPC jessica = new FerryAnnouncerNPC ("Jessica") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGoodbye("Goodbye!");
				addGreeting("Welcome to the Athor #ferry service! How can I #help you?");
				addHelp("You can #board the #ferry for only "
						+ PRICE
						+ " gold, but only when it's anchored near this harbor. Just ask me for the #status if you want to know where the ferry is.");
				addJob("If passengers want to #board the #ferry to the mainland, I take them to the ship with this rowing boat.");
				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
							@Override
							public void fire(Player player, String text,
									SpeakerNPC npc) {
								npc.say(AthorFerry.get()
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
								AthorFerry ferry = AthorFerry.get();
								if (ferry.getState() == AthorFerry.ANCHORED_AT_ISLAND) {
									npc.say("In order to board the ferry, you have to pay " + PRICE
								+ " gold. Do you want to pay?");
									npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
								} else {
									npc.say(AthorFerry.get()
										.getCurrentDescription()
										+ " You can only board the ferry when it's anchored at the island.");
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
				if (status == AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: The ferry has arrived at this coast! You can now #board the ship.");
				} else if (status == AthorFerry.DRIVING_TO_MAINLAND) {
					say("Attention: The ferry has taken off. You can no longer board it.");
				}
			}
		};
		NPCList.get().add(jessica);
		islandDocksZone.assignRPObjectID(jessica);
		jessica.put("class", "woman_008_npc");
		jessica.set(144, 89);
		jessica.setDirection(Direction.LEFT);
		jessica.initHP(100);
		AthorFerry.get().addListener(jessica);
		islandDocksZone.addNPC(jessica);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addToWorld() {
		StendhalRPZone mainlandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_ados_coast_s_w2");
		StendhalRPZone shipZone = (StendhalRPZone) StendhalRPWorld.get()
		.getRPZone("0_athor_ship_w2");
		StendhalRPZone shipUnderDeck1Zone = (StendhalRPZone) StendhalRPWorld.get()
		.getRPZone("-1_athor_ship_w2");
		StendhalRPZone islandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_athor_island_w");
		buildMainlandDocksArea(mainlandDocksZone, shipZone);
		buildShipArea(shipZone, mainlandDocksZone, islandDocksZone);
		buildShipUnderDeck1Area(shipUnderDeck1Zone);
		buildIslandDocksArea(islandDocksZone, shipZone);

	}

}
