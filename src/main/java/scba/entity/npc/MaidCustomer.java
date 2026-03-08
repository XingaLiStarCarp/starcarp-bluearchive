package scba.entity.npc;

import mcbase.component.trait.entity.GoalTrait;
import mcbase.entity.EntityRendererType;
import mcbase.entity.mob.BaseMob;
import mcbase.ext.tlm.entity.maid.MaidMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import scba.entity.npc.trait.CustomerTrait;

public class MaidCustomer extends MaidMob {

	public static final String CUSTOMER_MAID_TYPE_NAME = "npc_maid_customer";

	public static final RegistryObject<EntityType<MaidCustomer>> CUSTOMER_MAID_TYPE = MaidMob.newType(MaidCustomer.class, MIAD_WIDTH, MAID_HEIGHT, CUSTOMER_MAID_TYPE_NAME, BaseMob.PLAYER_ATTRIBUTES);

	public MaidCustomer(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
		this.addTrait(new GoalTrait<BaseMob>()
				.add(1, (mob) -> new PanicGoal((PathfinderMob) mob, 1.25)));
		this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD, 1));
	}

}
