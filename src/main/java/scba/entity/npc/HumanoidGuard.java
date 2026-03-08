package scba.entity.npc;

import mcbase.component.trait.entity.StepParticlesTrait;
import mcbase.entity.EntityRendererType;
import mcbase.entity.EntityInteractions.CombinedTask;
import mcbase.entity.mob.BaseMob;
import mcbase.entity.mob.HumanoidMob;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.trait.GuardTrait;

public class HumanoidGuard extends HumanoidMob {
	public static final String GUARD_MALE_TYPE_NAME = "npc_male_guard";

	public static final RegistryObject<EntityType<HumanoidGuard>> GUARD_MALE_TYPE = HumanoidMob.newMaleType(HumanoidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_MALE_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	public static final String GUARD_FEMALE_TYPE_NAME = "npc_female_guard";

	public static final RegistryObject<EntityType<HumanoidGuard>> GUARD_FEMALE_TYPE = HumanoidMob.newFemaleType(HumanoidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_FEMALE_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	protected CombinedTask consumeItems;

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public HumanoidGuard(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new StepParticlesTrait<>(2.0));
		this.addTrait(new GuardTrait("minecraft:iron_sword", 80));
	}
}