package sc.server.api.client.render.entity;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.modlauncher.api.INameMappingService;
import jvmsp.symbols;
import jvmsp.unsafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

/**
 * 实体渲染器操作
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD)
public class EntityRenderers {
	private static EntityRenderDispatcher entityRenderDispatcher;
	private static ResourceManager resourceManager;

	private static Font font;
	private static ItemRenderer itemRenderer;
	private static BlockRenderDispatcher blockRenderDispatcher;
	private static ItemInHandRenderer itemInHandRenderer;
	private static EntityModelSet entityModels;

	private static Map<EntityType<?>, EntityRenderer<?>> entityRenderers;
	private static Map<String, EntityRenderer<? extends Player>> playerRenderers;

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		Minecraft mc = Minecraft.getInstance();
		entityRenderDispatcher = mc.getEntityRenderDispatcher();
		resourceManager = mc.getResourceManager();
		font = (Font) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_114365_"));
		itemRenderer = (ItemRenderer) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_173995_"));
		blockRenderDispatcher = (BlockRenderDispatcher) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_234576_"));
		itemInHandRenderer = (ItemInHandRenderer) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_234577_"));
		entityModels = (EntityModelSet) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_173996_"));
		context = newContext();
	}

	public static final EntityRenderDispatcher renderDispatcher() {
		return entityRenderDispatcher;
	}

	/**
	 * 根据当前状态创建一个新的Context
	 * 
	 * @return
	 */
	public static final EntityRendererProvider.Context newContext() {
		return new EntityRendererProvider.Context(entityRenderDispatcher, itemRenderer, blockRenderDispatcher, itemInHandRenderer, resourceManager, entityModels, font);
	}

	private static EntityRendererProvider.Context context;

	/**
	 * 由于客户端启动后Context成员不变，因此可以使用延迟初始化的单例变量
	 * 
	 * @return
	 */
	public static final EntityRendererProvider.Context context() {
		return context;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void init(EntityRenderersEvent.AddLayers event) {
		// 将EntityRenderDispatcher的渲染器Map解锁
		entityRenderers = new HashMap<>(getEntityRenderers());
		playerRenderers = new HashMap<>(getPlayerRenderers());
		// 由于entityRenderers和playerRenderers从原则上讲不允许修改，因此其他处理该事件的代码对这两个Map是只读的，不需要将可变的HashMap对象更新到event对象
		setEntityRenderers(entityRenderers);
		setPlayerRenderers(playerRenderers);
	}

	public static final <_T extends EntityRenderer<?>> _T create(Class<_T> rendererClazz) {
		try {
			MethodHandle constructor = symbols.find_constructor(rendererClazz, EntityRendererProvider.Context.class);
			return (_T) constructor.invoke(context());
		} catch (Throwable ex) {
			return null;
		}
	}

	public static final Map<EntityType<?>, EntityRenderer<?>> getEntityRenderers() {
		return entityRenderDispatcher.renderers;
	}

	@SuppressWarnings("unchecked")
	public static final Map<String, EntityRenderer<? extends Player>> getPlayerRenderers() {
		return (Map<String, EntityRenderer<? extends Player>>) unsafe.read_member_reference(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_114363_"));
	}

	public static final void setEntityRenderers(Map<EntityType<?>, EntityRenderer<?>> entityRenderers) {
		entityRenderDispatcher.renderers = entityRenderers;
	}

	public static final void setPlayerRenderers(Map<String, EntityRenderer<? extends Player>> playerRenderers) {
		unsafe.write_member(entityRenderDispatcher, ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, "f_114363_"), playerRenderers);
	}

	/**
	 * 设置实体使用的渲染器
	 * 
	 * @param type
	 * @param renderer
	 */
	public static final void setEntityRenderer(EntityType<?> type, EntityRenderer<?> renderer) {
		entityRenderers.put(type, renderer);
	}

	public static final EntityRenderer<?> getEntityRenderer(EntityType<?> type) {
		return entityRenderers.get(type);
	}

	public static final void setPlayerRenderer(String name, EntityRenderer<? extends Player> renderer) {
		playerRenderers.put(name, renderer);
	}

	public static final EntityRenderer<? extends Player> getPlayerRenderer(String name) {
		return playerRenderers.get(name);
	}
}
