package sc.server.api.entity.maid;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.CustomModelPack;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.MaidModelInfo;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManager;
import com.github.tartaricacid.touhoulittlemaid.entity.data.MaidTaskDataMaps;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import javabase.HashFunctions;
import jvmsp.reflection;
import jvmsp.unsafe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import sc.server.api.ModPaths;
import sc.server.api.capability.CapabilityData;
import sc.server.api.entity.EntityData;
import sc.server.api.entity.SyncedRenderEntity;
import sc.server.api.entity.maid.SyncedRenderMaid.MaidModelAsset;

/**
 * 可以作为Touhou Little Maid模组女仆模型渲染的委托接口。<br>
 * 支持YSM模型渲染。<br>
 */
public interface SyncedRenderMaid extends SyncedRenderEntity<EntityMaid, MaidModelAsset> {
	/**
	 * 女仆模型信息。<br>
	 * 包含TLM的模型信息和YSM的模型信息。<br>
	 */
	public static final class MaidModelAsset {

		private String tlmModelId;
		private boolean isYsmModel;
		private String ysmModelId;
		private String ysmModelTexture;
		private Component ysmModelName;

		public static final String DEFAULT_MODEL_ID = "";
		public static final String DEFAULT_TEXTURE = "-";

		public MaidModelAsset(String tlmModelId, boolean isYsmModel, String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this.tlmModelId = tlmModelId;
			this.isYsmModel = isYsmModel;
			this.ysmModelId = ysmModelId;
			this.ysmModelTexture = ysmModelTexture;
			this.ysmModelName = ysmModelName;
		}

		public MaidModelAsset(String tlmModelId) {
			this(tlmModelId, false, DEFAULT_MODEL_ID, DEFAULT_TEXTURE, Component.empty());
		}

		public MaidModelAsset(String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this(DEFAULT_MODEL_ID, true, ysmModelId, ysmModelTexture, ysmModelName);
		}

		public MaidModelAsset(MaidModelAsset info) {
			this(info.tlmModelId, info.isYsmModel, info.ysmModelId, info.ysmModelTexture, info.ysmModelName);
		}

		public MaidModelAsset() {
			this(DEFAULT_MODEL_ID);
		}

		public String getTlmModelId() {
			return tlmModelId;
		}

		public void setTlmModelId(String tlmModelId) {
			this.tlmModelId = tlmModelId;
		}

		public boolean isYsmModel() {
			return isYsmModel;
		}

		public void setIsYsmModel(boolean isYsmModel) {
			this.isYsmModel = isYsmModel;
		}

		public String getYsmModelId() {
			return ysmModelId;
		}

		public void setYsmModelId(String ysmModelId) {
			this.ysmModelId = ysmModelId;
		}

		public String getYsmModelTexture() {
			return ysmModelTexture;
		}

		public void setYsmModelTexture(String ysmModelTexture) {
			this.ysmModelTexture = ysmModelTexture;
		}

		public Component getYsmModelName() {
			return ysmModelName;
		}

		public void setYsmModelName(Component ysmModelName) {
			this.ysmModelName = ysmModelName;
		}

		/**
		 * 自定义YSM模型路径
		 */
		public static final Path LOCAL_CUSTOM_YSM_MODEL_DIR = ModPaths.config("yes_steve_model/custom");

		private static boolean CACHED_TLM_MODEL_IDS = false;
		private static final ArrayList<String> TLM_MODEL_IDS = new ArrayList<>();

		/**
		 * 获取缓存的TLM模型id列表
		 * 
		 * @return
		 */
		public static final List<String> tlmModelIds() {
			if (!CACHED_TLM_MODEL_IDS) {
				CACHED_TLM_MODEL_IDS = true;
				return retrieveTlmModelIds();
			}

			return TLM_MODEL_IDS;
		}

		/**
		 * 获取最新的TLM模型id列表
		 * 
		 * @return
		 */
		public static final ArrayList<String> retrieveTlmModelIds() {
			TLM_MODEL_IDS.clear();
			List<CustomModelPack<MaidModelInfo>> packList = CustomPackLoader.MAID_MODELS.getPackList();
			for (CustomModelPack<MaidModelInfo> pack : packList) {
				List<MaidModelInfo> modelList = pack.getModelList();
				for (MaidModelInfo info : modelList) {
					TLM_MODEL_IDS.add(info.getModelId().toString());
				}
			}
			return TLM_MODEL_IDS;
		}

