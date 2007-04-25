/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.GameObjects;
import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.stendhal;
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

/**
 * This class is a link between client graphical objects and server attributes
 * objects.<br>
 * You need to extend this object in order to add new elements to the game.
 */
public abstract class RPEntity extends ActiveEntity {

	private boolean showBladeStrike;
	String[] attackSounds={
		"punch-1.wav","punch-2.wav","punch-3.wav"
		,"punch-4.wav","punch-5.wav","punch-6.wav",
		"swingaxe-1.wav","slap-1.wav","arrow-1.wav"};
	public enum Resolution {
		HIT(0), BLOCKED(1), MISSED(2);

		private final int val;

		Resolution(final int val) {
			this.val = val;
		}

		public int get() {
			return val;
		}
	};

	private int atk;

	private int def;

	private int xp;

	private int hp;

	private int adminlevel;

	private int base_hp;

	private float hp_base_hp;

	private int level;

	private boolean eating;

	private boolean poisoned;

	private Sprite nameImage;

	private long combatIconTime;

	private java.util.List<Sprite> floaterSprites;

	private java.util.List<Long> floaterSpritesTimes;

	private RPObject.ID attacking;

	private int mana;

	private int base_mana;

	private boolean fullghostmode;

	private String guild;

	private String clazz;

	/**
	 * Entity we are attacking.
	 * (need to reconsile this with 'attacking')
	 */
	protected RPEntity attackTarget;

	/**
	 * The last entity to attack this entity.
	 */
	protected Entity lastAttacker;

	/**
	 * The type of effect to show.
	 *
	 * These are NOT mutually exclusive
	 * - Maybe use bitmask and apply in priority order.
	 */
	private Resolution resolution;

	private int atkXp;

	private int defXp;

	private int atkItem = -1;

	private int defItem = -1;



	/** Create a new game entity */
	RPEntity()  {
		floaterSprites = new LinkedList<Sprite>();
		floaterSpritesTimes = new LinkedList<Long>();
		attackTarget = null;
		lastAttacker = null;
		clazz = null;
		adminlevel = 0;
	}

	/**
	 * Create/add a "floater" message.
	 *
	 * @param	text		The text message.
	 * @param	color		The color of the text.
	 */
	protected void addFloater(final String text, final Color color) {
		floaterSprites.add(GameScreen.get().createString(text, color));

		floaterSpritesTimes.add(Long.valueOf(System.currentTimeMillis()));
	}


	public boolean isAttacking() {
		return (attacking != null);
	}

	public int getLevel() {
		return level;
	}

	public int getHP() {
		return hp;
	}

	public String getGuild() {
	    return guild;
	}


	/**
	 * Get the nicely formatted entity title.
	 *
	 * This searches the follow attribute order:
	 *	title, name (w/o underscore), class (w/o underscore), type (w/o underscore).
	 *
	 * @return	The title, or <code>null</code> if unknown.
	 */
	public String getTitle() {
		if(title != null) {
			return title;
		} else if(name != null) {
			return name.replace('_', ' ');
		} else if(clazz != null) {
			return clazz.replace('_', ' ');
		} else if(type != null) {
			return type.replace('_', ' ');
		} else {
			return null;
		}
	}


	// Called when entity says text
	public void onTalk(final String text) {
		if (distanceToUser() < 15 * 15) {
			// TODO: Creature circle reference
			nonCreatureClientAddEventLine(text);

			String line = text.replace("|", "");

			// Allow for more characters and cut the text if possible at the
			// nearest space etc. intensifly@gmx.com
			if (line.length() > 84) {
				line = line.substring(0, 84);
				int l = line.lastIndexOf(" ");
				int ln = line.lastIndexOf("-");
				if (ln > l) {
					l = ln;
				}
				ln = line.lastIndexOf(".");
				if (ln > l) {
					l = ln;
				}
				ln = line.lastIndexOf(",");
				if (ln > l) {
					l = ln;
				}
				if (l > 0) {
					line = line.substring(0, l);
				}
				line = line + " ...";
			}
			GameObjects.getInstance().addText(this, /* getTitle()+" says: "+ */
			line, Color.black, true);
		}
	}



