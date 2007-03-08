package games.stendhal.client.events;

import games.stendhal.client.entity.*;

public interface AttackEvent {
	// When this entity attacks target.
	public void onAttack(Entity target);

	// When attacker attacks this entity.
	public void onAttacked(Entity attacker);

	// When this entity stops attacking
	public void onStopAttack();

	// When attacker stop attacking us
	public void onStopAttacked(Entity attacker);

	// When this entity causes damaged to adversary, with damage amount
	public void onAttackDamage(Entity target, int damage);

	// When this entity's attack is blocked by the adversary
	public void onAttackBlocked(Entity target);

	// When this entity's attack is missing the adversary
	public void onAttackMissed(Entity target);

	// When this entity is damaged by attacker with damage amount
	public void onDamaged(Entity attacker, int damage);

	// When this entity blocks the attack by attacker
	public void onBlocked(Entity attacker);

	// When this entity skip attacker's attack.
	public void onMissed(Entity attacker);
}
