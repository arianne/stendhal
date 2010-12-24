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
package games.stendhal.tools.rpclassdumper;

import games.stendhal.server.core.engine.RPClassGenerator;

import java.util.Iterator;

import marauroa.common.game.RPClass;

/**
 * dumps the rpclass definitions to .java files,
 * so that they can be reversed engineered into uml class diagrams.
 *
 * @author hendrik
 */
public class RPClassDumper {
	private String outputFolder;

	/**
	 * creates a new RPClassDumper
	 *
	 * @param outputFolder folder to dump the java files to
	 */
	public RPClassDumper(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	/**
	 * dumps all rpclasses.
	 */
	private void dumpAll() {
		Iterator<RPClass> itr = RPClass.iterator();
		while (itr.hasNext()) {
			RPClass rpclass = itr.next();
			dump(rpclass);
		}
	}

	/**
	 * dumps the specified RPClass
	 *
	 * @param rpclass rpclass to dump
	 */
	private void dump(RPClass rpclass) {
		System.out.println(rpclass);
	}

	/**
	 * creates java files based on the RPClass definitions, so that uml tools can reverse engineer them.
	 *
	 * @param args name of output folder
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("java " + RPClassDumper.class.getName() + " outputFolder");
			System.exit(1);
		}

		new RPClassGenerator().createRPClasses();
		new RPClassDumper(args[0]).dumpAll();
	}

}
