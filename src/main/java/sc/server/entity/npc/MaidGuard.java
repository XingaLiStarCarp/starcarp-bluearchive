package sc.server.entity.npc;

import java.util.List;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.entity.goal.NearestTargetGoal;
import sc.server.api.entity.goal.UseItemMeleeAttackGoal;
import sc.server.api.entity.maid.MaidMob;
import sc.server.api.entity.trait.ConstGoalTrait;

public class MaidGuard extends MaidMob {
	public static final String GUARD_MAID_TYPE_NAME = "npc_maid_guard";

	public static final List<Entry> GUARD_ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 100),
			Entry.of(Attributes.MOVEMENT_SPEED, 0.25),
			Entry.of(Attributes.FOLLOW_RANGE, 32),
			Entry.of(Attributes.ARMOR, 20),
			Entry.of(Attributes.ARMOR_TOUGHNESS, 10),
			Entry.of(Attributes.ATTACK_DAMAGE, 8),
			Entry.of(Attributes.ATTACK_KNOCKBACK, 1),
			Entry.of(Attributes.ATTACK_SPEED, 5));

	public static final RegistryObject<EntityType<MaidGuard>> GUARD_MAID_TYPE = MaidMob.newType(MaidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_MAID_TYPE_NAME, GUARD_ATTRIBUTES);

	public MaidGuard(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new ConstGoalTrait()
				.add(0, (mob) -> new WaterAvoidingRandomStrollGoal(mob, 1.0D))
				.add(1, (mob) -> new RandomLookAroundGoal(mob))
				.add(2, (mob) -> new NearestTargetGoal(mob, true, true, (target) -> true))
				.add(3, (mob) -> new UseItemMeleeAttackGoal(mob, 1.5, true, 4)));
		this.setMainHandHold(Items.NETHERITE_SWORD);
		this.equipItemIfPossible(new ItemStack(Items.SHIELD, 1));
	}

}
