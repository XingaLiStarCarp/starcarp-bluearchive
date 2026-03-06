package sc.server.api.entity.goal;

import net.minecraft.world.entity.Mob;
import sc.server.api.entity.EntityData;

/**
 * 疾跑与目标实体保持距离
 */
public class SprintKeepDistanceGoal extends KeepDistanceGoal {
	protected double finalSpeedModifier;
	private int speedUpTicks;
	private double speedUpAcc;

	public SprintKeepDistanceGoal(Mob mob, double keepDistance, double tolerance, double finalSpeedModifier, int speedUpTicks, boolean interrupt) {
		super(mob, keepDistance, tolerance, 1.0, interrupt);
		this.finalSpeedModifier = finalSpeedModifier;
		this.speedUpTicks = speedUpTicks;
		this.speedUpAcc = finalSpeedModifier / speedUpTicks;
	}

	public SprintKeepDistanceGoal(Mob mob, double keepDistance, double tolerance, double finalSpeedModifier, int speedUpTicks) {
		this(mob, keepDistance, tolerance, finalSpeedModifier, speedUpTicks, true);
	}

	public int getSpeedUpTicks() {
		return speedUpTicks;
	}

	public void setSpeedUpTicks(int speedUpTicks) {
		this.speedUpTicks = speedUpTicks;
		this.speedUpAcc = this.finalSpeedModifier / speedUpTicks;
	}

	public double getFinalSpeedModifier() {
		return finalSpeedModifier;
	}

	public void setFinalSpeedModifier(double finalSpeedModifier) {
		this.finalSpeedModifier = finalSpeedModifier;
		this.speedUpAcc = finalSpeedModifier / this.speedUpTicks;
	}

	@Override
	public void update() {
		if (!mob.level().isClientSide()) {
			double speedModifier = EntityData.getSpeedModifier(navigation);
			System.err.println("speedModifier=" + speedModifier);
			if (speedModifier < finalSpeedModifier) {
				navigation.setSpeedModifier(speedModifier + speedUpAcc);
			}
		}
		super.update();
	}
}
