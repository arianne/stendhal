/* $Id$
 * 
 */
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.Player;
import games.stendhal.server.util.Area;

/**
 * This is a collection of standard actions and conditions. Although most of
 * them are very simply in normal Java-code, they are annoying in Groovy because
 * anon classes are not supported.
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
		 * to provide additonal data.
		 *
		 * @param player the player talking to the NPC
		 * @param text   the text he said
		 * @param engine the NPC
		 */
		public void setChatInfo(Player player, String text, SpeakerNPC engine);
	}

	/**
	 * Is the player an admin?
	 */
	public static class AdminCondition extends SpeakerNPC.ChatCondition {
		private int requiredAdminlevel;

		public AdminCondition() {
			requiredAdminlevel = 5000;
		}

		public AdminCondition(int requiredAdminlevel) {
			this.requiredAdminlevel = requiredAdminlevel;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.has("adminlevel") && (player.getInt("adminlevel") >= requiredAdminlevel));
		}
	}

	/**
	 * Is the player in the specified area?
	 */
	public static class PlayerInAreaCondition extends SpeakerNPC.ChatCondition {
		private Area area = null;

		public PlayerInAreaCondition(Area area) {
			this.area  = area;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return area.contains(player);
		}

	}


	// ------------------------------------------------------------------------
	//                           quest related stuff
	// ------------------------------------------------------------------------

	/**
	 * Was this quest started?
	 */
	public static class QuestStartedCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;

		public QuestStartedCondition(String questname) {
			this.questname = questname;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.has(questname));
		}
	}

	/**
	 * Was this quest not started yet?
	 */
	public static class QuestNotStartedCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;

		public QuestNotStartedCondition(String questname) {
			this.questname = questname;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return (!player.hasQuest(questname));
		}
	}

	/**
	 * Was this quest completed?
	 */
	public static class QuestCompletedCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;

		public QuestCompletedCondition(String questname) {
			this.questname = questname;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return (player.isQuestCompleted(questname));
		}
	}

	/**
	 * Is this quest not completed?
	 */
	public static class QuestNotCompletedCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;

		public QuestNotCompletedCondition(String questname) {
			this.questname = questname;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return (!player.isQuestCompleted(questname));
		}
	}

	/**
	 * Is this quest in this state?
	 */
	public static class QuestInStateCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;
		private String state = null;

		public QuestInStateCondition(String questname, String state) {
			this.questname = questname;
			this.state = state;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
		    return (player.hasQuest(questname) && player.getQuest(questname).equals(state));
		}
	}
	
	/**
	 * Is this quest not in this state?
	 */
	public static class QuestNotInStateCondition extends SpeakerNPC.ChatCondition {
		private String questname = null;
		private String state = null;

		public QuestNotInStateCondition(String questname, String state) {
			this.questname = questname;
			this.state = state;
		}

		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
		    return (!player.hasQuest(questname) || !player.getQuest(questname).equals(state));
		}
	}

	/**
	 * Sets the current state of this quest
	 */
	public static class SetQuestAction extends SpeakerNPC.ChatAction {
		private String questname = null;
		private String state = null;

		public SetQuestAction(String questname, String state) {
			this.questname = questname;
			this.state = state;
		}

		@Override
		public void fire(Player player, String text, SpeakerNPC engine) {
			player.setQuest(questname, state);
		}
	}
}