	// TODO: this is just an ugly workaround to avoid cyclic dependencies with
	// Creature
	protected void nonCreatureClientAddEventLine(final String text) {
		StendhalUI.get().addEventLine(getTitle(), text);
	}

	// Called when entity listen to text from talker
	public  void onPrivateListen(final String text) {
		Color color = Color.darkGray;
		// TODO: replace this with its own RPEvent type after port to Marauroa 2.0
		if (text.startsWith("Tutorial: ")) {
			color = new Color(172, 0, 172);
		}
		StendhalUI.get().addEventLine(text, color);
		GameObjects.getInstance().addText(this, text.replace("|", ""), color, false);
	}

	// When entity gets healed
	public void onHealed(final int amount) {
		if (distanceToUser() < 15 * 15) {
			addFloater("+" + amount, Color.green);
		}
	}

	// When entity eats food
	public void onEat(final int amount) {
		eating = true;
	}

	public void onEatEnd() {
		eating = false;
	}


	public boolean isBeingStruck() {
		return showBladeStrike;
	}

	public void doneStriking() {
		showBladeStrike = false;
	}

	public boolean isDefending() {
                return (isBeingAttacked()
			&& (System.currentTimeMillis() - combatIconTime < 4 * 300));
	}

	public boolean isEating() {
		return eating;
	}

	public boolean isPoisoned() {
		return poisoned;
	}

	public boolean isAttackingUser() {
		return ((attacking != null)
			&& attacking.equals(User.get().getID()));
	}

	public boolean isBeingAttacked() {
		return (lastAttacker != null);
	}

	public Resolution getResolution() {
		return resolution;
	}


	// When entity is poisoned
	public final void onPoisoned(final int amount) {
		if ((distanceToUser() < 15 * 15)) {
			poisoned = true;

			addFloater("-" + amount, Color.red);

			StendhalUI.get().addEventLine(
			        getTitle() + " is poisoned, losing " + Grammar.quantityplnoun(amount, "health point") + ".",
			        Color.red);
		}
	}

	public void onPoisonEnd() {
		poisoned = false;
	}

	// Called when entity kills another entity
	public void onKill(final Entity killed) {
	}

	// Called when entity is killed by killer
	public void onDeath(final Entity killer) {
		if (killer != null) {
			StendhalUI.get().addEventLine(getTitle() + " has been killed by " + killer.getTitle());
		}

		/*
		 * see
		 * http://sourceforge.net/tracker/index.php?func=detail&aid=1554077&group_id=1111&atid=101111
		 * if (getID().equals(client.getPlayer().getID())) {
		 * client.addEventLine(getTitle() + " has died. " +
		 * Grammar.suffix_s(getTitle()) + " new level is " + getLevel()); }
		 */
	}


