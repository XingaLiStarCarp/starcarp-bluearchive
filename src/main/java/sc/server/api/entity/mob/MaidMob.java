package sc.server.api.entity.mob;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleDataCollection;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleRegister;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.client.render.entity.mob.MaidMobRenderer;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.EntityRendererType;

/**
 * Touhou Little Maid模组的女仆类型实体，需要配合渲染器EntityMaidRenderer使用
 */
public class MaidMob extends BaseMob implements RenderableMaid {

	public static final EntityRendererType<MaidModelInfo> RENDERER_TYPE = new EntityRendererType<>(MaidMobRenderer.class);

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	static {
		RENDERER_TYPE.setDefaultRenderAsset(new MaidModelInfo("touhou_little_maid:hakurei_reimu"));
	}

	public static final String TAG_TLM_MODEL_ID = "tlm_model_id";
	public static final String TAG_IS_YSM_MODEL = "is_ysm_model";
	public static final String TAG_YSM_MODEL_ID = "ysm_model_id";
	public static final String TAG_YSM_MODEL_TEXTURE = "ysm_model_texture";
	public static final String TAG_YSM_MODEL_NAME = "ysm_model_name";

	public static final EntityDataAccessor<String> ACC_TLM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().tlmModelId);
	public static final EntityDataAccessor<Boolean> ACC_IS_YSM_MODEL = EntityData.define(MaidMob.class, EntityDataSerializers.BOOLEAN, RENDERER_TYPE.defaultRenderAsset().isYsmModel);
	public static final EntityDataAccessor<String> ACC_YSM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().ysmModelId);
	public static final EntityDataAccessor<String> ACC_YSM_MODEL_TEXTURE = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultRenderAsset().ysmModelTexture);
	public static final EntityDataAccessor<Component> ACC_YSM_MODEL_NAME = EntityData.define(MaidMob.class, EntityDataSerializers.COMPONENT, RENDERER_TYPE.defaultRenderAsset().ysmModelName);
	public static final EntityDataAccessor<ChatBubbleDataCollection> ACC_CHAT_BUBBLE = EntityData.define(MaidMob.class, ChatBubbleRegister.INSTANCE, ChatBubbleDataCollection.getEmptyCollection());

	private EntityMaid renderingEntity;

	/**
	 * 虚假女仆实体，不实际存在于游戏中，仅仅用于模型渲染
	 * 
	 * @return
	 */
	@Override
	public final EntityMaid renderingEntity() {
		return renderingEntity;
	}

	@Override
	public final Entity bindEntity() {
		return this;
	}

	public MaidMob(EntityType<BaseMob> entityType, EntityRendererType<String> renderType, Level level) {
		super(entityType, renderType, level);
		renderingEntity = RenderableMaid.blankEntityMaid(this);
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.loadString(compound, TAG_TLM_MODEL_ID, entityData, ACC_TLM_MODEL_ID);
		EntityData.loadBool(compound, TAG_IS_YSM_MODEL, entityData, ACC_IS_YSM_MODEL);
		EntityData.loadString(compound, TAG_YSM_MODEL_ID, entityData, ACC_YSM_MODEL_ID);
		EntityData.loadString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ACC_YSM_MODEL_TEXTURE);
		EntityData.loadComponent(compound, TAG_YSM_MODEL_NAME, entityData, ACC_YSM_MODEL_NAME);
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.storeString(compound, TAG_TLM_MODEL_ID, entityData, ACC_TLM_MODEL_ID);
		EntityData.storeBool(compound, TAG_IS_YSM_MODEL, entityData, ACC_IS_YSM_MODEL);
		EntityData.storeString(compound, TAG_YSM_MODEL_ID, entityData, ACC_YSM_MODEL_ID);
		EntityData.storeString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ACC_YSM_MODEL_TEXTURE);
		EntityData.storeComponent(compound, TAG_YSM_MODEL_NAME, entityData, ACC_YSM_MODEL_NAME);
	}

	@Override
	public void tick() {
		super.tick();
		this.syncRenderingEntity();
	}

	@Override
	public String getModelId() {
		return entityData.get(ACC_TLM_MODEL_ID);
	}

	/**
	 * 设置TLM模型ID
	 * 
	 * @param modelId 模型ID
	 */
	@Override
	public void setModelId(String modelId) {
		entityData.set(ACC_TLM_MODEL_ID, modelId);
	}

	@Override
	public boolean isYsmModel() {
		return entityData.get(ACC_IS_YSM_MODEL);
	}

	@Override
	public void setIsYsmModel(boolean isYsmModel) {
		entityData.set(ACC_IS_YSM_MODEL, isYsmModel);
	}

	@Override
	public String getYsmModelId() {
		return entityData.get(ACC_YSM_MODEL_ID);
	}

	@Override
	public void setYsmModelId(String ysmModelId) {
		entityData.set(ACC_YSM_MODEL_ID, ysmModelId);
	}

	@Override
	public String getYsmModelTexture() {
		return entityData.get(ACC_YSM_MODEL_TEXTURE);
	}

	@Override
	public void setYsmModelTexture(String ysmModelTexture) {
		entityData.set(ACC_YSM_MODEL_TEXTURE, ysmModelTexture);
	}

	@Override
	public Component getYsmModelName() {
		return entityData.get(ACC_YSM_MODEL_NAME);
	}

	@Override
	public void setYsmModelName(Component ysmModelName) {
		entityData.set(ACC_YSM_MODEL_NAME, ysmModelName);
	}
}
