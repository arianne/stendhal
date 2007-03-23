package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnNotifier;

/**
 * Action to start a new deathmatch session for the player
 *
 * @author hendrik
 */
public class StartAction extends SpeakerNPC.ChatAction {

	private DeathmatchInfo deathmatchInfo;

	/**
	 * creates a new StartAction for the specified deathmatch
	 
	 * @param deathmatchInfo deathmatch to start
	 */
	public StartAction(DeathmatchInfo deathmatchInfo) {
		this.deathmatchInfo = deathmatchInfo;
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC engine) {
		engine.say("Have fun!");
		DeathmatchState deathmatchState = DeathmatchState.createStartState(player.getLevel());
		player.setQuest("deathmatch", deathmatchState.toQuestString());
		DeathmatchEngine scriptingAction = new DeathmatchEngine(player, deathmatchInfo);
		TurnNotifier.get().notifyInTurns(0, scriptingAction, null);
	}
}
