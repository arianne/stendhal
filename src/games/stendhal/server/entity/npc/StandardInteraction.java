/* $Id$
 *
 */
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

/**
 * This is a collection of standard actions and conditions. Although most of
 * them are very simply in normal Java-code, they are annoying in Groovy because
 * anon classes are not supported.
 * 
 * Note: This class will be deprecated after release of 0.64. Please
 * use the sub packages (action and condition) *after* the release.
 *
 * @author hendrik
 */
public class StandardInteraction {

	/**
	 * ScriptActions which are registered with ReqisterScriptAction can
	 * implement this interface to get additional data.
	 */
	public interface ChatInfoReceiver {

		/**
		 * before the ScriptAction is registered this method is called
		 * to provide additional data.
		 *
		 * @param player the player talking to the NPC
		 * @param text   the text he said
		 * @param engine the NPC
		 */
		void setChatInfo(Player player, String text, SpeakerNPC engine);
	}


}
