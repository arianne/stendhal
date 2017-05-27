/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.puzzle;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * manages puzzle building blocks
 */
public class PuzzleEventDispatcher {

	/** singleton instance */
	private static PuzzleEventDispatcher instance;

	private static final String SEP = ".";

	/** use strings to prevent having to deal with instantiation order */
	private Multimap<String, String> notifies = LinkedListMultimap.create();

	/** map building block names to entities */
	private Map<String, PuzzleBuildingBlock> buildingBlocks = new HashMap<>();

	/** groovy shell */
	private GroovyShell shell;

	/**
	 * gets the PuzzleEventDispatcher
	 *
	 * @return
	 */
	public static PuzzleEventDispatcher get() {
		if (instance == null) {
			instance = new PuzzleEventDispatcher();
		}
		return instance;
	}

	/**
	 * singleton constructor
	 */
	private PuzzleEventDispatcher() {
		CompilerConfiguration config = new CompilerConfiguration();
		config.setScriptBaseClass(GroovyPuzzlePropertyAdapter.class.getName());
		shell = new GroovyShell(this.getClass().getClassLoader(), new Binding(), config);
	}

	/**
	 * registers a building block
	 *
	 * @param zone
	 * @param name
	 * @param block
	 */
	public void register(PuzzleBuildingBlock block) {
		if (block.getName().contains(SEP)) {
			throw new RuntimeException("Entity \"" + block.getName() + "\" in zone " + block.getZoneName() + " must not contain " + SEP + ".");
		}

		String prefix = block.getZoneName() + SEP;
		String qualifiedName = prefix + block.getName();
		buildingBlocks.put(qualifiedName, block);

		for (String dependency : block.getDependencies()) {
			if (!dependency.contains(SEP)) {
				dependency = prefix + dependency;
			}
			notifies.put(dependency, qualifiedName);
		}
	}

	/**
	 * notifies about property changes
	 *
	 * @param sourceBlock PuzzleBuildingBlock which had a property changed
	 */
	public void notify(PuzzleBuildingBlock sourceBlock) {
		String qualifiedName = sourceBlock.getZoneName() + SEP + sourceBlock.getName();
		for (String notifyName : notifies.get(qualifiedName)) {
			PuzzleBuildingBlock targetBlock = buildingBlocks.get(notifyName);
			if (targetBlock != null) {
				targetBlock.onInputChanged();
			}
		}
	}

	/**
	 * gets the value of a property
	 *
	 * @param zone zone name
	 * @param name block name
	 * @param property property
	 * @return value
	 */
	public Object getValue(String zone, String name, String property) {
		String qualifiedName = zone + SEP + name;
		PuzzleBuildingBlock buildingBlock = buildingBlocks.get(qualifiedName);
		if (buildingBlock == null) {
			throw new IllegalArgumentException("Cannot resolve reference to " + qualifiedName);
		}
		return buildingBlock.get(property);
	}

	/**
	 * parses a Groovy expression
	 *
	 * @param buildingBlock BuildingBlock on which the expression is defined
	 * @param expression expression to parse
	 * @return Groovy script
	 */
	public Script parseExpression(PuzzleBuildingBlock buildingBlock, String expression) {
		try {
			Script script = shell.parse(expression);
			script.setProperty("buildingBlock", buildingBlock);
			return script;
		} catch (CompilationFailedException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
