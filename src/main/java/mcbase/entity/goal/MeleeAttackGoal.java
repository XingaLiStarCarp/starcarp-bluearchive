package mcbase.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;

public class MeleeAttackGoal extends AttackGoal {
	public MeleeAttackGoal(Mob mob, double distance, int attackInterval) {
		super(mob, distance, attackInterval);
		this.faceToleranceAngle = 120.0f;
	}

	public MeleeAttackGoal(Mob mob, double distance) {
		this(mob, distance, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	public void attack() {
		this.mob.swing(InteractionHand.MAIN_HAND);
		this.mob.doHurtTarget(mob.getTarget());
	}
}
