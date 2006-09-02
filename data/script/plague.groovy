if (player != null) {

	// extract position of admin
	myZone = game.getZone(player);
	x = player.getX();
	y = player.getY();
	game.setZone(myZone);

	// select creature
	String creatureClass = "rat";
	if (args.length >= 2) {
		creatureClass = args[1];
	}
	creature = game.getCreature(creatureClass);
	if (creature == null) {
		player.sendPrivateText("No such creature");
	} else {

		// spawn the specified amout of them
		if (args.length < 1) {
			game.add(creature, x, y);
		} else {
			int k = Integer.parseInt(args[0]);
			if (k < 3) {
				for (dx in -k..k) {
					for (dy in -k..k) {
						if ((dx != 0) || (dy != 0)) {
							game.add(creature, x + dx, y + dy + 1);
						}
					}
				}
			}
		}
	}
}