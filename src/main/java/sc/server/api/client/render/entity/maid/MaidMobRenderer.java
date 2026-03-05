package sc.server.api.client.render.entity.maid;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.client.render.entity.EntityRenderers;
import sc.server.api.entity.maid.MaidMob;
import sc.server.api.entity.maid.SyncedRenderMaid;

/**
 * MaidMob渲染器
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class MaidMobRenderer extends SyncedRenderMaidRenderer {
	static {
		MaidMob.RENDERER_TYPE.registerRenderer(MaidMobRenderer.class, EntityRendererProvider.Context.class);
	}

	public MaidMobRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public SyncedRenderMaid dispatch(Entity entity) {
		if (entity instanceof SyncedRenderMaid maid)
			return maid;
		else
			return null;
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.AddLayers event) {
		EntityRenderers.register(MaidMob.RENDERER_TYPE, EntityRenderers.context());
	}
}
