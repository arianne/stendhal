/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import games.stendhal.common.constants.Actions;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;

/**
 * translates the visual representation into server side commands.
 *
 * @author astridemma
 */
public enum ActionType {
	LOOK("look", "察看") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	READ("look", "读"),
	LOOK_CLOSELY("use", "细看"),
	INSPECT("inspect", "检查") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	ATTACK("attack", "攻击"),
	STOP_ATTACK("stop", "停止攻击"),
	PUSH("push", "推开"),
	CLOSE("use", "关闭"),
	OPEN("use", "打开") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	OWN("own", "拥有"),
	USE("use", "使用") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	HARVEST("use", "收获"),
	PICK("use", "捡起"),
	PROSPECT("use", "Prospect"),
	FISH("use", "钓鱼"),
	WISH("use", "许愿"),
	LEAVE_SHEEP("forsake", "Leave sheep") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("species", "sheep");
			return rpaction;
		}
	},
	LEAVE_PET("forsake", "Leave pet") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("species", "pet");
			return rpaction;
		}
	},
	ADD_BUDDY("addbuddy", "添加好友") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("target", entity.getName());
			return rpaction;
		}
	},
	IGNORE("ignore", "屏闭") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("target", entity.getName());
			return rpaction;
		}
	},
	UNIGNORE("unignore", "取消屏闭") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("target", entity.getName());
			return rpaction;
		}
	},
	TRADE("trade", "交易") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("action", "offer_trade");
			return rpaction;
		}
	},
	ADMIN_INSPECT("inspect", "(*)Inspect") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	ADMIN_DESTROY("destroy", "(*)Destroy") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	ADMIN_ALTER("alter", "(*)Alter") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			return fillTargetPath(super.fillTargetInfo(entity), entity);
		}
	},
	SET_OUTFIT("outfit", "配备"),
	WHERE("where", "Where") {
		@Override
		public RPAction fillTargetInfo(final IEntity entity) {
			RPAction rpaction = super.fillTargetInfo(entity);
			rpaction.put("target", entity.getName());
			return rpaction;
		}
	},
	ADMIN_VIEW_NPC_TRANSITIONS("npctransitions", "(*)View Transitions"),
	KNOCK("knock", "敲打"),
	INVITE("group_management", "邀请") {

		@Override
		public RPAction fillTargetInfo(IEntity entity) {
			// invite action needs to add additional parameters to the RPAction
			RPAction a = super.fillTargetInfo(entity);
			a.put("action", "invite");
			a.put("params", entity.getName());
			return a;
		}

	},
	WALK_START("walk", "行走"),
	WALK_STOP("walk", "停步"),
	CHALLENGE("challenge", "挑战") {

		@Override
		public RPAction fillTargetInfo(IEntity entity) {
			RPAction a = super.fillTargetInfo(entity);
			a.put("type", "challenge");
			a.put("action", "open");
			a.put("target", entity.getName());
			return a;
		}

	},
	ACCEPT_CHALLENGE("challenge", "接受") {

		@Override
		public RPAction fillTargetInfo(IEntity entity) {
			RPAction a = super.fillTargetInfo(entity);
			a.put("type", "challenge");
			a.put("action", "accept");
			a.put("target", entity.getName());
			return a;
		}

	};

	/**
	 * the String send to the server, if so.
	 */
	private final String actionCode;

	/**
	 * the String which is shown to the user.
	 */
	private final String actionRepresentation;

	/**
	 * Constructor.
	 *
	 * @param actCode
	 *            the code to be sent to the server
	 * @param actionRep
	 *            the String to be shown to the user
	 */
	private ActionType(final String actCode, final String actionRep) {
		actionCode = actCode;
		actionRepresentation = actionRep;
	}

	/**
	 * finds the ActionType that belongs to a visual String representation.
	 *
	 * @param representation
	 *            the menu String
	 * @return the Action Element or null if not found
	 */
	public static ActionType getbyRep(final String representation) {
		for (final ActionType at : ActionType.values()) {
			if (at.actionRepresentation.equals(representation)) {
				return at;
			}

		}
		return null;
	}

	/**
	 * @return the command code for usage on server side
	 */
	@Override
	public String toString() {
		return actionCode;
	}

	/**
	 * @return the String the user should see on the menu
	 */
	public String getRepresentation() {
		return actionRepresentation;
	}

	/**
	 * sends the requested action to the server.
	 *
	 * @param rpaction
	 *            action to be sent
	 */
	public void send(final RPAction rpaction) {
		StendhalClient.get().send(rpaction);
	}

	/**
	 * Create an RPAction with target information pointing to an entity.
	 *
	 * @param entity target entity
	 * @return action with entity as the target
	 */
	public RPAction fillTargetInfo(final IEntity entity) {
		RPAction rpaction = new RPAction();

		RPClass rpClass = RPClass.getRPClass(actionCode);
		boolean includeZone = true;
		if (rpClass != null) {
			rpaction.setRPClass(actionCode);
			if (rpClass.getDefinition(DefinitionClass.ATTRIBUTE, "zone") == null) {
				includeZone = false;
			}
		} else {
			rpaction.put("type", toString());
		}

		RPObject rpObject = entity.getRPObject();
		final int id = rpObject.getID().getObjectID();
		// Compatibility: Don't include zone if the action does not support it
		if (includeZone) {
			rpaction.put("zone", entity.getRPObject().getBaseContainer().get("zoneid"));
		}

		if (rpObject.isContained()) {
			/*
			 * Compatibility for old servers. Cannot handle nested objects.
			 * The actions that need to cope with contained objects should call
			 * fillTargetPath().
			 */
			rpaction.put("baseobject",
					rpObject.getContainer().getID().getObjectID());
			rpaction.put("baseslot", rpObject.getContainerSlot().getName());
			rpaction.put("baseitem", id);
		} else {
			StringBuilder target;
			target = new StringBuilder("#");
			target.append(Integer.toString(id));
			rpaction.put("target", target.toString());
		}

		return rpaction;
	}

	/**
	 * Add target information for a contained target object.
	 *
	 * @param action
	 * @param entity target entity
	 * @return the action
	 */
	RPAction fillTargetPath(RPAction action, IEntity entity) {
		action.put(Actions.TARGET_PATH, entity.getPath());
		return action;
	}

	/**
	 * gets the action code
	 *
	 * @return actioncode
	 */
	public String getActionCode() {
		return actionCode;
	}
}
