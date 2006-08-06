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
	if (creature == null) {
		player.sendPrivateText("No such creature");
	} else {

		// spawn the specified amout of them
		if (args.length < 1) {
			game.add(creature, x, y);
		} else {
			int max = Integer.parseInt(args[0]);
			if (max > 100) {
				player.sendPrivateText("Too many monsters");
			} else {

				int k = (int) (-0.5f + Math.sqrt(0.25f + 0.25f * (max - 1))) + 1
				int counter = 0;
				for (dx in -k..k) {
					for (dy in -k..k) {
						if ((dx != 0) || (dy != 0)) {
							game.add(creature, x + dx, y + dy);
						}
						counter++;
						if (counter == max) {
							break;
						}
					}
					if (counter == max) {
						break;
					}
				}
			}
		}
	}
}

/*
1: 3+3+1+1     = 8   = (1*2+1)*(1*2+1) - 1
2: 5+5+2*3+2*3 = 24  = (2*2+1)*(2*2+1) - 1
3:             = 48  = (3*2+1)*(3*2+1) - 1 
4:             = 80  = (4*2+1)*(4*2+1) - 1 


(2x+1) * (2x+1) - 1 = n     | (a+b)²  =  a² + 2ab + b²
4x^2  + 4x + 1 - 1  = n     | -n
4x^2  + 4x - n = 0          |  / 4
 x^2  + 1x - 1/4n = 0       | pq

x = -1/2 +/- sqrt (1/4 + 1/4n)
*/
