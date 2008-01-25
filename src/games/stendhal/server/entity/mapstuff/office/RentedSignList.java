package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.awt.Shape;

/**
 * A list of RentedSign as frontend for the zone storage.
 * 
 * @author hendrik
 */
public class RentedSignList extends StoreableEntityList<RentedSign> {
	private static final long EXPIRE_TIMEOUT = MathHelper.MILLISENCONDS_IN_ONE_DAY;

	/**
	 * Creates a new RentedSignList.
	 * 
	 * @param zone  zone to store the rented signs in
	 * @param shape 
	 */
	public RentedSignList(StendhalRPZone zone, Shape shape) {
		super(zone, shape, RentedSign.class);
		setupTurnNotifier(60 * 60);
	}

	@Override
    public String getName(RentedSign rentedSign) {
		return rentedSign.getRenter();
    }

	@Override
	protected boolean shouldExpire(RentedSign entity) {
		return entity.getTimestamp() + EXPIRE_TIMEOUT < System.currentTimeMillis();
	}

}
