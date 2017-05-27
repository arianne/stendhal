package games.stendhal.server.core.rp.equipping.validator;

import games.stendhal.server.core.rp.equipping.EquipmentActionData;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
/**
 * Checks if an item is offered for a trade only undamaged
 *
 * @author madmetzger
 */
public class IsSendUndamagedToTrade implements Validator {

	@Override
	public boolean validate(EquipmentActionData data) {
		if(data.getTargetSlot().getName().equals("trade")) {
			for(Entity e : data.getSourceItems()) {
				if(e instanceof Item) {
					Item i = (Item) e;
					return i.getDeterioration() == 0;
				}
			}
		}
		return true;
	}

}
