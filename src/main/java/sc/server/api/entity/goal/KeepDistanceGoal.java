package sc.server.api.entity.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

/**
 * 让实体与目标保持固定距离的Goal
 */
public class KeepDistanceGoal extends BaseGoal {
	/**
	 * 要保持的目标距离
	 */
	private double keepDistance;

	/**
	 * 允许的距离误差值，在keepDistance ± toleranceRange范围内实体不移动
	 */
	private double tolerance;

	private double minDistanceSqr;
	private double maxDistanceSqr;

	protected double speedModifier;
	protected boolean interrupt;

	protected final PathNavigation navigation;

	public KeepDistanceGoal(Mob mob, double keepDistance, double tolerance, double speedModifier, boolean interrupt) {
		super(mob);
		this.keepDistance = keepDistance;
		this.tolerance = tolerance;
		this.speedModifier = speedModifier;
		this.navigation = mob.getNavigation();
		this.interrupt = interrupt;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	public KeepDistanceGoal(Mob mob, double keepDistance, double tolerance) {
		this(mob, keepDistance, tolerance, 1.0, true);// 原速移动
	}

	public double getKeepDistance() {
		return keepDistance;
	}

	public double getTolerance() {
		return tolerance;
	}

	public double getMinDistanceSqr() {
		return minDistanceSqr;
	}

	public double getMaxDistanceSqr() {
		return maxDistanceSqr;
	}

	public void setKeepDistance(double keepDistance) {
		this.keepDistance = keepDistance;
		double minDistance = keepDistance - this.tolerance;
		double maxDistance = keepDistance + this.tolerance;
		this.minDistanceSqr = minDistance * minDistance;
		this.maxDistanceSqr = maxDistance * maxDistance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
		double minDistance = this.keepDistance - tolerance;
		double maxDistance = this.keepDistance + tolerance;
		this.minDistanceSqr = minDistance * minDistance;
		this.maxDistanceSqr = maxDistance * maxDistance;
	}

	@Override
	public boolean canUse() {
		return this.mob.isAlive()
				&& this.checkTarget()
				&& (interrupt || !this.navigation.isInProgress());// 可以打断其他MOVE类型的Goal，或当前navigation在空闲状态
	}

	@Override
	public void enter() {
		updateMovement();
	}

	@Override
	public void update() {
		if (this.checkTarget()) {
			if (this.mob.tickCount % 5 == 0) {
				updateMovement();
			}
		} else {
			this.navigation.stop(); // 目标丢失则停止当前Goal
		}
	}

	@Override
	public boolean canContinueToUse() {
		return this.checkTarget() && !isDistanceValid(); // 仅当距离不符合时继续
	}

	@Override
	public void exit() {
		this.navigation.stop(); // 停止导航，实体静止
	}

	/**
	 * 判断是否处于目标距离范围内
	 */
	public boolean isDistanceValid(Vec3 targetPos) {
		double distanceSqr = this.distanceSqrTo(targetPos);
		return distanceSqr >= this.minDistanceSqr && distanceSqr <= this.maxDistanceSqr;
	}

	public boolean isDistanceValid(Entity target) {
		return isDistanceValid(target.position());
	}

	public boolean isDistanceValid() {
		return isDistanceValid(this.mob.getTarget());
	}

	private void updateMovement() {
		double distanceSqr = distanceSqrToTarget();
		if (distanceSqr > maxDistanceSqr || distanceSqr < minDistanceSqr) {
			// 距离过远，实体要靠近。距离过近，实体要远离
			Vec3 targetPos = this.mob.getTarget().position();
			Vec3 direction = this.mob.position().subtract(targetPos).normalize();
			Vec3 moveToPos = targetPos.add(direction.scale(this.keepDistance));
			this.navigation.moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speedModifier);
		} else {
			this.navigation.stop();
		}
	}
}