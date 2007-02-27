/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.common.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;


public abstract class Creature extends NPC {
	@Override
	protected void nonCreatureClientAddEventLine(String text) {
		// no logging for Creature sounds in the client window
	}

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Creature.class);

	// some debug props
	/** should the path be hidden for this creature? */
	public boolean hidePath = false;

	/** display all debug messages for this creature in the game log */
	public boolean watch = false;

//	/** creature is sleeping */
//	private boolean sleeping = false;
//
//	/** creature has been attacked */
//	private boolean attacked = false;
//
//	/** objectid of the attacking rpobject */
//	private int attackedBy = 0;
//
//	/** true when this creature canceled the attack */
//	private boolean cancelAttack = false;
//
//	/** creature has choosen a new target */
//	private boolean newTarget = false;
//
//	/** id of the new target */
//	private int newTargetId = 0;

	/** creature patrols along its path */
	private boolean patrol = false;

	/** the patrolpath */
	private List<Node> patrolPath;

//	/** target is out of reach */
//	private boolean outOfReach = false;

	/** the target moved, so we'return trying to find a new path */
	private boolean targetMoved = false;

	/** new path to the target */
	private List<Node> targetMovedPath;

//	/** we're attacking */
//	private boolean attacking = false;

	/** we're moving towards the target */
	private boolean moveToTarget = false;

