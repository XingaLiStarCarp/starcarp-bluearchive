package mcbase.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;

public class MeleeAttackGoal extends AttackGoal {
	public MeleeAttackGoal(Mob mob, int attackInterval) {
		super(mob, attackInterval);
		this.faceToleranceAngle = 120.0f;
	}

	public MeleeAttackGoal(Mob mob) {
		this(mob, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	public void attack(double currentDistance, int currentBoundLevel) {
		this.mob.swing(InteractionHand.MAIN_HAND);
		this.mob.doHurtTarget(mob.getTarget());
	}
}
