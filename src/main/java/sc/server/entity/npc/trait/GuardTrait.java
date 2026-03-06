package sc.server.entity.npc.trait;

import java.util.List;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import sc.server.api.component.trait.entity.GoalTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.goal.MeleeAttackGoal;
import sc.server.api.entity.goal.MobGoalUtils;
import sc.server.api.entity.goal.NearestTargetGoal;
import sc.server.api.entity.goal.SprintKeepDistanceGoal;
import sc.server.api.entity.goal.UseItemGoal;
import sc.server.api.registry.Registers;

public class GuardTrait extends GoalTrait<BaseMob> {
	public static final List<Entry> GUARD_ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 100),
			Entry.of(Attributes.MOVEMENT_SPEED, 0.2),
			Entry.of(Attributes.FOLLOW_RANGE, 32),
			Entry.of(Attributes.ARMOR, 20),
			Entry.of(Attributes.ARMOR_TOUGHNESS, 10),
			Entry.of(Attributes.KNOCKBACK_RESISTANCE, 0.8),
			Entry.of(Attributes.ATTACK_DAMAGE, 10),
			Entry.of(Attributes.ATTACK_KNOCKBACK, 1),
			Entry.of(Attributes.ATTACK_SPEED, 5));

	protected String mainHandItem;
	protected String offhandItem;

	public GuardTrait(String mainHandItem, String offhandItem, MobCategory attackCategory, int speedUpTicks) {
		super();
		this.add(0, (mob) -> new MeleeAttackGoal(mob, 2));
		this.add(0, (mob) -> new UseItemGoal(mob, 2));
		this.add(0, (mob) -> new SprintKeepDistanceGoal(mob, 0.5, 0.5, 3.0, 40));
		this.add(0, (mob) -> new NearestTargetGoal(mob, true, true, MobGoalUtils.entityCategory(attackCategory)));
		this.add(1, (mob) -> new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, 1.0D));
		this.add(2, (mob) -> new RandomLookAroundGoal(mob));

		this.mainHandItem = mainHandItem;
		this.offhandItem = offhandItem;
	}

	public GuardTrait(String mainHandItem, String offhandItem, int speedUpTicks) {
		this(mainHandItem, offhandItem, MobCategory.MONSTER, speedUpTicks);
	}

	public GuardTrait(String mainHandItem, int speedUpTicks) {
		this(mainHandItem, "minecraft:shield", speedUpTicks);
	}

	@Override
	public void init(BaseMob mob) {
		super.init(mob);
		mob.setMainHandHold(Registers.item(mainHandItem));
		mob.setOffHandHold(Registers.item(offhandItem));
	}
}