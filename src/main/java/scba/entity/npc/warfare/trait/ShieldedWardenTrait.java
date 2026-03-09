package scba.entity.npc.warfare.trait;

import java.util.List;

import mcbase.component.trait.entity.GoalTrait;
import mcbase.entity.data.EntityDefaultAttributes.Entry;
import mcbase.entity.goal.MeleeAttackGoal;
import mcbase.entity.goal.NearestTargetGoal;
import mcbase.entity.goal.SprintKeepDistanceToTargetGoal;
import mcbase.entity.goal.UseItemGoal;
import mcbase.entity.mob.BaseMob;
import mcbase.registry.Registers;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class ShieldedWardenTrait extends GoalTrait<BaseMob> {
	public static final List<Entry> ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 20),
			Entry.of(Attributes.MOVEMENT_SPEED, 0.2),
			Entry.of(Attributes.FOLLOW_RANGE, 64),
			Entry.of(Attributes.ARMOR, 20),
			Entry.of(Attributes.ARMOR_TOUGHNESS, 8),
			Entry.of(Attributes.KNOCKBACK_RESISTANCE, 0.8),
			Entry.of(Attributes.ATTACK_DAMAGE, 10),
			Entry.of(Attributes.ATTACK_KNOCKBACK, 1),
			Entry.of(Attributes.ATTACK_SPEED, 1.5));

	protected String mainHandItem;
	protected String offhandItem;

	public ShieldedWardenTrait(String mainHandItem, String offhandItem, int speedUpTicks) {
		super();
		this.add(0, (mob) -> new MeleeAttackGoal(mob, 1.5));
		this.add(1, (mob) -> new UseItemGoal(mob, 2, true));// 盾牌被攻击打断时立即重新开盾
		this.add(2, (mob) -> new SprintKeepDistanceToTargetGoal(mob, 0.5, 0.5, 3.0, speedUpTicks));
		this.add(3, (mob) -> new NearestTargetGoal(mob, true, true, (m, e) -> true));
		this.add(4, (mob) -> new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, 1.0D));
		this.add(5, (mob) -> new RandomLookAroundGoal(mob));

		this.mainHandItem = mainHandItem;
		this.offhandItem = offhandItem;
	}

	public ShieldedWardenTrait(String mainHandItem, int speedUpTicks) {
		this(mainHandItem, "minecraft:shield", speedUpTicks);
	}

	@Override
	public void init(BaseMob mob) {
		super.init(mob);
		mob.setMainHandHold(Registers.item(mainHandItem));
		mob.setOffHandHold(Registers.item(offhandItem));
	}
}