		public static final String hashTlmModelId(UUID uuid) {
			String id = HashFunctions.modHash(uuid, tlmModelIds());
			return id == null ? DEFAULT_MODEL_ID : id;
		}

		public static final String YSM_EXTENSION_NAME = ".ysm";
		private static boolean CACHED_LOCAL_CUSTOM_YSM_MODEL_IDS = false;
		private static final ArrayList<String> LOCAL_CUSTOM_YSM_MODEL_IDS = new ArrayList<>();

		/**
		 * 获取缓存的YSM模型id列表
		 * 
		 * @return
		 */
		public static final List<String> localCustomYsmModelIds() {
			if (!CACHED_LOCAL_CUSTOM_YSM_MODEL_IDS) {
				CACHED_LOCAL_CUSTOM_YSM_MODEL_IDS = true;
				return retrieveLocalCustomYsmModelIds();
			}
			return LOCAL_CUSTOM_YSM_MODEL_IDS;
		}

		/**
		 * 获取最新的本地custom文件夹下YSM模型id列表。<br>
		 * 这些模型可能会有未被YSM加载的，需要玩家自行reload。<br>
		 * 
		 * @return
		 */
		public static final List<String> retrieveLocalCustomYsmModelIds() {
			LOCAL_CUSTOM_YSM_MODEL_IDS.clear();
			try (Stream<Path> paths = Files.walk(LOCAL_CUSTOM_YSM_MODEL_DIR)) {
				paths.forEach(file -> {
					if (Files.isRegularFile(file)) {
						if (file.endsWith(YSM_EXTENSION_NAME))
							LOCAL_CUSTOM_YSM_MODEL_IDS.add(file.getFileName().toString());// 文件只要.ysm文件
					} else {
						LOCAL_CUSTOM_YSM_MODEL_IDS.add(file.getFileName().toString());// 文件夹则直接加入
					}
				});
			} catch (IOException ex) {
				throw new java.lang.InternalError("retrieve local custom ysm model ids failed", ex);
			}
			return LOCAL_CUSTOM_YSM_MODEL_IDS;
		}

		public static final String hashLocalCustomYsmModelId(UUID uuid) {
			String id = HashFunctions.modHash(uuid, localCustomYsmModelIds());
			return id == null ? DEFAULT_MODEL_ID : id;
		}
	}

