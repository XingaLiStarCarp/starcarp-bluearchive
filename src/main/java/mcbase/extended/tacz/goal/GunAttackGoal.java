package mcbase.extended.tacz.goal;

import mcbase.entity.goal.AttackGoal;
import mcbase.extended.tacz.TaczGunOperator;
import net.minecraft.world.entity.Mob;

public class GunAttackGoal extends AttackGoal {
	protected TaczGunOperator gunOperator;

	public GunAttackGoal(Mob mob, int attackInterval) {
		super(mob, attackInterval);
		gunOperator = new TaczGunOperator(mob);
		this.setBoundDistances(4, 32);
	}

	public GunAttackGoal(Mob mob) {
		this(mob, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	@Override
	public void attack(double currentDistance, int currentBoundLevel) {
		System.err.println("GunAttackGoal currentDistance,   currentBoundLevel  " + currentDistance + "      " + currentBoundLevel);
		switch (currentBoundLevel) {
		case 0:
			gunOperator.craw(false);
			gunOperator.aim(false);
			gunOperator.melee();
			break;
		case 1:
			gunOperator.craw(true);
			gunOperator.aim(true);
			gunOperator.shootAuto(this.mob.getTarget().position());
			break;
		}
	}

	@Override
	public void exit() {
		gunOperator.craw(false);
	}
}
