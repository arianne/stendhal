package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

public class OfferRemover implements TurnListener {

	private Offer offerToRemove;
	private StendhalRPZone zone;

	public OfferRemover(Offer o, StendhalRPZone zone) {
		this.offerToRemove = o;
		this.zone = zone;
	}

	public void onTurnReached(int currentTurn) {
		Market m = TradeCenterZoneConfigurator.getShopFromZone(zone);
		if(m.getExpiredOffers().contains(offerToRemove)) {
			m.removeExpiredOffer(offerToRemove);
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of ");
			builder.append(offerToRemove.getItem().getName());
			builder.append(" has been removed permanently from the market.");
			offerToRemove.getOfferer().sendPrivateText(builder.toString());
			zone.add(offerToRemove, true);
		}
	}

}
