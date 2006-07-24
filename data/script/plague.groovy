if (player != null) {

	// extract position of admin
	myZone = game.getZone(player);
	x = player.getx();
	y = player.gety();
	game.setZone(myZone);

	// select creature
	String creatureClass = "rat";
	if (args.length >= 2) {
		creatureClass = args[1];
	}
	creature = game.getCreature(creatureClass);

	// spawn the specified amout of them
	if (args.length < 1) {
		game.add(creature, x, y);
	} else {
		int k = Integer.parseInt(args[0]);
		if (k < 5) {
			for (dx in -k..k) {
				for (dy in -k..k) {
					game.add(creature, x + dx, y + dy);
				}
			}
		}
	}
}