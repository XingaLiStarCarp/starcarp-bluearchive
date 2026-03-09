package mcbase.extended.tacz.goal;

import com.tacz.guns.api.entity.ShootResult;

import mcbase.entity.goal.AttackGoal;
import mcbase.extended.tacz.TaczGunOperator;
import net.minecraft.world.entity.Mob;
import scba.ModEntry;

public class GunAttackGoal extends AttackGoal {
	protected TaczGunOperator gunOperator;

	public GunAttackGoal(Mob mob, double distance, int attackInterval) {
		super(mob, distance, attackInterval);
		gunOperator = new TaczGunOperator(mob);
	}

	public GunAttackGoal(Mob mob, double distance) {
		this(mob, distance, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	@Override
	public void enter() {
		gunOperator.craw(true);
	}

	@Override
	public void attack() {
		try {
			gunOperator.aim(true);
			ShootResult result = gunOperator.shootAuto(this.mob.getTarget().position());
			System.err.println("result = " + result + " gun " + gunOperator.getGunItem().getOrCreateTag());
		} catch (Throwable ex) {
			ModEntry.LOGGER.error(this.mob + " gun attack failed", ex);
		}
	}

	@Override
	public void exit() {
		gunOperator.craw(false);
	}
}
