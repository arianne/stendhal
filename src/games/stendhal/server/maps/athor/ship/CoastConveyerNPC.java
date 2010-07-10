package games.stendhal.server.maps.athor.ship;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

import java.util.Arrays;

/** Factory for cargo worker on Athor Ferry. */
//TODO: take NPC definition elements which are currently in XML and include here
public class CoastConveyerNPC extends SpeakerNPCFactory {
	private static StendhalRPZone islandDocksZone;
	private static StendhalRPZone mainlandDocksZone;

	private StendhalRPZone getIslandDockZone() {
		if (islandDocksZone == null) {

			islandDocksZone = SingletonRepository.getRPWorld().getZone("0_athor_island");
		}

		return islandDocksZone;
	}


	private Status ferryState;

	@Override
	public void createDialog(final SpeakerNPC npc) {
		//TODO: sailor-style language
		npc.addGoodbye("Goodbye!"); 
		npc.addGreeting("Ahoy, Matey! How can I #help you?");
		npc.addHelp("Ye can #disembark, but only when we're anchored a harbor. Just ask me for the #status if ye have no idea where we are.");
		npc.addJob("I'm taking passengers who want to #disembark to the coast with me rowing boat.");

		npc.add(ConversationStates.ATTENDING, "status",
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferryState.toString());
					}
				});

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("disembark", "leave"),
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						switch (ferryState) {
						case ANCHORED_AT_MAINLAND:
							npc.say("Do ye really want me to take ye to the mainland with me skiff?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
							break;
						case ANCHORED_AT_ISLAND:
							npc.say("Do ye really want me to take ye to the island with me skiff?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
							break;

						default:
							npc.say(ferryState.toString()
								+ " Ye can only get off the boat when it's anchored near a harbor.");

						}
				}
			});


		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						switch (ferryState) {
						case ANCHORED_AT_MAINLAND:
							player.teleport(getMainlandDocksZone(), 100, 100, Direction.LEFT, null);
							npc.setCurrentState(ConversationStates.IDLE);
							break;
						case ANCHORED_AT_ISLAND:
							player.teleport(getIslandDockZone(), 16, 89, Direction.LEFT, null);
							npc.setCurrentState(ConversationStates.IDLE);
							break;

						default:
							npc.say("Too bad! The ship has already set sail.");

						}

					}
				});

		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Aye, matey!", null);
		new AthorFerry.FerryListener() {

			
			public void onNewFerryState(final Status status) {
				ferryState = status;
				switch (status) {
				case ANCHORED_AT_MAINLAND:
					npc.say("Attention: The ferry has arrived at the mainland! You can now #disembark.");
					break;
				case ANCHORED_AT_ISLAND:
					npc.say("Attention: The ferry has arrived at the island! You can now #disembark.");
					break;
				default:
					npc.say("Attention: The ferry has set sail.");
					break;
				}

			}
			};
	}

	private static StendhalRPZone getMainlandDocksZone() {
		if (mainlandDocksZone == null) {
			mainlandDocksZone = SingletonRepository.getRPWorld().getZone("0_ados_coast_s_w2");
		}
		return mainlandDocksZone;
	}
}
