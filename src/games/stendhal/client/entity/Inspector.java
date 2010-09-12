package games.stendhal.client.entity;

import games.stendhal.client.gui.SlotWindow;
import marauroa.common.game.RPSlot;

public interface Inspector {
	SlotWindow inspectMe(IEntity entity, RPSlot content, SlotWindow container, int width, int height);
}
