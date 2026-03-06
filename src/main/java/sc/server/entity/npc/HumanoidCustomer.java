package sc.server.entity.npc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.component.trait.entity.GoalTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityInteractions.CombinedTask;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.entity.mob.HumanoidMob;
import sc.server.entity.npc.trait.CustomerTrait;

/**
 * 模拟经营的顾客，原版模型
 */
public class HumanoidCustomer extends HumanoidMob {
	public static final String CUSTOMER_MALE_TYPE_NAME = "npc_male_customer";

	public static final RegistryObject<EntityType<HumanoidCustomer>> CUSTOMER_MALE_TYPE = HumanoidMob.newMaleType(HumanoidCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, CUSTOMER_MALE_TYPE_NAME, BaseMob.PLAYER_ATTRIBUTES);

	public static final String CUSTOMER_FEMALE_TYPE_NAME = "npc_female_customer";

	public static final RegistryObject<EntityType<HumanoidCustomer>> CUSTOMER_FEMALE_TYPE = HumanoidMob.newFemaleType(HumanoidCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, CUSTOMER_FEMALE_TYPE_NAME, BaseMob.PLAYER_ATTRIBUTES);

	protected CombinedTask consumeItems;

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public HumanoidCustomer(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
		this.addTrait(new GoalTrait<BaseMob>()
				.add(1, (mob) -> new PanicGoal(mob, 1.25)));
	}
}
