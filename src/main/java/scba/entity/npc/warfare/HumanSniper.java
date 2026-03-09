package scba.entity.npc.warfare;

import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.extended.entity.GeneralHumanoidMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.warfare.trait.SniperTrait;

public class HumanSniper extends GeneralHumanoidMob {
	public static final String TYPE_NAME = "npc_human_sniper";

	public static final RegistryObject<EntityType<HumanSniper>> TYPE = newType(HumanSniper.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, TYPE_NAME, SniperTrait.ATTRIBUTES);

	public HumanSniper(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new SniperTrait("tacz:m95", 20));
	}
}
