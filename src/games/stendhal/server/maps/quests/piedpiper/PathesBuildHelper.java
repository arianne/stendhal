package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.server.core.pathfinder.Node;
import java.util.LinkedList;
import marauroa.common.Pair;

public class PathesBuildHelper {

	/**
	 * route for pied piper incoming
	 * @return - incoming path
	 */
	public static LinkedList<Pair<String, LinkedList<Node>>> getAwaitingPhasePath() {
		LinkedList<Pair<String, LinkedList<Node>>> fullPath = 
			new LinkedList<Pair<String, LinkedList<Node>>>();
		
		LinkedList<Node> localroute = new LinkedList<Node>();
		
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
		fullPath.add(new Pair<String, LinkedList<Node>>("0_ados_wall_n2", 
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
		fullPath.add(new Pair<String, LinkedList<Node>>("0_ados_city_n2", 
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
		fullPath.add(new Pair<String, LinkedList<Node>>("0_ados_city_n", 
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
		fullPath.add(new Pair<String, LinkedList<Node>>("0_ados_city", 
				new LinkedList<Node>(localroute)));
		
		// town hall 
		localroute.clear();
		localroute.add(new Node(22,16));
		localroute.add(new Node(22,8));
		localroute.add(new Node(9,8));
		localroute.add(new Node(9,4));
		localroute.add(new Node(5,4));
		fullPath.add(new Pair<String, LinkedList<Node>>("int_ados_town_hall", 
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
		fullPath.add(new Pair<String, LinkedList<Node>>("int_ados_town_hall_1", 
				new LinkedList<Node>(localroute)));
		
		// 2 floor
		localroute.clear();
		localroute.add(new Node(35,3));
		localroute.add(new Node(26,3));
		localroute.add(new Node(26,14));
		//localroute.add(new Node(26,14)); // very left point
		//localroute.add(new Node(32,14)); // very right point
		fullPath.add(new Pair<String, LinkedList<Node>>("int_ados_town_hall_2", 
				new LinkedList<Node>(localroute)));	
		
		return fullPath;		
	}

}
