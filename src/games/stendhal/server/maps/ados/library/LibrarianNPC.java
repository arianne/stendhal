package games.stendhal.server.maps.ados.library;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.WikipediaAccess;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Library (Inside / Level 0).
 *
 * @author hendrik
 */
public class LibrarianNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildLibrary(zone, attributes);
	}

	private void buildLibrary(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Wikipedian") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(9, 9));
				nodes.add(new Node(9, 27));
				nodes.add(new Node(20, 27));
				nodes.add(new Node(20, 10));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian");
				addHelp("Just ask me to #explain something");
				add(ConversationStates.ATTENDING, "explain", null, ConversationStates.ATTENDING, null,
				        new ChatAction() {
					        public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
						        // extract the title
					        	final String title = sentence.getExpressionStringAfterVerb();

						        if (title == null) {
							        npc.say("What do you want to be explained?");
							        return;
						        }

						        final WikipediaAccess access = new WikipediaAccess(title);
						        final Thread thread = new Thread(access);
						        thread.setPriority(Thread.MIN_PRIORITY);
						        thread.setDaemon(true);
						        thread.start();
						        SingletonRepository.getTurnNotifier().notifyInTurns(10, new WikipediaWaiter(npc, access));
						        npc.say("Please wait, while I am looking it up in the book called #Wikipedia!");
					        }
				        });
				addReply("wikipedia",
						"Wikipedia is an Internet based project to create a #free encyclopedia");
				addReply("free",
				        "The Wikipedia content may be used according to the rules specified in the Creative Commons Attribution-ShareAlike License which can be found at #http://en.wikipedia.org/wiki/Wikipedia:Text_of_Creative_Commons_Attribution-ShareAlike_3.0_Unported_License");
				addGoodbye();
			}
		};

		npc.setEntityClass("investigatornpc");
		npc.setPosition(9, 9);
		npc.initHP(100);
		zone.add(npc);
	}

	protected class WikipediaWaiter implements TurnListener {

		private final WikipediaAccess access;

		private final SpeakerNPC npc;

		public WikipediaWaiter(final SpeakerNPC npc, final WikipediaAccess access) {
			this.npc = npc;
			this.access = access;
		}

		public void onTurnReached(final int currentTurn) {
			if (!access.isFinished()) {
				SingletonRepository.getTurnNotifier().notifyInTurns(3, new WikipediaWaiter(npc, access));
				return;
			}
			if (access.getError() != null) {
				npc.say("Sorry, I cannot access the bookcase at the moment.");
				return;
			}

			if ((access.getText() != null) && (access.getText().length() > 0)) {
				final String content = access.getProcessedText();
				npc.say(content);
			} else {
				npc.say("Sorry, this book has still to be written.");
			}
		}
	}
}
