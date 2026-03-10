package scba.entity.npc.warfare;

import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.extended.entity.GeneralHumanoidMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.warfare.trait.WardenTrait;

/**
 * 典狱长
 */
public class HumanWarden extends GeneralHumanoidMob {
	public static final String TYPE_NAME = "npc_human_warden";

	public static final RegistryObject<EntityType<HumanWarden>> TYPE = newType(HumanWarden.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, TYPE_NAME, WardenTrait.ATTRIBUTES);

	public HumanWarden(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new WardenTrait(80));
	}
}