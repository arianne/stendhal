package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class InspectAction extends AdministrationAction {
	private static final String _INSPECT = "inspect";

	public static void register() {
		CommandCenter.register(_INSPECT, new InspectAction(), 600);
	}

	@Override
	public void perform(final Player player, final RPAction action) {

		final Entity target = getTargetAnyZone(player, action);

		if (target == null) {
			final String text = "Entity not found for action" + action;
			player.sendPrivateText(text);
			return;
		}

		final StringBuilder st = new StringBuilder();

		if (target instanceof RPEntity) {
			final RPEntity inspected = (RPEntity) target;

			// display type and name/title of the entity if they are available

			final String type = inspected.get("type");
			st.append("Inspected ");
			if (type != null) {
				st.append(type);
			} else {
				st.append("entity");
			}
			st.append(" is ");

			String name = inspected.getName();
			if (name == null) {
				name = inspected.getTitle();
			}
			if (name != null) {
				st.append("called \"");
				st.append(name);
				st.append("\"");
			} else {
				st.append("unnamed");
			}

			st.append(" and has the following attributes:");

			// st.append(target.toString());
			// st.append("\n===========================\n");

			st.append("\nID:     " + inspected.getID());
			st.append("\nATK:    " + inspected.getATK() + "("
					+ inspected.getATKXP() + ")");
			st.append("\nDEF:    " + inspected.getDEF() + "("
					+ inspected.getDEFXP() + ")");
			st.append("\nHP:     " + inspected.getHP() + " / "
					+ inspected.getBaseHP());
			st.append("\nXP:     " + inspected.getXP());
			st.append("\nLevel:  " + inspected.getLevel());

			st.append("\nequips");
			for (final RPSlot slot : inspected.slots()) {
				// showing these is either irrelevant, private, or spams too much
				if (slot.getName().equals("!buddy")
					|| slot.getName().equals("!ignore") 
					|| slot.getName().equals("!visited")
					|| slot.getName().equals("!tutorial")
					|| slot.getName().equals("skills")
					|| slot.getName().equals("spells")
					|| slot.getName().equals("!features")
					|| slot.getName().equals("!kills")) {
					continue;
				}
				st.append("\n    Slot " + slot.getName() + ": ");

				if (slot.getName().startsWith("!")) {
					for (final RPObject object : slot) {
						st.append(object);
					}
				} else {
					for (final RPObject object : slot) {
						if (!(object instanceof Item)) {
							continue;
						}

						String item = object.get("type");

						if (object.has("name")) {
							item = object.get("name");
						}
						if (object instanceof StackableItem) {
							st.append("[" + item + " Q="
									+ object.get("quantity") + "], ");
						} else {
							st.append("[" + item + "], ");
						}
					}
				}
			}
		} else {
			st.append("Inspected entity has id " + action.get("target")
					+ " and has attributes:\r\n");
			st.append(target.toString());
		}

		player.sendPrivateText(st.toString());
	}

}