//	/** we're ran against a obstacle */
//	private boolean moveToTargetBlocked = false;
//
//	/** we're waiting for the path to clear */
//	private boolean moveToTargetWaiting = false;

	/** searching new path to the target */
	private boolean moveToTargetNew = false;

	/** the path we got */
	private List<Node> moveToTargetPath;

	public Creature( RPObject object)
			throws AttributeNotFoundException {
		super( object);

		String type = getType();
		String name = object.get("name");

		// cyclic sound management
		if (type.startsWith("creature")) {
			if (name.equals("wolf")) {
				SoundSystem.startSoundCycle(this, "wolf-patrol", 40000, 10, 50,
						100);
			} else if (name.equals("rat") || name.equals("caverat")
					|| name.equals("venomrat")) {
				SoundSystem.startSoundCycle(this, "rats-patrol", 15000, 10, 30,
						80);
			} else if (name.equals("razorrat")) {
				SoundSystem.startSoundCycle(this, "razorrat-patrol", 60000, 10,
						50, 75);
			} else if (name.equals("gargoyle")) {
				SoundSystem.startSoundCycle(this, "gargoyle-patrol", 45000, 10,
						50, 100);
			} else if (name.equals("boar")) {
				SoundSystem.startSoundCycle(this, "boar-patrol", 30000, 20, 50,
						100);
			} else if (name.equals("bear")) {
				SoundSystem.startSoundCycle(this, "bear-patrol", 45000, 30, 80,
						75);
			} else if (name.equals("giantrat")) {
				SoundSystem.startSoundCycle(this, "giantrat-patrol", 30000, 30,
						60, 65);
			} else if (name.equals("cobra")) {
				SoundSystem.startSoundCycle(this, "cobra-patrol", 60000, 20,
						60, 65);
			} else if (name.equals("kobold")) {
				SoundSystem.startSoundCycle(this, "kobold-patrol", 30000, 40,
						70, 80);
			} else if (name.equals("goblin")) {
				SoundSystem.startSoundCycle(this, "goblin-patrol", 50000, 30,
						85, 65);
			} else if (name.equals("troll")) {
				SoundSystem.startSoundCycle(this, "troll-patrol", 25000, 20,
						60, 100);
			} else if (name.equals("orc")) {
				SoundSystem.startSoundCycle(this, "orc-patrol", 45000, 30, 80,
						50);
			} else if (name.equals("ogre")) {
				SoundSystem.startSoundCycle(this, "ogre-patrol", 40000, 30, 60,
						80);
			} else if (name.equals("skeleton")) {
				SoundSystem.startSoundCycle(this, "skeleton-patrol", 60000, 30,
						60, 80);
			} else if (name.equals("cyclops")) {
				SoundSystem.startSoundCycle(this, "cyclops-patrol", 45000, 30,
						75, 100);
			}
		}
	}

	protected static String translate(String type) {
		return "data/sprites/monsters/" + type + ".png";
	}

	public void drawPath(GameScreen screen, List<Node> path, int delta) {
		Graphics g2d = screen.expose();
		Point2D p1 = screen.invtranslate(new Point.Double(getX(), getY()));

		for (Node node : path) {
			Point2D p2 = screen.invtranslate(new Point.Double(node.x, node.y));

			g2d.drawLine((int) p1.getX() + delta, (int) p1.getY() + delta,
					(int) p2.getX() + delta, (int) p2.getY() + delta);
			p1 = p2;
		}
	}

	@Override
	public void draw(GameScreen screen) {
		super.draw(screen);

		if (Debug.CREATURES_DEBUG_CLIENT && !hidePath) {
			Graphics g2d = screen.expose();

			if (targetMoved && (targetMovedPath != null)) {
				int delta = GameScreen.SIZE_UNIT_PIXELS / 2;
				g2d.setColor(Color.red);
				drawPath(screen, targetMovedPath,
						GameScreen.SIZE_UNIT_PIXELS / 2);
			}

			if (patrol && (patrolPath != null)) {
				g2d.setColor(Color.green);
				drawPath(screen, patrolPath,
						GameScreen.SIZE_UNIT_PIXELS / 2 + 1);
			}

			if ((moveToTarget || moveToTargetNew) && (moveToTargetPath != null)) {
				g2d.setColor(Color.blue);
				drawPath(screen, moveToTargetPath,
						GameScreen.SIZE_UNIT_PIXELS / 2 + 2);
			}
		}
	}

	public List<Node> getPath(String token) {
		String[] values = token.replace(',', ' ').replace('(', ' ').replace(
				')', ' ').replace('[', ' ').replace(']', ' ').split(" ");
		List<Node> list = new ArrayList<Node>();

		int x = 0;
		int pass = 1;

		for (String value : values) {
			if (value.trim().length() > 0) {
				int val = Integer.parseInt(value.trim());
				if (pass % 2 == 0) {
					list.add(new Node(x, val));
				} else {
					x = val;
				}
				pass++;
			}
		}

		return list;
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		// Check if debug is enabled
		if (diff.has("debug") && Debug.CREATURES_DEBUG_CLIENT) {
//			sleeping = false;
//			attacked = false;
//			cancelAttack = false;
//			newTarget = false;
			patrol = false;
//			outOfReach = false;
			targetMoved = false;
//			attacking = false;
			moveToTarget = false;
//			moveToTargetBlocked = false;
//			moveToTargetWaiting = false;
			moveToTargetNew = false;

			String debug = diff.get("debug");

			if (watch) {
				StendhalClient.get().addEventLine(getID() + " - " + debug);
			}

			String[] actions = debug.split("\\|");
			// parse all actions
			for (String action : actions) {
				if (action.length() > 0) {
					StringTokenizer tokenizer = new StringTokenizer(action, ";");

					try {
						String token = tokenizer.nextToken();
						System.out.println("- creature action: " + token);
						if (token.equals("sleep")) {
//							sleeping = true;
							break;
//						} else if (token.equals("attacked")) {
//							attacked = true;
//							attackedBy = Integer
//									.parseInt(tokenizer.nextToken());
//						} else if (token.equals("cancelattack")) {
//							cancelAttack = true;
//						} else if (token.equals("newtarget")) {
//							newTarget = true;
//							newTargetId = Integer.parseInt(tokenizer
//									.nextToken());
						} else if (token.equals("patrol")) {
							patrol = true;
							patrolPath = getPath(tokenizer.nextToken());
//						} else if (token.equals("outofreachstopped")) {
//							outOfReach = true;
						} else if (token.equals("targetmoved")) {
							targetMoved = true;
							targetMovedPath = getPath(tokenizer.nextToken());
						} else if (token.equals("attacking")) {
//							attacking = true;
						} else if (token.equals("movetotarget")) {
							moveToTarget = true;
//							moveToTargetBlocked = false;
//							moveToTargetWaiting = false;
							moveToTargetNew = false;
							String nextToken = tokenizer.nextToken();

							if (nextToken.equals("blocked")) {
//								moveToTargetBlocked = true;
								nextToken = tokenizer.nextToken();
							}

							if (nextToken.equals("waiting")) {
//								moveToTargetWaiting = true;
								nextToken = tokenizer.nextToken();
							}

							if (nextToken.equals("newpath")) {
								moveToTargetNew = true;
								nextToken = tokenizer.nextToken();
								if (nextToken.equals("blocked")) {
									moveToTargetPath = null;
								} else {
									moveToTargetPath = getPath(nextToken);
								}
							}
						}
					} catch (Exception e) {
						logger.warn("error parsing debug string '" + debug
								+ "' actions [" + Arrays.asList(actions)
								+ "] action '" + action + "'", e);
					}
				}
			}
		}
	}

	@Override
	public String defaultAction() {
		return "Attack";
	}

	@Override
	public String[] offeredActions() {
		String[] superList = super.offeredActions();

		if (!Debug.CREATURES_DEBUG_CLIENT) {
			return superList;
		}

		String[] list = new String[superList.length + 2];

		System.arraycopy(superList, 0, list, 0, superList.length);
		list[superList.length + 0] = "[" + (hidePath ? "show" : "hide")
				+ " path]";
		list[superList.length + 1] = "[" + (watch ? "disable" : "enable")
				+ " watch]";

		return list;
	}

	@Override
	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("[show path]")) {
			hidePath = false;
		} else if (action.equals("[hide path]")) {
			hidePath = true;
		} else if (action.equals("[enable watch]")) {
			watch = true;
		} else if (action.equals("[disable watch]")) {
			watch = false;
		} else {
			super.onAction(client, action, params);
		}
	}

	private class Node {
		int x, y;

		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	protected Sprite loadAnimationSprite(RPObject object) {
		String name = null;

		if (object.has("subclass")) {
			name = object.get("class") + "/" + object.get("subclass");
		} else {
			name = object.get("class");
		}

		SpriteStore store = SpriteStore.get();
		sprite = store.getSprite("data/sprites/monsters/" + name + ".png");
		return sprite;
	}

}
