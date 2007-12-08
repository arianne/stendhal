package games.stendhal.server.actions;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.actions.buddy.BuddyAction;
import games.stendhal.server.actions.equip.EquipmentAction;
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
		ActionListener command = getActionsMap().putIfAbsent(action,
				actionClass);
		if (command != null) {
			logger.error("not registering " + command.getClass()
					+ ". it has the same handler: " + action + " as  "
					+ CommandCenter.getAction(action).getClass());
		}

	}
	public static void register(String action, ActionListener actionClass, int requiredAdminLevel) {
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

	}

	public static void execute(RPObject caster, RPAction action) {

		try {

			Player player = (Player) caster;
			ActionListener actionListener = getAction(action);
			
			actionListener.onAction(player, action);

		} catch (Exception e) {
			logger.error("Cannot execute action " + action + " send by " + caster, e);
		}
	}

	private static ActionListener getAction(RPAction action) {
		if (action == null){
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

		public void onAction(Player player, RPAction action) {
			String type = "null";
			if (action != null) {
				type = action.get("type");
			}
			logger.warn(player + " tried to execute unknown action " + type);
			if (player != null) {
				player.sendPrivateText(NotificationType.ERROR, "Unknown command " + type);
			}
		}

	}

}
