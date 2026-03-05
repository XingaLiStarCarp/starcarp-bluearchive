package sc.server.api.entity.goal;

import java.util.EnumSet;
import java.util.function.Predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

/**
 * 瞄准实体的目标。<br>
 * 如果需要近战攻击瞄准的目标，则需要配合使用MeleeAttackGoal。<br>
 */
public class NearestTargetGoal extends TargetGoal {
	public static final int DEFAULT_RANDOM_INTERVAL = 10;

	protected double searchHeight = 4.0;
	protected int randomInterval;
	protected LivingEntity target;
	protected TargetingConditions targetConditions;
	protected Predicate<LivingEntity> attackCondition;

	/**
	 * 攻击行为
	 * 
	 * @param mob
	 * @param randomInterval
	 * @param musetSee        是否只能攻击看见的目标
	 * @param mustReach       是否必须要移动到目标面前才能进行攻击
	 * @param targetCondition 筛选攻击目标
	 * @param attackCondition 攻击执行的条件
	 */
	public NearestTargetGoal(Mob mob, int randomInterval, boolean musetSee, boolean mustReach, Predicate<LivingEntity> targetCondition, Predicate<LivingEntity> attackCondition) {
		super(mob, musetSee, mustReach);
		this.randomInterval = reducedTickDelay(randomInterval);
		this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetCondition);
		this.attackCondition = attackCondition;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	public NearestTargetGoal(Mob mob, boolean mustSee, boolean mustReach, Predicate<LivingEntity> targetCondition, Predicate<LivingEntity> attackCondition) {
		this(mob, DEFAULT_RANDOM_INTERVAL, mustSee, mustReach, targetCondition, attackCondition);
	}

	public NearestTargetGoal(Mob mob, boolean mustSee, boolean mustReach, Predicate<LivingEntity> targetCondition) {
		this(mob, mustSee, mustReach, targetCondition, (Predicate<LivingEntity>) null);
	}

	/**
	 * 设置搜索的高度范围，范围是以实体为中心点上下等高
	 * 
	 * @param searchHeight
	 * @return
	 */
	public NearestTargetGoal setSearchHeight(double searchHeight) {
		this.searchHeight = searchHeight;
		return this;
	}

	protected AABB getTargetSearchArea(double range) {
		return this.mob.getBoundingBox().inflate(range, searchHeight, range);
	}

	protected LivingEntity findTarget() {
		return this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(LivingEntity.class, this.getTargetSearchArea(this.getFollowDistance()), (entity) -> {
			return true;
		}), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
	}

	@Override
	public boolean canUse() {
		if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
			return false;
		} else {
			return (this.target = this.findTarget()) != null && (this.attackCondition == null ? true : this.attackCondition.test(this.mob));
		}
	}

	@Override
	public void start() {
		this.mob.setTarget(this.target);
		super.start();
	}

	public NearestTargetGoal setTarget(LivingEntity target) {
		this.target = target;
		return this;
	}
}