package sc.server.entity.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.component.trait.entity.StepParticlesTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityInteractions.CombinedTask;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.entity.mob.HumanoidMob;
import sc.server.entity.npc.trait.GuardTrait;

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