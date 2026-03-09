package scba.entity.npc.citizens;

import mcbase.component.trait.entity.GoalTrait;
import mcbase.entity.EntityInteractions.CombinedTask;
import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.extended.entity.GeneralHumanoidMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.citizens.trait.CustomerTrait;

/**
 * 模拟经营的顾客，原版模型
 */
public class HumanCustomer extends GeneralHumanoidMob {
	public static final String TYPE_NAME = "npc_human_customer";

	public static final RegistryObject<EntityType<HumanCustomer>> TYPE = newType(HumanCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, TYPE_NAME, BaseMob.PLAYER_ATTRIBUTES);

	protected CombinedTask consumeItems;

	/**
	 * 所有Npc的子类都必须含有的构造函数。<br>
	 */
	public HumanCustomer(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
		this.addTrait(new GoalTrait<BaseMob>()
				.add(1, (mob) -> new PanicGoal(mob, 1.25)));
		this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
	}
}
