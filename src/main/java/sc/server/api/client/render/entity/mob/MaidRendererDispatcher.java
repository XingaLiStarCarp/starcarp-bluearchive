package sc.server.api.client.render.entity.mob;

import java.util.HashMap;
import java.util.function.BiConsumer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.client.render.entity.EntityRenderers;
import sc.server.api.entity.mob.RenderableMaid;

/**
 * 将指定EntityType渲染为指定TLM或YSM模型的渲染器
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE)
public class MaidRendererDispatcher extends RenderableMaidRenderer {

	private BiConsumer<Entity, RenderableMaid> updateProc;
	private HashMap<Entity, RenderableMaid> renderableMaids = new HashMap<>();

	private MaidRendererDispatcher(EntityRendererProvider.Context context, BiConsumer<Entity, RenderableMaid> updateProc) {
		super(context);
		this.updateProc = updateProc;
	}

	@Override
	public RenderableMaid getRenderableMaid(Entity entity) {
		RenderableMaid maid = renderableMaids.computeIfAbsent(entity, (newEntity) -> RenderableMaid.bind(newEntity));
		updateProc.accept(entity, maid);
		return maid;
	}

	protected void syncRenderingEntities() {
		for (RenderableMaid maid : renderableMaids.values()) {
			maid.syncRenderingEntity();// 在客户端中需要每tick同步实体数据
		}
	}

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			for (MaidRendererDispatcher dispathcer : DISPATCHERS.values()) {
				dispathcer.syncRenderingEntities();
			}
		}
	}

	private static final HashMap<EntityType<?>, MaidRendererDispatcher> DISPATCHERS = new HashMap<>();
	private static final HashMap<EntityType<?>, EntityRenderer<?>> ORIGINAL_RENDERERS = new HashMap<>();

	/**
	 * 指派指定实体类型的渲染器
	 * 
	 * @param entityTypes
	 */
	public static void dispatch(EntityType<?> type, BiConsumer<Entity, RenderableMaid> updateProc) {
		MaidRendererDispatcher dispatcher = new MaidRendererDispatcher(EntityRenderers.context(), updateProc);
		DISPATCHERS.put(type, dispatcher);
		ORIGINAL_RENDERERS.put(type, EntityRenderers.getEntityRenderer(type));
		EntityRenderers.setEntityRenderer(type, dispatcher);
	}

	public static void remove(EntityType<?> type) {
		if (DISPATCHERS.containsKey(type)) {
			EntityRenderers.setEntityRenderer(type, ORIGINAL_RENDERERS.get(type));// 复原渲染器
			DISPATCHERS.remove(type);
			ORIGINAL_RENDERERS.remove(type);
		}
	}
}