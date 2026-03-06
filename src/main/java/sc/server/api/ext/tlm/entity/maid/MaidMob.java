package sc.server.api.ext.tlm.entity.maid;

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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;
import sc.server.api.entity.BaseMob;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.entity.EntityRendererType;

/**
 * Touhou Little Maid模组的女仆类型实体，需要配合渲染器EntityMaidRenderer使用
 */
public class MaidMob extends BaseMob implements SyncedRenderMaid {

	public static final EntityRendererType<MaidModelAsset> RENDERER_TYPE = new EntityRendererType<>();

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	static {
		RENDERER_TYPE.setDefaultRenderAsset(new MaidModelAsset());
	}

	public static final String TAG_TLM_MODEL_ID = "tlm_model_id";
	public static final String TAG_IS_YSM_MODEL = "is_ysm_model";
	public static final String TAG_YSM_MODEL_ID = "ysm_model_id";
	public static final String TAG_YSM_MODEL_TEXTURE = "ysm_model_texture";
	public static final String TAG_YSM_MODEL_NAME = "ysm_model_name";

	/**
	 * NBT储存数据
	 */
	public static final EntityDataAccessor<String> ST_TLM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultAsset().getTlmModelId());
	public static final EntityDataAccessor<Boolean> ST_IS_YSM_MODEL = EntityData.define(MaidMob.class, EntityDataSerializers.BOOLEAN, RENDERER_TYPE.defaultAsset().isYsmModel());
	public static final EntityDataAccessor<String> ST_YSM_MODEL_ID = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultAsset().getYsmModelId());
	public static final EntityDataAccessor<String> ST_YSM_MODEL_TEXTURE = EntityData.define(MaidMob.class, EntityDataSerializers.STRING, RENDERER_TYPE.defaultAsset().getYsmModelTexture());
	public static final EntityDataAccessor<Component> ST_YSM_MODEL_NAME = EntityData.define(MaidMob.class, EntityDataSerializers.COMPONENT, RENDERER_TYPE.defaultAsset().getYsmModelName());

	public static final EntityDataAccessor<ChatBubbleDataCollection> RT_CHAT_BUBBLE = EntityData.define(MaidMob.class, ChatBubbleRegister.INSTANCE, ChatBubbleDataCollection.getEmptyCollection());

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

	public MaidMob(EntityType<BaseMob> entityType, EntityRendererType<MaidModelAsset> renderType, Level level) {
		super(entityType, renderType, level);
		renderingEntity = this.blankRenderingEntity(this);
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.loadString(compound, TAG_TLM_MODEL_ID, entityData, ST_TLM_MODEL_ID);
		EntityData.loadBool(compound, TAG_IS_YSM_MODEL, entityData, ST_IS_YSM_MODEL);
		EntityData.loadString(compound, TAG_YSM_MODEL_ID, entityData, ST_YSM_MODEL_ID);
		EntityData.loadString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ST_YSM_MODEL_TEXTURE);
		EntityData.loadComponent(compound, TAG_YSM_MODEL_NAME, entityData, ST_YSM_MODEL_NAME);
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.storeString(compound, TAG_TLM_MODEL_ID, entityData, ST_TLM_MODEL_ID);
		EntityData.storeBool(compound, TAG_IS_YSM_MODEL, entityData, ST_IS_YSM_MODEL);
		EntityData.storeString(compound, TAG_YSM_MODEL_ID, entityData, ST_YSM_MODEL_ID);
		EntityData.storeString(compound, TAG_YSM_MODEL_TEXTURE, entityData, ST_YSM_MODEL_TEXTURE);
		EntityData.storeComponent(compound, TAG_YSM_MODEL_NAME, entityData, ST_YSM_MODEL_NAME);
	}

	@Override
	public void tick() {
		super.tick();
		this.syncRenderingEntity();
	}

	protected void sleepOnBed() {
		if (isSleeping()) {
			getSleepingPos().ifPresent(pos -> setPos(pos.getX() + 0.5, pos.getY() + 0.5625, pos.getZ() + 0.5));
			setDeltaMovement(Vec3.ZERO);
			if (!isSilent()) {
				this.setSilent(true);
			}
		} else {
			if (isSilent()) {
				this.setSilent(false);
			}
		}
	}

	@Override
	public String getTlmModelId() {
		return entityData.get(ST_TLM_MODEL_ID);
	}

	/**
	 * 设置TLM模型ID
	 * 
	 * @param modelId 模型ID
	 */
	@Override
	public void setTlmModelId(String modelId) {
		entityData.set(ST_TLM_MODEL_ID, modelId);
	}

	@Override
	public boolean isYsmModel() {
		return entityData.get(ST_IS_YSM_MODEL);
	}

	@Override
	public void setIsYsmModel(boolean isYsmModel) {
		entityData.set(ST_IS_YSM_MODEL, isYsmModel);
	}

	@Override
	public String getYsmModelId() {
		return entityData.get(ST_YSM_MODEL_ID);
	}

	@Override
	public void setYsmModelId(String ysmModelId) {
		entityData.set(ST_YSM_MODEL_ID, ysmModelId);
	}

	@Override
	public String getYsmModelTexture() {
		return entityData.get(ST_YSM_MODEL_TEXTURE);
	}

	@Override
	public void setYsmModelTexture(String ysmModelTexture) {
		entityData.set(ST_YSM_MODEL_TEXTURE, ysmModelTexture);
	}

	@Override
	public Component getYsmModelName() {
		return entityData.get(ST_YSM_MODEL_NAME);
	}

	@Override
	public void setYsmModelName(Component ysmModelName) {
		entityData.set(ST_YSM_MODEL_NAME, ysmModelName);
	}

	@Override
	public MaidModelAsset modelAsset() {
		return new MaidModelAsset(this.getTlmModelId(), this.isYsmModel(), this.getYsmModelId(), this.getYsmModelTexture(), this.getYsmModelName());
	}

	@Override
	public boolean isSwingingArms() {
		return this.swinging;
	}
}
