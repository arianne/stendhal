package games.stendhal.client.events;

import games.stendhal.client.entity.*;

public interface AttackEvent 
  {
  // When this entity attacks target.
  public void onAttack(RPEntity target);
  // When attacker attacks this entity.
  public void onAttacked(RPEntity attacker);
  // When this entity stops attacking 
  public void onStopAttack();
  // When attacket stop attacking us
  public void onStopAttacked(RPEntity attacker);
  
  // When this entity causes damaged to adversary, with damage amount 
  public void onAttackDamage(RPEntity target, int damage); 
  // When this entity's attack is blocked by the adversary 
  public void onAttackBlocked(RPEntity target); 
  // When this entity's attack is missing the adversary 
  public void onAttackMissed(RPEntity target); 

  // When this entity is damaged by attacker with damage amount
  public void onDamaged(RPEntity attacker, int damage);
  // When this entity blocks the attack by attacker
  public void onBlocked(RPEntity attacker);
  // When this entity skip attacker's attack.
  public void onMissed(RPEntity attacker);
  }
