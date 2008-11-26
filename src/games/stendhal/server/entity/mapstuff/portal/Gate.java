package games.stendhal.server.entity.mapstuff.portal;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

import org.apache.log4j.Logger;

import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

public class Gate extends Entity implements UseListener {

	
	private static final String HORIZONTAL = "h";
	private static final String VERTICAL = "v";
	private static final String ORIENTATION = "orientation";

	public static void generateGateRPClass() {
		if (!RPClass.hasRPClass("gate")) {
			RPClass gate = new RPClass("gate");
			gate.isA("entity");
			gate.addAttribute(ORIENTATION, Type.STRING);
		}
	}
	
	private boolean isOpen;

	public Gate() {
		
		setRPClass("gate");
		put("type", "gate");
		put(ORIENTATION, VERTICAL);
		setOpen(false);
		Logger.getLogger(Gate.class).info("gatecreated");
	}

	public void open() {
		setOpen(true);
	}

	public boolean isOpen() {

		return isOpen;
	}

	public void close() {
		setOpen(false);

	}

	public boolean onUsed(final RPEntity user) {
		Logger.getLogger(Gate.class).info("use-called");
		if (this.nextTo(user)) {
			setOpen(!isOpen());
			return true;
		}
		return false;
	}

	private void setOpen(final boolean b) {
		if (b) {
			setResistance(0);
		} else {
			setResistance(100);
		}
		isOpen = b;
		notifyWorldAboutChanges();

	}

}
