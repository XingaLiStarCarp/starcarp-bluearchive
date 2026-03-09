package mcbase.entity.data;

import java.util.HashMap;
import java.util.List;

import mcbase.registry.Registers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.RegistryObject;

/**
 * 实体的默认实体属性
 */
@EventBusSubscriber(modid = Registers.MOD_ID, bus = Bus.MOD)
public final class EntityDefaultAttributes {
	public static final class Entry {
		Attribute attrib;
		double value;

		private Entry(Attribute attrib, double value) {
			this.attrib = attrib;
			this.value = value;
		}

		public static final Entry of(Attribute attrib, double value) {
			return new Entry(attrib, value);
		}
	}

	@SuppressWarnings("rawtypes")
	private static final HashMap<RegistryObject, List<Entry>> ENTITY_ATTRIBS = new HashMap<>();

	/**
	 * 设置指定实体的默认属性
	 * 
	 * @param type
	 * @param entries
	 */
	@SuppressWarnings("rawtypes")
	public static final void set(RegistryObject type, List<Entry> entries) {
		ENTITY_ATTRIBS.put(type, entries);
	}

	@SuppressWarnings("rawtypes")
	public static final void set(RegistryObject type, Entry... entries) {
		set(type, List.of(entries));
	}

	/**
	 * 将type的默认属性设置成与target相同
	 * 
	 * @param type
	 * @param target
	 */
	@SuppressWarnings("rawtypes")
	public static final void set(RegistryObject type, RegistryObject target) {
		set(type, get(target));
	}

	/**
	 * 获取指定实体的默认属性
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final List<Entry> get(RegistryObject type) {
		return ENTITY_ATTRIBS.get(type);
	}

	@SubscribeEvent
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerDefaultAttributes(EntityAttributeCreationEvent event) {
		for (java.util.Map.Entry<RegistryObject, List<Entry>> entityEntry : ENTITY_ATTRIBS.entrySet()) {
			AttributeSupplier.Builder attribs = LivingEntity.createLivingAttributes();
			for (Entry attribEntry : entityEntry.getValue()) {
				attribs.add(attribEntry.attrib, attribEntry.value);
			}
			event.put((EntityType) entityEntry.getKey().get(), attribs.build());
		}
	}
}