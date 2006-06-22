/**
 * Kills the server the hard way without doing a normal shutdown.
 * Do not use it unless the server has already crashed. You should
 * warn connected players to logout if that is still possible.
 * 
 * If the server is started in a loop, it will come up again:
 * while sleep 60; do java -jar marauroa -c marauroa.ini -l; done
 */

if (player != null) {
	Runtime.getRuntime().halt(1);
}