package games.stendhal.server.entity.mapstuff.sign;

import games.stendhal.common.constants.Events;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

public class OpenOfferPanelEvent extends RPEvent {
	
	public static void generateRPClass() {
		RPClass clazz = new RPClass(Events.OPEN_OFFER_PANEL);
	}
	
	public OpenOfferPanelEvent() {
		super(Events.OPEN_OFFER_PANEL);
	}

}
