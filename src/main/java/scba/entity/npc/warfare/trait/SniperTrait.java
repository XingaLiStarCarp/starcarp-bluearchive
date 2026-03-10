package scba.entity.npc.warfare.trait;

import java.util.function.Supplier;

import mcbase.component.trait.MultiTrait;
import mcbase.component.trait.entity.GoalTrait;
import mcbase.component.trait.entity.ItemHoldTrait;
import mcbase.component.trait.entity.RandomWanderingTrait;
import mcbase.entity.goal.action.UseItemGoal;
import mcbase.entity.goal.navigation.KeepDistanceToTargetGoal;
import mcbase.entity.goal.target.NearestTargetGoal;
import mcbase.extended.tacz.goal.GunAttackGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SniperTrait extends MultiTrait {
	public static final Supplier<AttributeSupplier> ATTRIBUTES = () -> Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 20)
			.add(Attributes.MOVEMENT_SPEED, 0.25)
			.add(Attributes.FOLLOW_RANGE, 64)
			.add(Attributes.ARMOR, 12)
			.add(Attributes.ARMOR_TOUGHNESS, 8)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0)
			.add(Attributes.ATTACK_DAMAGE, 16)
			.add(Attributes.ATTACK_KNOCKBACK, 3)
			.add(Attributes.ATTACK_SPEED, 1)
			.build();

	public SniperTrait(String taczGun) {
		super();
		this.add(new ItemHoldTrait(taczGun)); // 手持物品
		this.add(new RandomWanderingTrait());
		this.add(new GoalTrait()
				.add(0, (mob) -> new GunAttackGoal(mob, 50).setBoundDistances(2.5))
				.add(1, (mob) -> new UseItemGoal(mob, true).setBoundDistances(2))
				.add(2, (mob) -> new KeepDistanceToTargetGoal(mob, 0, 64.0))
				.add(3, (mob) -> new NearestTargetGoal(mob, true, true, (m, e) -> true)));
	}
}
