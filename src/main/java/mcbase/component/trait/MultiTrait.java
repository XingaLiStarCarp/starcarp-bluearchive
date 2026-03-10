package mcbase.component.trait;

import mcbase.component.TraitProvider;
import mcbase.component.TraitProvider.TraitComponent;

/**
 * 多个TraitComponent打包
 */
public class MultiTrait extends ElementTrait<TraitProvider, TraitComponent<?>> {

	@Override
	public void init(TraitProvider target, TraitComponent<?> trait) {
		target.addTrait(trait);
	}

	@Override
	public void uninit(TraitProvider target, TraitComponent<?> trait) {
		target.removeTrait(trait);
	}
}
