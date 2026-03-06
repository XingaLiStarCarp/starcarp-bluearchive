package sc.server.api.component.trait.entity;

import net.minecraft.world.entity.LivingEntity;
import sc.server.api.component.TraitProvider.TraitComponent;
import sc.server.api.entity.EntityInteractions;

public class StepParticlesTrait<_Entity extends LivingEntity> implements TraitComponent<_Entity> {
	private double speedThreshold;
	private int interval;

	public static final int DEFAULT_PARTICLE_SPAWN_INTERVAL = 1;

	public StepParticlesTrait(double speedThreshold, int interval) {
		this.speedThreshold = speedThreshold;
		this.interval = interval;
	}

	public StepParticlesTrait(double speedThreshold) {
		this(speedThreshold, DEFAULT_PARTICLE_SPAWN_INTERVAL);
	}

	@Override
	public void tick(_Entity entity) {
		if (entity.level().isClientSide()) {
			EntityInteractions.spawnRunningGroundParticles(entity, interval, speedThreshold);// 奔跑时的粒子效果
		}
	}
}
