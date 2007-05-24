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

import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Direction;
import games.stendhal.common.Rand;

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** A Sheep entity */
public class Sheep extends NPC {
	public static final String	STATE_BIG_DOWN	= "big_" + STATE_DOWN;
	public static final String	STATE_BIG_UP	= "big_" + STATE_UP;
	public static final String	STATE_BIG_LEFT	= "big_" + STATE_LEFT;
	public static final String	STATE_BIG_RIGHT	= "big_" + STATE_RIGHT;

	private int weight;
	private String	idea;


	@Override
	public void onChangedAdded(final RPObject base, final RPObject diff) {
		super.onChangedAdded(base, diff);

		if (diff.has("weight")) {
			int oldWeight = weight;
			weight = diff.getInt("weight");

			if (weight > oldWeight) {
				SoundMaster.play("eat-1.wav",x,y);//playSound("sheep-eat", 8, 15);
				changed();
			}
		}
		
		if (diff.has("idea")) {
			idea = diff.get("idea");

			if ("eat".equals(idea)) {
				probableChat(15);
			} else if ("food".equals(idea)) {
				probableChat(20);
			} else if ("walk".equals(idea)) {
				probableChat(20);
			} else if ("follow".equals(idea)) {
				probableChat(20);
			} else if ("stop".equals(idea)){
				idea = null;
			}

			changed();
		}
	}


	/**
	 * Get the idea setting.
	 *
	 *
	 */
	public String getIdea() {
		return idea;
	}


	/**
	 * Get the weight.
	 *
	 *
	 */
	public int getWeight() {
		return weight;
	}


	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at = handleAction(action);
		switch (at) {
			case OWN:
				RPAction rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();
				rpaction.put("target", id);
				at.send(rpaction);
				
				SoundMaster.play("sheep-2.wav",x,y);//playSound("sheep-chat-2", 25, 60);
				break;

			default:
				SoundMaster.play((weight > 50) ?"sheep-2.wav":"sheep-1.wav",x,y);
				//playSound((weight > 50 ? "sheep-chat-2" : "sheep-chat"), 15, 40);
				super.onAction(at, params);
				break;
		}

	}

	private void probableChat(final int chance) {
		
		String[][] soundnames={{"sheep-1.wav","sheep-3.wav"},{"sheep-2.wav","sheep-4.wav"}};
		int which=Rand.rand(2);
		
		
//		sfx.sheep-mix.a = sheep-1.wav
//		sfx.sheep-mix.b = sheep-3.wav
//		
//		sfx.sheep-mix2.b = sheep-2.wav
//		sfx.sheep-mix2.a = sheep-4.wav
	
		if (Rand.rand(100)<chance){
		String token = weight > 50 ? soundnames[0][which]:soundnames[1][which];
		SoundMaster.play(token,x,y);//playSound(token, 20, 35, chance);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see games.stendhal.client.entity.RPEntity#buildOfferedActions(java.util.List)
	 */
	@Override
	protected void buildOfferedActions(List<String> list) {

		super.buildOfferedActions(list);
		if (!(User.isNull())) {
	        if (!User.get().hasSheep()) {
				list.add(ActionType.OWN.getRepresentation());
			}
		}
	}


	//
	// RPEntity
	//

	/**
	 * Get the appropriete named state for a direction.
	 *
	 * @param	direction	The direction.
	 *
	 * @return	A named state.
	 */
	@Override
	protected String getDirectionState(final Direction direction) {
		String state = super.getDirectionState(direction);

		if (getWeight() >= 60) {
			state = "big_" + state;
		}

		return state;
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
		return new Sheep2DView(this);
	}



}
