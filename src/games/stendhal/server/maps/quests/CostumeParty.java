package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.util.List;

/**
 * Puts the player into a funny constume
 */
public class CostumeParty extends AbstractQuest {

	// TODO: use server.entity.npc.OutfitChangeBehaviour

	private static final String QUEST_SLOT = "costume_party";
	
	private class CostumeStripTimer implements TurnListener {
		public void onTurnReached(int currentTurn, String message) {
			List<Player> players = StendhalRPRuleProcessor.get().getPlayers();
			for (Player player : players) {
				if (player.hasQuest(QUEST_SLOT)) {
					if (!player.isQuestCompleted(QUEST_SLOT)) {
						long expireTime = Long.parseLong(player.getQuest(QUEST_SLOT));
						if (expireTime < System.currentTimeMillis()) {
							if (player.has("outfit_org")) {
								player.put("outfit", player.get("outfit_org"));
								player.remove("outfit_org");
								player.notifyWorldAboutChanges();
								player.sendPrivateText("My costume is wearing away.");
							}
							player.setQuest(QUEST_SLOT, "done");
						}
					}
				}
			}
			TurnNotifier.get().notifyInTurns(65*3, this, null);
		}
	}
	
	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();
		TurnNotifier.get().notifyInTurns(65, new CostumeStripTimer(), null);
	}
}
