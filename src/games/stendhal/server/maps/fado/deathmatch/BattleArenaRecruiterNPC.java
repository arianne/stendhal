package games.stendhal.server.maps.fado.deathmatch;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Entrance to battle arena.
 */
public class BattleArenaRecruiterNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildBattleArenaRecruiter(zone);
	}

	private void buildBattleArenaRecruiter(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Brutus") {

			@Override
			protected void createPath() {
				// TODO: Determine if this is really a 'no path'
				List<Node> path = new LinkedList<Node>();
				path.add(new Node(42, 39));
				setPath(new FixedPath(path, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey there. You look like a reasonable fighter. Maybe you might like to try the #Battle #Arena #challenge...");
				addJob("I'm recruiter for the Fado #battle #arena, as #Thonatun may have told you.");
				addHelp("Have you ever heard of the Semos #deathmatch.");
				add(ConversationStates.ATTENDING, "battle", null, ConversationStates.ATTENDING,
				        "The battle arena is the ultimate challenge for true #heroes.", null);
				add(ConversationStates.ATTENDING, "heroes", null, ConversationStates.ATTENDING,
				        "Are you such a hero? I can take you to the #challenge.", null);
				addGoodbye("I hope you will enjoy the Battle Arena");

				add(ConversationStates.ATTENDING, "challenge", null, ConversationStates.ATTENDING, null,
				        new SpeakerNPC.ChatAction() {

					        @Override
					        public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						        if (player.getLevel() >= 20) {
							        StendhalRPZone zone = StendhalRPWorld.get().getZone("int_fado_battle_arena");
							        player.teleport(zone, 33, 26, Direction.DOWN, null);
						        } else {
							        engine.say("Sorry, you are too weak! Maybe you should train a bit more before coming back.");
						        }
					        }
				        });
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(42, 39);
		npc.initHP(100);
		zone.add(npc);
	}
}
