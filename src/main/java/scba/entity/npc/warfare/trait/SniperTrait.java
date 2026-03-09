package scba.entity.npc.warfare.trait;

import java.util.List;

import mcbase.component.trait.entity.GoalTrait;
import mcbase.entity.data.EntityDefaultAttributes.Entry;
import mcbase.entity.goal.NearestTargetGoal;
import mcbase.entity.goal.SprintKeepDistanceToTargetGoal;
import mcbase.entity.mob.BaseMob;
import mcbase.extended.tacz.TaczGuns;
import mcbase.extended.tacz.goal.GunAttackGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class SniperTrait extends GoalTrait<BaseMob> {
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

	private String taczGun;

	public SniperTrait(String taczGun, int speedUpTicks) {
		super();
		this.add(0, (mob) -> new GunAttackGoal(mob, 32));
		this.add(1, (mob) -> new SprintKeepDistanceToTargetGoal(mob, 32, 8, 1.2, speedUpTicks));
		this.add(2, (mob) -> new NearestTargetGoal(mob, true, false, (m, e) -> true));
		this.add(3, (mob) -> new WaterAvoidingRandomStrollGoal((PathfinderMob) mob, 1.0D));
		this.add(4, (mob) -> new RandomLookAroundGoal(mob));

		this.taczGun = taczGun;
	}

	@Override
	public void init(BaseMob mob) {
		super.init(mob);
		mob.setMainHandHold(TaczGuns.getGun(taczGun));
	}
}
