package games.stendhal.server.maps.athor.ship;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;

/** Factory for cargo worker on Athor Ferry */
public class CoastConveyerNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		// The NPC is defined as a ferry announcer because he notifies
		// passengers when the ferry arrives or departs.
		SpeakerNPC npc = new AthorFerry.FerryAnnouncerNPC(name) {
			@Override
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
		return npc;
	}

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGoodbye("Goodbye!"); //TODO: sailor-style language
		npc.addGreeting("Ahoy, Matey! How can I #help you?");
		npc.addHelp("Ye can #disembark, but only when we're anchored a harbor. Just ask me for the #status if ye have no idea where we are.");
		npc.addJob("I'm taking passengers who want to #disembark to the coast with me rowing boat.");

		npc.add(ConversationStates.ATTENDING, "status",
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

		npc.add(ConversationStates.ATTENDING,
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
						} else {
							npc.say(AthorFerry.get()
								.getCurrentDescription()
								+ " Ye can only get off the boat when it's anchored near a harbor.");
						}
					}
				});


		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC npc) {
						AthorFerry ferry = AthorFerry.get();
						if (ferry.getState() == AthorFerry.ANCHORED_AT_MAINLAND) {
							ferry.disembarkToMainland(player);
							npc.setCurrentState(ConversationStates.IDLE);
						} else if (ferry.getState() == AthorFerry.ANCHORED_AT_ISLAND) {
							ferry.disembarkToIsland(player);
							npc.setCurrentState(ConversationStates.IDLE);
						} else {
							npc.say("Too bad! The ship has already set sail.");
						}
					}
				});

		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Aye, matey!", null);
		AthorFerry.get().addListener(
				(AthorFerry.FerryAnnouncerNPC) npc);
	}
}
