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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * writes a class definition to a new java file
 *
 * @author hendrik
 */
public class JavaClassWriter {
	private PrintStream out;
	private String className;

	/**
	 * creates a new JavaClassWriter
	 *
	 * @param outputFolder the folder to write the java file to
	 * @param className the name of the class
	 * @throws FileNotFoundException in case the folder does not exist, or the class name contains dangerous characters.
	 */
	public JavaClassWriter(String outputFolder, String className) throws FileNotFoundException {
		if (className.indexOf("..") > -1) {
			throw new FileNotFoundException("Invalid class name: " + className);
		}
		this.out = new PrintStream(new FileOutputStream(outputFolder + "/" + className + ".java"));
		this.className = className;
	}

	/**
	 * closes the written file.
	 */
	public void close() {
		out.close();
	}

	/**
	 * writes the class definition
	 *
	 * @param parent optional parent class
	 */
	public void writeClassDefinition(String parent) {
		out.print("public class " + className);
		if (parent != null) {
			out.print(" extends " + parent);
		}
		out.println(" {");
	}

	/**
	 * writes the closing of the class definition
	 */
	public void writeEndOfClass() {
		out.println("}");
	}

	/**
	 * writes an attribute
	 *
	 * @param visibility visibility of the attribute
	 * @param staticFlag static?
	 * @param type datatype
	 * @param name name
	 */
	public void writeAttribute(String visibility, boolean staticFlag, String type, String name) {
		out.print("\t" + visibility + " ");
		if (staticFlag) {
			out.print("static ");
		}
		out.println(type + " " + name + ";");
	}
}