	protected void fireAttackEvent(final RPObject base, final RPObject diff) {
		if ((diff == null) && (base == null)) {
			// Remove case

			onStopAttack();

			if (attackTarget != null) {
				attackTarget.onStopAttacked(this);
				attackTarget = null;
			}
		} else if (diff == null) {
			// Modified case
			if (base.has("target")) {
				int risk = (base.has("risk") ? base.getInt("risk") : 0);
				int damage = (base.has("damage") ? base.getInt("damage") : 0);
				int target = base.getInt("target");

				RPObject.ID targetEntityID = new RPObject.ID(target, base.get("zoneid"));

				RPEntity targetEntity = (RPEntity) GameObjects.getInstance().get(targetEntityID);

				if (targetEntity != null) {
					if (targetEntity != attackTarget) {
						onAttack(targetEntity);
						targetEntity.onAttacked(this);
					}

					if (risk == 0) {
						onAttackMissed(targetEntity);
						targetEntity.onMissed(this);
					}

					if ((risk > 0) && (damage == 0)) {
						onAttackBlocked(targetEntity);
						targetEntity.onBlocked(this);
					}

					if ((risk > 0) && (damage > 0)) {
						onAttackDamage(targetEntity, damage);
						targetEntity.onDamaged(this, damage);
					}

					// targetEntity.onAttack(this,risk,damage);
					attackTarget = targetEntity;
				}
				if (base.has("heal")) {
					onHealed(base.getInt("heal"));
				}
			}
		} else {
			// Modified case
			if (diff.has("target") && base.has("target") && !base.get("target").equals(diff.get("target"))) {
				onStopAttack();

				if (attackTarget != null) {
					attackTarget.onStopAttacked(this);
					attackTarget = null;
				}

			}

			if (diff.has("target") || base.has("target")) {
				boolean thereIsEvent = false;

				int risk = 0;
				if (diff.has("risk")) {
					thereIsEvent = true;
					risk = diff.getInt("risk");
				} else if (base.has("risk")) {
					risk = base.getInt("risk");
				} else {
					risk = 0;
				}

				int damage = 0;
				if (diff.has("damage")) {
					thereIsEvent = true;
					damage = diff.getInt("damage");
				} else if (base.has("damage")) {
					damage = base.getInt("damage");
				} else {
					damage = 0;
				}

				int target = -1;
				if (diff.has("target")) {
					target = diff.getInt("target");
				} else if (base.has("target")) {
					target = base.getInt("target");
				}

				RPObject.ID targetEntityID = new RPObject.ID(target, diff.get("zoneid"));

				RPEntity targetEntity = (RPEntity) GameObjects.getInstance().get(targetEntityID);

				if (targetEntity != null) {
					onAttack(targetEntity);
					targetEntity.onAttacked(this);

					if (thereIsEvent) {
						if (risk == 0) {
							onAttackMissed(targetEntity);
							targetEntity.onMissed(this);
						}

						if ((risk > 0) && (damage == 0)) {
							onAttackBlocked(targetEntity);
							targetEntity.onBlocked(this);
						}

						if ((risk > 0) && (damage > 0)) {
							onAttackDamage(targetEntity, damage);
							targetEntity.onDamaged(this, damage);
						}
					}

					attackTarget = targetEntity;
				}
			}
			if (diff.has("heal")) {
				onHealed(diff.getInt("heal"));
			}
		}
	}

	protected void fireAttackEventChangedRemoved(final RPObject base, final RPObject diff) {
		if (diff.has("target")) {
			onStopAttack();

			if (attackTarget != null) {
				attackTarget.onStopAttacked(this);
				attackTarget = null;
			}
		}
	}

	protected void fireKillEvent(final RPObject base,final  RPObject diff) {
		if (diff!=null){
				if (diff.has("hp/base_hp") && (diff.getDouble("hp/base_hp") == 0)) {
					onDeath(lastAttacker);
				}
		}
	}

	protected void fireTalkEvent(final RPObject base,final  RPObject diff) {
		if ((diff == null) && (base == null)) {
			// Remove case
		} else if (diff == null) {
			// First time case.
			if (base.has("text")) {
				String text = base.get("text");
				onTalk(text);
			}

			if (base.has("private_text")) {
				String text = base.get("private_text");
				onPrivateListen(text);
			}
		} else {
			if (diff.has("text")) {
				String text = diff.get("text");
				onTalk(text);
			}

			if (diff.has("private_text")) {
				String text = diff.get("private_text");
				onPrivateListen(text);
			}
		}
	}

	protected void fireHPEvent(final RPObject base, final RPObject diff) {

		if ((diff == null) && (base == null)) {
			// Remove case
		} else if (diff == null) {
			// First time case.
		} else {
			if (diff.has("hp") && base.has("hp")) {
				int healing = diff.getInt("hp") - base.getInt("hp");
				if (healing > 0) {
					onHealed(healing);
				}
			}
			//only for player
			if (diff.has("poisoned")) {
				// To remove the - sign on poison.
				onPoisoned(Math.abs(diff.getInt("poisoned")));
			}

			// only for Players
			if (diff.has("eating")) {
				onEat(0);
			}
		}
	}

	private void fireHPEventChangedRemoved(final RPObject base, final RPObject diff) {
		if (diff.has("poisoned")) {
			onPoisonEnd();
		}

		if (diff.has("eating")) {
			onEatEnd();
		}
	}


