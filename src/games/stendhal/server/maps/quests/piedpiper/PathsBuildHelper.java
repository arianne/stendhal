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
package games.stendhal.server.maps.quests.piedpiper;


import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.RPZonePath;


public class PathsBuildHelper {

	/**
	 * route for pied piper incoming
	 * @return - incoming path
	 */
	public static List<RPZonePath> getAdosIncomingPath() {
		final List<RPZonePath> fullPath =
			new LinkedList<RPZonePath>();

		final List<Node> localroute = new LinkedList<Node>();

		// from house
		localroute.add(new Node(74,17));
		localroute.add(new Node(74,20));
		localroute.add(new Node(77,20));
		localroute.add(new Node(77,27));
		localroute.add(new Node(79,27));
		localroute.add(new Node(79,31));
		localroute.add(new Node(81,31));
		localroute.add(new Node(81,35));
		localroute.add(new Node(84,35));
		localroute.add(new Node(84,37));
		localroute.add(new Node(86,37));
		localroute.add(new Node(86,39));
		localroute.add(new Node(106,39));
		localroute.add(new Node(106,46));
		localroute.add(new Node(108,46));
		localroute.add(new Node(108,52));
		localroute.add(new Node(115,52));
		localroute.add(new Node(115,43));
		localroute.add(new Node(127,43));
		fullPath.add(
				new RPZonePath("0_ados_wall_n2",
				new LinkedList<Node>(localroute)));

		// market
		localroute.clear();
		localroute.add(new Node(0,43));
		localroute.add(new Node(25,43));
		localroute.add(new Node(25,50));
		localroute.add(new Node(41,50));
		localroute.add(new Node(41,107));
		localroute.add(new Node(35,107));
		localroute.add(new Node(35,127));
		fullPath.add(
				new RPZonePath("0_ados_city_n2",
				new LinkedList<Node>(localroute)));

		// city north part
		localroute.clear();
		localroute.add(new Node(35,0));
		localroute.add(new Node(35,29));
		localroute.add(new Node(37,29));
		localroute.add(new Node(37,31));
		localroute.add(new Node(38,31));
		localroute.add(new Node(38,35));
		localroute.add(new Node(44,35));
		localroute.add(new Node(44,40));
		localroute.add(new Node(44,41));
		localroute.add(new Node(46,41));
		localroute.add(new Node(46,42));
		localroute.add(new Node(47,42));
		localroute.add(new Node(47,44));
		localroute.add(new Node(48,44));
		localroute.add(new Node(48,47));
		localroute.add(new Node(49,47));
		localroute.add(new Node(49,54));
		localroute.add(new Node(52,54));
		localroute.add(new Node(52,56));
		localroute.add(new Node(53,56));
		localroute.add(new Node(53,65));
		localroute.add(new Node(56,65));
		localroute.add(new Node(56,77));
		localroute.add(new Node(53,77));
		localroute.add(new Node(53,81));
		localroute.add(new Node(51,81));
		localroute.add(new Node(51,84));
		localroute.add(new Node(49,84));
		localroute.add(new Node(49,106));
		localroute.add(new Node(47,106));
		localroute.add(new Node(47,113));
		localroute.add(new Node(45,113));
		localroute.add(new Node(45,119));
		localroute.add(new Node(49,119));
		localroute.add(new Node(49,125));
		localroute.add(new Node(52,125));
		localroute.add(new Node(52,127));
		fullPath.add(
				new RPZonePath("0_ados_city_n",
				new LinkedList<Node>(localroute)));

		// city central
		localroute.clear();
		localroute.add(new Node(52,0));
		localroute.add(new Node(52,5));
		localroute.add(new Node(55,5));
		localroute.add(new Node(55,8));
		localroute.add(new Node(60,8));
		localroute.add(new Node(60,11));
		localroute.add(new Node(66,11));
		localroute.add(new Node(66,55));
		localroute.add(new Node(55,55));
		localroute.add(new Node(55,97));
		localroute.add(new Node(42,97));
		localroute.add(new Node(42,94));
		fullPath.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));

		// town hall
		localroute.clear();
		localroute.add(new Node(22,16));
		localroute.add(new Node(22,8));
		localroute.add(new Node(9,8));
		localroute.add(new Node(9,4));
		localroute.add(new Node(5,4));
		fullPath.add(
				new RPZonePath("int_ados_town_hall",
				new LinkedList<Node>(localroute)));

		// 1 floor
		localroute.clear();
		localroute.add(new Node(3,4));
		localroute.add(new Node(2,4));
		localroute.add(new Node(2,7));
		localroute.add(new Node(6,7));
		localroute.add(new Node(6,15));
		localroute.add(new Node(37,15));
		localroute.add(new Node(37,3));
		fullPath.add(
				new RPZonePath("int_ados_town_hall_1",
				new LinkedList<Node>(localroute)));

		// 2 floor
		localroute.clear();
		localroute.add(new Node(35,3));
		localroute.add(new Node(26,3));
		localroute.add(new Node(26,14));
		//localroute.add(new Node(26,14)); // very left point
		//localroute.add(new Node(32,14)); // very right point
		fullPath.add(
				new RPZonePath("int_ados_town_hall_2",
				new LinkedList<Node>(localroute)));

		return fullPath;
	}


	/**
	 * route for pied piper outgoing
	 * @return - outgoing path
	 */
	public static List<RPZonePath> getAdosTownHallBackwardPath() {
		final List<RPZonePath> fullPath =
			new LinkedList<RPZonePath>();

		final List<Node> localroute = new LinkedList<Node>();

		// 2 floor
		localroute.clear();
		localroute.add(new Node(26,14));
		localroute.add(new Node(26,3));
		localroute.add(new Node(35,3));
		fullPath.add(
				new RPZonePath("int_ados_town_hall_2",
				new LinkedList<Node>(localroute)));

		// 1 floor
		localroute.clear();
		localroute.add(new Node(37,3));
		localroute.add(new Node(37,15));
		localroute.add(new Node(6,15));
		localroute.add(new Node(6,7));
		localroute.add(new Node(2,7));
		localroute.add(new Node(2,4));
		localroute.add(new Node(3,4));
		fullPath.add(
				new RPZonePath("int_ados_town_hall_1",
				new LinkedList<Node>(localroute)));

		// town hall
		localroute.clear();
		localroute.add(new Node(5,4));
		localroute.add(new Node(9,4));
		localroute.add(new Node(9,8));
		localroute.add(new Node(22,8));
		localroute.add(new Node(22,16));
		fullPath.add(
				new RPZonePath("int_ados_town_hall",
				new LinkedList<Node>(localroute)));
		/*
		// city central
		localroute.clear();
		localroute.add(new Node(42,94));
		fullPath.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));
		 */
		return fullPath;
	}

	/**
	 * it is a point where piper should go after speaking with mayor.
	 * @return - return point where pied piper can go through his multi zones path.
	 */
	public static Node getAdosTownHallMiddlePoint() {
		return new Node(26,14);
	}

	/**
	 * route for pied piper outgoing event
	 * @return - outgoing path
	 */
	public static List<List<RPZonePath>> getAdosCollectingRatsPaths() {

		final List<Node> localroute = new LinkedList<Node>();
		final List<RPZonePath> globalroute = new LinkedList<RPZonePath>();
		final List<List<RPZonePath>> fullPath = new LinkedList<List<RPZonePath>>();

		/*
		 * 1st creature (town hall)
		 */
		localroute.clear();
		localroute.add(new Node(42,94));
		localroute.add(new Node(42,97));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * Susi's house
		 */
		localroute.clear();
		localroute.add(new Node(42,97));
		localroute.add(new Node(42,116));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * next house from Susi
		 */
		localroute.clear();
		localroute.add(new Node(42,116));
		localroute.add(new Node(34,116));
		localroute.add(new Node(22,116));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * left house in houses group at southleft part of ados
		 */
		localroute.clear();
		localroute.add(new Node(22,116));
		localroute.add(new Node(22,127));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));

		localroute.clear();
		localroute.add(new Node(22,0));
		localroute.add(new Node(22,3));
		localroute.add(new Node(9,3));
		localroute.add(new Node(9,12));
		localroute.add(new Node(8,12));

		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * middle house in houses group at southleft part of ados
		 */
		localroute.clear();
		localroute.add(new Node(8,12));
		localroute.add(new Node(16,12));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * right house in houses group at southleft part of ados
		 */
		localroute.clear();
		localroute.add(new Node(16,12));
		localroute.add(new Node(28,12));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * upperleft house in houses group at south part of ados
		 */
		localroute.clear();
		localroute.add(new Node(28,12));
		localroute.add(new Node(28,41));
		localroute.add(new Node(31,41));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * bottomleft house in houses group at south part of ados
		 */
		localroute.clear();
		localroute.add(new Node(31,41));
		localroute.add(new Node(31,52));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * bottomright house in houses group at south part of ados
		 */
		localroute.clear();
		localroute.add(new Node(31,52));
		localroute.add(new Node(31,55));
		localroute.add(new Node(50,55));
		localroute.add(new Node(50,46));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * separate house in houses group at south part of ados
		 */
		localroute.clear();
		localroute.add(new Node(50,46));
		localroute.add(new Node(58,46));
		localroute.add(new Node(58,51));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * upperright house in houses group at south part of ados
		 */
		localroute.clear();
		localroute.add(new Node(58,51));
		localroute.add(new Node(59,51));
		localroute.add(new Node(59,52));
		localroute.add(new Node(74,52));
		localroute.add(new Node(74,40));
		localroute.add(new Node(58,40));
		localroute.add(new Node(58,46));
		localroute.add(new Node(50,46));
		localroute.add(new Node(50,37));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * ados church
		 */
		localroute.clear();
		localroute.add(new Node(50,37));
		localroute.add(new Node(50,32));
		localroute.add(new Node(52,32));
		localroute.add(new Node(52,28));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * long house
		 */
		localroute.clear();
		localroute.add(new Node(52,28));
		localroute.add(new Node(31,28));
		localroute.add(new Node(31,0));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city_s",
				new LinkedList<Node>(localroute)));

		localroute.clear();
		localroute.add(new Node(31,127));
		localroute.add(new Node(31,118));
		localroute.add(new Node(14,118));
		localroute.add(new Node(14,97));

		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * ados house 77
		 */
		localroute.add(new Node(14,97));
		localroute.add(new Node(0,97));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_city",
				new LinkedList<Node>(localroute)));

		localroute.clear();
		localroute.add(new Node(127,97));
		localroute.add(new Node(115,97));
		localroute.add(new Node(115,95));

		globalroute.add(
				new RPZonePath("0_ados_wall",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * ados house 76
		 */
		localroute.clear();
		localroute.add(new Node(115,95));
		localroute.add(new Node(106,95));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_wall",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * ados house 75
		 */
		localroute.clear();
		localroute.add(new Node(106,95));
		localroute.add(new Node(97,95));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_wall",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));

		/*
		 * ados Farmers' family house
		 */
		localroute.clear();
		localroute.add(new Node(97,95));
		localroute.add(new Node(92,95));
		localroute.add(new Node(92,81));
		localroute.add(new Node(122,81));
		localroute.add(new Node(122,78));

		globalroute.clear();
		globalroute.add(
				new RPZonePath("0_ados_wall",
				new LinkedList<Node>(localroute)));
		fullPath.add(new LinkedList<RPZonePath>(globalroute));




		/*
		 * thats all :)
		 */
		return fullPath;
	}

}
