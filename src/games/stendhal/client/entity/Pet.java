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
import games.stendhal.common.Rand;

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/** A Pet entity */
public class Pet extends RPEntity {
	/**
	 * Pet idea property.
	 */
	public final static Object	PROP_IDEA	= new Object();

	/**
	 * Pet weight property.
	 */
	public final static Object	PROP_WEIGHT	= new Object();

	/**
	 * The pet's weight (0-100).
	 */
	private int weight;

	/**
	 * The pet's idea.
	 */
	private String	idea;


	//
	// Pet
	//

	/**
	 * Get the idea setting.
	 *
	 * @return	The pet's idea.
	 */
	public String getIdea() {
		return idea;
	}


	/**
	 * Get the weight.
	 *
	 * @return	The pet's weight.
	 */
	public int getWeight() {
		return weight;
	}


	/**
	 * The idea changed.
	 *
	 * @param	idea		The idea, or <code>null</code>.
	 */
	protected void onIdea(final String idea) {
		if(idea == null) {
			// No "idea" - Do nothing
		} else if ("eat".equals(idea)) {
			probableChat(15);
		} else if ("food".equals(idea)) {
			probableChat(20);
		} else if ("walk".equals(idea)) {
			probableChat(20);
		} else if ("follow".equals(idea)) {
			probableChat(20);
		} else if ("stop".equals(idea)) {
			// Do nothing
		}
	}


	private void probableChat(final int chance) {

		String[][] soundnames={{"pet-1.wav","pet-3.wav"},{"pet-2.wav","pet-4.wav"}};
		int which=Rand.rand(2);
		if (Rand.rand(100)<chance){
		String token = weight > 50 ? soundnames[0][which]:soundnames[1][which];
		SoundMaster.play(token,x,y);//playSound(token, 20, 35, chance);
		}
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

				SoundMaster.play("pet-2.wav",x,y);
				break;

			default:
				SoundMaster.play((weight > 50) ?"pet-2.wav":"pet-1.wav",x,y);
				super.onAction(at, params);
				break;
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
	        if (!User.get().hasPet()) {
				list.add(ActionType.OWN.getRepresentation());
			}
		}
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
		return new Pet2DView(this);
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

		/*
		 * Idea
		 */
		if (object.has("idea")) {
			idea = object.get("idea");
		} else {
			idea = null;
		}

		/*
		 * Weight
		 */
		if (object.has("weight")){
			//TODO: find why there are pet without "weight" attribute
			weight = object.getInt("weight");
		} else {
			weight = 0;
		}

		onIdea(idea);
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
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		/*
		 * Idea
		 */
		if (changes.has("idea")) {
			idea = changes.get("idea");
			onIdea(idea);
			fireChange(PROP_IDEA);
		}

		/*
		 * Weight
		 */
		if (changes.has("weight")) {
			int oldWeight = weight;
			weight = changes.getInt("weight");

			if (weight > oldWeight) {
				SoundMaster.play("eat-1.wav",x,y);
			}

			fireChange(PROP_WEIGHT);
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

		/*
		 * Idea
		 */
		if (changes.has("idea")) {
			idea = null;
			onIdea(idea);
			fireChange(PROP_IDEA);
		}
	}
}
