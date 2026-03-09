package mcbase.client.render.entity.mob;

import mcbase.client.render.entity.EntityRenderers;
import mcbase.entity.Humanoid;
import mcbase.entity.mob.HumanoidMob;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * 渲染实现了Humanoid接口的实体
 * 
 * @param <_T>
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class HumanoidRenderer<_T extends LivingEntity & Humanoid> extends PlayerModelRenderer<_T> {

	public HumanoidRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(_T entity) {
		return entity.getSkin();
	}

	@Override
	protected boolean isSlim(_T entity) {
		return entity.isSlim();
	}

	static {
		HumanoidMob.RENDERER_TYPE.registerRenderer(HumanoidRenderer.class, EntityRendererProvider.Context.class);
	}

	@SubscribeEvent
	public static void register(EntityRenderersEvent.AddLayers event) {
		EntityRenderers.register(HumanoidMob.RENDERER_TYPE, EntityRenderers.context());
	}
}