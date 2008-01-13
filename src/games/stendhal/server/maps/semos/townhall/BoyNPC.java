package games.stendhal.server.maps.semos.townhall;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
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
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTownhallArea(zone, attributes);
	}

	private void buildSemosTownhallArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Tad") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {

					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {

						if (player.hasQuest("introduce_players")) {
							if (!player.isQuestCompleted("introduce_players")) {
							engine.say("*sniff* *sniff* I still feel ill, please hurry");
							} else {
							engine.say("Hi again " + player.getTitle() + "! Thanks again, I'm feeling much better now.");
							}
						} else {
							engine.say("Ssshh! Come here, " + player.getTitle() + "! I have a #task for you.");
						}
					}
				});
				addGoodbye();
			}
		};

		npc.addInitChatMessage(null, new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
				if (!player.hasQuest("TadFirstChat")) {
					player.setQuest("TadFirstChat", "done");
					engine.listenTo(player, "hi");
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