	/**
	 * Get the ratio of HP to base HP.
	 *
	 * @return	The HP ratio (0.0 - 1.0).
	 */
	public float getHPRatio() {
		return hp_base_hp;
	}


	/**
	 * Determine if in full ghostmode.
	 *
	 * @return	<code>true</code> is in full ghostmode.
	 */
	public boolean isFullGhostMode() {
		return fullghostmode;
	}


	/**
	 * Get the admin level.
	 *
	 * @return	The admin level.
	 */
	public int getAdminLevel() {
		return adminlevel;
	}


	/** Draws only the Name and hp bar **/
	public void drawHPbar(final GameScreen screen) {
		/*
		 * Don't draw if full ghostmode
		 */
		if(isFullGhostMode()) {
			return;
		}

		if (nameImage != null) {
			screen.draw(nameImage, x, y - 0.5);
		}

		float hpRatio = getHPRatio();

		float r = Math.min((1.0f - hpRatio) * 2.0f, 1.0f);
		float g = Math.min(hpRatio * 2.0f, 1.0f);

		Graphics g2d = screen.expose();
		Point p = screen.convertWorldToScreen(x, y);

		g2d.setColor(Color.gray);
		g2d.fillRect(p.x, p.y - 3, 32, 3);

		g2d.setColor(new Color(r, g, 0.0f));
		g2d.fillRect(p.x, p.y - 3, (int) (hpRatio * 32.0), 3);

		g2d.setColor(Color.black);
		g2d.drawRect(p.x, p.y - 3, 32, 3);
	}

	/** Draws this entity in the screen */
	@Override
	public void draw(final GameScreen screen) {
		super.draw(screen);

		if (!floaterSprites.isEmpty()) {
			// Draw the floaters
			long current = System.currentTimeMillis();

			int i = 0;
			for (Sprite floater : floaterSprites) {
				double tx = x + 0.6 - (floater.getWidth() / (GameScreen.SIZE_UNIT_PIXELS * 2.0f));
				double ty = y - ((current - floaterSpritesTimes.get(i)) / (6.0 * 300.0));
				screen.draw(floater, tx, ty);
				i++;
			}

			if ((floaterSpritesTimes.size() > 0) && (current - floaterSpritesTimes.get(0) > 6 * 300)) {
				floaterSprites.remove(0);
				floaterSpritesTimes.remove(0);
			}
		}
	}

	@Override
	public ActionType defaultAction() {
		return ActionType.LOOK;
	}

	protected void buildOfferedActions(List<String> list) {
		super.buildOfferedActions(list);
		list.add(ActionType.ATTACK.getRepresentation());

		if (!User.isNull()) {
	        if (User.get().isAttacking()) {
				list.add(ActionType.STOP_ATTACK.getRepresentation());
			}
        }
	}

	@Override
	public void onAction(final ActionType at, final String... params) {
		// ActionType at = handleAction(action);
		RPAction rpaction;
		switch (at) {
			case ATTACK:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				int id = getID().getObjectID();
				rpaction.put("target", id);
				at.send(rpaction);
				break;
			case STOP_ATTACK:
				rpaction = new RPAction();
				rpaction.put("type", at.toString());
				rpaction.put("attack", "");
				at.send(rpaction);
				break;
			default:
				super.onAction(at, params);
				break;
		}

	}

	@Override
	public int compareTo(final Entity entity) {
		if (!(entity instanceof RPEntity)) {
			return super.compareTo(entity);
		}

		double dx = getArea().getX() - entity.getArea().getX();
		double dy = getArea().getY() - entity.getArea().getY();

		if (dy < 0) {
			return -1;
		} else if (dy > 0) {
			return 1;
		} else if (dx != 0) {
			return (int) Math.signum(dx);
		} else {
			// Same tile...
			return 0;
		}
	}

	/**
	 * @return Returns the atk.
	 */
	public int getAtk() {
		return atk;
	}

	/**
	 * @return Returns the def.
	 */
	public int getDef() {
		return def;
	}

