package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LoginNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * fixed negative hp
 *
 * @author hendrik
 */
public class FixNegativeHp extends ScriptImpl implements LoginListener {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		
		LoginNotifier.get().addListener(this);
	}

	public void onLoggedIn(Player player) {
		if (player.getHP() <= 0) {
			SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper", "set hp of player " + player.getName() + " from " + player.getHP() + " to 1.");
			player.setHP(1);
		}
	}

	
}
