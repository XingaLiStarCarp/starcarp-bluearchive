package sc.server.api.entity.mob;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.chatbubble.ChatBubbleManager;
import com.github.tartaricacid.touhoulittlemaid.entity.data.MaidTaskDataMaps;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import jvmsp.reflection;
import jvmsp.unsafe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sc.server.api.capability.CapabilityData;
import sc.server.api.entity.EntityData;

/**
 * 可以作为Touhou Little Maid模组女仆模型渲染的委托接口。<br>
 * 支持YSM模型渲染。<br>
 */
public interface RenderableMaid {
	/**
	 * 女仆模型信息。<br>
	 * 包含TLM的模型信息和YSM的模型信息。<br>
	 */
	public static final class MaidModelInfo {

		public String tlmModelId;
		public boolean isYsmModel;
		public String ysmModelId;
		public String ysmModelTexture;
		public Component ysmModelName;

		public static final String DEFAULT_TEXTURE = "-";

		public MaidModelInfo(String tlmModelId, boolean isYsmModel, String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this.tlmModelId = tlmModelId;
			this.isYsmModel = isYsmModel;
			this.ysmModelId = ysmModelId;
			this.ysmModelTexture = ysmModelTexture;
			this.ysmModelName = ysmModelName;
		}

		public MaidModelInfo(String tlmModelId) {
			this(tlmModelId, false, "", DEFAULT_TEXTURE, Component.empty());
		}

		public MaidModelInfo(String ysmModelId, String ysmModelTexture, Component ysmModelName) {
			this("", true, ysmModelId, ysmModelTexture, ysmModelName);
		}

		public MaidModelInfo() {
			this("");
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

			static {
				EntityMaid_chatBubbleManager = reflection.find_declared_field(EntityMaid.class, "chatBubbleManager");
				EntityMaid_taskDataMaps = reflection.find_declared_field(EntityMaid.class, "taskDataMaps");
			}
		}
		EntityMaid entity = EntityData.blankEntity(EntityMaid.class);
		EntityData.copyData(templateEntity, entity);
		unsafe.write(entity, Symbols.EntityMaid_chatBubbleManager, new ChatBubbleManager(entity));
		unsafe.write(entity, Symbols.EntityMaid_taskDataMaps, new MaidTaskDataMaps());
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
	 * 获取渲染的虚假EntityMaid实体。<br>
	 * 该虚假实体需要通过blankEntityMaid()方法创建。<br>
	 * 
	 * @return
	 */
	public abstract EntityMaid renderingEntity();

	/**
	 * 渲染时要绑定的实体数据，包括其位置信息、角度、血量等。<br>
	 * 
	 * @return
	 */
	public abstract Entity bindEntity();

	String getModelId();

	void setModelId(String modelId);

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
	public default void syncRenderingEntity() {
		EntityMaid renderingEntity = renderingEntity();
		renderingEntity.setModelId(this.getModelId());
		renderingEntity.setIsYsmModel(this.isYsmModel());
		renderingEntity.setYsmModel(this.getYsmModelId(), this.getYsmModelTexture(), this.getYsmModelName());
		EntityData.copyData(bindEntity(), renderingEntity);
	}

	public default void playRouletteAnim(String rouletteAnim) {
		renderingEntity().playRouletteAnim(rouletteAnim);
	}

	public default void stopRouletteAnim() {
		renderingEntity().stopRouletteAnim();
	}

	public static RenderableMaid bind(Entity bindEntity, MaidModelInfo model) {
		return new Binder(bindEntity, model);
	}

	public static RenderableMaid bind(Entity bindEntity) {
		return new Binder(bindEntity, new MaidModelInfo());
	}

	static class Binder implements RenderableMaid {
		private Entity bindEntity;
		private MaidModelInfo model;

		private EntityMaid renderingEntity;

		private static final ArrayList<RenderableMaid> BIND_ENTITIES = new ArrayList<>();

		@Override
		public final EntityMaid renderingEntity() {
			return renderingEntity;
		}

		@Override
		public final Entity bindEntity() {
			return bindEntity;
		}

		Binder(Entity bindEntity, MaidModelInfo model) {
			this.bindEntity = bindEntity;
			this.renderingEntity = RenderableMaid.blankEntityMaid(bindEntity);
			this.model = model;
			BIND_ENTITIES.add(this);
		}

		@SubscribeEvent
		public static void onServerTick(TickEvent.ServerTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				// 每tick更新计算完成后同步渲染模型
				for (RenderableMaid maid : BIND_ENTITIES) {
					maid.syncRenderingEntity();
				}
			}
		}

		@Override
		public String getModelId() {
			return model.tlmModelId;
		}

		@Override
		public void setModelId(String modelId) {
			model.tlmModelId = modelId;
		}

		@Override
		public boolean isYsmModel() {
			return model.isYsmModel;
		}

		@Override
		public void setIsYsmModel(boolean isYsmModel) {
			model.isYsmModel = isYsmModel;
		}

		@Override
		public String getYsmModelId() {
			return model.ysmModelId;
		}

		@Override
		public void setYsmModelId(String ysmModelId) {
			model.ysmModelId = ysmModelId;
		}

		@Override
		public String getYsmModelTexture() {
			return model.ysmModelTexture;
		}

		@Override
		public void setYsmModelTexture(String ysmModelTexture) {
			model.ysmModelTexture = ysmModelTexture;
		}

		@Override
		public Component getYsmModelName() {
			return model.ysmModelName;
		}

		@Override
		public void setYsmModelName(Component ysmModelName) {
			model.ysmModelName = ysmModelName;
		}
	}
}
