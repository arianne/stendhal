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
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.CREATURE;
import static games.stendhal.common.constants.Actions.SUMMON;
import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.config.annotations.ServerModeUtil;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.mapstuff.block.Block;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class SummonAction extends AdministrationAction {
	private static final String USAGE = "Usage: /summon <whatToSummon> [<x> <y>]";


	public static void register() {
		CommandCenter.register(SUMMON, new SummonAction(), 800);
	}

	/**
	 * Inline class to create entities of all creature types including pets.
	 */
	abstract static class EntityFactory {
		final Player player;
		final EntityManager manager = SingletonRepository.getEntityManager();

		protected boolean searching = true;

		public EntityFactory(final Player player) {
			this.player = player;
		}

		boolean isSearching() {
			return searching;
		}

		abstract void found(String type, Entity entity);
		abstract void error(String message);

		/**
		 * Create the named entity (creature, pet or sheep) of type 'type'.
		 * 
		 * @param type
		 */
		private void createEntity(final String type) {
			final Entity entity = manager.getEntity(type);

			if (entity != null) {
				found(type, entity);
			} else if ("cat".equals(type)) {
				if (player.hasPet()) {
					error("You already own a pet!");
				} else {
					final Cat cat = new Cat(player);
					found(type, cat);
				}
			} else if ("baby dragon".equals(type)) {
				if (player.hasPet()) {
					error("You already own a pet!");
				} else {
					final BabyDragon dragon = new BabyDragon(player);
					found(type, dragon);
				}
			} else if ("sheep".equals(type)) {
				if (player.hasSheep()) {
					error("You already own a sheep!");
				} else {
					final Sheep sheep = new Sheep(player);
					found(type, sheep);
				}
			}
		}
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if ("gate".equals(action.get(CREATURE))) {
			final Gate gate = new Gate();
			gate.setPosition(action.getInt(X), action.getInt(Y));
			player.getZone().add(gate);
			return;
		}
		
        if ("block".equals(action.get(CREATURE))) {
		    final Block block = new Block(action.getInt(X), action.getInt(Y), true);
		    player.getZone().add(block);
		    player.getZone().addMovementListener(block);
		    player.getZone().addZoneEnterExitListener(block);
		}

		try {
			if (action.has(CREATURE) && action.has(X) && action.has(Y)) {
				final StendhalRPZone zone = player.getZone();
				final int x = action.getInt(X);
				final int y = action.getInt(Y);

				if (!zone.collides(player, x, y)) {
					final EntityFactory factory = new EntityFactory(player) {
						@Override
						void found(final String type, final Entity entity) {
							final Entity entityToBePlaced;
							if (manager.isCreature(type)) {
								entityToBePlaced = new RaidCreature((Creature) entity);
								if (((Creature) entity).isRare() && !ServerModeUtil.isTestServer()) {
									// Rare creatures should not be summoned even in raids
									// Require parameter -Dstendhal.testserver=junk
									error("Rare creatures may not be summoned.");
									return;
								}
							} else {
								entityToBePlaced = entity;
							}
							StendhalRPAction.placeat(zone, entityToBePlaced, x, y);
							new GameEvent(player.getName(), SUMMON, type).raise();
							// We found what we are searching for.
							searching = false;
						}

						@Override
						void error(final String message) {
							player.sendPrivateText(message);

							// Stop searching because of an error.
							searching = false;
						}
					};

					final String typeName = action.get(CREATURE);
					String type = typeName;

					factory.createEntity(type);

					if (factory.isSearching()) {
						// see it the name was in plural
						type = Grammar.singular(typeName);
						factory.createEntity(type);

						if (factory.isSearching()) {
							// see it the name was in singular but the registered type is in plural
							type = Grammar.plural(typeName);
							factory.createEntity(type);

							// Did we still not find any matching class?
							if (factory.isSearching()) {
								logger.info("onSummon: Entity \"" + typeName + "\" not found.");
								factory.error("onSummon: Entity \"" + typeName + "\" not found.");
							}
						}
					}
				}
			}
		} catch (final NumberFormatException e) {
			player.sendPrivateText(USAGE);
		}
	}

}
