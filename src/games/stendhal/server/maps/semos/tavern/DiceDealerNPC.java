package games.stendhal.server.maps.semos.tavern;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.CroupierNPC;

import java.awt.Rectangle;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 0 (ground floor)
 */
public class DiceDealerNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildRicardo(zone);
	}

	private void buildRicardo(final StendhalRPZone zone) {
		final CroupierNPC ricardo = new CroupierNPC("Ricardo") {

			@Override
			protected void createPath() {
				// Ricardo doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {

				addGreeting("Welcome to the #gambling table, where dreams can come true.");
				addJob("I'm the only person in Semos who is licensed to offer gambling activities.");
				addReply(
				        "gambling",
				        "The rules are simple: just tell me if you want to #play, pay the stake, and throw the dice on the table. The higher the sum of the upper faces is, the nicer will be your prize. Take a look at the blackboards on the wall!");
				addHelp("If you are looking for Ouchit: he's upstairs.");
				addGoodbye();
			}
		};

		ricardo.setEntityClass("naughtyteen2npc");
		ricardo.setPosition(26, 2);
		ricardo.setDirection(Direction.DOWN);
		ricardo.setDescription("Ricardo is jingling loose change in his pockets. He looks quite young to have a business in a tavern.");
		ricardo.initHP(100);
		final Rectangle tableArea = new Rectangle(25, 4, 2, 3);
		
		zone.add(ricardo);
		ricardo.setTableArea(tableArea);
	}
}
