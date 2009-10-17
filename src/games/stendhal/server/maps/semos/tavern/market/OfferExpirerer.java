package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
/**
 * handles the expiring time of an offer
 * 
 * @author madmetzger
 *
 */
public class OfferExpirerer implements TurnListener {
	
	private final Offer offerToExpire;
	private final StendhalRPZone zone;

	public OfferExpirerer(final Offer o, final StendhalRPZone zone) {
		this.offerToExpire = o;
		this.zone = zone;
	}

	public void onTurnReached(final int currentTurn) {
		Market m = TradeCenterZoneConfigurator.getShopFromZone(zone);
		if(m.getOffers().contains(offerToExpire)) {
			m.expireOffer(offerToExpire);
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of ");
			builder.append(offerToExpire.getItem().getName());
			builder.append("has expired. You have ");
			builder.append(TradingUtility.DAYS_TO_OFFER_EXPIRING_AFTER_WARNING);
			builder.append( "days left to get the item back or prolongue the offer.");
			SingletonRepository.getRuleProcessor().getPlayer(offerToExpire.getOfferer()).sendPrivateText(builder.toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((offerToExpire == null) ? 0 : offerToExpire.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OfferExpirerer other = (OfferExpirerer) obj;
		if (offerToExpire == null) {
			if (other.offerToExpire != null) {
				return false;
			}
		} else if (!offerToExpire.equals(other.offerToExpire)) {
			return false;
		}
		if (zone == null) {
			if (other.zone != null) {
				return false;
			}
		} else if (!zone.equals(other.zone)) {
			return false;
		}
		return true;
	}
}
