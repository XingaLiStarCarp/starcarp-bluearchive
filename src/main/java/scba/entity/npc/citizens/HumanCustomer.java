package scba.entity.npc.citizens;

import mcbase.entity.EntityInteractions.CombinedTask;
import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.extended.entity.GeneralHumanoidMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.citizens.trait.CustomerTrait;

/**
 * 模拟经营的顾客，原版模型
 */
public class HumanCustomer extends GeneralHumanoidMob {
	public static final String TYPE_NAME = "npc_human_customer";

	public static final RegistryObject<EntityType<HumanCustomer>> TYPE = newType(HumanCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, TYPE_NAME, CustomerTrait.ATTRIBUTES);

	protected CombinedTask consumeItems;

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public HumanCustomer(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
	}
}
