package mcbase.entity.goal;

import java.util.EnumSet;

import mcbase.TimeUtils;
import mcbase.entity.EntityInteractions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public abstract class AttackGoal extends InDistanceGoal {
	protected int attackInterval;
	protected AttributeInstance attackSpeedAttribute;
	/**
	 * 攻击时实体是否面朝目标
	 */
	protected boolean mustFace = true;
	protected float faceToleranceAngle = 60.0f;
	protected float yMaxRotSpeed = 30.0f;
	protected float xMaxRotAngle = 30.0f;

	public static final int ENTITY_ATTRIBUTE_ATTACK_INTERVAL = -1;

	public AttackGoal(Mob mob, double distance, int attackInterval) {
		super(mob, distance);
		this.attackInterval = attackInterval;
		this.attackSpeedAttribute = mob.getAttribute(Attributes.ATTACK_SPEED);
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
	}

	public AttackGoal(Mob mob, double distance) {
		this(mob, distance, ENTITY_ATTRIBUTE_ATTACK_INTERVAL);
	}

	@Override
	public void update() {
		LivingEntity target = mob.getTarget();
		this.mob.getLookControl().setLookAt(target, yMaxRotSpeed, xMaxRotAngle);// 攻击时朝向目标
		// 判断是否面对目标
		if (!mustFace || (mustFace && EntityInteractions.isEntityFacing(this.mob, target.getEyePosition(), faceToleranceAngle))) {
			if (this.attackInterval > 0) {
				// 手动设置了attackInterval
				this.attack();
			} else {
				// 使用实体的的attack speed
				int attributeAttackInterval = (int) (TimeUtils.TICKS_PER_SECOND / attackSpeedAttribute.getBaseValue());
				if (this.getTicks() % attributeAttackInterval == 0) {
					this.attack();
				}
			}
		}
	}

	public abstract void attack();
}
