/***************************************************************************
 *                   (C) Copyright 2003-2016 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

 import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.ViewChangeEvent;
import marauroa.common.game.RPAction;

 /**
  * Sends an remote view event
  *
  * @author hendrik
  */
 public class RemoteViewAction extends AdministrationAction {

	 public static void register() {
		 CommandCenter.register("remoteview", new RemoteViewAction(), 500);

	 }

	 /**
	  * performs the remote view action
	  *
	  * @param player the player invoking this action
	  * @param action RPAction
	  */
	 @Override
	 public void perform(Player player, RPAction action) {
		 String target = action.get("target");
		 String args = action.get("args");

		 if (args.trim().equals("")) {
			 player.sendPrivateText(NotificationType.ERROR, "Usage: /remoteview [player] x y");
		 }

		 Player targetPlayer = player;
		 String x = target;
		 String y = args;
		 if (args.trim().indexOf(" ") > -1) {
			 targetPlayer = SingletonRepository.getRuleProcessor().getPlayer(target);
			 String[] temp = args.split(" ");
			 x = temp[0];
			 y = temp[1];
		 }

		 if (targetPlayer == null) {
			 player.sendPrivateText(NotificationType.ERROR, "Player not online");
			 return;
		 }

		 targetPlayer.addEvent(new ViewChangeEvent(Integer.parseInt(x), Integer.parseInt(y)));
		 targetPlayer.notifyWorldAboutChanges();
	 }

 }
