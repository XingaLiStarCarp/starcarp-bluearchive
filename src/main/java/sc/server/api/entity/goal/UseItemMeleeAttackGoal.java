package sc.server.api.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * 近战攻击且尝试盾牌格挡的Goal
 */
public class UseItemMeleeAttackGoal extends MeleeAttackGoal {

	protected double usingDistance;

	private byte startUsingEvent = -1;
	private byte stopUsingEvent = -1;

	protected int startTicks = 0;
	protected boolean isUsing;
	protected InteractionHand hand;

	public UseItemMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double usingDistance, InteractionHand hand) {
		super(mob, speedModifier, followingTargetEvenIfNotSeen);
		this.usingDistance = usingDistance;
		this.isUsing = false;
		this.hand = hand;
	}

	public UseItemMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen, double usingDistance) {
		this(mob, speedModifier, followingTargetEvenIfNotSeen, usingDistance, InteractionHand.OFF_HAND);
	}

	@Override
	public void start() {
		super.start();
		this.isUsing = false;
		startTicks = 1;
		this.startUsing();
	}

	@Override
	public void stop() {
		this.stopUsing();
		this.isUsing = false;
		startTicks = 0;
		super.stop();
	}

	public int getStartTicks() {
		return startTicks;
	}

	public UseItemMeleeAttackGoal setStartUsingEvent(byte startUsingEvent) {
		this.startUsingEvent = startUsingEvent;
		return this;
	}

	public UseItemMeleeAttackGoal setStopUsingEvent(byte stopUsingEvent) {
		this.stopUsingEvent = stopUsingEvent;
		return this;
	}

	@Override
	public void tick() {
		super.tick();
		if (startTicks > 0) {
			++startTicks;
		}
		LivingEntity target = this.mob.getTarget();
		if (target != null) {
			double distanceToTarget = this.mob.distanceToSqr(target);
			double usingDistanceSqr = this.usingDistance * this.usingDistance;
			if (distanceToTarget < usingDistanceSqr) {
				this.startUsing();
			} else {
				this.stopUsing();
			}
		}
	}

	/**
	 * 当前实体手上是否具有物品
	 * 
	 * @return
	 */
	protected boolean hasItem() {
		return !this.mob.getItemInHand(hand).isEmpty();
	}

	/**
	 * 开始格挡
	 */
	protected void startUsing() {
		if (!this.isUsing && this.hasItem()) {
			this.mob.startUsingItem(hand);
			if (startUsingEvent > 0)
				this.mob.level().broadcastEntityEvent(this.mob, startUsingEvent);
			this.isUsing = true;
		}
	}

	/**
	 * 停止格挡
	 */
	protected void stopUsing() {
		if (this.isUsing) {
			this.mob.stopUsingItem();
			if (stopUsingEvent > 0)
				this.mob.level().broadcastEntityEvent(this.mob, stopUsingEvent);
			this.isUsing = false;
		}
	}

	public UseItemMeleeAttackGoal setInteractionHand(InteractionHand hand) {
		this.hand = hand;
		return this;
	}
}