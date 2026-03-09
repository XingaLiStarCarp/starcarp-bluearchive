package mcbase.extended.entity;

import java.util.List;

import com.github.tartaricacid.touhoulittlemaid.datagen.tag.TagEntity;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import mcbase.entity.EntityRendererType;
import mcbase.entity.Humanoid.HumanoidEntity;
import mcbase.entity.data.EntityDefaultAttributes.Entry;
import mcbase.entity.data.SynchedEntityDataOp;
import mcbase.entity.mob.BaseMob;
import mcbase.entity.mob.HumanoidMob;
import mcbase.extended.tlm.entity.maid.MaidMob;
import mcbase.extended.tlm.entity.maid.SyncedRenderMaid.SyncedRenderMaidEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

/**
 * 通用人型实体，支持TLM、YSM和原版玩家模型
 */
public class GeneralHumanoidMob extends BaseMob implements HumanoidEntity, SyncedRenderMaidEntity {

	public static class GeneralHumanoidModelInfo {
		public static final int TYPE_PLAYER = 0;
		public static final int TYPE_MAID = 1;

		public int renderType;
		public PlayerModelAsset vallinaPlayerModel;
		public MaidModelAsset maidModel;

		private GeneralHumanoidModelInfo(int renderType, PlayerModelAsset vallinaPlayerModel, MaidModelAsset maidModel) {
			this.renderType = renderType;
			this.vallinaPlayerModel = vallinaPlayerModel;
			this.maidModel = maidModel;
		}

		public static final GeneralHumanoidModelInfo pack(int renderType, PlayerModelAsset vallinaPlayerModel, MaidModelAsset maidModel) {
			return new GeneralHumanoidModelInfo(renderType, vallinaPlayerModel, maidModel);
		}

		public static final GeneralHumanoidModelInfo DEFAULT = GeneralHumanoidModelInfo.pack(GeneralHumanoidModelInfo.TYPE_PLAYER, HumanoidMob.RENDERER_TYPE.defaultAsset(), MaidMob.RENDERER_TYPE.defaultAsset());
	}

	public static final EntityRendererType<GeneralHumanoidModelInfo> RENDERER_TYPE = new EntityRendererType<>();

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	static {
		RENDERER_TYPE.setDefaultRenderAsset(GeneralHumanoidModelInfo.DEFAULT);
	}

	public static final String TAG_RENDER_TYPE = "render_type";

	/**
	 * 渲染类型
	 */
	public static final EntityDataAccessor<Integer> ST_RENDER_TYPE = SynchedEntityDataOp.define(GeneralHumanoidMob.class, EntityDataSerializers.INT, RENDERER_TYPE.defaultAsset().renderType);

	/**
	 * 原版玩家数据
	 */
	public static final EntityDataAccessor<?>[] PLAYER_ACCS = HumanoidEntity.defineAllHumanoidEntityData(GeneralHumanoidMob.class);

	/**
	 * 女仆数据
	 */
	public static final EntityDataAccessor<?>[] MAID_ACCS = SyncedRenderMaidEntity.defineAllMaidEntityData(GeneralHumanoidMob.class);

	@Override
	@SuppressWarnings("rawtypes")
	public EntityDataAccessor[] maidEntityDataAccs() {
		return MAID_ACCS;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public EntityDataAccessor[] humanoidEntityDataAccs() {
		return PLAYER_ACCS;
	}

	protected final EntityMaid renderingEntity;

	@Override
	public final EntityMaid renderingEntity() {
		return renderingEntity;
	}

	@Override
	public final Entity bindEntity() {
		return this;
	}

	protected ResourceLocation skin = HumanoidMob.RENDERER_TYPE.defaultAsset().getSkin();

	public GeneralHumanoidMob(EntityType<BaseMob> entityType, EntityRendererType<GeneralHumanoidModelInfo> renderType, Level level) {
		super(entityType, renderType, level);
		renderingEntity = this.blankRenderingEntity(this);
	}

	public int getRenderType() {
		return entityData.get(ST_RENDER_TYPE);
	}

	public void setRenderType(int type) {
		entityData.set(ST_RENDER_TYPE, type);
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		SynchedEntityDataOp.loadInt(compound, TAG_RENDER_TYPE, entityData, ST_RENDER_TYPE);
		this.loadAllHumanoidEntityData(compound, entityData);
		this.loadAllMaidEntityData(compound, entityData);
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		SynchedEntityDataOp.storeInt(compound, TAG_RENDER_TYPE, entityData, ST_RENDER_TYPE);
		this.storeAllHumanoidEntityData(compound, entityData);
		this.storeAllMaidEntityData(compound, entityData);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> acc) {
		super.onSyncedDataUpdated(acc);
		this.onHumanoidEntitySyncedDataUpdated(acc);
	}

	@Override
	public void tick() {
		super.tick();
		switch (this.getRenderType()) {
		case GeneralHumanoidModelInfo.TYPE_MAID: {
			this.syncRenderingEntity();
			break;
		}
		}
	}

	@Override
	public void rideTick() {
		super.rideTick();
		switch (this.getRenderType()) {
		case GeneralHumanoidModelInfo.TYPE_MAID: {
			Entity vehicle = this.getVehicle();
			if (vehicle != null && !vehicle.getType().is(TagEntity.MAID_VEHICLE_ROTATE_BLOCKLIST)) {
				this.setYHeadRot(vehicle.getYRot());
				this.setYBodyRot(vehicle.getYRot());
			}
			break;
		}
		}
	}

	@Override
	public ResourceLocation getSkin() {
		return skin;
	}

	@Override
	public boolean isSwingingArms() {
		return this.swinging;
	}

	@Override
	public void updateSkin(String skinId) {
		this.skin = ResourceLocation.parse(skinId);
	}

}
