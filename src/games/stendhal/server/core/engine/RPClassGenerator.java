package games.stendhal.server.core.engine;


import games.stendhal.common.constants.Events;
import games.stendhal.server.core.rp.guilds.Guild;
import games.stendhal.server.core.rp.guilds.GuildMember;
import games.stendhal.server.core.rp.guilds.GuildPermission;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Blood;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.Fire;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.area.WalkBlocker;
import games.stendhal.server.entity.mapstuff.chest.Chest;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.portal.Door;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.sound.LoopedAmbientSoundSource;
import games.stendhal.server.entity.mapstuff.source.FishSource;
import games.stendhal.server.entity.mapstuff.source.GoldSource;
import games.stendhal.server.entity.mapstuff.source.WellSource;
import games.stendhal.server.entity.mapstuff.spawner.GrowingPassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.events.BuddyLoginEvent;
import games.stendhal.server.events.BuddyLogoutEvent;
import games.stendhal.server.events.ExamineEvent;
import games.stendhal.server.events.HealedEvent;
import games.stendhal.server.events.PrivateTextEvent;
import games.stendhal.server.events.ShowItemListEvent;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.events.TextEvent;
import games.stendhal.server.events.TransitionGraphEvent;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

public class RPClassGenerator {
	private static boolean inited = false;

	public void createRPClasses() {
		if (inited) {
			return;
		}
		inited = true;

		if (!RPClass.hasRPClass("entity")) {
			Entity.generateRPClass();
		}

		// Entity sub-classes
		if (!RPClass.hasRPClass("active_entity")) {
			ActiveEntity.generateRPClass();
		}
		if (!RPClass.hasRPClass("area")) {
			AreaEntity.generateRPClass();
		}
		if (!RPClass.hasRPClass("blood")) {
			Blood.generateRPClass();
		}
		if (!RPClass.hasRPClass("chest")) {
			Chest.generateRPClass();
		}
		if (!RPClass.hasRPClass("corpse")) {
			Corpse.generateRPClass();
		}
		if (!RPClass.hasRPClass("door")) {
			Door.generateRPClass();
		}
		if (!RPClass.hasRPClass("fire")) {
			Fire.generateRPClass();
		}
		if (!RPClass.hasRPClass("fish_source")) {
			FishSource.generateRPClass();
		}
		if (!RPClass.hasRPClass("game_board")) {
			GameBoard.generateRPClass();
		}
		if (!RPClass.hasRPClass("gate")) {
			Gate.generateGateRPClass();
		}
		if (!RPClass.hasRPClass("gold_source")) {
			GoldSource.generateRPClass();
		}
		if (!RPClass.hasRPClass("well_source")) {
			WellSource.generateRPClass();
		}
		if (!RPClass.hasRPClass("item")) {
			Item.generateRPClass();
		}
		if (!RPClass.hasRPClass("plant_grower")) {
			PassiveEntityRespawnPoint.generateRPClass();
		}
		if (!RPClass.hasRPClass("portal")) {
			Portal.generateRPClass();
		}
		if (!RPClass.hasRPClass("sign")) {
			Sign.generateRPClass();
		}
		if (!RPClass.hasRPClass("shop_sign")) {
			ShopSign.generateRPClass();
		}
		if (!RPClass.hasRPClass("spell")) {
			Spell.generateRPClass();
		}
		if (!RPClass.hasRPClass("wallblocker")) {
			WalkBlocker.generateRPClass();
		}
		if (!RPClass.hasRPClass("house_portal")) {
			HousePortal.generateRPClass();
		}

		// ActiveEntity sub-classes
		if (!RPClass.hasRPClass("rpentity")) {
			RPEntity.generateRPClass();
		}

		// RPEntity sub-classes
		if (!RPClass.hasRPClass("npc")) {
			NPC.generateRPClass();
		}
		if (!RPClass.hasRPClass("player")) {
			Player.generateRPClass();
		}

		// NPC sub-classes
		if (!RPClass.hasRPClass("creature")) {
			Creature.generateRPClass();
		}

		// Creature sub-classes
		if (!RPClass.hasRPClass("sheep")) {
			Sheep.generateRPClass();
		}
		if (!RPClass.hasRPClass("pet")) {
			Pet.generateRPClass();
		}
		if (!RPClass.hasRPClass("cat")) {
			Cat.generateRPClass();
		}
		if (!RPClass.hasRPClass("baby_dragon")) {
			BabyDragon.generateRPClass();
		}

		// PassiveEntityRespawnPoint sub-class
		if (!RPClass.hasRPClass("ambient_sound_source")) {
			LoopedAmbientSoundSource.generateRPClass();
		}
		if (!RPClass.hasRPClass("growing_entity_spawner")) {
			GrowingPassiveEntityRespawnPoint.generateRPClass();
		}
		if (!RPClass.hasRPClass("food")) {
			SheepFood.generateRPClass();
		}

		// zone storage
		if (!RPClass.hasRPClass("arrest_warrant")) {		
			ArrestWarrant.generateRPClass();
		}
		if (!RPClass.hasRPClass("rented_sign")) {
			RentedSign.generateRPClass();
		}
		if (!RPClass.hasRPClass(Market.MARKET_RPCLASS_NAME)) {
			Market.generateRPClass();
		}
		if (!RPClass.hasRPClass(Offer.OFFER_RPCLASS_NAME)) {
			Offer.generateRPClass();
		}
		if (!RPClass.hasRPClass(Earning.EARNING_RPCLASS_NAME)) {
			Earning.generateRPClass();
		}

		// rpevents
		if (!RPClass.hasRPClass("buddy_login")) {
			BuddyLoginEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("buddy_logout")) {
			BuddyLogoutEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("examine")) {
			ExamineEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("healed")) {
			HealedEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("private_text")) {
			PrivateTextEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("show_item_list")) {
			ShowItemListEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass(Events.SOUND)) {
			SoundEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("transition_graph")) {
			TransitionGraphEvent.generateRPClass();
		}
		if (!RPClass.hasRPClass("text")) {
			TextEvent.generateRPClass();
		}

		// guilds
		if (!RPClass.hasRPClass("guild")) {
			Guild.generateRPClass();
		}
		if (!RPClass.hasRPClass("guild_member")) {
			GuildMember.generateRPClass();
		}
		if (!RPClass.hasRPClass("guild_permission")) {
			GuildPermission.generateRPClass();
		}

		if (!RPClass.hasRPClass("chat")) {
			createChatActionRPClass();
		}

		if (!RPClass.hasRPClass("tell")) {
			createTellActionRPClass();
		}
	}

	private void createTellActionRPClass() {
		RPClass chatAction;
		chatAction = new RPClass("tell");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "target", Type.LONG_STRING);
	}

	private void createChatActionRPClass() {
		RPClass chatAction = new RPClass("chat");
		chatAction.add(DefinitionClass.ATTRIBUTE, "type", Type.STRING);
		chatAction.add(DefinitionClass.ATTRIBUTE, "text", Type.LONG_STRING);
	}

}
