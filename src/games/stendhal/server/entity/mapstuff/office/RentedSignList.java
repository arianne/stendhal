package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.core.engine.StendhalRPZone;

import java.awt.Shape;

/**
 * a list of RentedSign as frontend for the the zone storage
 * 
 * @author hendrik
 */
public class RentedSignList extends StoreableEntityList<RentedSign> {

	/**
	 * creates a new RentedSignList
	 * 
	 * @param zone  zone to store the rented signs in
	 */
	public RentedSignList(StendhalRPZone zone, Shape shape) {
		super(zone, shape, RentedSign.class);
	}

	@Override
    public String getName(RentedSign rentedSign) {
		return rentedSign.getRenter();
    }
}
