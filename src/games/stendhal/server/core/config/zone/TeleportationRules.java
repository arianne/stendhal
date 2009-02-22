package games.stendhal.server.core.config.zone;

public class TeleportationRules {
	private boolean isInAllowed = true;

	private boolean isOutAllowed = true;

	public void disAllowIn() {
		this.isInAllowed = false;
	}

	public boolean isInAllowed() {
		return isInAllowed;
	}

	public void disallowOut() {
		this.isOutAllowed = false;
	}

	public boolean isOutAllowed() {
		return isOutAllowed;
	}
}
