package mcbase.client.render.entity.mob;

import mcbase.client.render.entity.EntityRenderers;
import mcbase.entity.mob.HumanoidMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class HumanoidMobRenderer extends HumanoidEntityRenderer<HumanoidMob> {
	static {
		HumanoidMob.MALE_RENDERER_TYPE.registerRenderer(HumanoidMobRenderer.class, EntityRendererProvider.Context.class, boolean.class);
		HumanoidMob.FEMALE_RENDERER_TYPE.registerRenderer(HumanoidMobRenderer.class, EntityRendererProvider.Context.class, boolean.class);
	}

	public HumanoidMobRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, slim);
	}

	@Override
	public ResourceLocation getTextureLocation(HumanoidMob entity) {
		return entity.getSkin();
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.AddLayers event) {
		EntityRenderers.register(HumanoidMob.MALE_RENDERER_TYPE, EntityRenderers.context(), false);
		EntityRenderers.register(HumanoidMob.FEMALE_RENDERER_TYPE, EntityRenderers.context(), true);
	}
}