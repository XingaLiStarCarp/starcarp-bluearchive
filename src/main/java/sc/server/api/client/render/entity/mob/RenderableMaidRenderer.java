package sc.server.api.client.render.entity.mob;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.entity.EntityMaidRenderer;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntity;
import com.github.tartaricacid.touhoulittlemaid.geckolib3.geo.IGeoEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import jvmsp.unsafe;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import sc.server.api.entity.mob.RenderableMaid;

/**
 * Touhou Little Maid模组的实体渲染
 */
public abstract class RenderableMaidRenderer extends EntityRenderer<Entity> {
	protected final EntityMaidRenderer entityMaidRenderer;
	protected final IGeoEntityRenderer<Mob> ysmMaidRenderer;

	@SuppressWarnings("unchecked")
	public RenderableMaidRenderer(EntityRendererProvider.Context context) {
		super(context);
		entityMaidRenderer = new EntityMaidRenderer(context);
		ysmMaidRenderer = (IGeoEntityRenderer<Mob>) unsafe.read_member_reference(entityMaidRenderer, "ysmMaidRenderer");
	}

	public final void render(RenderableMaid maid, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		// YSM渲染依赖entity.getCapability()，如果获取到的IGeoEntity Capability为empty，则不执行渲染。因此CapabilityProvider必须正确初始化。
		if (maid != null)
			entityMaidRenderer.render(maid.renderingEntity(), entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}

	public abstract RenderableMaid getRenderableMaid(Entity entity);

	@Override
	public void render(Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		this.render(this.getRenderableMaid(entity), entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		RenderableMaid maid = this.getRenderableMaid(entity);
		if (maid == null)
			return null;
		else
			return entityMaidRenderer.getTextureLocation(maid.renderingEntity());
	}

	public IGeoEntity getOrCreateYsmGeoEntityCapability(EntityMaid maidEntity) {
		// ysmMaidRenderer.getGeoEntity()使用强制转换将参数转换为EntityMaid，如果仅仅是实现了IMaid接口将转换失败导致崩溃
		// 该函数将使用getCapability()获取实体的YSM模型的IGeoEntity，若已存在则返回现存值，否则创建新值。
		IGeoEntity geoEntity = this.ysmMaidRenderer.getGeoEntity(maidEntity);
		geoEntity.setYsmModel(maidEntity.getYsmModelId(), maidEntity.getYsmModelTexture());
		geoEntity.updateRoamingVars(maidEntity.roamingVars);
		return geoEntity;
	}
}
