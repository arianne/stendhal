package games.stendhal.server.entity.mapstuff.office;

import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.Definition.Type;


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
	 *
	 * @param owner player who rented this sign
	 * @param text text to display on this sign
	 */
	public RentedSign(Player owner, String text) {
		setRPClass(RPCLASS_NAME);
		store();
		put(OWNER, owner.getName());
		put(TIMESTAMP, System.currentTimeMillis());
		super.setText(text);
	}

	/**
	 * Creates a RentedSign based on an existing RPObject. This is just for loading 
	 * a sign from the database, use the other constructors.
	 */
	public RentedSign(RPObject rpobject) {
	    super(rpobject);
    }

}
