package sc.server.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.component.trait.entity.StepParticlesTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.ext.tlm.entity.maid.MaidMob;
import sc.server.entity.npc.trait.GuardTrait;

public class MaidGuard extends MaidMob {
	public static final String GUARD_MAID_TYPE_NAME = "npc_maid_guard";

	public static final RegistryObject<EntityType<MaidGuard>> GUARD_MAID_TYPE = MaidMob.newType(MaidGuard.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, GUARD_MAID_TYPE_NAME, GuardTrait.GUARD_ATTRIBUTES);

	public MaidGuard(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new StepParticlesTrait<>(2.0));
		this.addTrait(new GuardTrait("minecraft:iron_sword", 80));
	}

}
