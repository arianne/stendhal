package games.stendhal.server.maps.semos.mines;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

public class HighPriestNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildMineArea(zone, attributes);
	}

	private void buildMineArea(StendhalRPZone zone,
			Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Aenihata") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						String reply = "I am summoning a barrier to keep the #balrog away.";

						if (player.getLevel() < 150) {
							reply += " The balrog will kill you instantly. Run away!.";
						} else {
							reply += " I will keep the barrier to protect Faiumoni. Kill it.";
						}
						engine.say(reply);
					}
				});

				addReply("balrog",
						"The fearest creature that Bolrogh army has.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.addInitChatMessage(null, new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (!player.hasQuest("AenihataReward")
						&& player.getLevel() >= 150) {
					player.setQuest("AenihataReward", "done");

					player.setATKXP(1000000 + player.getATKXP());
					player.setDEFXP(10000000 + player.getDEFXP());
					player.addXP(100000);

					player.incATKXP();
					player.incDEFXP();
				}

				if (!player.hasQuest("AenihataFirstChat")) {
					player.setQuest("AenihataFirstChat", "done");
					engine.listenTo(player, "hi");
				}
			}
		});
		npc.put("class", "highpriestnpc");
		npc.setPosition(23, 44);
		npc.setDirection(Direction.RIGHT);
		npc.setLevel(390);
		npc.initHP(85);
		zone.add(npc);
	}
}
