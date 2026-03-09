package mcbase.entity.goal;

import net.minecraft.world.entity.Mob;

/**
 * 远程攻击目标
 */
public class RangedAttackGoal extends AttackGoal {
	public RangedAttackGoal(Mob mob, double distance, int attackInterval) {
		super(mob, distance, attackInterval);
	}

	public RangedAttackGoal(Mob mob, double distance) {
		super(mob, distance);
	}

	@Override
	public void attack() {
	}
}
