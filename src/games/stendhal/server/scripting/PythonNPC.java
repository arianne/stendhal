package games.stendhal.server.scripting;

import games.stendhal.server.entity.npc.SpeakerNPC;

public abstract class PythonNPC extends SpeakerNPC {
	public PythonNPC() {
		super("PythonNPC");
	}

	@Override
	protected void createPath() {
		// do nothing
	}

	@Override
	protected void createDialog() {
		// do nothing
	}
}
