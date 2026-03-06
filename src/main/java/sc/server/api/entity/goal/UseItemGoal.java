package sc.server.api.entity.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;

public class UseItemGoal extends InDistanceGoal {

	protected InteractionHand hand;

	protected int useTime;
	private int useTimeCounter;
	protected int useInterval;
	private int useIntervalCounter;

	public static final int INFINITE_USE_TIME = -1;
	public static final int NO_USE_INTERVAL = 0;

	public UseItemGoal(Mob mob, double distance, InteractionHand hand, int useTime, int useInterval) {
		super(mob, distance);
		this.hand = hand;
		this.useTime = useTime;
		this.useInterval = useInterval;
	}

	public UseItemGoal(Mob mob, double distance, InteractionHand hand) {
		this(mob, distance, hand, INFINITE_USE_TIME, NO_USE_INTERVAL);
	}

	public UseItemGoal(Mob mob, double distance, int useTime, int useInterval) {
		this(mob, distance, InteractionHand.OFF_HAND, useTime, useInterval);
	}

	public UseItemGoal(Mob mob, double distance) {
		this(mob, distance, INFINITE_USE_TIME, NO_USE_INTERVAL);
	}

	@Override
	public void enter() {
		useTimeCounter = 0;
		useIntervalCounter = 0;
		if (useTime <= 0) {
			this.mob.swing(hand);
			this.mob.startUsingItem(hand);// 使用时间小于等于0时，视为永久使用直到该Goal执行结束
			return;
		}
	}

	@Override
	public void update() {
		if (useTime > 0) {
			if (useTimeCounter <= 0) {
				this.mob.swing(hand);
				this.mob.startUsingItem(hand);
			}
			if (useTimeCounter < useTime) {
				++useTimeCounter;
			} else {
				// 当物品使用时间达到阈值useTime
				if (useIntervalCounter <= 0) {
					this.mob.stopUsingItem();
				} else {
					++useIntervalCounter;
				}
				// 判断是否冷却结束
				if (useIntervalCounter >= useInterval) {
					useTimeCounter = 0;
					useIntervalCounter = 0;
				}
			}
		}
	}

	@Override
	public void exit() {
		this.mob.stopUsingItem();
		useTimeCounter = 0;
		useIntervalCounter = 0;
	}
}
