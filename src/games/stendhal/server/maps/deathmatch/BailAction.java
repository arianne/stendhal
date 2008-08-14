package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * handles "bail" trigger to free the player from deathmatch with a penalty.
 */
public class BailAction implements ChatAction {

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final String questInfo = player.getQuest("deathmatch");
		if (questInfo == null) {
			engine.say("Coward, you haven't even #started!");
			return;
		}

		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		if (deathmatchState.getLifecycleState() != DeathmatchLifecycle.START) {
			engine.say("Coward, we haven't even #started!");
			return;
		}

		deathmatchState.setLifecycleState(DeathmatchLifecycle.BAIL);
		player.setQuest("deathmatch", deathmatchState.toQuestString());

		// TODO: fix race condition until bail is processed in DeathmatchEngine
		final Item helmet = player.getFirstEquipped("trophy helmet");
		if ((helmet != null) && helmet.has("def") && (helmet.getInt("def") > 1)) {
			engine.say("Coward! I'm sorry to inform you, for this your helmet has been magically weakened.");
		} else {
			engine.say("Coward! You're not as experienced as you used to be.");
		}
		return;
	}
}
