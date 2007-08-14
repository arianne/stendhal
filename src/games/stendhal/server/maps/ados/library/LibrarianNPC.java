package games.stendhal.server.maps.ados.library;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.util.WikipediaAccess;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Library (Inside / Level 0)
 *
 * @author hendrik
 */
public class LibrarianNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildLibrary(zone, attributes);
	}

	private void buildLibrary(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Wikipedian") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(9, 8));
				nodes.add(new Node(9, 26));
				nodes.add(new Node(20, 26));
				nodes.add(new Node(20, 9));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian");
				addHelp("Just ask me to #explain #something");
				add(ConversationStates.ATTENDING, "explain", null, ConversationStates.ATTENDING, null,
				        new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC npc) {
						        // extract the title
						        int pos = text.indexOf(" ");
						        if (pos < 0) {
							        npc.say("What do you want to be explained?");
							        return;
						        }
						        String title = text.substring(pos + 1).trim();

						        WikipediaAccess access = new WikipediaAccess(title);
						        Thread thread = new Thread(access);
						        thread.setPriority(Thread.MIN_PRIORITY);
						        thread.setDaemon(true);
						        thread.start();
						        TurnNotifier.get().notifyInTurns(10, new WikipediaWaiter(npc, access));
						        npc.say("Please wait, while i am looking it up in the book called #Wikipedia!");
					        }
					        // TODO: implement pointer to authors, GFDL, etc...
				        });
				addReply("wikipedia",
						"Wikipedia is an Internet based project to create a #free encyclopedia");
				addReply("free",
				        "The Wikipedia content may be used according to the rules specified in the GNU General Documentation License which can be found at http://en.wikipedia.org/wiki/Wikipedia:Text_of_the_GNU_Free_Documentation_License");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "investigatornpc");
		npc.set(9, 8);
		npc.initHP(100);
		zone.add(npc);
	}

	protected class WikipediaWaiter implements TurnListener {

		private WikipediaAccess access;

		private SpeakerNPC npc;

		public WikipediaWaiter(SpeakerNPC npc, WikipediaAccess access) {
			this.npc = npc;
			this.access = access;
		}

		public void onTurnReached(int currentTurn, String message) {
			if (!access.isFinished()) {
				TurnNotifier.get().notifyInTurns(3, new WikipediaWaiter(npc, access));
				return;
			}
			if (access.getError() != null) {
				npc.say("Sorry, I cannot access the bookcase at the moment.");
				return;
			}

			if ((access.getText() != null) && (access.getText().length() > 0)) {
				String content = access.getProcessedText();
				npc.say(content);
			} else {
				npc.say("Sorry, this book has still to be written.");
			}
		}
	}
}
