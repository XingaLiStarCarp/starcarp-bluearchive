package mcbase.client.render.entity.mob;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

/**
 * 使用玩家模型的实体渲染器
 * 
 * @param <_T>
 */
public abstract class HumanoidEntityRenderer<_T extends LivingEntity> extends LivingEntityRenderer<_T, PlayerModel<_T>> {

	public HumanoidEntityRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new PlayerModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.5F);
		// 盔甲层
		this.addLayer(new HumanoidArmorLayer<>(this,
				new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)),
				new HumanoidArmorModel<>(context.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)),
				context.getModelManager()));
		this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
		this.addLayer(new ArrowLayer<>(context, this));
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
		this.addLayer(new ElytraLayer<>(this, context.getModelSet()));
		this.addLayer(new SpinAttackEffectLayer<>(this, context.getModelSet()));
		this.addLayer(new BeeStingerLayer<>(this));
	}

	/**
	 * 根据手持物品获取手臂姿态
	 * 
	 * @param <_T>
	 * @param entity
	 * @param hand
	 * @return
	 */
	public static final <_T extends LivingEntity> HumanoidModel.ArmPose getArmPose(_T entity, InteractionHand hand) {
		ItemStack item = entity.getItemInHand(hand);
		if (item.isEmpty()) {
			return HumanoidModel.ArmPose.EMPTY;
		} else {
			if (entity.getUsedItemHand() == hand && entity.getUseItemRemainingTicks() > 0) {
				UseAnim useanim = item.getUseAnimation();
				if (useanim == UseAnim.BLOCK) {
					return HumanoidModel.ArmPose.BLOCK;
				}

				if (useanim == UseAnim.BOW) {
					return HumanoidModel.ArmPose.BOW_AND_ARROW;
				}

				if (useanim == UseAnim.SPEAR) {
					return HumanoidModel.ArmPose.THROW_SPEAR;
				}

				if (useanim == UseAnim.CROSSBOW && hand == entity.getUsedItemHand()) {
					return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
				}

				if (useanim == UseAnim.SPYGLASS) {
					return HumanoidModel.ArmPose.SPYGLASS;
				}

				if (useanim == UseAnim.TOOT_HORN) {
					return HumanoidModel.ArmPose.TOOT_HORN;
				}

				if (useanim == UseAnim.BRUSH) {
					return HumanoidModel.ArmPose.BRUSH;
				}
			} else if (!entity.swinging && item.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(item)) {
				return HumanoidModel.ArmPose.CROSSBOW_HOLD;
			}

			HumanoidModel.ArmPose forgeArmPose = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(item).getArmPose(entity, hand, item);
			if (forgeArmPose != null)
				return forgeArmPose;

			return HumanoidModel.ArmPose.ITEM;
		}
	}

	/**
	 * 设置模型渲染参数和姿态
	 * 
	 * @param entity
	 */
	protected void setModelProperties(_T entity) {
		PlayerModel<_T> playermodel = this.getModel();
		if (entity.isSpectator()) {
			playermodel.setAllVisible(false);
			playermodel.head.visible = true;
			playermodel.hat.visible = true;
		} else {
			playermodel.setAllVisible(true);
			playermodel.crouching = entity.isCrouching();
			HumanoidModel.ArmPose mainHandPose = getArmPose(entity, InteractionHand.MAIN_HAND);
			HumanoidModel.ArmPose offHandPose = getArmPose(entity, InteractionHand.OFF_HAND);
			if (mainHandPose.isTwoHanded()) {
				offHandPose = entity.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
			}

			if (entity.getMainArm() == HumanoidArm.RIGHT) {
				playermodel.rightArmPose = mainHandPose;
				playermodel.leftArmPose = offHandPose;
			} else {
				playermodel.rightArmPose = offHandPose;
				playermodel.leftArmPose = mainHandPose;
			}
		}
	}

	/**
	 * 是否渲染名字。<br>
	 * 采用MobRenderer同款判定。<br>
	 */
	protected boolean shouldShowName(_T entity) {
		return super.shouldShowName(entity) && (entity.shouldShowName() || entity.hasCustomName() && entity == this.entityRenderDispatcher.crosshairPickEntity);
	}

	@Override
	public void render(_T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
		this.setModelProperties(entity);
		super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
	}
}
