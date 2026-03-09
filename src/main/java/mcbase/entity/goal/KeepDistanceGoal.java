package mcbase.entity.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * 让实体与目标点保持固定距离的Goal
 */
public abstract class KeepDistanceGoal extends NavigationGoal {
	protected double speedModifier;

	/**
	 * 要保持的目标距离最小值
	 */
	private double keepDistanceMin;

	/**
	 * 要保持的目标距离最大值
	 */
	private double keepDistanceMax;

	private double minDistanceSqr;
	private double maxDistanceSqr;

	public KeepDistanceGoal(Mob mob, double keepDistanceMin, double keepDistanceMax, double speedModifier, boolean interrupt, int updateTicks) {
		super(mob, interrupt, updateTicks);
		this.speedModifier = speedModifier;
		this.keepDistanceMin = keepDistanceMin;
		this.keepDistanceMax = keepDistanceMax;
	}

	public KeepDistanceGoal(Mob mob, double keepDistanceMin, double keepDistanceMax) {
		this(mob, keepDistanceMin, keepDistanceMax, 1.0, true, DEFAULT_UPDATE_TICKS);// 原速移动
	}

	public double getKeepDistanceMin() {
		return keepDistanceMin;
	}

	public double getKeepDistanceMax() {
		return keepDistanceMax;
	}

	public double getMinDistanceSqr() {
		return minDistanceSqr;
	}

	public double getMaxDistanceSqr() {
		return maxDistanceSqr;
	}

	public void setKeepDistanceMin(double keepDistanceMin, double keepDistanceMax) {
		this.keepDistanceMin = keepDistanceMin;
		this.keepDistanceMax = keepDistanceMax;
		this.minDistanceSqr = keepDistanceMin * keepDistanceMin;
		this.maxDistanceSqr = keepDistanceMax * keepDistanceMax;
	}

	/**
	 * 判断是否处于目标距离范围内
	 */
	public boolean isInRange(Vec3 targetPos) {
		double distanceSqr = this.distanceSqrTo(targetPos);
		return distanceSqr >= this.minDistanceSqr && distanceSqr <= this.maxDistanceSqr;
	}

	@Override
	public boolean isNavigationComplete() {
		return isInRange(this.targetPos);// 仅当距离不符合时继续
	}

	protected double targetDistance(Vec3 targetPos, Vec3 direction, double distance) {
		if (distance < keepDistanceMin)
			return keepDistanceMin;
		else if (distance > keepDistanceMax)
			return keepDistanceMax;
		else
			return distance;
	}

	@Override
	protected void updateMovement(Vec3 targetPos, Vec3 direction, double distance) {
		// 距离过远，实体要靠近。距离过近，实体要远离
		Vec3 moveToPos = targetPos.add(direction.scale(this.targetDistance(targetPos, direction, distance)));
		this.navigation.moveTo(moveToPos.x, moveToPos.y, moveToPos.z, this.speedModifier);
	}
}