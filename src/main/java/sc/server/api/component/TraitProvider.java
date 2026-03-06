package sc.server.api.component;

import java.util.ArrayList;

public interface TraitProvider<_TraitTarget> {
	/**
	 * 特征组件，所有种类的行为组件都必须实现此接口
	 */
	public static interface TraitComponent<_TraitTarget> {
		public default void init(_TraitTarget target) {

		}

		public default void uninit(_TraitTarget target) {

		}

		public default void tick(_TraitTarget target) {

		}
	}

	public abstract ArrayList<TraitComponent<_TraitTarget>> getTraits();

	@SuppressWarnings("unchecked")
	public default void addTrait(TraitComponent<_TraitTarget> trait) {
		getTraits().add(trait);
		trait.init((_TraitTarget) this);
	}

	@SuppressWarnings("unchecked")
	public default void removeTrait(TraitComponent<_TraitTarget> trait) {
		trait.uninit((_TraitTarget) this);
		getTraits().remove(trait);
	}

	/**
	 * 子类必须每tick调用此方法更新
	 */
	@SuppressWarnings("unchecked")
	public default void tickTraits() {
		ArrayList<TraitComponent<_TraitTarget>> traits = this.getTraits();
		for (TraitComponent<_TraitTarget> trait : traits) {
			trait.tick((_TraitTarget) this);
		}
	}
}