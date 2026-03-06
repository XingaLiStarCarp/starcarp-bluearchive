package sc.server.api.entity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import cpw.mods.modlauncher.api.INameMappingService;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import javabase.HashKey;
import jvmsp.reflection;
import jvmsp.symbols;
import jvmsp.unsafe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import sc.server.ModEntry;
import sc.server.api.capability.CapabilityData;

/**
 * 实体数据操作
 */
@SuppressWarnings("unchecked")
public class EntityData {

	private static MethodHandle createDataItem;

	static {
		createDataItem = symbols.find_virtual_method(SynchedEntityData.class,
				ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_135385_"),
				MethodType.methodType(void.class, EntityDataAccessor.class, Object.class));
	}

	/**
	 * 安全地初始化字段。如果字段已经初始化则忽略该操作。
	 * 
	 * @param <T>
	 * @param synchedEntityData
	 * @param accessor
	 * @param value
	 */
	public static <T> void define(SynchedEntityData synchedEntityData, EntityDataAccessor<T> accessor, T value) {
		int i = accessor.getId();
		if (i > 254) {
			throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
		} else if (synchedEntityData.hasItem(accessor)) {
			return;// 如果已经初始化，则不再重复初始化
		} else if (EntityDataSerializers.getSerializedId(accessor.getSerializer()) < 0) {
			throw new IllegalArgumentException("Unregistered serializer " + accessor.getSerializer() + " for " + i + "!");
		} else {
			try {
				createDataItem.invoke(synchedEntityData, accessor, value);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	private static final HashMap<Class<? extends Entity>, ArrayList<EntityDataAccessor<?>>> DATA_ENTRIES = new HashMap<>();

	/**
	 * 不同EntityDataAccessor分别对应的默认值。<br>
	 * EntityDataAccessor的id是字段索引，且该索引从0开始包含全部父类的字段，最大为254。<br>
	 * EntityDataAccessor的hashCode()为id，且equals()也是判断id是否相等，因此两个id相同的EntityDataAccessor作为key在HashMap中对应同一个value。<br>
	 */
	private static final HashMap<HashKey<EntityDataAccessor<?>>, Object> DEFAULT_VALUES = new HashMap<>();

	/**
	 * 获取指定实体类定义的数据
	 * 
	 * @param entityClazz
	 * @return
	 */
	public static ArrayList<EntityDataAccessor<?>> dataEntries(Class<? extends Entity> entityClazz) {
		return DATA_ENTRIES.computeIfAbsent(entityClazz, (k) -> new ArrayList<>());
	}

	public static void setDefaultValue(EntityDataAccessor<?> acc, Object value) {
		DEFAULT_VALUES.put(HashKey.of(acc), value);
	}

	public static Object getDefaultValue(EntityDataAccessor<?> acc) {
		return DEFAULT_VALUES.get(HashKey.of(acc));
	}

	/**
	 * 为指定类型的实体创建一个新字段
	 * 
	 * @param <_T>
	 * @param entityClazz  实体类
	 * @param dataType     字段数据类型
	 * @param defaultValue 字段默认值
	 * @return
	 */
	public static <_T> EntityDataAccessor<_T> define(Class<? extends Entity> entityClazz, EntityDataSerializer<_T> dataType, _T defaultValue) {
		EntityDataAccessor<_T> acc = SynchedEntityData.defineId(entityClazz, dataType);
		dataEntries(entityClazz).add(acc);
		setDefaultValue(acc, Objects.requireNonNull(defaultValue));
		return acc;
	}

	/**
	 * 指定的实体类是否有字段。<br>
	 * 只有通过本类的define()方法加入的字段才能识别。<br>
	 * 
	 * @param entityClazz
	 * @return
	 */
	public static boolean hasEntries(Class<? extends Entity> entityClazz) {
		return DATA_ENTRIES.containsKey(entityClazz);
	}

	@SuppressWarnings({ "rawtypes" })
	public static void reset(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		ArrayList<EntityDataAccessor<?>> entries = dataEntries(entityClazz);
		for (EntityDataAccessor<?> acc : entries) {
			entityData.set((EntityDataAccessor) acc, getDefaultValue(acc));
		}
	}

	public static void reset(Entity entity) {
		reset(entity.getClass(), entity.getEntityData());
	}

	/**
	 * 运行时初始化实体数据。<br>
	 * 仅初始化实体自身类的数据，父类的数据不会初始化。<br>
	 * 
	 * @param entityClazz
	 * @param entityData
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void define(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		if (hasEntries(entityClazz)) {
			ArrayList<EntityDataAccessor<?>> entries = dataEntries(entityClazz);
			for (EntityDataAccessor<?> acc : entries) {
				define(entityData, (EntityDataAccessor) acc, getDefaultValue(acc));
			}
		}
	}

	public static void define(Entity entity) {
		define(entity.getClass(), entity.getEntityData());
	}

	/**
	 * 递归地初始化实体类自身及其父类的字段。
	 * 
	 * @param entityClazz
	 * @param entityData
	 */
	public static void defineAll(Class<? extends Entity> entityClazz, SynchedEntityData entityData) {
		while (entityClazz != Entity.class) {
			define(entityClazz, entityData);
			entityClazz = (Class<? extends Entity>) entityClazz.getSuperclass();
		}
	}

	public static void defineAll(Entity entity) {
		defineAll(entity.getClass(), entity.getEntityData());
	}

	/**
	 * 判断一个字符串属性是否是有效值
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validate(String str) {
		return str != null && !"".equals(str);
	}

	public static String string(Component component) {
		return Component.Serializer.toJson(component);
	}

	public static MutableComponent component(String component) {
		return component == null ? Component.empty() : Component.Serializer.fromJson(component);
	}

	public static String string(ResourceLocation loc) {
		return loc.toString();
	}

	public static ResourceLocation resourceLocation(String loc) {
		return ResourceLocation.parse(loc);
	}

	public static void loadString(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<String> accessor) {
		String data = null;
		if (!compound.contains(tag, Tag.TAG_STRING)) {
			data = (String) getDefaultValue(accessor);
		} else {
			data = compound.getString(tag);
		}
		entityData.set(accessor, data);
	}

	/**
	 * 加载序列化后的对象到entityData，并返回反序列化后的对象。<br>
	 * 
	 * @param <_T>
	 * @param compound
	 * @param tag
	 * @param entityData
	 * @param accessor
	 * @param serializer
	 * @param deserializer
	 * @return
	 */
	public static <_T> _T loadObject(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<String> accessor, Function<_T, String> serializer, Function<String, _T> deserializer) {
		_T obj = null;
		String data = null;// obj序列化后的字符串

		if (!compound.contains(tag, Tag.TAG_STRING)) {
			obj = (_T) getDefaultValue(accessor);
			data = serializer.apply(obj);
		} else {
			data = compound.getString(tag);
			obj = deserializer.apply(data);
		}
		entityData.set(accessor, data);
		return obj;
	}

	public static void loadComponent(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Component> accessor) {
		Component data = null;
		if (!compound.contains(tag, Tag.TAG_STRING)) {
			data = (Component) getDefaultValue(accessor);
		} else {
			data = component(compound.getString(tag));
		}
		entityData.set(accessor, data);
	}

	public static void loadBool(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Boolean> accessor) {
		boolean data = false;
		if (!compound.contains(tag, Tag.TAG_BYTE)) {
			data = (boolean) getDefaultValue(accessor);
		} else {
			data = compound.getBoolean(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadByte(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Byte> accessor) {
		byte data = 0;
		if (!compound.contains(tag, Tag.TAG_BYTE)) {
			data = (byte) getDefaultValue(accessor);
		} else {
			data = compound.getByte(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadShort(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Short> accessor) {
		short data = 0;
		if (!compound.contains(tag, Tag.TAG_SHORT)) {
			data = (short) getDefaultValue(accessor);
		} else {
			data = compound.getShort(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadInt(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Integer> accessor) {
		int data = 0;
		if (!compound.contains(tag, Tag.TAG_INT)) {
			data = (int) getDefaultValue(accessor);
		} else {
			data = compound.getInt(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadLong(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Long> accessor) {
		long data = 0;
		if (!compound.contains(tag, Tag.TAG_LONG)) {
			data = (long) getDefaultValue(accessor);
		} else {
			data = compound.getLong(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadFloat(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Float> accessor) {
		float data = 0;
		if (!compound.contains(tag, Tag.TAG_FLOAT)) {
			data = (int) getDefaultValue(accessor);
		} else {
			data = compound.getFloat(tag);
		}
		entityData.set(accessor, data);
	}

	public static void loadDouble(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Double> accessor) {
		double data = 0;
		if (!compound.contains(tag, Tag.TAG_DOUBLE)) {
			data = (double) getDefaultValue(accessor);
		} else {
			data = compound.getDouble(tag);
		}
		entityData.set(accessor, data);
	}

	public static void storeString(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<String> accessor) {
		String data = entityData.get(accessor);
		if (data == null) {
			data = (String) getDefaultValue(accessor);
		}
		compound.putString(tag, data);
	}

	public static void storeComponent(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Component> accessor) {
		Component data = entityData.get(accessor);
		if (data == null) {
			data = (Component) getDefaultValue(accessor);
		}
		compound.putString(tag, string(data));
	}

	public static void storeBool(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Boolean> accessor) {
		Boolean data = entityData.get(accessor);
		if (data == null) {
			data = (Boolean) getDefaultValue(accessor);
		}
		compound.putByte(tag, data ? (byte) 1 : (byte) 0);
	}

	public static void storeByte(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Byte> accessor) {
		Byte data = entityData.get(accessor);
		if (data == null) {
			data = (Byte) getDefaultValue(accessor);
		}
		compound.putByte(tag, data);
	}

	public static void storeShort(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Short> accessor) {
		Short data = entityData.get(accessor);
		if (data == null) {
			data = (Short) getDefaultValue(accessor);
		}
		compound.putShort(tag, data);
	}

	public static void storeInt(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Integer> accessor) {
		Integer data = entityData.get(accessor);
		if (data == null) {
			data = (Integer) getDefaultValue(accessor);
		}
		compound.putInt(tag, data);
	}

	public static void storeLong(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Long> accessor) {
		Long data = entityData.get(accessor);
		if (data == null) {
			data = (Long) getDefaultValue(accessor);
		}
		compound.putLong(tag, data);
	}

	public static void storeFloat(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Float> accessor) {
		Float data = entityData.get(accessor);
		if (data == null) {
			data = (Float) getDefaultValue(accessor);
		}
		compound.putFloat(tag, data);
	}

	public static void storeDouble(CompoundTag compound, String tag, SynchedEntityData entityData, EntityDataAccessor<Double> accessor) {
		Double data = entityData.get(accessor);
		if (data == null) {
			data = (Double) getDefaultValue(accessor);
		}
		compound.putDouble(tag, data);
	}

	private static Field Entity_entityData;
	private static Field Entity_position;
	private static Field Entity_blockPosition;
	private static Field Entity_chunkPosition;
	private static Field Entity_forgeFluidTypeHeight;
	private static Field Entity_level;
	private static Field Entity_passengers;
	private static MethodHandle defineSynchedData;
	private static EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = null;
	private static EntityDataAccessor<Integer> DATA_AIR_SUPPLY_ID = null;
	private static EntityDataAccessor<Optional<Component>> DATA_CUSTOM_NAME = null;
	private static EntityDataAccessor<Boolean> DATA_CUSTOM_NAME_VISIBLE = null;
	private static EntityDataAccessor<Boolean> DATA_SILENT = null;
	private static EntityDataAccessor<Boolean> DATA_NO_GRAVITY = null;
	private static EntityDataAccessor<Pose> DATA_POSE = null;
	private static EntityDataAccessor<Integer> DATA_TICKS_FROZEN = null;

	static {
		Entity_entityData = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19804_"));
		Entity_position = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19825_"));
		Entity_blockPosition = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19826_"));
		Entity_chunkPosition = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_185933_"));
		Entity_level = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19853_"));
		Entity_passengers = reflection.find_declared_field(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19823_"));
		Entity_forgeFluidTypeHeight = reflection.find_declared_field(Entity.class, "forgeFluidTypeHeight");
		defineSynchedData = symbols.find_virtual_method(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "m_8097_"), void.class);
		DATA_SHARED_FLAGS_ID = (EntityDataAccessor<Byte>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19805_"));
		DATA_AIR_SUPPLY_ID = (EntityDataAccessor<Integer>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19832_"));
		DATA_CUSTOM_NAME = (EntityDataAccessor<Optional<Component>>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19833_"));
		DATA_CUSTOM_NAME_VISIBLE = (EntityDataAccessor<Boolean>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19834_"));
		DATA_SILENT = (EntityDataAccessor<Boolean>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19835_"));
		DATA_NO_GRAVITY = (EntityDataAccessor<Boolean>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19836_"));
		DATA_POSE = (EntityDataAccessor<Pose>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_19806_"));
		DATA_TICKS_FROZEN = (EntityDataAccessor<Integer>) unsafe.read_static_reference(Entity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_146800_"));
	}

	/**
	 * 创建并初始化entity关联的空entityData。<br>
	 * 这些数据字段是Entity类所属的，无法通过defineSynchedData()定义。<br>
	 * 
	 * @param entity
	 * @return
	 */
	public static final SynchedEntityData newBasicEntityData(Entity entity) {
		SynchedEntityData entityData = new SynchedEntityData(entity);
		entityData.define(DATA_SHARED_FLAGS_ID, (byte) 0);
		entityData.define(DATA_AIR_SUPPLY_ID, 300);
		entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
		entityData.define(DATA_CUSTOM_NAME, Optional.empty());
		entityData.define(DATA_SILENT, false);
		entityData.define(DATA_NO_GRAVITY, false);
		entityData.define(DATA_POSE, Pose.STANDING);
		entityData.define(DATA_TICKS_FROZEN, 0);
		return entityData;
	}

	/**
	 * 两种实体的所有entityData字段及其顺序必须完全相同，否则使用EntityDataAccessor会导致访问到错误的或不存在的值。<br>
	 * 
	 * @param <_T>
	 * @param entityClazz
	 * @param entityData
	 * @return
	 */
	public static final <_T extends Entity> void setEntityData(_T entity, SynchedEntityData entityData) {
		unsafe.write(entity, EntityData.Entity_entityData, entityData);
	}

	public static final <_T extends Entity> void setPosition(_T entity, Vec3 pos, BlockPos blockPos, ChunkPos chunkPos) {
		unsafe.write(entity, EntityData.Entity_position, pos);
		unsafe.write(entity, EntityData.Entity_blockPosition, blockPos);
		unsafe.write(entity, EntityData.Entity_chunkPosition, chunkPos);
	}

	public static final <_T extends Entity> Vec3 getPosition(_T entity) {
		return (Vec3) unsafe.read_reference(entity, EntityData.Entity_position);
	}

	public static final <_T extends Entity> BlockPos getBlockPosition(_T entity) {
		return (BlockPos) unsafe.read_reference(entity, EntityData.Entity_blockPosition);
	}

	public static final <_T extends Entity> ChunkPos getChunkPosition(_T entity) {
		return (ChunkPos) unsafe.read_reference(entity, EntityData.Entity_chunkPosition);
	}

	public static final <_T extends Entity> void setFluidTypeHeight(_T entity, Object2DoubleMap<net.minecraftforge.fluids.FluidType> fluidTypeHeight) {
		unsafe.write(entity, EntityData.Entity_forgeFluidTypeHeight, fluidTypeHeight);
	}

	public static final <_T extends Entity> Object2DoubleMap<FluidType> getFluidTypeHeight(_T entity) {
		return (Object2DoubleMap<FluidType>) unsafe.read_reference(entity, EntityData.Entity_forgeFluidTypeHeight);
	}

	public static final <_T extends Entity> void setLevel(_T entity, Level level) {
		unsafe.write(entity, EntityData.Entity_level, level);
	}

	public static final <_T extends Entity> void setPassengers(_T entity, ImmutableList<Entity> passengers) {
		unsafe.write(entity, EntityData.Entity_passengers, passengers);
	}

	private static final long TOP_Entity;
	private static final long BOTTOM_Entity;

	private static final long BOTTOM_LivingEntity;

	private static final long TOP_Mob;
	private static final long BOTTOM_Mob;

	static {
		TOP_Entity = unsafe.top_offset(Entity.class);
		BOTTOM_Entity = unsafe.bottom_offset(Entity.class);
		BOTTOM_LivingEntity = unsafe.bottom_offset(LivingEntity.class);
		TOP_Mob = unsafe.top_offset(Mob.class);
		BOTTOM_Mob = unsafe.bottom_offset(Mob.class);
	}

	/**
	 * 拷贝从Entity到Mob继承链的实体数据
	 * 
	 * @param src
	 * @param dest
	 */
	public static final void copyData(Entity src, Entity dest) {
		if (src instanceof Mob srcMob) {
			if (dest instanceof Mob destMob)
				EntityData.copyMobData(srcMob, destMob);
			else if (dest instanceof LivingEntity destLivingEntity)
				EntityData.copyLivingEntityData(srcMob, destLivingEntity);
			else if (dest instanceof Entity destEntity)
				EntityData.copyEntityData(srcMob, destEntity);
		} else if (src instanceof LivingEntity srcLivingEntity) {
			if (dest instanceof LivingEntity destLivingEntity)
				EntityData.copyLivingEntityData(srcLivingEntity, destLivingEntity);
			else if (dest instanceof Entity destEntity)
				EntityData.copyEntityData(srcLivingEntity, destEntity);
		} else if (src instanceof Entity srcEntity) {
			if (dest instanceof Entity destEntity)
				EntityData.copyEntityData(srcEntity, destEntity);
		}
	}

	/**
	 * 拷贝Entity.class的成员字段，但entityData保留原本的。
	 * 
	 * @param <_T>
	 * @param src
	 * @param dest
	 */
	public static final void copyEntityData(Entity src, Entity dest) {
		SynchedEntityData entityData = dest.getEntityData();
		CapabilityProvider<?> capabilityProvider = CapabilityData.copy(dest);// TOP_Entity小于CapabilityProvider的成员字段终点，因此CapabilityProvider的成员字段需要先拷贝。
		unsafe.copy_member_fields(src, dest, TOP_Entity, BOTTOM_Entity);// Forge新加了字段导致BOTTOM_Entity比TOP_LivingEntity还大，因此可能会拷贝部分LivingEntity的数据
		CapabilityData.copy(capabilityProvider, dest);
		EntityData.setEntityData(dest, entityData);
	}

	/**
	 * 创建一个具有该实体种类默认entityData的空实体。<br>
	 * 虚假实体不实际存在于游戏，因此它也无法自动更新，无法自动接收处理Packet和SynchedEntityData。
	 * 
	 * @param <_T>
	 * @param entityClazz
	 * @return
	 */
	public static final <_T extends Entity> _T blankEntity(Class<_T> entityClazz) {
		_T entity = CapabilityData.construct(entityClazz, Entity.class);// 调用CapabilityProvider(Entity.class)初始化一个Entity对象
		setEntityData(entity, newBasicEntityData(entity));
		try {
			defineSynchedData.invokeExact(entity);
		} catch (Throwable ex) {
			ModEntry.LOGGER.error("create blank entity '" + entityClazz + "' failed", ex);
		}
		return entity;
	}

	private static Field LivingEntity_attributes;
	private static Field LivingEntity_walkAnimation;

	static {
		LivingEntity_attributes = reflection.find_declared_field(LivingEntity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_20943_"));
		LivingEntity_walkAnimation = reflection.find_declared_field(LivingEntity.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_267362_"));
	}

	public static final <_T extends LivingEntity> void setAttributes(_T entity, AttributeMap attributes) {
		unsafe.write(entity, EntityData.LivingEntity_attributes, attributes);
	}

	public static final <_T extends LivingEntity> AttributeMap getAttributes(_T entity) {
		return (AttributeMap) unsafe.read_reference(entity, EntityData.LivingEntity_attributes);
	}

	public static final <_T extends LivingEntity> void setWalkAnimation(_T entity, WalkAnimationState walkAnimation) {
		unsafe.write(entity, EntityData.LivingEntity_walkAnimation, walkAnimation);
	}

	public static final <_T extends LivingEntity> WalkAnimationState getWalkAnimation(_T entity) {
		return (WalkAnimationState) unsafe.read_reference(entity, EntityData.LivingEntity_walkAnimation);
	}

	public static final void copyLivingEntityData(LivingEntity src, LivingEntity dest) {
		SynchedEntityData entityData = dest.getEntityData();
		// Forge新加了字段导致Entity类和LivingEntity类字段偏移量区间有重叠，因此需要在整体拷贝完成以后再次设定entityData
		CapabilityProvider<?> capabilityProvider = CapabilityData.copy(dest);
		unsafe.copy_member_fields(src, dest, TOP_Entity, BOTTOM_LivingEntity);
		CapabilityData.copy(capabilityProvider, dest);
		EntityData.setEntityData(dest, entityData);
	}

	private static Field Mob_handItems;
	private static Field Mob_armorItems;
	private static Field Mob_navigation;

	static {
		Mob_handItems = reflection.find_declared_field(Mob.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_21350_"));
		Mob_armorItems = reflection.find_declared_field(Mob.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_21351_"));
		Mob_navigation = reflection.find_declared_field(Mob.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_21344_"));
	}

	public static final <_T extends Mob> void setHandItems(_T entity, NonNullList<ItemStack> handItems) {
		unsafe.write(entity, EntityData.Mob_handItems, handItems);
	}

	public static final <_T extends Mob> void setArmorItems(_T entity, NonNullList<ItemStack> armorItems) {
		unsafe.write(entity, EntityData.Mob_armorItems, armorItems);
	}

	public static final <_T extends Mob> PathNavigation getNavigation(_T entity) {
		return (PathNavigation) unsafe.read_reference(entity, EntityData.Mob_navigation);
	}

	public static final <_T extends Mob> void setNavigation(_T entity, PathNavigation navigation) {
		unsafe.write(entity, EntityData.Mob_navigation, navigation);
	}

	public static final void copyMobData(Mob src, Mob dest) {
		EntityData.copyLivingEntityData(src, dest);
		unsafe.copy_member_fields(src, dest, TOP_Mob, BOTTOM_Mob);
	}

	private static Field PathNavigation_speedModifier;

	static {
		PathNavigation_speedModifier = reflection.find_declared_field(PathNavigation.class, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_26497_"));
	}

	public static final double getSpeedModifier(PathNavigation navigation) {
		return (double) unsafe.read_double(navigation, EntityData.PathNavigation_speedModifier);
	}

}
