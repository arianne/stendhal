package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.scroll.BalloonScroll;
import games.stendhal.server.entity.player.Player;


/**
 * QUEST: Balloon
 *
 * NOTES:
 * <ul>
 * <li>We need to ensure that players can't login in the clouds.</li>
 * </ul>
 */
public class Balloon extends AbstractQuest {


	private static BalloonScroll scroll;

	@Override
	public void init(String name) {
		super.init(name, "balloon");
		if (scroll == null) {
			scroll = (BalloonScroll) SingletonRepository.getEntityManager().getItem("balloon");
		}

		/* login notifier to teleport away players logging into the clouds.
		 * TODO: this should be done in the TimedTeleportScroll class or it's subclass.
		 */
		SingletonRepository.getLoginNotifier().addListener(new LoginListener() {
			public void onLoggedIn(Player player) {
				scroll.teleportBack(player);
			}

		});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

	}
}
