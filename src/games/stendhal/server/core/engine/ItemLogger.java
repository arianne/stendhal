package games.stendhal.server.core.engine;

/**
 * Item Logger
 *
 * @author hendrik
 */
public class ItemLogger {
	/*
	CREATE TABLE itemlog (
	  id         SERIAL,
	  itemid     INTEGER,
	  player_id  INTEGER,
	  event,     VARCHAR(20),
	  param1     VARCHAR(50),
	  param2     VARCHAR(50),
	  param3     VARCHAR(50),
	  param4     VARCHAR(50)
	);
	
	
	
	create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity
	slot-to-slot       source       source-slot       target    target-slot
	ground-to-slot     zone         x         y       target    target-slot
	slot-to-ground     source       source-slot       zone         x         y
	ground-to-ground   zone         x         y       zone         x         y
	use                old-quantity new-quantity
	destroy
	timeout
	merge into         outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
	merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
	split out          new_id            old-quantity         outliving-quantity       new-quantity
	splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity
	
	the last two are redundant pairs to simplify queries
	 */

	
}
