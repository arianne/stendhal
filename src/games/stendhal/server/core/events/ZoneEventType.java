package games.stendhal.server.core.events;

/**
 * Event types used in the new Zone notifier
 * 
 * @author kymara (based on TutorialEventType by hendrik)
 */
public enum ZoneEventType {

	VISIT_SUB1_SEMOS_CATACOMBS(
			"Screams and wails fill the air of these ghastly catacombs ..."),
	VISIT_SUB2_SEMOS_CATACOMBS(
			"Your sense of foreboding grows as you enter deeper to the catacombs. You spy some lethal looking spikes and vow to be careful of them."),
	VISIT_KIKAREUKIN_CAVE(
			"Your head spins as the portal lifts you high into the air, past clouds and birds. You're sucked towards a floating group of islands. You're pulled through layers of rock and finally you land in a vast network of caves."),
	VISIT_KIKAREUKIN_ISLANDS(
			"After your long journey through the caves of the floating islands you finally reach fresh air again, and marvel at the clouds around you, and how high up you are."),
	VISIT_KANMARARN_PRISON(
			"PRISON BREAKOUT! You've stumbled into a heist. It looks like the duergars have come to break their leaders and heroes free from their imprisonment by the dwarves."),
	VISIT_IMPERIAL_CAVES(
			"Commands and orders are heard from afar. You can only imagine you must be approaching an army of some sort. Worryingly, you can also hear some very very heavy footsteps."),
	VISIT_MAGIC_CITY_N(
			"Your skin prickles as you explore further. There is definitely magic here."),
	VISIT_MAGIC_CITY(
			"You now sense a strong magical presence. Perhaps sorcerors are nearby, or some strong enchantments?"),
	VISIT_DREAMSCAPE(
			"You start to hallucinate. You think you're in a new land, somewhere unknown, with strange, terrible creatures and fantastic sights."),
	VISIT_SEMOS_CAVES(
			"The ground in this cave trembles from the footfalls of GIANTS! The weak should explore no further here. Turn around and run away!"),
	VISIT_ADOS_CASTLE(
			"You sense that great atrocities have happened here. The castle must be overrun with evil creatures, as the sounds of their last victims ring in your ears. It may be wise to stay away.");

	private String message;

	/**
	 * create a new ZoneEventType
	 * 
	 * @param message
	 *            human readable message
	 */
	private ZoneEventType(String message) {
		this.message = message;
	}

	/**
	 * get the descriptive message
	 * 
	 * @return message
	 */
	String getMessage() {
		return message;
	}
}
