package sc.server.api.entity;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jvmsp.symbols;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;
import sc.server.ModEntry;
import sc.server.api.entity.EntityDefaultAttributes.Entry;
import sc.server.api.registry.Registers;

@EventBusSubscriber(modid = Registers.MOD_ID, bus = Bus.FORGE)
public abstract class BaseMob extends PathfinderMob {

	private final EntityRendererType<?> rendererType;

	/**
	 * 实体所属性别
	 * 
	 * @return
	 */
	public final EntityRendererType<?> rendererType() {
		return rendererType;
	}

	public final Object defaultRenderAsset() {
		return rendererType.defaultAsset();
	}

	@SuppressWarnings("unchecked")
	public final <_T> _T defaultRenderAsset(Class<_T> clazz) {
		return (_T) rendererType.defaultAsset();
	}

	public static final float HUMANOID_WIDTH = 0.6f;
	public static final float HUMANOID_HEIGHT = 1.8f;

	/**
	 * 注册一种新的生物实体类型。<br>
	 * 不同的生物实体类型具有不同的行为。<br>
	 * 
	 * @param <T>
	 * @param entityClazz
	 * @param rendererType
	 * @param typeName
	 * @param category
	 * @param attributes   生物实体的默认属性，属性必须至少包含MAX_HEALTH和FOLLOW_TDNGE
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, EntityRendererType<?> rendererType, String typeName, MobCategory category, List<Entry> attributes) {
		RegistryObject<EntityType<BaseMob>> type = Registers.ENTITIES_REG.register(typeName,
				() -> EntityType.Builder.of((EntityType<BaseMob> entityType, Level level) -> {
					try {
						MethodHandle constructor = symbols.find_constructor(entityClazz, EntityType.class, EntityRendererType.class, Level.class);
						return (T) constructor.invoke(entityType, rendererType, level);
					} catch (Throwable ex) {
						ModEntry.LOGGER.error("create base mob of type '" + typeName + "' failed", ex);
						return null;
					}
				}, category)
						.sized(width, height)
						.build(Registers.MOD_ID + ":" + typeName));
		rendererType.apply(type);// 将实体类型添加到指定渲染类型
		EntityDefaultAttributes.set(type, attributes);// 设置默认的生物实体属性
		return (RegistryObject<EntityType<T>>) (RegistryObject) type;
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, EntityRendererType<?> renderType, String typeName, MobCategory category, Entry... attributes) {
		return newType(entityClazz, width, height, renderType, typeName, category, List.of(attributes));
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, EntityRendererType<?> renderType, String typeName, List<Entry> attributes) {
		return newType(entityClazz, width, height, renderType, typeName, MobCategory.MISC, attributes);
	}

	public static final <T extends BaseMob> RegistryObject<EntityType<T>> newType(Class<T> entityClazz, float width, float height, EntityRendererType<?> renderType, String typeName, Entry... attributes) {
		return newType(entityClazz, width, height, renderType, typeName, MobCategory.MISC, attributes);
	}

	public static final List<Entry> PLAYER_ATTRIBUTES = List.of(
			Entry.of(Attributes.MAX_HEALTH, 20),
			Entry.of(Attributes.MOVEMENT_SPEED, 0.25), // 与玩家的默认移动速度相同
			Entry.of(Attributes.FOLLOW_RANGE, 16),
			Entry.of(Attributes.ARMOR, 0),
			Entry.of(Attributes.ARMOR_TOUGHNESS, 0),
			Entry.of(Attributes.ATTACK_DAMAGE, 1),
			Entry.of(Attributes.ATTACK_KNOCKBACK, 0),
			Entry.of(Attributes.ATTACK_SPEED, 4));

	private boolean pushable;
	private boolean attackable;
	private double rmDistance;

	public BaseMob(EntityType<BaseMob> entityType, EntityRendererType<?> renderType, Level level) {
		super(entityType, level);
		this.rendererType = renderType;
	}

	/**
	 * 定义并初始化实体数据字段默认值
	 */
	@Override
	protected final void defineSynchedData() {
		super.defineSynchedData();
		EntityData.defineAll(this);
	}

	/**
	 * 从entityData中读取数据并序列化存入compound
	 * 
	 * @param compound
	 * @param entityData
	 */
	protected void storeData(CompoundTag compound, SynchedEntityData entityData) {

	}

