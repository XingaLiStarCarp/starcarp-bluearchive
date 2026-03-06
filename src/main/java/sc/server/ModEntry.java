package sc.server;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sc.server.api.ModPaths;
import sc.server.api.ext.tlm.entity.maid.SyncedRenderMaid.MaidModelAsset;
import sc.server.api.registry.Registers;
import sc.server.block.Blocks;

@Mod(ModEntry.MOD_ID)
public class ModEntry {
	public static final String MOD_ID = "sc";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ModEntry(FMLJavaModLoadingContext context) {
		LOGGER.info("Classpath: " + ModPaths.classpath(ModEntry.class));
		IEventBus modEventBus = context.getModEventBus();
		// 注册注册表条目
		Registers.register(modEventBus);
		Blocks.register();

		LOGGER.warn(MaidModelAsset.LOCAL_CUSTOM_YSM_MODEL_DIR.toString());

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);

		context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		ModPaths.load(true, "sc.server.entity", true);

	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("commonSetup");

	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		LOGGER.info("onServerStarting");
	}

	@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			LOGGER.info("onClientSetup");
			event.enqueueWork(() -> {
			});
		}
	}
}
