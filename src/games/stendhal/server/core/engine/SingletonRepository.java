package games.stendhal.server.core.engine;

import games.stendhal.server.core.events.LoginNotifier;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.events.achievements.AchievementNotifier;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.guilds.GuildList;
import games.stendhal.server.core.rp.guilds.GuildPermissionList;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.GagManager;
import games.stendhal.server.entity.player.Jail;
import games.stendhal.server.entity.slot.BankAccessorManager;
import games.stendhal.server.maps.athor.ship.AthorFerry;
import marauroa.server.db.TransactionPool;


/**
 * Takes an instance of every 'singleton' created and provides setters and getters.
 * <p>
 * 
 * 
 * It is not meant to be a high sophisticated class.
 * 
 * Just one step into getting rid of the singleton hell;
 * 
 * 
 * 
 * 
 * 
 * @author astridEmma
 *
 */
public class SingletonRepository {
	
	private static EntityManager entityManager;
	private static Jail jailInstance;

	/**
	 * @return the actual StendhalRPRuleProcessor instance
	 */
	public static StendhalRPRuleProcessor getRuleProcessor() {
		return StendhalRPRuleProcessor.get();
	}

	/**
	 * @return the actual StendhalRPWorld instance
	 */
	public static StendhalRPWorld getRPWorld() {
		return StendhalRPWorld.get();
	}

	/**
	 * @return the actual TurnNotifier instance
	 */
	public static TurnNotifier getTurnNotifier() {
		return TurnNotifier.get();
	}

	/**
	 * @return the actual GagManager instance
	 */
	public static GagManager getGagManager() {
		return GagManager.get();
	}

	/**
	 * @return the actual LoginNotifier instance
	 */
	public static LoginNotifier getLoginNotifier() {
		return LoginNotifier.get();
	}

	/**
	 * @return the actual Jail instance
	 */
	public static Jail getJail() {
		return jailInstance;
	}
	
	/**
	 * Sets the Jail instance
	 * @param jail
	 */
	public static void setJail(final Jail jail) {
		jailInstance = jail;
		
	}

	/**
	 * @return the actual NPCList instance
	 */
	public static NPCList getNPCList() {
		return NPCList.get();
	}

	/**
	 * @return the actual StendhalQuestSystem instance
	 */
	public static StendhalQuestSystem getStendhalQuestSystem() {
		return StendhalQuestSystem.get();
	}
	
	/**
	 * @return the actual ProducerRegister instance
	 */
	public static ProducerRegister getProducerRegister() {
		return ProducerRegister.get();
	}

	/**
	 * @return the actual TransactionPool instance
	 */
	public static TransactionPool getTransactionPool() {
		return TransactionPool.get();
	}

	public static GuildPermissionList getGuildPermissionList() {
		return GuildPermissionList.get();
	}

	/**
	 * @return the actual ShopList instance
	 */
	public static ShopList getShopList() {
		return ShopList.get();
	}

	public static GuildList getGuildList() {
		return GuildList.get();
	}

	/**
	 * @return the BankAccessorManager instance
	 */
	public static BankAccessorManager getBankAccessorManager() {
		return BankAccessorManager.get();
	}

	/**
	 * @return the AthorFerry instance
	 */
	public static AthorFerry getAthorFerry() {
		return AthorFerry.get();
	}

	/**
	 * @return the AchievementNotifier instance
	 */
	public static AchievementNotifier getAchievementNotifier() {
		return AchievementNotifier.get();
	}

	/**
	 * @return the actual EntityManager instance
	 */
	public static EntityManager getEntityManager() {
		if (entityManager == null) {
				entityManager = new DefaultEntityManager();
			}
			return entityManager;
	}

	/**
	 * Sets a new EntityManager 
	 * @param entityManager
	 */
	static void setEntityManager(final EntityManager entityManager) {
		SingletonRepository.entityManager = entityManager;
	}

	

	

}
