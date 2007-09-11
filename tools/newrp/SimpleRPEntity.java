package newrp;


/**
 * This is a simpler, human understandable implementation of the RP.
 * 
 * @author miguel
 *
 */
public class SimpleRPEntity extends RPEntity {	
    public SimpleRPEntity(Race race, School type, Sex sex) {
	    super(race, type, sex);
    }

    @Override
	protected int getAttackRate() {
    	/*
    	 * REASONING: 
    	 *   The more the weapon weight the slower.
    	 *   The stronger we are the faster.    
    	 */
    	int rate=20+weapon.weight-strengh;
    	
    	if(rate<=0) {
    		rate=1;    	
    	}
    	
    	return rate;
    }


	protected int getDodgeRate() {
		/*
		 * REASONING:
		 *   The more our inventory weight the less often we can dodge.
		 */
		int weaponWeight=0;
		int armorWeight=0;
		int shieldWeight=0;
		
		if(weapon!=null) {
			weaponWeight=weapon.weight;
		}
		if(armor!=null) {
			armorWeight=armor.weight;
		}
		if(shield!=null) {
			shieldWeight=shield.weight;
		}
		
		int rate= 10+weaponWeight+armorWeight+shieldWeight - agility;

		if(rate<=0) {
    		rate=1;    	
    	}
    	
    	return rate;
	}

	protected int getShieldRate() {
		/*
		 * REASONING:
		 *   The more our shield weight the less often we can use it.
		 *   The more dextrexity we have the more often we can use it.
		 */
		int rate= 10+shield.weight - dextrexity;

		if(rate<=0) {
    		rate=1;    	
    	}
    	
    	return rate;
	}

	
    protected RollResult doHit(float attitude) {
    	/*
    	 * REASONING:
    	 *   The stronger and the more dextrexity we are the simpler is to do a hit.
    	 */
    	if(Dice.r1D20()<(strengh+dextrexity)*attitude) {
    		return RollResult.SUCCESS; 
    	} else {
    		return RollResult.FAILURE;
    	}
    }

	protected RollResult doDodge(float attitude) {
    	/*
    	 * REASONING:
    	 *   The stronger and the more agile we are the simpler is to dodge a hit.
    	 *   Dodge a hit is harder than doing a hit. ( Twice as harder )
    	 */
    	if(Dice.rND20(2)<(strengh+agility)*attitude) {
    		return RollResult.SUCCESS; 
    	} else {
    		return RollResult.FAILURE;
    	}
	}

	protected RollResult doCast(Spell spell, float attitude) {		
    	/*
    	 * REASONING:
    	 *   The more inteligent the simpler.
    	 *   The harder the spell the more hard to cast it.
    	 */
    	if(Dice.r1D20()+(spell.level-level)<(inteligence+wisdom)*attitude) {
    		return RollResult.SUCCESS; 
    	} else {
    		return RollResult.FAILURE;
    	}
	}

	private int calculateAbsorb(DamageEffect damage, int rulingAttribute) {
	    int base=damage.amount+damage.amount/5*(rulingAttribute-10);
	    
	    /*
	     * We calculate a min and max values and apply bonus for level.
	     */
	    int min=base+base/3*level;
	    int max=base+base/2*level;
	    
	    int absorbed=Dice.between(min, max);
	    return absorbed;
    }

	protected int shieldAbsorb(DamageType type, int amount, float attitude) {
		for (Effect effect : shield.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage=(DamageEffect)effect;
				
				int absorbed = calculateAbsorb(damage,dextrexity);
				amount=amount-absorbed;
			}				
		}
		
		return amount;
	}

	protected int weaponAbsorb(DamageType type, int amount, float attitude) {
		for (Effect effect : weapon.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage=(DamageEffect)effect;
				
				int absorbed = calculateAbsorb(damage,strengh);
				amount=amount-absorbed;
			}				
		}
		
		return amount;
	}

	protected int armorAbsorb(DamageType type, int amount) {
		for (Effect effect : armor.protect) {
			if (effect instanceof DamageEffect) {
				DamageEffect damage=(DamageEffect)effect;
				
				/*
				 * Armor is a passive element and so is not ruled by any attribute.
				 */
				int absorbed = calculateAbsorb(damage,10);
				amount=amount-absorbed;
			}				
		}
		
		return amount;
	}
}
