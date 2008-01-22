package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * handle the players request to leave the deathmatch
 * (if it is allowed in the current state).
 *
 * @author hendrik
 */
public class LeaveAction extends SpeakerNPC.ChatAction {

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		DeathmatchState deathmatchState = DeathmatchState.createFromQuestString(player.getQuest("deathmatch"));

		if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.DONE) {
			StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_plains_n");
			player.teleport(zone, 100, 115, null, player);
		} else if (deathmatchState.getLifecycleState() == DeathmatchLifecycle.VICTORY) {
			engine.say("I don't think you claimed your #victory yet.");
		} else {
			engine.say("What are you? A coward?");
		}
	}
}
