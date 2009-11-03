package games.stendhal.server.entity.player;

/**
 * a special PlayerChatBucket for admins which does not limit the message
 * (postman generates at least twice the amount of manages than the attacker).
 *
 * @author hendrik
 */
public class AdminChatBucket extends PlayerChatBucket {

	@Override
	public boolean checkAndAdd() {
		return true;
	}
	
}