	/**
	 * @return Returns the xp.
	 */
	public int getXp() {
		return xp;
	}

	/**
	 * @return Returns the base_hp.
	 */
	public int getBase_hp() {
		return base_hp;
	}

	/**
	 * @return the attack xp
	 */
	public int getAtkXp() {
		return atkXp;
	}

	/**
	 * @return the defence xp
	 */
	public int getDefXp() {
		return defXp;
	}

	/**
	 * @return Returns the atk of items
	 */
	public int getAtkItem() {
		return atkItem;
	}

	/**
	 * @return Returns the def of items
	 */
	public int getDefItem() {
		return defItem;
	}

	/**
	 *@return Returns the total mana of a player
	 */
	public int getMana() {
		return mana;
	}

	/**
	 *@return Returns the base mana value
	 */
	public int getBaseMana() {
		return base_mana;
	}

	// When this entity attacks target.
	public void onAttack(final Entity target) {
		attacking = target.getID();
	}

	// When attacker attacks this entity.
	public void onAttacked(final Entity attacker) {
		/*
		 * Could keep track of all attackers, but right now we only
		 * need one of them for onDeath() sake
		 */
		lastAttacker = attacker;
	}

	// When this entity stops attacking
	public void onStopAttack() {
		attacking = null;
	}

	// When attacket stop attacking us
	public void onStopAttacked(final Entity attacker) {
		if (attacker == lastAttacker) {
			lastAttacker = null;
		}
	}

	// When this entity causes damaged to adversary, with damage amount
	public void onAttackDamage(final Entity target, final int damage) {
		showBladeStrike = true;
	}

	// When this entity's attack is blocked by the adversary
	public void onAttackBlocked(final Entity target) {
		showBladeStrike = true;
	}

	// When this entity's attack is missing the adversary
	public void onAttackMissed(final Entity target) {
		showBladeStrike = true;
	}

	// When this entity is damaged by attacker with damage amount
	public void onDamaged(final Entity attacker, final int damage) {
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.HIT;
		 try{

			    SoundMaster.play(attackSounds[Rand.rand(attackSounds.length)], x, y);
			}
			catch(NullPointerException e){

			}

		//playSound("punch-mix", 20, 60, 80);

		addFloater("-" + damage, Color.red);

		boolean showAttackInfoForPlayer = (!User.isNull())
		        && (this.equals(User.get()) || attacker.equals(User.get()));
		showAttackInfoForPlayer = showAttackInfoForPlayer & (!stendhal.FILTER_ATTACK_MESSAGES);

		if (stendhal.SHOW_EVERYONE_ATTACK_INFO || showAttackInfoForPlayer) {
			StendhalUI.get().addEventLine(
			        getTitle() + " suffers " + Grammar.quantityplnoun(damage, "point") + " of damage from "
			                + attacker.getTitle(), Color.RED);
		}
	}

	// When this entity blocks the attack by attacker
	public void onBlocked(final Entity attacker) {
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.BLOCKED;
	}

	// When this entity skip attacker's attack.
	public void onMissed(final Entity attacker) {
		combatIconTime = System.currentTimeMillis();
		resolution = Resolution.MISSED;
	}

	/**
	 * Initialize this entity for an object.
	 *
	 * @param	object		The object.
	 *
	 * @see-also	#release()
	 */
	@Override
	public void initialize(final RPObject object) {
		super.initialize(object);

		fireTalkEvent(object, null);
		fireHPEvent(object, null);
		fireKillEvent(object, null);
		fireAttackEvent(object, null);
	}

	/**
	 * Release this entity. This should clean anything that isn't
	 * automatically released (such as unregister callbacks, cancel
	 * external operations, etc).
	 *
	 * @see-also	#initialize(RPObject)
	 */
	@Override
	public void release() {
		super.release();

		fireTalkEvent(null, null);
		fireHPEvent(null, null);
		fireKillEvent(null, null);
		fireAttackEvent(null, null);
	}


	//
	// RPObjectChangeListener
	//

