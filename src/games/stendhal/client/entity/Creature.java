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

import games.stendhal.client.StendhalUI;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Debug;
import games.stendhal.common.Rand;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import marauroa.common.Logger;

public class Creature extends RPEntity {

	@Override
	protected void nonCreatureClientAddEventLine(final String text) {
		// no logging for Creature "sounds" in the client window
	}

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Creature.class);

	/**
	 * The current metamorphosis.
	 */
	private String	metamorphosis;

	/**
	 * The entity width.
	 */
	private double width;

	/**
	 * The entity height.
	 */
	private double height;


	// some debug props
	/** should the path be hidden for this creature? */
	public boolean hidePath = false;

	/** display all debug messages for this creature in the game log */
	public boolean watch = false;

	/** the patrolpath */
	private List<Node> patrolPath;

	/** new path to the target */
	private List<Node> targetMovedPath;

	/** the path we got */
	private List<Node> moveToTargetPath;


	protected static String translate(final String type) {
		return "data/sprites/monsters/" + type + ".png";
	}

	public List<Node> getPatrolPath() {
		return patrolPath;
	}

	public List<Node> getTargetMovedPath() {
		return targetMovedPath;
	}

	public List<Node> getMoveToTargetPath() {
		return moveToTargetPath;
	}

	public boolean isPathHidden() {
		return hidePath;
	}


	public List<Node> getPath(final String token) {
		String[] values = token.replace(',', ' ').replace('(', ' ').replace(')', ' ').replace('[', ' ').replace(']',
		        ' ').split(" ");
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


	protected void onDebug(String debug) {
		if (!Debug.CREATURES_DEBUG_CLIENT) {
			return;
		}

		patrolPath = null;
		targetMovedPath = null;
		moveToTargetPath = null;

		if (watch) {
			StendhalUI.get().addEventLine(getID() + " - " + debug);
		}

		String[] actions = debug.split("\\|");
		// parse all actions
		for (String action : actions) {
			if (action.length() > 0) {
				StringTokenizer tokenizer = new StringTokenizer(action, ";");

				try {
					String token = tokenizer.nextToken();
					logger.debug("- creature action: " + token);
					if (token.equals("sleep")) {
						break;
					} else if (token.equals("patrol")) {
						patrolPath = getPath(tokenizer.nextToken());
					} else if (token.equals("targetmoved")) {
						targetMovedPath = getPath(tokenizer.nextToken());
					} else if (token.equals("movetotarget")) {
						moveToTargetPath = null;
						String nextToken = tokenizer.nextToken();

						if (nextToken.equals("blocked")) {
							nextToken = tokenizer.nextToken();
						}

						if (nextToken.equals("waiting")) {
							nextToken = tokenizer.nextToken();
						}

						if (nextToken.equals("newpath")) {
							moveToTargetPath = null;
							nextToken = tokenizer.nextToken();
							if (nextToken.equals("blocked")) {
								moveToTargetPath = null;
							} else {
								moveToTargetPath = getPath(nextToken);
							}
						}
					}
				} catch (Exception e) {
					logger.warn("error parsing debug string '" + debug + "' actions [" + Arrays.asList(actions)
					        + "] action '" + action + "'", e);
				}
			}
		}
	}


	@Override
	public ActionType defaultAction() {
		return ActionType.ATTACK;
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at = handleAction(action);
		switch (at) {
			case DEBUG_SHOW_PATH:
				hidePath = false;
				break;
			case DEBUG_HIDE_PATH:
				hidePath = true;
				break;
			case DEBUG_ENABLE_WATCH:
				watch = true;
				break;
			case DEBUG_DISABLE_WATCH:
				watch = false;
				break;
			default:
				super.onAction(at, params);
				break;
		}
	}

	public class Node {
		public int nodeX, nodeY;

		public Node(final int x, final int y) {
			this.nodeX = x;
			this.nodeY = y;
		}
	}

	@Override
	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		if (Debug.CREATURES_DEBUG_CLIENT) {
			if (hidePath) {
	            list.add(ActionType.DEBUG_SHOW_PATH.getRepresentation());
            } else {
	            list.add(ActionType.DEBUG_HIDE_PATH.getRepresentation());
            }
			if (watch) {
				list.add(ActionType.DEBUG_DISABLE_WATCH.getRepresentation());
			} else {
				list.add(ActionType.DEBUG_ENABLE_WATCH.getRepresentation());
			}

		}

	}


	//
	// Creature
	//

	/**
	 * Get the metamorphosis in effect.
	 *
	 * @return	The metamorphosis, or <code>null</code>.
	 */
	public String getMetamorphosis() {
		return metamorphosis;
	}


	//
	// Entity
	//

	/**
	 * Transition method. Create the screen view for this entity.
	 *
	 * @return	The on-screen view of this entity.
	 */
	@Override
	protected Entity2DView createView() {
		return new Creature2DView(this);
	}

	/**
	 * Get the area the entity occupies.
	 *
	 * @return	A rectange (in world coordinate units).
	 */
	@Override
	public Rectangle2D getArea() {
		// Hack for human like creatures
		if ((Math.abs(width - 1) < .1) && (Math.abs(height - 2) < .1)) {
			return new Rectangle.Double(getX(), getY() + 1, 1, 1);
		}

		return super.getArea();
	}


	/**
	 * Get the entity height.
	 *
	 * @return	The height.
	 */
	@Override
	public double getHeight() {
		return height;
	}

	/**
	 * Get the entity width.
	 *
	 * @return	The width.
	 */
	@Override
	public double getWidth() {
		return width;
	}


	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		if(object.has("height")) {
			height = object.getDouble("height");
		} else {
			height = 1.0;
		}

		if(object.has("width")) {
			width = object.getDouble("width");
		} else {
			width = 1.5;
		}

		String type = getType();

		if (object.has("name")){
			String name = object.get("name");

			if (type.startsWith("creature")) {
				if (name.equals("wolf")) {
					moveSounds=new String[]{"bark-1.wav","howl-5.wav","howl-2.wav","howl-11.wav"};
					
	
				} else if (name.equals("rat") || name.equals("caverat") || name.equals("venomrat")) {
					moveSounds=new String[2];
					moveSounds[0]="rats-2.wav";
					moveSounds[1]="rats-41.wav";
				//	moveSounds[2]="rats-3.wav";
				
				} else if (name.equals("razorrat")) {
					moveSounds=new String[1];
					moveSounds[0]="rats-1.wav";
					
				} else if (name.equals("gargoyle")) {
					moveSounds=new String[3];
					moveSounds[0]="hyena-1.wav";
					moveSounds[1]="hyena-2.aiff";
					moveSounds[2]="hyena-3.wav";
					
					
				} else if (name.equals("boar")) {
					moveSounds=new String[2];
					moveSounds[0]="pig-1.wav";
					moveSounds[1]="pig-2.wav";
					
					
				} else if (name.equals("bear")) {
					moveSounds=new String[3];
					moveSounds[0]="bear-1.aiff";
					moveSounds[1]="bear-2.wav";
					moveSounds[2]="bear-3.wav";
					
				} else if (name.equals("giantrat")) {
					moveSounds=new String[2];
					moveSounds[0]="bobcat-1.wav";
					moveSounds[1]="leopard-11.wav";

					
				} else if (name.equals("cobra")) {
					moveSounds=new String[1];
					moveSounds[0]="snake-1.wav";
					
				} else if (name.equals("kobold")) {
					moveSounds=new String[4];
					moveSounds[0]="panda-1.wav";
					moveSounds[1]="panda-2.aiff";
					moveSounds[2]="racoon-1.aiff";
					moveSounds[3]="lama-1.wav";
					
					
				} else if (name.equals("goblin")) {
					moveSounds=new String[2];
					moveSounds[0]="saur-3.au";
					moveSounds[1]="saur-4.wav,x";
					
				} else if (name.equals("troll")) {
					moveSounds=new String[5];
					moveSounds[0]="gorilla-1.wav";
					moveSounds[1]="gorilla-2.wav";
					moveSounds[2]="gorilla-3.wav";
					moveSounds[3]="gorilla-4.au";
					moveSounds[4]="gorilla-5.aiff";
					
				} else if (name.equals("orc")) {
					moveSounds=new String[2];
					moveSounds[0]= "lion-11.wav";
					moveSounds[1]= "lion-22.wav";
					
				} else if (name.equals("ogre")) {
					moveSounds=new String[4];
					moveSounds[0]="yell-1.wav";
					moveSounds[1]="groan-1.wav";
					moveSounds[2]="moan-1.wav";
					moveSounds[3]="fart-1.wav";
					
				} else if (name.equals("skeleton")) {
					moveSounds=new String[5];
					moveSounds[0]="bones-1.aiff";
					moveSounds[1]="evillaugh-3.wav";
					moveSounds[2]="evillaugh-5.wav";
					moveSounds[3]="ghost-1.wav";
					moveSounds[4]="ghost-2.wav";
					
				} else if (name.equals("cyclops")) {
					moveSounds=new String[4];
					moveSounds[0]="laugh-33.wav";
					moveSounds[1]="evillaugh-4.wav";
					moveSounds[2]="grunt-1.wav";
					moveSounds[3]="grunt-2.wav";
					
					
				}
			}
		}

		if(object.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
		} else {
			metamorphosis = null;
		}
	}

	private long soundWait = 0L;

	/**
	 * When the entity's position changed.
	 *
	 * @param	x		The new X coordinate.
	 * @param	y		The new Y coordinate.
	 */
	@Override
	protected void onPosition(double x, double y) {
		super.onPosition(x, y);

		if((soundWait < System.currentTimeMillis()) && (Rand.rand(100) < 5)) {
			try {
		    		SoundMaster.play(moveSounds[Rand.rand(moveSounds.length)], x, y);
			} catch(NullPointerException e){
			}

			soundWait = System.currentTimeMillis() + 1000L;
		}
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) throws AttributeNotFoundException {
		super.onChangedAdded(object, changes);

		if (changes.has("width")) {
			width = changes.getDouble("width");
			changed();
		}

		if (changes.has("height")) {
			height = changes.getDouble("height");
			changed();
		}

		/*
		 * Debuging?
		 */
		if (changes.has("debug")) {
			onDebug(changes.get("debug"));
		}

		if (changes.has("metamorphosis")) {
			metamorphosis = object.get("metamorphosis");
			changed();
		}
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		if (changes.has("metamorphosis")) {
			metamorphosis = null;
			changed();
		}
	}
}
