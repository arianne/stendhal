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
package games.stendhal.server.maps.quests;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.item.scroll.BalloonScroll;
import games.stendhal.server.entity.player.Player;


/**
 * QUEST: Balloon
 *
 * NOTES:
 * <ul>
 * <li>We need to ensure that players can't login in the clouds.</li>
 * </ul>
 */
public class Balloon extends AbstractQuest {

	private static final String BALLOON = "balloon";

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Balloon",
				"Balloons can float away ...",
				true);
		/* login notifier to teleport away players logging into the clouds.
		 * there is a note in TimedTeleportScroll that it should be done there or its subclass.
		 */
		SingletonRepository.getLoginNotifier().addListener(new LoginListener() {
			public void onLoggedIn(final Player player) {
				BalloonScroll scroll = (BalloonScroll) SingletonRepository.getEntityManager().getItem(BALLOON);
				scroll.teleportBack(player);
			}

		});

	}

	@Override
	public String getSlotName() {
		return BALLOON;
	}

	@Override
	public String getName() {
		
		return "Balloon";
	}

	@Override
	public int getMinLevel() {
		return 150;
	}
}
