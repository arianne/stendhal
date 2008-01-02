package games.stendhal.server.entity.mapstuff.office;

import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;
import games.stendhal.server.entity.mapstuff.sign.Sign;


/**
 * a sign rented by a player for a fixed amount of time
 *
 * @author hendrik
 */
public class RentedSign extends Sign implements StoreableEntity {
	public static final String RPCLASS_NAME = "rented_sign";
	private static final String OWNER = "owner";
	private static final String TIMESTAMP = "timestamp";

	public static void generateRPClass() {
		RPClass clazz = new RPClass(RPCLASS_NAME);
		clazz.isA("sign");
		clazz.addAttribute(OWNER, Type.STRING, Definition.HIDDEN);
		clazz.addAttribute(TIMESTAMP, Type.FLOAT, Definition.HIDDEN);
	}

	/**
	 * Creates a RentedSign.
	 */
	public RentedSign() {
	    super();
    }

	/**
	 * Creates a RentedSign based on an existing RPObject. This is just for loading 
	 * a sign from the database, use the other constructors.
	 */
	public RentedSign(RPObject rpobject) {
	    super(rpobject);
    }

}
