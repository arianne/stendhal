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
package games.stendhal.server.pathfinder;

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

public class PathfinderTest {
	public static void main(String[] args) throws Exception {

		StendhalRPWorld world = StendhalRPWorld.get();
		world.addArea("int_pathfinding");

		StendhalRPZone zone = (StendhalRPZone) world
				.getRPZone("int_pathfinding");


		List<String> tests = Arrays.asList(
						"rat;50;10;rat;55;3",
						"rat;55;3;rat;50;10",
						"rat;50;12;rat;52;15",
						"rat;52;15;rat;50;12",
						"bat;50;9;rat;55;3",
						"bat;55;2;rat;50;10",
						"bat;50;11;rat;52;15",
						"bat;52;14;rat;50;12",
						"rat;50;10;bat;55;2",
						"rat;55;3;bat;50;9",
						"rat;50;12;bat;52;14",
						"rat;52;15;bat;50;11",
						"giantrat;55;3;bat;50;9",
						"giantrat;52;15;bat;50;11",
						"bat;55;2;bat;50;9",
						"bat;52;14;bat;50;11"
					);
		DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

		for (String test : tests) {
			String testData[] = test.split(";");
			Creature entity = manager.getCreature(testData[0]);
			int x0 = Integer.parseInt(testData[1]);
			int y0 = Integer.parseInt(testData[2]);
			zone.assignRPObjectID(entity);
			StendhalRPAction.placeat(zone, entity,  x0, y0);
			zone.add(entity);

			Creature target = manager.getCreature(testData[3]);
			int x1 = Integer.parseInt(testData[4]);
			int y1 = Integer.parseInt(testData[5]);
			zone.assignRPObjectID(target);
			StendhalRPAction.placeat(zone, target,  x1, y1);
			zone.add(target);	

			List<Path.Node> nodes = null;

			// calculate the destArea
			Rectangle2D entityArea = entity.getArea(entity.getX(), entity.getY());
			Rectangle2D targetArea = target.getArea(target.getX(), target.getY());
			Rectangle destArea = new Rectangle((int)(targetArea.getX() - entityArea.getWidth()), 
			                                        (int)(targetArea.getY() - entityArea.getHeight()), 
			                                        (int)(entityArea.getWidth() + targetArea.getWidth() + 1), 
			                                        (int)(entityArea.getHeight() + targetArea.getHeight() + 1));

			// for 1x2 size creatures the destArea, needs bo be one up
			destArea.translate(0, (int)(entity.getY() - entityArea.getY()));

			long startTime = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
// 				nodes = Path.searchPath(entity, target, 20.0);
				nodes = Path.searchPath(entity, entity.getX(), entity.getY(), destArea, 20.0);
			}
			long endTime = System.currentTimeMillis();
						
			if ((args.length > 0) && (args[0].equals("print"))) {
				for (int j = 0; j < zone.getHeight(); j++) {
					for (int i = 0; i < zone.getWidth(); i++) {
						boolean contained = false;
						int stepNr = 0;
						for (Path.Node node : nodes) {
							stepNr++;
							if (node.x == i && node.y == j) {
								contained = true;
								break;
							}
						}

						if (entity.nextTo(i, j, 0.25)) {
							if (contained) {
								System.out.print("E");
							} else {
								System.out.print("e");
							}
						} else if (target.nextTo(i, j, 0.25)) {
							if (contained) {
								System.out.print("T");
							} else {
								System.out.print("t");
							}
						} else if (contained) {
							System.out.print(stepNr % 10);
						} else if (zone.collides(i, j)) {
							System.out.print("*");
						} else if (destArea.contains(i, j)) {
							System.out.print(" ");
						} else {
							System.out.print(".");
						}
					}
				}

					System.out.println();
		 				}
			boolean pass = (nodes.size() > 0);
			if (pass) {
				Path.Node lastNode = nodes.get(nodes.size() - 1);
				entity.set(lastNode.x, lastNode.y);
				pass = entity.nextTo(target, 0.25);
				if (zone.collides(entity, lastNode.x, lastNode.y)) {
					System.out.print("C ");
						 				}
						 			}
			if (pass) {
				System.out.print("Pass ");
			} else {
				System.out.print("FAIL ");
			}
			System.out.println("Test: " + test + " time: " + ((double)(endTime - startTime) / 1000) + "ms");
			System.out.print("\tentityArea: (" + entityArea.getX() + ", " + entityArea.getY() + ", " + entityArea.getWidth() + ", " + entityArea.getHeight() + ")");
			System.out.print(" targetArea: (" + targetArea.getX() + ", " + targetArea.getY() + ", " + targetArea.getWidth() + ", " + targetArea.getHeight() + ")");
			System.out.println(" destArea: (" + destArea.getX() + ", " + destArea.getY() + ", " + destArea.getWidth() + ", " + destArea.getHeight() + ")");
			System.out.println("\tpath: " + nodes);
			System.out.println();
			zone.remove(entity);
			zone.remove(target);
		}
	}
}
