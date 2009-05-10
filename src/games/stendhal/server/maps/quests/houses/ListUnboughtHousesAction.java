/**
 * 
 */
package games.stendhal.server.maps.quests.houses;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.List;

final class ListUnboughtHousesAction implements ChatAction {
	private final String location;

	/**
	 * Creates a new ListUnboughtHousesAction.
	 * 
	 * @param location
	 *            where are the houses?
	 */
	ListUnboughtHousesAction(final String location) {
		this.location = location;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		final List<String> unbought = HouseUtilities.getUnboughtHousesInLocation(location);
		if (unbought.size() > 0) {
			engine.say("According to my records, " + Grammar.enumerateCollection(unbought) + " are all available for #purchase.");
		} else {
			engine.say("Sorry, there are no houses available for sale in " + Grammar.makeUpperCaseWord(location) + ".");
		}
	}
}
