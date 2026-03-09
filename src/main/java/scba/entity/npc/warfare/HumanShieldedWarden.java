package scba.entity.npc.warfare;

import mcbase.component.trait.entity.StepParticlesTrait;
import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.entity.mob.HumanoidMob;
import mcbase.extended.entity.GeneralHumanoidMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.warfare.trait.ShieldedWardenTrait;

public class HumanShieldedWarden extends GeneralHumanoidMob {
	public static final String TYPE_NAME = "npc_human_shielded_warden";

	public static final RegistryObject<EntityType<HumanShieldedWarden>> TYPE = HumanoidMob.newType(HumanShieldedWarden.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, TYPE_NAME, ShieldedWardenTrait.ATTRIBUTES);

	public HumanShieldedWarden(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new StepParticlesTrait<>(2.0));
		this.addTrait(new ShieldedWardenTrait("minecraft:iron_sword", 80));
	}
}