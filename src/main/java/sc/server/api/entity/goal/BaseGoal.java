package sc.server.api.entity.goal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public abstract class BaseGoal extends Goal {
	protected final Mob mob;
	private int ticks = -1;

	public static final int DEFAULT_TICK_INTERVAL = 1;

	/**
	 * 更新频率
	 */
	protected int interval;

	protected BaseGoal(Mob mob, int interval) {
		this.mob = mob;
		this.interval = interval;
	}

	protected BaseGoal(Mob mob) {
		this(mob, DEFAULT_TICK_INTERVAL);// 默认每次tick都更新
	}

	public void enter() {

	}

	public void update() {

	}

	public void exit() {

	}

	@Override
	public final void start() {
		ticks = 0;
		this.enter();
	}

	@Override
	public final void stop() {
		this.exit();
		ticks = -1;
	}

	@Override
	public final void tick() {
		if (ticks % interval == 0) {
			this.update();
		}
		if (ticks >= 0) {
			++ticks;
		}
	}

	public final Mob getMob() {
		return mob;
	}

	@Override
	public final boolean requiresUpdateEveryTick() {
		return true;
	}

	/**
	 * 获取从Goal执行开始到现在已经的tick数，不包含本次tick。<br>
	 * 
	 * @return
	 */
	public final int getTicks() {
		return ticks;
	}

	/**
	 * 当前是否在执行本Goal
	 * 
	 * @return
	 */
	public final boolean isExecuting() {
		return ticks > 0;
	}

	/**
	 * 检查当前目标是否合法。<br>
	 * 需要目标均非null且必须存活。<br>
	 * 
	 * @return
	 */
	public final boolean checkTarget() {
		LivingEntity target = this.mob.getTarget();
		return target != null && target.isAlive();
	}

	public final double distanceSqrTo(Vec3 targetPos) {
		return this.mob.distanceToSqr(targetPos);
	}

	public final double distanceSqrTo(Entity target) {
		return distanceSqrTo(target.position());
	}

	public final double distanceSqrToTarget() {
		return distanceSqrTo(this.mob.getTarget());
	}
}
