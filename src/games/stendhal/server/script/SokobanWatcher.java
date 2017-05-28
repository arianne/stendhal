package games.stendhal.server.script;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.mapstuff.area.Wall;
import games.stendhal.server.entity.player.Player;

public class SokobanWatcher extends ScriptImpl implements TurnListener {
	private static Logger logger = Logger.getLogger(SokobanWatcher.class);

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		cleanup();
		TurnNotifier.get().notifyInSeconds(2, this);

		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		Wall wall = new Wall(20, 1);
		wall.setPosition(26, 107);
		wall.setEntityClass("block/mine_cart_empty");
		wall.setDescription("You see a wall.");
		zone.add(wall);
	}

	@SuppressWarnings("unchecked")
	private void cleanup() {

			// all events that are equal to this one should be forgotten.
			// TurnEvent turnEvent = new TurnEvent(turnListener);
	try {
		Field field = TurnNotifier.class.getDeclaredField("register");
		field.setAccessible(true);
		final Map<Integer, Set<TurnListener>> register = (Map<Integer, Set<TurnListener>>) field.get(TurnNotifier.get());
			for (final Map.Entry<Integer, Set<TurnListener>> mapEntry : register.entrySet()) {
				final Set<TurnListener> set = mapEntry.getValue();
				// We don't remove directly, but first store in this
				// set. This is to avoid ConcurrentModificationExceptions.
				final Set<TurnListener> toBeRemoved = new HashSet<TurnListener>();
				for (TurnListener t : set) {
					if (t.getClass().getName().indexOf("SokobanWatcher") > -1) {
						toBeRemoved.add(t);
					}
				}
				for (final TurnListener event : toBeRemoved) {
					set.remove(event);
				}
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	@Override
	public void unload(Player admin, List<String> args) {
		TurnNotifier.get().dontNotify(this);
	}

	@Override
	public void onTurnReached(int currentTurn) {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		StendhalRPZone target = SingletonRepository.getRPWorld().getZone("int_semos_townhall");

		Player first = null;
		List<Player> list = new LinkedList<Player>(zone.getPlayers());
		for (Player player : list) {
			int x = player.getX();
			int y = player.getY();
			if (x > 26 && x < 26 + 20 && y > 107 && y < 107 + 16) {
				if (first != null) {
					logger.error("Two players in Sokoban game: " + first.getName() + ", " + player.getName());
					player.teleport(target, 16, 24, Direction.DOWN, player);
					first.teleport(target, 15, 24, Direction.DOWN, player);
				} else {
					first = player;
				}
			}
		}
		TurnNotifier.get().notifyInSeconds(2, this);
	}
}
