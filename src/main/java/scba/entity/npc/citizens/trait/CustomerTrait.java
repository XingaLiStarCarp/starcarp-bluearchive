package scba.entity.npc.citizens.trait;

import mcbase.component.trait.entity.CombinedInteractionTrait;
import mcbase.entity.EntityInteractions;
import mcbase.entity.mob.BaseMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class CustomerTrait extends CombinedInteractionTrait<BaseMob> {
	@SuppressWarnings("unchecked")
	public CustomerTrait() {
		super((event, player, mob, hand) -> {
			return EntityInteractions.receiveItemFromPlayerMainHandAndHold(player, mob, "minecraft:diamond", 1);
		});
	}

	@Override
	public void onSuccess(EntityInteract event, Player player, BaseMob mob, InteractionHand hand) {
		EntityInteractions.sengMsgToPlayer(player, "success");
		// mob.swing(InteractionHand.MAIN_HAND);
		if (mob.isUsingItem())
			mob.stopUsingItem();
		else {
			mob.swing(InteractionHand.OFF_HAND);
			mob.startUsingItem(InteractionHand.OFF_HAND);
		}
	}
}
