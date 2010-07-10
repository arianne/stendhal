package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * handle the players request to leave the deathmatch
 * (if it is allowed in the current state).
 */
public class LeaveAction implements ChatAction {

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.DONE) {
			final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
			player.teleport(zone, 100, 115, null, player);
		} else if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.VICTORY) {
			raiser.say("I don't think you claimed your #victory yet.");
		} else {
			raiser.say("What are you? A coward?");
		}
	}
}
