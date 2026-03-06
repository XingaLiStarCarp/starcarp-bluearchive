package sc.server.api.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class MeleeAttackGoal extends InDistanceGoal {
	public MeleeAttackGoal(Mob mob, double distance, int interval) {
		super(mob, distance, interval);
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}

	public MeleeAttackGoal(Mob mob, double distance) {
		this(mob, distance, 10);
	}

	@Override
	public void update() {
		this.mob.swing(InteractionHand.MAIN_HAND);
		this.mob.doHurtTarget(mob.getTarget());
	}
}