	/**
	 * The object added/changed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedAdded(final RPObject object, final RPObject changes) {
		super.onChangedAdded(object, changes);

		if (!inAdd) {

			fireTalkEvent(object, changes);
			fireHPEvent(object, changes);
			fireKillEvent(object, changes);
			fireAttackEvent(object, changes);
		}

		if (changes.has("base_hp")) {
			base_hp = changes.getInt("base_hp");
		}
		if (changes.has("hp")) {
			hp = changes.getInt("hp");
		}
		if (changes.has("hp/base_hp")) {
			hp_base_hp = (float) changes.getDouble("hp/base_hp");

			if (hp_base_hp > 1.0f) {
				hp_base_hp = 1.0f;
			} else if (hp_base_hp < 0.0f) {
				hp_base_hp = 0.0f;
			}
		}
		if (changes.has("atk")) {
			atk = changes.getInt("atk");
		}
		if (changes.has("def")) {
			def = changes.getInt("def");
		}
		if (changes.has("xp")) {
			xp = changes.getInt("xp");
		}
		if (changes.has("level")) {
			level = changes.getInt("level");
		}
		if (changes.has("atk_xp")) {
			atkXp = changes.getInt("atk_xp");
		}
		if (changes.has("def_xp")) {
			defXp = changes.getInt("def_xp");
		}
		if (changes.has("atk_item")) {
			atkItem = changes.getInt("atk_item");
		}
		if (changes.has("def_item")) {
			defItem = changes.getInt("def_item");
		}
		if (changes.has("mana")) {
			mana = changes.getInt("mana");
		}
		if (changes.has("base_mana")) {
			base_mana = changes.getInt("base_mana");
		}
		if (changes.has("fullghostmode")) {
		    fullghostmode = (changes.getInt("fullghostmode") != 0);
		}
		if (changes.has("guild")) {
		    guild = changes.get("guild");
		}

		Color nameColor = Color.white;

		if (changes.has("adminlevel")) {
			adminlevel = changes.getInt("adminlevel");

			if (adminlevel >= 800) {
				nameColor = new Color(200, 200, 0);
			} else if (adminlevel >= 400) {
				nameColor = new Color(255, 255, 0);
			} else if (adminlevel > 0) {
				nameColor = new Color(255, 255, 172);
			}
		}

		String titleType = null;

		if (changes.has("title_type")) {
			titleType = changes.get("title_type");
		} else if (object.has("title_type")) {
			titleType = object.get("title_type");
		}

		if (titleType != null) {
			if (titleType.equals("npc")) {
				nameColor = new Color(200, 200, 255);
			} else if (titleType.equals("enemy")) {
				nameColor = new Color(255, 200, 200);
			}
		}

		boolean titleChange = false;

		if (changes.has("clazz")) {
			clazz = changes.get("class");
			titleChange = true;
		}

		if (changes.has("name") || changes.has("title") || changes.has("type")) {
			titleChange = true;
		}

		if(titleChange) {
			nameImage = GameScreen.get().createString(getTitle(), nameColor);
		}

		if (changes.has("xp") && object.has("xp")) {
			if (distanceToUser() < 15 * 15) {
				addFloater("+" + (changes.getInt("xp") - object.getInt("xp")), Color.cyan);

				StendhalUI.get().addEventLine(
				        getTitle() + " earns "
				                + Grammar.quantityplnoun(changes.getInt("xp") - object.getInt("xp"), "experience point")
				                + ".", Color.blue);
			}
		}

		if (changes.has("level") && object.has("level")) {
			if (distanceToUser() < 15 * 15) {
				String text = getTitle() + " reaches Level " + getLevel();

				GameObjects.getInstance().addText(this, GameScreen.get().createString(text, Color.green), 0);
				StendhalUI.get().addEventLine(text, Color.green);
			}
		}
	}


	/**
	 * The object removed attribute(s).
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 */
	@Override
	public void onChangedRemoved(final RPObject object, final RPObject changes) {
		super.onChangedRemoved(object, changes);

		fireHPEventChangedRemoved(object, changes);
		fireAttackEventChangedRemoved(object, changes);
	}
}
