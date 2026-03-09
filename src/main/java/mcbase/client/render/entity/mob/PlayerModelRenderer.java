package mcbase.client.render.entity.mob;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * 综合男女两种体型的使用玩家模型的实体渲染器
 * 
 * @param <_T>
 */
public abstract class PlayerModelRenderer<_T extends LivingEntity> extends EntityRenderer<_T> {
	private class ModelRenderer extends FixedPlayerModelRenderer<_T> {
		public ModelRenderer(Context context, boolean slim) {
			super(context, slim);
		}

		@Override
		public ResourceLocation getTextureLocation(_T entity) {
			return PlayerModelRenderer.this.getTextureLocation(entity);
		}
	}

	private FixedPlayerModelRenderer<_T> wideRenderer;
	private FixedPlayerModelRenderer<_T> slimRenderer;

	public PlayerModelRenderer(EntityRendererProvider.Context context) {
		super(context);
		wideRenderer = this.new ModelRenderer(context, false);
		slimRenderer = this.new ModelRenderer(context, true);
	}

	/**
	 * 实体是否是slim类型的皮肤
	 * 
	 * @param entity
	 * @return
	 */
	protected abstract boolean isSlim(_T entity);

	@Override
	public void render(_T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		if (this.isSlim(entity))
			slimRenderer.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
		else
			wideRenderer.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}
}
