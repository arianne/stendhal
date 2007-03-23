package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.WikipediaAccess;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Library (Inside / Level 0)
 *
 * @author hendrik
 */
public class IL0_Library implements ZoneConfigurator {

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
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(9, 26));
				nodes.add(new Path.Node(20, 26));
				nodes.add(new Path.Node(20, 9));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian");
				addHelp("Just ask me to #explain #something");
				add(ConversationStates.ATTENDING, "explain", null, ConversationStates.ATTENDING, null,
				        new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, String text, SpeakerNPC engine) {
						        // extract the title
						        int pos = text.indexOf(" ");
						        if (pos < 0) {
							        engine.say("What do you want to be explained?");
							        return;
						        }
						        String title = text.substring(pos + 1).trim();

						        WikipediaAccess access = new WikipediaAccess(title);
						        Thread thread = new Thread(access);
						        thread.setPriority(Thread.MIN_PRIORITY);
						        thread.setDaemon(true);
						        thread.start();
						        TurnNotifier.get().notifyInTurns(10, new WikipediaWaiter(engine, access), null);
						        engine.say("Please wait, while i am looking it up in the book called #Wikipedia!");
					        }
					        // TODO: implement pointer to authors, GFDL, etc...
				        });
				addReply("wikipedia", "Wikipedia is an Internet based to create a #free encyclopedia");
				addReply(
				        "free",
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

		private WikipediaAccess access = null;

		private SpeakerNPC engine = null;

		public WikipediaWaiter(SpeakerNPC engine, WikipediaAccess access) {
			this.engine = engine;
			this.access = access;
		}

		public void onTurnReached(int currentTurn, String message) {
			if (!access.isFinished()) {
				TurnNotifier.get().notifyInTurns(3, new WikipediaWaiter(engine, access), null);
				return;
			}
			if (access.getError() != null) {
				engine.say("Sorry, I cannot access the bookcase at the moment");
				return;
			}

			if ((access.getText() != null) && (access.getText().length() > 0)) {
				String content = access.getProcessedText();
				engine.say(content);
			} else {
				engine.say("Sorry, this book has still to be written");
			}
		}
	}
}
