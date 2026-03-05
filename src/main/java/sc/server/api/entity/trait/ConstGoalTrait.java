package sc.server.api.entity.trait;

import java.util.ArrayList;
import java.util.function.Function;

import net.minecraft.world.entity.ai.goal.Goal;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.BaseMob.TraitComponent;

/**
 * 添加常驻的Goal的特性
 */
public class ConstGoalTrait implements TraitComponent {
	private static class GoalEntry {
		private int priority;
		private Goal goal;
		private Function<BaseMob, Goal> goalCtor;

		public GoalEntry(int priority, Function<BaseMob, Goal> goalCtor) {
			this.priority = priority;
			this.goalCtor = goalCtor;
		}

		public int priority() {
			return priority;
		}

		public Goal goal(BaseMob mob) {
			return goal == null ? (goal = goalCtor.apply(mob)) : goal;
		}
	}

	private final ArrayList<GoalEntry> goals;

	public ConstGoalTrait() {
		goals = new ArrayList<GoalEntry>();
	}

	public ConstGoalTrait add(int priority, Function<BaseMob, Goal> goalCtor) {
		goals.add(new GoalEntry(priority, goalCtor));
		return this;
	}

	@Override
	public void init(BaseMob mob) {
		for (GoalEntry entry : goals) {
			mob.goalSelector.addGoal(entry.priority(), entry.goal(mob));
		}
	}

	@Override
	public void uninit(BaseMob mob) {
		for (GoalEntry entry : goals) {
			mob.goalSelector.removeGoal(entry.goal(mob));
		}
	}
}