	/**
	 * 创建一个虚假女仆实体，仅用于渲染
	 * 
	 * @param templateEntity 数据模板，新的EntityMaid将沿用此实体的数据。
	 * @return
	 */
	public static EntityMaid blankEntityMaid(Entity templateEntity) {
		class Symbols {
			private static Field EntityMaid_chatBubbleManager;
			private static Field EntityMaid_taskDataMaps;
			private static Field EntityMaid_handItemsForAnimation;

			static {
				EntityMaid_chatBubbleManager = reflection.find_declared_field(EntityMaid.class, "chatBubbleManager");
				EntityMaid_taskDataMaps = reflection.find_declared_field(EntityMaid.class, "taskDataMaps");
				EntityMaid_handItemsForAnimation = reflection.find_declared_field(EntityMaid.class, "handItemsForAnimation");
			}
		}
		EntityMaid entity = EntityData.blankEntity(EntityMaid.class);
		EntityData.copyData(templateEntity, entity);
		unsafe.write(entity, Symbols.EntityMaid_chatBubbleManager, new ChatBubbleManager(entity));
		unsafe.write(entity, Symbols.EntityMaid_taskDataMaps, new MaidTaskDataMaps());
		unsafe.write(entity, Symbols.EntityMaid_handItemsForAnimation, new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY });// 设置空手持物品，渲染时需要
		entity.renderState = MaidRenderState.ENTITY;
		entity.rouletteAnimPlaying = false;
		entity.rouletteAnim = "empty";
		entity.rouletteAnimDirty = false;
		entity.roamingVarsUpdateFlag = 0;
		entity.roamingVars = new Object2FloatOpenHashMap<>();
		entity.animationId = 0;
		entity.animationRecordTime = -1L;
		entity.shouldReset = false;
		CapabilityData.gatherCapabilities(entity);// 必须！否则YSM不会渲染模型
		return entity;
	}

	/**
	 * 禁止覆写此方法。<br>
	 * 创建一个虚假的女仆渲染实体。<br>
	 */
	@Override
	public default EntityMaid blankRenderingEntity(Entity bindEntity) {
		return blankEntityMaid(bindEntity);
	}

	String getTlmModelId();

	void setTlmModelId(String modelId);

	boolean isYsmModel();

	void setIsYsmModel(boolean isYsmModel);

	String getYsmModelId();

	void setYsmModelId(String ysmModelId);

	String getYsmModelTexture();

	void setYsmModelTexture(String ysmModelTexture);

	Component getYsmModelName();

	void setYsmModelName(Component ysmModelName);

	/**
	 * 设置YSM模型
	 * 
	 * @param ysmModelId
	 * @param ysmModelTexture
	 * @param ysmModelName
	 */
	public default void setYsmModel(String ysmModelId, String ysmModelTexture, Component ysmModelName) {
		setYsmModelId(ysmModelId);
		setYsmModelTexture(ysmModelTexture);
		setYsmModelName(ysmModelName);
	}

	/**
	 * 同步EntityMaid虚假实体的模型.<br>
	 * 同时同步绑定的的实体数据到maid，包括渲染使用到的实体相关数据。<br>
	 * 该方法必须被子类调用以实时同步渲染模型，每tick调用一次即可。<br>
	 */
	@Override
	public default EntityMaid syncRenderingEntity() {
		EntityMaid renderingEntity = SyncedRenderEntity.super.syncRenderingEntity();
		renderingEntity.setModelId(this.getTlmModelId());
		renderingEntity.setIsYsmModel(this.isYsmModel());
		renderingEntity.setYsmModel(this.getYsmModelId(), this.getYsmModelTexture(), this.getYsmModelName());
		return renderingEntity;
	}

	public default void playRouletteAnim(String rouletteAnim) {
		renderingEntity().playRouletteAnim(rouletteAnim);
	}

	public default void stopRouletteAnim() {
		renderingEntity().stopRouletteAnim();
	}

	public static SyncedRenderMaid bind(Entity bindEntity, MaidModelAsset model) {
		return Binder.bind(bindEntity, model);
	}

	/**
	 * 绑定实体到女仆模型并实时同步实体数据
	 * 
	 * @param bindEntity
	 * @return
	 */
	public static SyncedRenderMaid bind(Entity bindEntity, BiConsumer<Entity, MaidModelAsset> modelResolver) {
		return Binder.bind(bindEntity, modelResolver);
	}

	/**
	 * 绑定实体的女仆模型
	 */
	@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.FORGE)
	static class Binder extends ModelBinder<EntityMaid, MaidModelAsset> implements SyncedRenderMaid {

		public static final SyncedRenderMaid bind(Entity bindEntity, MaidModelAsset model) {
			return bind(SyncedRenderMaid.Binder::new, bindEntity, model);
		}

		public static final SyncedRenderMaid bind(Entity bindEntity, BiConsumer<Entity, MaidModelAsset> modelResolver) {
			return bind(bindEntity, (newEntity) -> {
				MaidModelAsset model = new MaidModelAsset();
				modelResolver.accept(bindEntity, model);
				return model;
			}, SyncedRenderMaid.Binder::new);
		}

		protected Binder(Entity bindEntity, MaidModelAsset model) {
			super(bindEntity, model);
		}

		@Override
		public String getTlmModelId() {
			return model.getTlmModelId();
		}

		@Override
		public void setTlmModelId(String tlmModelId) {
			model.setTlmModelId(tlmModelId);
		}

		@Override
		public boolean isYsmModel() {
			return model.isYsmModel();
		}

		@Override
		public void setIsYsmModel(boolean isYsmModel) {
			model.setIsYsmModel(isYsmModel);
		}

		@Override
		public String getYsmModelId() {
			return model.getYsmModelId();
		}

		@Override
		public void setYsmModelId(String ysmModelId) {
			model.setYsmModelId(ysmModelId);
		}

		@Override
		public String getYsmModelTexture() {
			return model.getYsmModelTexture();
		}

		@Override
		public void setYsmModelTexture(String ysmModelTexture) {
			model.setYsmModelTexture(ysmModelTexture);
		}

		@Override
		public Component getYsmModelName() {
			return model.getYsmModelName();
		}

		@Override
		public void setYsmModelName(Component ysmModelName) {
			model.setYsmModelName(ysmModelName);
		}

		@Override
		public MaidModelAsset modelAsset() {
			return model;
		}
	}
}
