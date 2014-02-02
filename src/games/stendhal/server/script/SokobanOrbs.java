package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.useable.ViewChangeEntity;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

public class SokobanOrbs extends ScriptImpl {
	private List<Entity> entities = new LinkedList<Entity>();

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		
		Entity entity = new ViewChangeEntity(36, 115);
		entity.setPosition(26, 122);
		entity.put("width", 5);
		SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2").add(entity);

		entity = new ViewChangeEntity(36, 115);
		entity.setPosition(31, 122);
		entity.put("width", 5);
		SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2").add(entity);

		entity = new ViewChangeEntity(36, 115);
		entity.setPosition(36, 122);
		entity.put("width", 5);
		SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2").add(entity);

		entity = new ViewChangeEntity(36, 115);
		entity.setPosition(40, 122);
		entity.put("width", 5);
		SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2").add(entity);

		entities.add(entity);
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		for (Entity entity : entities) {
			SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2").remove(entity);
		}
		
	}

	


}
