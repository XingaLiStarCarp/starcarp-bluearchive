package sc.server.entity.npc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.component.trait.entity.GoalTrait;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityRendererType;
import sc.server.api.ext.tlm.entity.maid.MaidMob;
import sc.server.entity.npc.trait.CustomerTrait;

public class MaidCustomer extends MaidMob {

	public static final String CUSTOMER_MAID_TYPE_NAME = "npc_maid_customer";

	public static final RegistryObject<EntityType<MaidCustomer>> CUSTOMER_MAID_TYPE = MaidMob.newType(MaidCustomer.class, HUMANOID_WIDTH, HUMANOID_HEIGHT, CUSTOMER_MAID_TYPE_NAME, BaseMob.PLAYER_ATTRIBUTES);

	public MaidCustomer(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> rendererType, Level level) {
		super(entityType, rendererType, level);
		this.addTrait(new CustomerTrait());
		this.addTrait(new GoalTrait<BaseMob>()
				.add(1, (mob) -> new PanicGoal((PathfinderMob) mob, 1.25)));
	}

}
