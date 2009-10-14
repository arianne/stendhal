package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;
/**
 * handles the warnig before the expiring time of an offer
 * 
 * @author madmetzger
 *
 */
public class OfferExpireWarner implements TurnListener {
	
	private final Offer offerToExpire;
	private final StendhalRPZone zone;

	public OfferExpireWarner(Offer o, StendhalRPZone z) {
		this.zone = z;
		this.offerToExpire = o;
	}

	public void onTurnReached(int currentTurn) {
		Market m = TradeCenterZoneConfigurator.getShopFromZone(zone);
		if(m.getOffers().contains(offerToExpire)) {
			StringBuilder builder = new StringBuilder();
			builder.append("Your offer of an ");
			builder.append(offerToExpire.getItem().getName());
			builder.append(" will expire in ");
			builder.append(TradingUtility.DAYS_TO_OFFER_EXPIRING_AFTER_WARNING);
			builder.append(" days.");
			SingletonRepository.getRuleProcessor().getPlayer(offerToExpire.getOfferer()).sendPrivateText(builder.toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 1;
		result = prime * result
				+ ((offerToExpire == null) ? 0 : offerToExpire.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OfferExpireWarner other = (OfferExpireWarner) obj;
		if (offerToExpire == null) {
			if (other.offerToExpire != null) {
				return false;
			}
		} else if (!offerToExpire.equals(other.offerToExpire)) {
			return false;
		}
		return true;
	}
}
