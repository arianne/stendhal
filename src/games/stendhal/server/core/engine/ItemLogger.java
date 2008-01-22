package games.stendhal.server.core.engine;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Item Logger.
 *
 * @author hendrik
 */
public class ItemLogger {

	private static final String ATTR_ITEM_LOGID = "logid";

	private static String getQuantity(RPObject item) {
		int quantity = 1;
		if (item.has("quantity")) {
			quantity = item.getInt("quantity");
		}
		return Integer.toString(quantity);
	}

	public static void loadOnLogin(Player player, RPSlot slot, Item item) {
		if (item.has(ATTR_ITEM_LOGID)) {
			return;
		}
		itemLog(item, player, "create", item.get("name"), getQuantity(item), "olditem", slot.getName());
	}

	public static void destroyOnLogin(Player player, RPSlot slot, RPObject item) {
		itemLog(item, player, "destroy", item.get("name"), getQuantity(item), "on login", slot.getName());
    }

	public static void destroy(RPEntity entity, RPSlot slot, RPObject item) {
		itemLog(item, entity, "destroy", item.get("name"), getQuantity(item), "quest", slot.getName());
    }

	public static void dropQuest(Player player, Item item) {
		itemLog(item, player, "destroy", item.get("name"), getQuantity(item), "quest", null);
    }

	public static void timeout(Item item) {
		itemLog(item, null, "destroy", item.get("name"), getQuantity(item), "timeout", item.getZone().getID().getID() + " " + item.getX() + " " + item.getY());
    }

	public static void displace(Player player, PassiveEntity item, StendhalRPZone zone, int x, int y) {
		itemLog(item, player, "ground-to-ground", zone.getID().getID(), item.getX() + " " + item.getY(), zone.getID().getID(), x + " " + y);
    }

	public static void equipAction(Player player, Entity entity, String[] sourceInfo, String[] destInfo) {
	    itemLog(entity, player, sourceInfo[0] + "-to-" + destInfo[0], sourceInfo[1], sourceInfo[2], destInfo[1], destInfo[2]);
    }

	public static void merge(RPEntity entity, Item oldItem, Item outlivingItem) {
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;

		((StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase()).itemLogAssignIDIfNotPresent(oldItem, outlivingItem);
		String oldQuantity = getQuantity(oldItem);
		String oldOutlivingQuantity = getQuantity(outlivingItem);
		String newQuantity = Integer.toString(Integer.parseInt(oldQuantity) + Integer.parseInt(oldOutlivingQuantity));
	    itemLog(oldItem, player, "merge in", outlivingItem.get(ATTR_ITEM_LOGID), oldQuantity, oldOutlivingQuantity, newQuantity);
	    itemLog(outlivingItem, player, "merged in", oldItem.get(ATTR_ITEM_LOGID), oldOutlivingQuantity, oldQuantity, newQuantity);
    }

	public static void splitOff(RPEntity player, Item item, int quantity) {
		String oldQuantity = getQuantity(item);
		String outlivingQuantity = Integer.toString(Integer.parseInt(oldQuantity) - quantity);
	    itemLog(item, player, "split out", "-1", oldQuantity, outlivingQuantity, Integer.toString(quantity));
    }

	private static void itemLog(RPObject item, RPEntity player, String event, String param1, String param2, String param3, String param4) {
		((StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase()).itemLog(item, player, event, param1, param2, param3, param4);
	}

	public static void splitOff(Player player, Item item, StackableItem newItem, int quantity) {
		((StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase()).itemLogAssignIDIfNotPresent(item, newItem);
		String outlivingQuantity = getQuantity(item);
		String newQuantity = getQuantity(newItem);
		String oldQuantity = Integer.toString(Integer.parseInt(outlivingQuantity) + Integer.parseInt(newQuantity));
	    itemLog(item, player, "split out", newItem.get(ATTR_ITEM_LOGID), oldQuantity, outlivingQuantity, newQuantity);
	    itemLog(newItem, player, "splitted out", item.get(ATTR_ITEM_LOGID), oldQuantity, newQuantity, outlivingQuantity);
    }

	/*
	create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity / olditem
	slot-to-slot       source       source-slot       target    target-slot
	ground-to-slot     zone         x         y       target    target-slot
	slot-to-ground     source       source-slot       zone         x         y
	ground-to-ground   zone         x         y       zone         x         y
	use                old-quantity new-quantity
	destroy            name         quantity          by admin / by quest / on login / timeout on ground
	merge in           outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
	merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
	split out          new_id            old-quantity         outliving-quantity       new-quantity
	splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity

	the last two are redundant pairs to simplify queries
	 */


}
