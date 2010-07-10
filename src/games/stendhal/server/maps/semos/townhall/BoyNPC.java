package games.stendhal.server.maps.semos.townhall;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

public class BoyNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosTownhallArea(zone, attributes);
	}

	private void buildSemosTownhallArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Tad") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {

						if (player.hasQuest("introduce_players")) {
							if (player.isQuestCompleted("introduce_players")) {
							raiser.say("Hi again " + player.getTitle() + "! Thanks again, I'm feeling much better now.");
							} else {
							raiser.say("*sniff* *sniff* I still feel ill, please hurry with that #favour for me.");
							}
						} else {
							if (!player.isGhost()) {
								raiser.say("Ssshh! Come here, " + player.getTitle() + "! I have a #task for you.");
							}
						}
					}
				});
				addGoodbye();
			}

			/* (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#onGoodbye(games.stendhal.server.entity.player.Player)
			 */
			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.RIGHT);
			}
			
			
			
		};

		npc.addInitChatMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("TadFirstChat")) {
					player.setQuest("TadFirstChat", "done");
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setEntityClass("childnpc");
		npc.setPosition(13, 38);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
