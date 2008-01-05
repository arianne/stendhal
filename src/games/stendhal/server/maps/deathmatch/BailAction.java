package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * handles "bail" trigger to free the player from deathmatch with a penalty.
 *
 * @author hendrik
 */
public class BailAction extends SpeakerNPC.ChatAction {

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		String questInfo = player.getQuest("deathmatch");
		if (questInfo == null) {
			engine.say("Coward, you haven't even #started!");
			return;
		}

		DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.START) {
			engine.say("Coward, we haven't even #started!");
			return;
		}

		deathmatchState.setLifecycleState(DeathmatchLifecycle.BAIL);
		player.setQuest("deathmatch", deathmatchState.toQuestString());

		// TODO: fix race condition until bail is processed in DeathmatchEngine
		Item helmet = player.getFirstEquipped("trophy_helmet");
		if (helmet != null && helmet.has("def") && helmet.getInt("def") > 1) {
			engine.say("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.");
		} else {
			engine.say("Coward! You're not as experienced as you used to be.");
		}
		return;
	}
}
