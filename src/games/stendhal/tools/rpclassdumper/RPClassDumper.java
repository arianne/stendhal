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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import games.stendhal.server.core.engine.RPClassGenerator;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPClass;

/**
 * dumps the rpclass definitions to .java files,
 * so that they can be reversed engineered into uml class diagrams.
 *
 * @author hendrik
 */
public class RPClassDumper {
	private String outputFolder;
	private JavaClassWriter writer;

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
	 * @throws FileNotFoundException in case the output folder is invalid
	 */
	private void dumpAll() throws FileNotFoundException {
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
	 * @throws FileNotFoundException in case the output folder is invalid
	 */
	private void dump(RPClass rpclass) throws FileNotFoundException {
		if (rpclass.getName().equals("")) {
			return;
		}

		String parent = null;
		if (rpclass.getParent() != null) {
			parent = rpclass.getParent().getName();
		}

		writer = new JavaClassWriter(outputFolder, rpclass.getName());
		writer.writeClassDefinition(parent);
		for (Definition def : rpclass.getDefinitions()) {
			if (def.getName().equals("#clientid") || def.getName().equals("#db_id") || def.getName().equals("id")) {
				continue;
			}
			dumpDefinition(def);
		}
		writer.writeEndOfClass();
		writer.close();
	}

	/**
	 * dumps the definition
	 *
	 * @param def Definition
	 */
	private void dumpDefinition(Definition def) {

		// visibility
		String visibility = "public";
		if ((def.getFlags() & Definition.PRIVATE) > 0) {
			visibility = "protected";
		} else if ((def.getFlags() & Definition.HIDDEN) > 0) {
			visibility = "private";
		}

		// static?
		DefinitionClass defClass = def.getDefinitionClass();
		boolean staticFlag = (defClass == DefinitionClass.STATIC);

		// name
		String name = def.getName().replace("!", "").replace("#", "").replace("-", "_").replace("class", "clazz");

		// type
		String type = def.getType().toString().toLowerCase();
		if (defClass == DefinitionClass.RPSLOT) {
			type = "List";
		} else if (defClass == DefinitionClass.RPEVENT) {
			type = def.getName();
		}

		writer.writeAttribute(visibility, staticFlag, type, name);
	}

	/**
	 * creates java files based on the RPClass definitions, so that uml tools can reverse engineer them.
	 *
	 * @param args name of output folder
	 * @throws FileNotFoundException in case the output folder is invalid
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			System.err.println("java " + RPClassDumper.class.getName() + " outputFolder");
			System.exit(1);
		}
		new File(args[0]).mkdirs();

		new RPClassGenerator().createRPClassesWithoutBaking();
		new RPClassDumper(args[0]).dumpAll();
	}

}
