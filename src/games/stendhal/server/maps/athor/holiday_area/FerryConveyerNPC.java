package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry;

/**
 * Factory for an NPC who brings players from the docks to Athor Ferry
 * in a rowing boat.
 */
public class FerryConveyerNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		// The NPC is defined as a ferry announcer because he notifies
		// passengers when the ferry arrives or departs.
		SpeakerNPC npc = new AthorFerry.FerryAnnouncerNPC(name) {
			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_ISLAND) {
					say("Attention: The ferry has arrived at this coast! You can now #board the ship.");
				} else if (status == AthorFerry.DRIVING_TO_MAINLAND) {
					say("Attention: The ferry has taken off. You can no longer board it.");
				}
			}
		};
		return npc;
	}

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGoodbye("Goodbye!");
		npc.addGreeting("Welcome to the Athor #ferry service! How can I #help you?");
		npc.addHelp("You can #board the #ferry for only "
				+ AthorFerry.PRICE
				+ " gold, but only when it's anchored near this harbor. Just ask me for the #status if you want to know where the ferry is.");
		npc.addJob("If passengers want to #board the #ferry to the mainland, I take them to the ship with this rowing boat.");
		npc.addReply("ferry", "The ferry sails regularly between this island and the mainland, Faiumoni. You can #board it when it's here. Ask me for the #status to find out where it is currently.");
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
							npc.say("In order to board the ferry, you have to pay " + AthorFerry.PRICE
						+ " gold. Do you want to pay?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
						} else {
							npc.say(AthorFerry.get()
								.getCurrentDescription()
								+ " You can only board the ferry when it's anchored at the island.");
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
						if (player.drop("money", AthorFerry.PRICE)) {
							AthorFerry.get().boardFerry(player);
						} else {
							npc.say("Hey! You don't have enough money!");
						}
					}
				});

		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"You don't know what you're missing, landlubber!",
				null);

		AthorFerry.get().addListener(
				(AthorFerry.FerryAnnouncerNPC) npc);
	}
}