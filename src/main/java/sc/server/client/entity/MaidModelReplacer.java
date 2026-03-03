package sc.server.client.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.client.render.entity.mob.MaidRendererDispatcher;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class MaidModelReplacer {
	@SubscribeEvent
	public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
		MaidRendererDispatcher.dispatch(EntityType.SLIME, (entity, maid) -> {
			maid.setIsYsmModel(true);
			maid.setYsmModelId("BA_百合园圣娅.ysm");
		});
		MaidRendererDispatcher.dispatch(EntityType.WITHER, (entity, maid) -> {
			maid.setIsYsmModel(true);
			maid.setYsmModelId("ba_白洲梓（泳装）.2.0.ysm");
		});
		MaidRendererDispatcher.dispatch(EntityType.WARDEN, (entity, maid) -> {
			maid.setIsYsmModel(true);
			maid.setYsmModelId("BA_空崎日奈：礼服.ysm");
		});
		MaidRendererDispatcher.dispatch(EntityType.IRON_GOLEM, (entity, maid) -> {
			maid.setIsYsmModel(true);
			maid.setYsmModelId("BA_圣园未花3.0.ysm");
		});
	}
}
