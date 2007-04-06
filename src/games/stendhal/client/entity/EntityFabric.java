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

import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * @author astridemma
 * 
 */
public class EntityFabric {
   protected EntityFabric(){
	   
   }
	/**
	 *  Create a Entity of the correct type depending of the arianne object
	 *
	 * @param object the underlying server RPObject
	 * @return the created Entity
	 */
	public static Entity createEntity(final RPObject object) {
		try {
			String type = object.get("type");

			if (type.equals("player") && object.has("name")){
				if (StendhalClient.get().getUserName().equals(object.get("name"))){
					User me = new User();
					me.init(object);
					return me;
					
				}
			}

			String eclass = null;
			if (object.has("class")) {
				eclass = object.get("class");
			}

			// TODO: remove this hack and adjust EntityMap after release of 0.59
			if (type.equals("creature") && object.has("width") && object.has("height")) {
				ResizeableCreature resCreature =new ResizeableCreature();
				resCreature.init(object);
				return resCreature;
			}

			Class entityClass = EntityMap.getClass(type, eclass);

			if (entityClass == null) {
				// If there is no entity, let's try without using class.
				entityClass = EntityMap.getClass(type, null);
			}

			Entity en=null;
				en= (Entity) entityClass.newInstance();
				en.init(object);
				
			
			if (en instanceof Inspectable) {
				if (StendhalUI.get()!=null)
				
				((Inspectable) en).setInspector(StendhalUI.get().getInspector());
			}
			return en;
		} catch (Exception e) {
			
			Logger logger = Log4J.getLogger(EntityFabric.class);
			logger.error("cannot create entity for object " + object, e);
			return null;
		}
	}

}
