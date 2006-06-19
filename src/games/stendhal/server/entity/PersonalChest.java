package games.stendhal.server.entity;

import marauroa.common.game.*;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.StendhalScriptSystem;
import java.util.LinkedList;
import java.util.List;

public class PersonalChest extends Chest {
	private Player attending;

	private IRPZone zone;

	private PersonalChest outer;

	public PersonalChest() throws AttributeNotFoundException {
		super();
		outer = this;

		attending = null;

		/** Add a script to copy automatically. */
		StendhalScriptSystem scripts = StendhalScriptSystem.get();
		scripts.addScript(null, new ScriptAction() {
			public void fire() {
				if (attending != null) {
					/* Can be replace when we add Equip event */
					/* Mirror player objects */
					RPSlot content = attending.getSlot("bank");
					content.clear();

					for (RPObject item : getSlot("content")) {
						content.add(item);
					}

					// A hack to allow client update correctly the chest...
					content = getSlot("content");
					content.clear();

					for (RPObject item : attending.getSlot("bank")) {
						content.add(item);
					}

					/* If player is not next to depot clean it. */
					if (!nextto(attending, 0.25)
							|| !zone.has(attending.getID())) {
						content = getSlot("content");
						
						List<RPObject> itemsList=new LinkedList<RPObject>();

						for (RPObject item : getSlot("content")) {
							itemsList.add(item);
						}
						
						content.clear();

						// NOTE: As content.clear() remove the contained flag of the object
						// we need to do this hack.
						RPSlot playerContent = attending.getSlot("bank");
						playerContent.clear();
						
						for(RPObject item: itemsList) {
							playerContent.add(item);							
						}					
						
						close();
						world.modify(outer);
						
						attending = null;
					}
				}
			}
		});
	}

	@Override
	public void onUsed(RPEntity user) {
		Player player = (Player) user;

		zone = world.getRPZone(player.getID());

		if (player.nextto(this, 0.25)) {
			if (isOpen()) {
				close();
			} else {
				attending = player;

				RPSlot content = getSlot("content");
				content.clear();

				for (RPObject item : player.getSlot("bank")) {					
					content.add(item);
				}

				open();
			}

			world.modify(this);
		}
	}
}
