package games.stendhal.server.maps.athor.ship;

import games.stendhal.common.Direction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;

/** Factory for the captain of Athor Ferry */
public class CaptainNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(String name) {
		// The NPC is defined as a ferry announcer because he notifies
		// passengers when the ferry arrives or departs.
		SpeakerNPC npc = new AthorFerry.FerryAnnouncerNPC(name) {
			public void onNewFerryState(int status) {
				if (status == AthorFerry.ANCHORED_AT_MAINLAND
						|| status == AthorFerry.ANCHORED_AT_ISLAND) {
					// capital letters symbolize screaming
					say("LET GO ANCHOR!");
				} else  {
					say("ANCHORS AWEIGH! SET SAIL!");
				}
				// Turn back to the wheel
				setDirection(Direction.DOWN);
			}

			@Override
			protected void onGoodbye(Player player) {
				// Turn back to the wheel
				setDirection(Direction.DOWN);
			}
		};
		return npc;
	}

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addGreeting("Yo-ho-ho, me bucko!");
		npc.addGoodbye("So long...");
		// if you can make up a help message that is more helpful to the,
		// player, feel free to replace this one.
		npc.addHelp("Never look up when a sea gull is flying over ye head!");
		npc.addJob("I'm th' captain of me boat.");

		npc.add(ConversationStates.ATTENDING,
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
		
		AthorFerry.get().addListener(
				(AthorFerry.FerryAnnouncerNPC) npc);
	}
}