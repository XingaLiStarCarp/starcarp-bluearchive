package sc.server.api.entity;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;

import jvmsp.symbols;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.registries.RegistryObject;
import sc.server.ModEntry;

/**
 * 实体渲染器相关信息
 */
public class EntityRendererType<_RenderAsset> {

	private final Class<? extends EntityRenderer<?>> rendererClazz;

	// 每种实体渲染类型都有哪些EntityType
	private final ArrayList<RegistryObject<EntityType<?>>> entityTypes;

	// 默认的渲染使用的全部模型、纹理数据
	private _RenderAsset defaultRenderAsset;

	public EntityRendererType(Class<? extends EntityRenderer<?>> rendererClazz, _RenderAsset defaultRenderAsset) {
		this.rendererClazz = rendererClazz;
		this.defaultRenderAsset = defaultRenderAsset;
		this.entityTypes = new ArrayList<>();
	}

	public EntityRendererType(Class<? extends EntityRenderer<?>> rendererClazz) {
		this(rendererClazz, null);
	}

	public final Class<? extends EntityRenderer<?>> rendererType() {
		return rendererClazz;
	}

	public String toString() {
		return "EntityRendererType[" + rendererClazz + "]";
	}

	/**
	 * 获取该性别下所有的EntityType
	 * 
	 * @return
	 */
	public final ArrayList<RegistryObject<EntityType<?>>> entityTypes() {
		return entityTypes;
	}

	/**
	 * 获取默认渲染信息
	 * 
	 * @return
	 */
	public final _RenderAsset defaultRenderAsset() {
		if (defaultRenderAsset == null)
			throw new java.lang.IllegalStateException("no default render asset defined for '" + this.toString() + "'");
		else
			return defaultRenderAsset;
	}

	public final void setDefaultRenderAsset(_RenderAsset ra) {
		this.defaultRenderAsset = ra;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void register(EntityRenderersEvent.RegisterRenderers event) {
		for (RegistryObject<EntityType<?>> type : entityTypes) {
			event.registerEntityRenderer((EntityType) type.get(), (EntityRendererProvider.Context context) -> {
				try {
					MethodHandle constructor = symbols.find_constructor(rendererClazz, EntityRendererProvider.Context.class);
					return (EntityRenderer) constructor.invoke(context);
				} catch (Throwable ex) {
					ModEntry.LOGGER.error("register renderer of type '" + rendererClazz + "' failed", ex);
					return null;
				}
			});
		}
	}

	/**
	 * 将一个EntityType的渲染器注册为本渲染器。<br>
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void apply(RegistryObject type) {
		entityTypes.add(type);
	}
}