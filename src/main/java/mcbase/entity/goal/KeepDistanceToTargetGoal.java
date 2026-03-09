package mcbase.entity.goal;

import net.minecraft.world.entity.Mob;

/**
 * 让实体与瞄准的target实体保持一定距离的Goal
 */
public class KeepDistanceToTargetGoal extends KeepDistanceGoal {

	public KeepDistanceToTargetGoal(Mob mob, double keepDistanceMin, double keepDistanceMax, double speedModifier, boolean interrupt, int updateTicks) {
		super(mob, keepDistanceMin, keepDistanceMax, speedModifier, interrupt, updateTicks);
	}

	public KeepDistanceToTargetGoal(Mob mob, double keepDistanceMin, double keepDistanceMax) {
		super(mob, keepDistanceMin, keepDistanceMax);
	}

	@Override
	protected void updateTargetPos() {
		this.targetPos = this.retrieveTargetPos();// 实时更新目标实体的位置
	}
}
