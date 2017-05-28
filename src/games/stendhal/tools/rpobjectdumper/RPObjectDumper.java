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
package games.stendhal.tools.rpobjectdumper;

import java.io.IOException;
import java.sql.SQLException;

import games.stendhal.server.core.engine.RPClassGenerator;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.RPObjectDAO;

/**
 * dumps an rpobject
 *
 * @author hendrik
 */
public class RPObjectDumper {

	public static void main(String[] args) throws SQLException, IOException {
		new DatabaseFactory().initializeDatabase();

		new RPClassGenerator().createRPClasses();

		int objectid = Integer.parseInt(args[0]);
		RPObject object = DAORegister.get().get(RPObjectDAO.class).loadRPObject(objectid);
		RPObject object2 = new RPObject(object);
		System.out.println("transformed object: " + object);
		System.out.println("untransformed object: " + object2);

		System.out.println("Class: !" + object.getRPClass() + "!");
		System.out.println("Class-Name: !" + object.getRPClass().getName() + "!");

		System.exit(0);
	}
}
