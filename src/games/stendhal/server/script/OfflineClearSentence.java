/**
 *
 */
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

/**
 * Clear the sentence attribute of an offline player
 *
 * @author madmetzger
 */
public class OfflineClearSentence extends AbstractOfflineAction {

	@Override
	public boolean validateParameters(Player admin, List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("/script OfflineClearSentence.class <playername>");
			return false;
		}
		return true;
	}

	@Override
	public void process(Player admin, RPObject object, List<String> args) {
		object.remove("sentence");
	}

}