	/**
	 * 序列化实体的数据
	 */
	@Override
	public final void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		this.storeData(compound, super.entityData);
	}

	protected void loadData(CompoundTag compound, SynchedEntityData entityData) {

	}

	/**
	 * 反序列化实体的数据
	 */
	@Override
	public final void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.loadData(compound, super.entityData);
	}

	@Override
	public boolean isPushable() {
		return pushable;
	}

	/**
	 * 设置生物实体是否可以被推动
	 * 
	 * @param pushable
	 * @return
	 */
	public void setPushable(boolean pushable) {
		this.pushable = pushable;
	}

	/**
	 * 设置生物实体远离消失的阈值距离。<br>
	 * 若小于等于0则永远不移除.<br>
	 * 
	 * @param distance
	 * @return
	 */
	public void setRemoveDistance(double distance) {
		this.rmDistance = distance;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return rmDistance > 0 ? (distanceToClosestPlayer > rmDistance) : false;
	}

	@Override
	public boolean attackable() {
		return attackable;
	}

	/**
	 * 设置实体是否可以被攻击
	 * 
	 * @param attackable
	 * @return
	 */
	public void setAttackable(boolean attackable) {
		this.attackable = attackable;
	}

	/**
	 * 让生物实体朝向指定的方向
	 * 
	 * @param loc
	 * @return
	 */
	public void face(Vec3 loc) {
		this.lookAt(Anchor.EYES, loc);
	}

	/**
	 * 让生物实体朝向指定的实体
	 * 
	 * @param entity
	 * @return
	 */
	public void face(Entity entity) {
		face(entity.getEyePosition());
	}

	/**
	 * 设置手持物品，该设置会替换生物实体当前手持物品
	 * 
	 * @param item
	 * @return
	 */
	public void setMainHandHold(ItemStack item) {
		this.setItemInHand(InteractionHand.MAIN_HAND, item);
	}

	public void setMainHandHold(Item item) {
		this.setMainHandHold(new ItemStack(item, 1));
	}

	public void setMainHandHold(String item) {
		this.setMainHandHold(Registers.item(item));
	}

	public void setOffHandHold(ItemStack item) {
		this.setItemInHand(InteractionHand.OFF_HAND, item);
	}

	public void setOffHandHold(Item item) {
		this.setOffHandHold(new ItemStack(item, 1));
	}

	public void setOffHandHold(String item) {
		this.setOffHandHold(Registers.item(item));
	}

	/**
	 * 单独向一个玩家发送消息
	 * 
	 * @param player
	 * @param msg
	 * @return
	 */
	public void chat(Player player, String msg) {
		Component name = this.getCustomName();
		if (name == null) {
			// 生物实体如果没有名字则省去名字前缀，直接发送消息
			player.sendSystemMessage(Component.literal(msg));
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append('<');
			sb.append(name.getString());
			sb.append("> ");
			sb.append(msg);
			player.sendSystemMessage(Component.literal(sb.toString()));
		}
	}

	private final ArrayList<TraitComponent> traitComponents = new ArrayList<>();

	/**
	 * 特征组件，所有种类的行为组件都必须实现此接口
	 */
	public static interface TraitComponent {
		public default void init(BaseMob mob) {

		}

		public default void uninit(BaseMob mob) {

		}
	}

	public final void addTrait(TraitComponent trait) {
		traitComponents.add(trait);
		trait.init(this);
	}

	public final void removeTrait(TraitComponent trait) {
		traitComponents.remove(trait);
		trait.uninit(this);
	}

	private final HashMap<Class<?>, OpComponent<?>> opComponents = new HashMap<>();

	/**
	 * 行为组件
	 */
	public static final class OpComponent<_Param> {
		@FunctionalInterface
		public static interface Operation<_Param> {
			/**
			 * 执行一段动作
			 * 
			 * @param param 任意参数
			 * @return 是否继续遍历执行下一个操作
			 */
			public boolean operate(BaseMob mob, _Param param);
		}

		private BaseMob mob;
		private ArrayList<Operation<_Param>> ops;

		private OpComponent(BaseMob mob) {
			this.mob = mob;
			this.ops = new ArrayList<>();
		}

		public void add(Operation<_Param> op) {
			ops.add(op);
		}

		public void remove(Operation<_Param> op) {
			ops.remove(op);
		}

		/**
		 * 执行全部行为
		 * 
		 * @param param
		 * @return 是否所有行为都执行成功
		 */
		public boolean execute(_Param param) {
			for (Operation<_Param> op : ops) {
				if (!op.operate(mob, param))
					return false;
			}
			return true;
		}
	}

	/**
	 * 获取实体的行为组件，若不存在则新建一个对应的行为组件。
	 * 
	 * @param <_Param>
	 * @param paramClazz 参数类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <_Param> OpComponent<_Param> opComponent(Class<_Param> paramClazz) {
		return (OpComponent<_Param>) opComponents.computeIfAbsent(paramClazz, (k) -> new OpComponent<>(this));
	}

	/**
	 * 执行行为
	 * 
	 * @param <_Param>
	 * @param paramClazz
	 * @return
	 */
	public <_Param> boolean executeOpComponent(Class<_Param> paramClazz, _Param param) {
		return this.opComponent(paramClazz).execute(param);
	}

	/**
	 * 实体攻击/被攻击时执行的行为
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onLivingAttackEvent(LivingAttackEvent event) {
		if (event.getSource().getEntity() instanceof BaseMob damager) {
			damager.executeOpComponent(LivingAttackEvent.class, event);
		}
		if (event.getEntity() instanceof BaseMob damagee) {
			damagee.executeOpComponent(LivingAttackEvent.class, event);
		}
	}

	/**
	 * 实体被交互时执行的行为
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() instanceof BaseMob baseMob) {
			baseMob.executeOpComponent(PlayerInteractEvent.EntityInteract.class, event);
		}
	}
}
