package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
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


	private static final String BALLOON = "balloon";
	private static BalloonScroll scroll;

	@Override
	public void addToWorld() {
		super.addToWorld();
		if (scroll == null) {
			scroll = (BalloonScroll) SingletonRepository.getEntityManager().getItem(BALLOON);
		}

		/* login notifier to teleport away players logging into the clouds.
		 * there is a note in TimedTeleportScroll that it should be done there or its subclass.
		 */
		SingletonRepository.getLoginNotifier().addListener(new LoginListener() {
			public void onLoggedIn(final Player player) {
				scroll.teleportBack(player);
			}

		});

	}

	@Override
	public String getSlotName() {
		return BALLOON;
	}

	@Override
	public String getName() {
		
		return "Balloon";
	}
}
