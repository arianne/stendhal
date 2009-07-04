package games.stendhal.server.core.engine;

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
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.RentedSign;
import games.stendhal.server.entity.mapstuff.portal.Door;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.mapstuff.portal.HousePortal;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.source.FishSource;
import games.stendhal.server.entity.mapstuff.source.GoldSource;
import games.stendhal.server.entity.mapstuff.source.WellSource;
import games.stendhal.server.entity.mapstuff.spawner.GrowingPassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.Spell;
import games.stendhal.server.events.BuddyLoginEvent;
import games.stendhal.server.events.BuddyLogoutEvent;
import games.stendhal.server.events.DamagedEvent;
import games.stendhal.server.events.ExamineEvent;
import games.stendhal.server.events.HealedEvent;
import games.stendhal.server.events.PrivateTextEvent;
import games.stendhal.server.events.TextEvent;
import games.stendhal.server.trade.Earning;
import games.stendhal.server.trade.Offer;
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
		Entity.generateRPClass();

		// Entity sub-classes
		ActiveEntity.generateRPClass();
		AreaEntity.generateRPClass();
		Blood.generateRPClass();
		Chest.generateRPClass();
		Corpse.generateRPClass();
		Door.generateRPClass();
		Fire.generateRPClass();
		FishSource.generateRPClass();
		Gate.generateGateRPClass();
		GoldSource.generateRPClass();
		WellSource.generateRPClass();
		Item.generateRPClass();
		PassiveEntityRespawnPoint.generateRPClass();
		Portal.generateRPClass();
		Sign.generateRPClass();
		Spell.generateRPClass();
		WalkBlocker.generateRPClass();
		HousePortal.generateRPClass();

		// ActiveEntity sub-classes
		RPEntity.generateRPClass();

		// RPEntity sub-classes
		NPC.generateRPClass();
		Player.generateRPClass();

		// NPC sub-classes
		Creature.generateRPClass();

		// Creature sub-classes
		Sheep.generateRPClass();
		Pet.generateRPClass();
		Cat.generateRPClass();
		BabyDragon.generateRPClass();

		// PassiveEntityRespawnPoint sub-class
		GrowingPassiveEntityRespawnPoint.generateRPClass();
		SheepFood.generateRPClass();

		// zone storage
		ArrestWarrant.generateRPClass();
		RentedSign.generateRPClass();
		Offer.generateRPClass();
		Earning.generateRPClass();

		// rpevents
		BuddyLoginEvent.generateRPClass();
		BuddyLogoutEvent.generateRPClass();
		DamagedEvent.generateRPClass();
		ExamineEvent.generateRPClass();
		HealedEvent.generateRPClass();
		PrivateTextEvent.generateRPClass();
		TextEvent.generateRPClass();

		//guilds
		Guild.generateRPClass();
		GuildMember.generateRPClass();
		GuildPermission.generateRPClass();

		createChatActionRPClass();

		createTellActionRPClass();
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
