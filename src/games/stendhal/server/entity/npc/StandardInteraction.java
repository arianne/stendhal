/* $Id$
 * 
 */
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.Player;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptCondition;
import games.stendhal.server.scripting.StendhalGroovyScript;

/**
 * This is a collection of standard actions and conditions.
 * Although most of them are very simply in normal Java-code,
 * they are annoying in Groovy because anon classes
 * are not supported.
 * 
 * @author hendrik
 */
public class StandardInteraction {

	/**
	 * Is the player an admin?
	 */
	public class AdminCondition extends SpeakerNPC.ChatCondition {
		int requiredAdminlevel;

		public AdminCondition() {
			requiredAdminlevel = 5000;
		}

		public AdminCondition(int requiredAdminlevel) {
			this.requiredAdminlevel = requiredAdminlevel;
		}

		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.has("adminlevel") && (player.getInt("adminlevel") >= 5000));
		}
	}

	
	public class ReqisterScriptAction extends SpeakerNPC.ChatAction {
	    StendhalGroovyScript game = null;
	    ScriptCondition scriptCondition = null;
	    ScriptAction scriptAction = null;

	    public ReqisterScriptAction (StendhalGroovyScript game, ScriptAction scriptAction) {
	      this.game = game;
	      this.scriptAction = scriptAction;
	    }

	    public ReqisterScriptAction (StendhalGroovyScript game, ScriptCondition scriptCondition, ScriptAction scriptAction) {
	      this.game = game;
	      this.scriptAction = scriptAction;
	      this.scriptCondition = scriptCondition;
	    }

	    public void fire(Player player, String text, SpeakerNPC engine) {
	    	// TODO player, text and engine should be forwarded to the script 
	        game.add(scriptCondition, scriptAction);
	    }
	}
}
