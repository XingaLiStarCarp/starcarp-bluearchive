package sc.server.api.client.render.entity.mob;

import java.util.function.BiConsumer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import sc.server.api.client.render.entity.EntityRenderers;
import sc.server.api.entity.mob.RenderableMaid;

/**
 * 将指定EntityType渲染为指定TLM或YSM模型的渲染器
 */
public class MaidModelDispatcher extends RenderableMaidRenderer {
	private BiConsumer<Entity, RenderableMaid> modelResolver;

	public MaidModelDispatcher(EntityRendererProvider.Context context, BiConsumer<Entity, RenderableMaid> modelResolver) {
		super(context);
		this.modelResolver = modelResolver;
	}

	public MaidModelDispatcher(BiConsumer<Entity, RenderableMaid> modelResolver) {
		this(EntityRenderers.context(), modelResolver);
	}

	@Override
	public RenderableMaid getRenderableMaid(Entity entity) {
		RenderableMaid maid = RenderableMaid.bind(entity, modelResolver);// 第一次获取绑定实体时内部会同步数据一次
		modelResolver.accept(entity, maid);// 执行更新，每tick同步一次数据
		return maid;
	}

	/**
	 * 指派指定实体类型的渲染器。<br>
	 * 必须在Minecraft客户端初始化完成后调用。<br>
	 * 
	 * @param entityTypes
	 */
	public static void dispatch(EntityType<?> type, BiConsumer<Entity, RenderableMaid> modelResolver) {
		EntityRenderers.setEntityRenderer(type, new MaidModelDispatcher(modelResolver));
	}
}