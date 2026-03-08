package scba.entity.npc;

import mcbase.component.trait.entity.StepParticlesTrait;
import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.ext.tlm.entity.maid.MaidMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.trait.GuardTrait;

public class MaidGuard extends MaidMob {
	public static final String GUARD_MAID_TYPE_NAME = "npc_maid_guard";

	public static final RegistryObject<EntityType<MaidGuard>> GUARD_MAID_TYPE = MaidMob.newType(MaidGuard.class, MIAD_WIDTH, MAID_HEIGHT, GUARD_MAID_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	public MaidGuard(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new StepParticlesTrait<>(2.0));
		this.addTrait(new GuardTrait("minecraft:iron_sword", 80));
	}

}
