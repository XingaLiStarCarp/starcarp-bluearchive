package mcbase.entity.goal;

import java.util.EnumSet;

import mcbase.entity.data.EntityData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;

/**
 * 导航通用Goal
 */
public abstract class NavigationGoal extends BaseGoal {
	/**
	 * 是否可以打断其他类型为MOVE的Goal
	 */
	protected boolean interrupt;

	/**
	 * 每多少tick执行一次路径规划
	 */
	protected int updateTicks;

	protected final PathNavigation navigation;

	/**
	 * 当前的目标点
	 */
	protected Vec3 targetPos;

	/**
	 * 上次tick的目标点引用，用于判断是否目标点有更换。<br>
	 * 同一个实体的位置都是固定的引用。<br>
	 */
	private Vec3 prevTargetPos = null;

	/**
	 * 从更改targetPos目标引用直到现在的总tick次数。<br>
	 */
	private int targetPosTotalTicks = 0;

	/**
	 * 默认的每次更新路径规划的时间间隔
	 */
	public static final int DEFAULT_UPDATE_TICKS = 5;

	public NavigationGoal(Mob mob, boolean interrupt, int updateTicks) {
		super(mob);
		this.navigation = mob.getNavigation();
		this.interrupt = interrupt;
		this.updateTicks = updateTicks;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	protected void updateTargetPos() {

	}

	@Override
	public boolean canUse() {
		this.updateTargetPos();// canUse()判断通过之前update()的内容都不会执行，因此判断之前先更新目标
		return this.mob.isAlive()
				&& this.checkTarget()
				&& (this.interrupt || !this.navigation.isInProgress());// 可以打断其他MOVE类型的Goal，或当前navigation在空闲状态
	}

	@Override
	public boolean canContinueToUse() {
		this.updateTargetPos();
		return this.checkTarget() && !isNavigationComplete();
	}

	public boolean checkTarget() {
		return this.targetPos != null;
	}

	/**
	 * 判断目标点引用是否有更新
	 */
	protected void checkTargetUpdate() {
		if (prevTargetPos != targetPos) {
			targetPosTotalTicks = 0;
			prevTargetPos = targetPos;
		} else if (targetPos != null) {
			++targetPosTotalTicks;// 如果是同一个目标位置引用，则更新计数
		}
	}

	public final int getTargetTicks() {
		return targetPosTotalTicks;
	}

	@Override
	public void update() {
		this.checkTargetUpdate();
		// 执行路径规划
		if (this.checkMobTarget()) {
			if (this.mob.tickCount % updateTicks == 0) {
				if (this.isNavigationComplete()) {
					this.navigation.stop();// 满足到达终点的条件，结束导航
				} else {
					updateMovement(targetPos, this.mob.position().subtract(targetPos).normalize());
				}
			}
		} else {
			this.navigation.stop(); // 目标丢失则停止当前Goal
		}
	}

	@Override
	public void exit() {
		this.navigation.stop(); // 停止导航，实体静止
	}

	public final double getCurrentSpeedModifier() {
		return EntityData.getSpeedModifier(navigation);
	}

	public final void setCurrentSpeedModifier(double speedModifier) {
		navigation.setSpeedModifier(speedModifier);
	}

	/**
	 * 获取mob的目标的位置
	 * 
	 * @param entity
	 * @return
	 */
	public final Vec3 retrieveTargetPos(Mob entity) {
		if (entity == null)
			return null;
		LivingEntity target = entity.getTarget();
		if (target == null)
			return null;
		return target.position();
	}

	public final Vec3 retrieveTargetPos() {
		return retrieveTargetPos(this.mob);
	}

	protected abstract boolean isNavigationComplete();

	/**
	 * 更新目标点。<br>
	 * 
	 * @param targetPos 目标点坐标
	 * @param direction 当前mob朝向目标点的方向
	 */
	protected abstract void updateMovement(Vec3 targetPos, Vec3 direction);
}
