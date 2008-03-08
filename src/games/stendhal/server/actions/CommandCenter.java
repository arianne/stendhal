package games.stendhal.server.actions;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.actions.attack.AttackAction;
import games.stendhal.server.actions.attack.StopAction;
import games.stendhal.server.actions.buddy.BuddyAction;
import games.stendhal.server.actions.chat.AwayAction;
import games.stendhal.server.actions.chat.ChatAction;
import games.stendhal.server.actions.equip.DisplaceAction;
import games.stendhal.server.actions.equip.EquipmentAction;
import games.stendhal.server.actions.equip.WrapAction;
import games.stendhal.server.actions.guild.CreateGuildAction;
import games.stendhal.server.actions.move.FaceAction;
import games.stendhal.server.actions.move.MoveAction;
import games.stendhal.server.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class CommandCenter {
	private static final UnknownAction UNKNOWN_ACTION = new UnknownAction();
	private static ConcurrentHashMap<String, ActionListener> actionsMap;
	private static Logger logger = Logger.getLogger(CommandCenter.class);

	private static ConcurrentHashMap<String, ActionListener> getActionsMap() {
		if (actionsMap == null) {
			actionsMap = new ConcurrentHashMap<String, ActionListener>();
			registerActions();

		}
		return actionsMap;
	}

	public static void register(String action, ActionListener actionClass) {
		ActionListener command = getActionsMap().putIfAbsent(action, actionClass);

		//TODO mf - register slash commands as verbs in WordList
		if (command == null) {
//			WordList.getInstance().registerVerb(action);
		} else {
			logger.error("not registering " + command.getClass()
					+ ". it has the same handler: " + action + " as  "
					+ CommandCenter.getAction(action).getClass());
		}
	}

	public static void register(String action, ActionListener actionClass,
			int requiredAdminLevel) {
		register(action, actionClass);
		AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
	}

	private static void registerActions() {
		AdministrationAction.register();
		AttackAction.register();
		AwayAction.register();
		BuddyAction.register();
		ChatAction.register();
		DisplaceAction.register();
		EquipmentAction.register();
		FaceAction.register();
		LookAction.register();
		MoveAction.register();
		OutfitAction.register();
		OwnAction.register();
		PlayersQuery.register();
		QuestListAction.register();
		StopAction.register();
		UseAction.register();
		CreateGuildAction.register();
		WrapAction.register();
	}

	public static boolean execute(RPObject caster, RPAction action) {
		try {

			Player player = (Player) caster;
			ActionListener actionListener = getAction(action);

			actionListener.onAction(player, action);

			return true;
		} catch (Exception e) {
			logger.error("Cannot execute action " + action + " send by "
					+ caster, e);
			return false;
		}
	}

	private static ActionListener getAction(RPAction action) {
		if (action == null) {
			return UNKNOWN_ACTION;
		} else {
			return getAction(action.get("type"));
		}
	}

	private static ActionListener getAction(String type) {
		if (type == null) {
			return UNKNOWN_ACTION;
		}

		ActionListener action = getActionsMap().get(type);
		if (action == null) {
			return UNKNOWN_ACTION;
		} else {
			return action;
		}
	}

	static class UnknownAction implements ActionListener {
		private static Logger logger = Logger.getLogger(UnknownAction.class);

		public void onAction(Player player, RPAction action) {
			String type = "null";
			if (action != null) {
				type = action.get("type");
			}
			logger.warn(player + " tried to execute unknown action " + type);
			if (player != null) {
				player.sendPrivateText(NotificationType.ERROR,
						"Unknown command " + type);
			}
		}
	}

}
