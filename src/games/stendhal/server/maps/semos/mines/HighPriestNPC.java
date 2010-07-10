package games.stendhal.server.maps.semos.mines;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

public class HighPriestNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildMineArea(zone, attributes);
	}

	private void buildMineArea(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Aenihata") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						String reply = "I am summoning a barrier to keep the #balrog away.";

						if (player.getLevel() < 150) {
							reply += " The balrog will kill you instantly. Run away!.";
						} else {
							reply += " I will keep the barrier to protect Faiumoni. Kill it.";
						}
						raiser.say(reply);
					}
				});

				addReply("balrog",
						"The fearest creature that Bolrogh army has.");
				addGoodbye();
			}
		};

		npc.addInitChatMessage(null, new ChatAction() {
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("AenihataReward")
						&& (player.getLevel() >= 150)) {
					player.setQuest("AenihataReward", "done");

					player.setATKXP(1000000 + player.getATKXP());
					player.setDEFXP(10000000 + player.getDEFXP());
					player.addXP(100000);

					player.incATKXP();
					player.incDEFXP();
				}

				if (!player.hasQuest("AenihataFirstChat")) {
					player.setQuest("AenihataFirstChat", "done");
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setEntityClass("highpriestnpc");
		npc.setPosition(23, 44);
		npc.setDirection(Direction.RIGHT);
		npc.setLevel(390);
		npc.initHP(85);
		zone.add(npc);
	}
}
