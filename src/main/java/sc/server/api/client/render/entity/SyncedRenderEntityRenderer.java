package sc.server.api.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import sc.server.api.entity.SyncedRenderEntity;

/**
 * RenderSyncEntity渲染器。<br>
 * 用于将一种实体类型的全部实体渲染为另一种实体。<br>
 * 
 * @param <_RenderingEntity>
 * @param <_SyncEntity>
 */
public abstract class SyncedRenderEntityRenderer<_RenderingEntity extends Entity, _SyncEntity extends SyncedRenderEntity<_RenderingEntity, ?>> extends EntityRenderer<Entity> {
	protected final EntityRenderer<_RenderingEntity> syncEntityRenderer;

	@SuppressWarnings("unchecked")
	protected SyncedRenderEntityRenderer(EntityRendererProvider.Context context, EntityRenderer<?> syncEntityRenderer) {
		super(context);
		this.syncEntityRenderer = (EntityRenderer<_RenderingEntity>) syncEntityRenderer;
	}

	public final void render(_SyncEntity syncEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		if (syncEntity != null)
			syncEntityRenderer.render(syncEntity.renderingEntity(), entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}

	/**
	 * 为指定的实体指派一个同步渲染模型
	 * 
	 * @param entity 原本的实体
	 * @return 要替换的模型虚假实体。返回null则不会渲染任何模型
	 */
	public abstract _SyncEntity dispatch(Entity entity);

	@Override
	public void render(Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		this.render(this.dispatch(entity), entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		_SyncEntity syncEntity = this.dispatch(entity);
		if (syncEntity == null)
			return null;
		else
			return syncEntityRenderer.getTextureLocation(syncEntity.renderingEntity());
	}
}
