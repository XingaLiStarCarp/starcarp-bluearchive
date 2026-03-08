package mcbase.component.trait.entity;

import java.util.ArrayList;
import java.util.function.Function;

import mcbase.component.TraitProvider.TraitComponent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

/**
 * 添加常驻的Goal的特性
 */
public class GoalTrait<_GoalHolder extends Mob> implements TraitComponent<_GoalHolder> {
	private static class GoalEntry<_GoalHolder> {
		private int priority;
		private Goal goal;
		private Function<_GoalHolder, Goal> goalCtor;

		public GoalEntry(int priority, Function<_GoalHolder, Goal> goalCtor) {
			this.priority = priority;
			this.goalCtor = goalCtor;
		}

		public int priority() {
			return priority;
		}

		public Goal goal(_GoalHolder mob) {
			return goal == null ? (goal = goalCtor.apply(mob)) : goal;
		}
	}

	private _GoalHolder mob;
	private final ArrayList<GoalEntry<_GoalHolder>> goalEntries;
	private final ArrayList<Goal> goals;

	public GoalTrait() {
		goalEntries = new ArrayList<>();
		goals = new ArrayList<>();
	}

	/**
	 * 添加Goal。<br>
	 * 
	 * @param priority Goal的优先级，priority越小优先级越高。
	 * @param goalCtor Goal的构造函数。
	 * @return
	 */
	public GoalTrait<_GoalHolder> add(int priority, Function<_GoalHolder, Goal> goalCtor) {
		goalEntries.add(new GoalEntry<>(priority, goalCtor));
		return this;
	}

	@Override
	public void init(_GoalHolder mob) {
		goals.clear();
		this.mob = mob;
		for (GoalEntry<_GoalHolder> entry : goalEntries) {
			Goal goal = entry.goal(mob);
			goals.add(goal);
			mob.goalSelector.addGoal(entry.priority(), goal);
		}
	}

	@Override
	public void uninit(_GoalHolder mob) {
		for (Goal goal : goals) {
			mob.goalSelector.removeGoal(goal);
		}
		goals.clear();
	}

	public final _GoalHolder getMob() {
		return mob;
	}

	public final int getPriority(int idx) {
		return goalEntries.get(idx).priority();
	}

	public final Goal getGoal(int idx) {
		return goals.get(idx);
	}

	/**
	 * 移除指定索引的Goal
	 * 
	 * @param idx
	 */
	public final void removeGoal(int idx) {
		mob.goalSelector.removeGoal(getGoal(idx));
	}
}
