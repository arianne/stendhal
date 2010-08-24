package games.stendhal.server.maps.ados.city;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.OutfitChangerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates the NPCs and portals in Ados City.
 *
 * @author hendrik
 */
public class MakeupArtistNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildFidorea(zone);
	}

	private void buildFidorea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Fidorea") {

			@Override
			protected void createPath() {
				// npc does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, there. Do you need #help with anything?");
				addHelp("I sell masks. If you don't like your mask, you can #return and I will remove it, or you can just wait an hour, until it wears off.");
				
				// this is a hint that one of the items Anna wants is a dress (goblin dress)
				addQuest("Are you looking for toys for Anna? She loves my costumes, perhaps she'd like a #dress to try on. If you already got her one, I guess she'll have to wait till I make more costumes!"); 
				addJob("I am a makeup artist.");
				addReply(
				        "dress",
				        "I read stories that goblins wear a dress as a kind of armor! If you're scared of goblins, like me, maybe you can buy a dress somewhere. ");
				//addReply("offer", "Normally I sell masks. But I ran out of clothes and cannot by new ones until the cloth seller gets back from his search.");
				addGoodbye("Bye, come back soon.");

				final Map<String, Integer> priceList = new HashMap<String, Integer>();
				priceList.put("mask", 20);
				// wears off in 12000 turns = 60 minutes
				// if you change it change her Help message too please
				final OutfitChangerBehaviour behaviour = new OutfitChangerBehaviour(priceList, 12000, "Your mask has worn off.");
				new OutfitChangerAdder().addOutfitChanger(this, behaviour, "buy");
			}
		};

		npc.setEntityClass("woman_008_npc");
		npc.setPosition(20, 13);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setDescription("You see a beautiful looking woman. Her name is Fidorea and she loooves colours.");
		zone.add(npc);
	}
}
