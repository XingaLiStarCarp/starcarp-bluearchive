package mcbase.entity.mob;

import java.util.List;

import mcbase.entity.EntityData;
import mcbase.entity.EntityRendererType;
import mcbase.entity.EntityDefaultAttributes.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

/**
 * 原版玩家模型实体
 */
public class HumanoidMob extends BaseMob {
	public static final EntityRendererType<ResourceLocation> MALE_RENDERER_TYPE = new EntityRendererType<>();
	public static final EntityRendererType<ResourceLocation> FEMALE_RENDERER_TYPE = new EntityRendererType<>();

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newMaleType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, MALE_RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newMaleType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newMaleType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newFemaleType(Class<T> entityClazz, float width, float height, String typeName, MobCategory category, List<Entry> attributes) {
		return BaseMob.newType(entityClazz, width, height, FEMALE_RENDERER_TYPE, typeName, category, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newFemaleType(Class<T> entityClazz, float width, float height, String typeName, List<Entry> attributes) {
		return newFemaleType(entityClazz, width, height, typeName, MobCategory.MISC, attributes);
	}

	static {
		MALE_RENDERER_TYPE.setDefaultRenderAsset(ResourceLocation.parse("minecraft:textures/entity/player/wide/steve.png"));
		FEMALE_RENDERER_TYPE.setDefaultRenderAsset(ResourceLocation.parse("minecraft:textures/entity/player/slim/alex.png"));
	}

	public static final float HUMANOID_WIDTH = 0.6f;
	public static final float HUMANOID_HEIGHT = 1.8f;

	public static final String TAG_SKIN = "skin";

	public static final EntityDataAccessor<String> ACC_SKIN = EntityData.define(HumanoidMob.class, EntityDataSerializers.STRING, "");

	public HumanoidMob(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> renderType, Level level) {
		super(entityType, renderType, level);
	}

	public HumanoidMob(EntityType<BaseMob> entityType, EntityRendererType<ResourceLocation> renderType, ResourceLocation skin, Level level) {
		super(entityType, renderType, level);
		this.setSkin(skin);
	}

	private ResourceLocation skin;

	/**
	 * 获取皮肤
	 * 
	 * @return
	 */
	public final ResourceLocation getSkin() {
		if (skin == null) {
			String storedSkin = entityData.get(ACC_SKIN);
			if (EntityData.validate(storedSkin)) {
				skin = ResourceLocation.parse(storedSkin);// 缓存
			} else {
				setSkin(this.defaultRenderAsset(ResourceLocation.class));
			}
		}
		return skin;
	}

	/**
	 * 设置皮肤
	 * 
	 * @param skin
	 */
	public final void setSkin(ResourceLocation skin) {
		this.skin = skin;
		entityData.set(ACC_SKIN, skin.toString());
	}

	public final void setSkin(String skin) {
		setSkin(ResourceLocation.parse(skin));
	}

	@Override
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {
		compound.putString(TAG_SKIN, getSkin().toString());
	}

	@Override
	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {
		EntityData.loadString(compound, TAG_SKIN, entityData, ACC_SKIN);
	}
}
