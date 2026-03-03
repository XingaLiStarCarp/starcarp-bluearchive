package sc.server.api.client.render.entity.mob;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.entity.mob.MaidMob;
import sc.server.api.entity.mob.RenderableMaid;

/**
 * MaidMob渲染器
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class MaidMobRenderer extends RenderableMaidRenderer {

	public MaidMobRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public RenderableMaid getRenderableMaid(Entity entity) {
		if (entity instanceof RenderableMaid maid)
			return maid;
		else
			return null;
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.RegisterRenderers event) {
		MaidMob.RENDERER_TYPE.register(event);
	}
}
