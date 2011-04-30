/* $Id$
 * $Log$
 */
package games.stendhal.server.core.rp.equipping.builder;

import static games.stendhal.common.constants.Actions.BASEITEM;
import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;
import games.stendhal.common.EquipActionConsts;
import games.stendhal.common.MathHelper;
import games.stendhal.server.actions.equip.EquipUtil;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.GroundSlot;
import games.stendhal.server.util.EntityHelper;

import java.util.Iterator;
import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * builds an EquipmentActionData object based on an action
 *
 * @author hendrik
 */
public class EquipmentActionBuilder {
	private final EquipmentActionData data;
	private final Player player;
	private final RPAction action;

	/**
	 * creates a new EquipmentActionBuilder
	 *
	 * @param player player
	 * @param action RPAction to build from
	 */
	public EquipmentActionBuilder(Player player, RPAction action) {
		data = new EquipmentActionData();
		this.player = player;
		this.action = action;
	}

	/**
	 * gets the built EquipmentActionData
	 *
	 * @return data
	 */
	public EquipmentActionData getData() {
		return data;
	}

	/**
	 * build
	 */
	public void build() {
		data.setPlayer(player);
		if (action.has("quantity")) {
			data.setQuantity(action.getInt("quantity"));
		}

		buildSource();
		buildTarget();

		// TODO: /drop, NPC quests, NPC sells, NPC buyes, player trade, Harold
		// TODO: check that the client sent the request for this zone
		// TODO: get name from sourceItems
	}

	private void buildSource() {
		if (action.has(EquipActionConsts.SOURCE_PATH)) {
			buildSourceFromPath();
		} else if (action.has(EquipActionConsts.BASE_OBJECT)) {
			buildSourceFromOldContainedFormat();
		} else if (action.has(BASEITEM)) {
			buildSourceFromGround();
		} else {
			data.setErrorMessage("");
		}
	}

	private void buildTarget() {
		if (action.has(EquipActionConsts.TARGET_PATH)) {
			buildTargetFromPath();
		} else if (action.has(X)) {
			buildTargetFromGround();
		} else if (action.has(EquipActionConsts.TARGET_OBJECT)) {
			buildTargetFromOldContainedFormat();
		} else {
			data.setErrorMessage("");
		}
	}

	private void buildSourceFromPath() {
		List<String> path = action.getList(EquipActionConsts.SOURCE_PATH);
		Iterator<String> it = path.iterator();

		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, MathHelper.parseInt(it.next()));
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// Walk the slot path
		Entity entity = parent;
		String slotName = null;
		while (it.hasNext()) {
			slotName = it.next();
			if (!entity.hasSlot(slotName)) {
				data.setErrorMessage("");
				return;
			}

			final RPSlot slot = ((EntitySlot) entity.getSlot(slotName)).getWriteableSlot();
			if (slot == null) {
				data.setErrorMessage("");
				return;
			}
			if (!it.hasNext()) {
				data.setErrorMessage("");
				return;
			}
			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				data.setErrorMessage("");
				return;
			}

			entity = (Entity) slot.get(itemId);
		}

		// if the item is not contained, the item is on the ground
		if (parent == entity) {
			data.addSourceItem(entity);
			data.addSourceSlot(new GroundSlot(player.getZone(), entity));
		} else {
			data.addSourceItem(entity);
			data.addSourceSlot((EntitySlot) entity.getContainerSlot());
		}
	}

	private void buildSourceFromOldContainedFormat() {
		final Entity parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.BASE_OBJECT));

		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		final String slotName = action.get(EquipActionConsts.BASE_SLOT);
		if (!parent.hasSlot(slotName)) {
			data.setErrorMessage("");
			return;
		}
		final RPSlot slot = ((EntitySlot) parent.getSlot(slotName)).getWriteableSlot();
		if (slot == null || !(slot instanceof EntitySlot)) {
			data.setErrorMessage("");
			return;
		}

		final RPObject.ID baseItemId = new RPObject.ID(action.getInt(EquipActionConsts.BASE_ITEM), "");
		if (!slot.has(baseItemId)) {
			data.setErrorMessage("");
			return;
		}
		final Entity entity = (Entity) slot.get(baseItemId);
		data.addSourceItem(entity);
		data.addSourceSlot((EntitySlot) slot);
	}


	private void buildSourceFromGround() {
		final StendhalRPZone zone = player.getZone();
		final int objectid = action.getInt(BASEITEM);
		final Entity object = EntityHelper.entityFromZoneByID(objectid, zone);
		if (!(object instanceof PassiveEntity)) {
			return;
		}
		data.addSourceItem(object);
		data.addSourceSlot(new GroundSlot(zone, object));
	}

	private void buildTargetFromGround() {
		final int x = action.getInt(X);
		final int y = action.getInt(Y);
		data.setTargetSlot(new GroundSlot(player.getZone(), x, y));
	}

	private void buildTargetFromPath() {
		List<String> path = action.getList(EquipActionConsts.TARGET_PATH);
		Iterator<String> it = path.iterator();

		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, MathHelper.parseInt(it.next()));
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// Walk the slot path
		EntitySlot slot = null;
		while (it.hasNext()) {
			String slotName = it.next();
			if (!parent.hasSlot(slotName)) {
				data.setErrorMessage("");
				return;
			}
			slot = parent.getEntitySlot(slotName);
			if (!it.hasNext()) {
				break;
			}

			final RPObject.ID itemId = new RPObject.ID(MathHelper.parseInt(it.next()), "");
			if (!slot.has(itemId)) {
				data.setErrorMessage("");
				return;
			}
			parent = (Entity) slot.get(itemId);
		}
		data.setTargetSlot(slot);
	}

	private void buildTargetFromOldContainedFormat() {
		// get parent
		Entity parent = EquipUtil.getEntityFromId(player, action.getInt(EquipActionConsts.TARGET_OBJECT));
		if (parent == null) {
			data.setErrorMessage("");
			return;
		}

		// get slot
		String slotName = action.get(EquipActionConsts.TARGET_SLOT);
		if (!parent.hasSlot(slotName)) {
			data.setErrorMessage("");
			return;
		}
		data.setTargetSlot(parent.getEntitySlot(slotName));
	}
}
