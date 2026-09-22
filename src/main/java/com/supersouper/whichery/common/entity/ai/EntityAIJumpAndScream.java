package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIJumpAndScream extends EntityAIBase {

    private final EntityLiving entity;
    private final float chancePerTickInPercent;
    private final String screamSound;

    private int durationTicks;

    public EntityAIJumpAndScream(EntityLiving entity, float chancePerTick, String screamSound) {
        this.entity = entity;
        this.chancePerTickInPercent = chancePerTick;
        this.screamSound = screamSound;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getRNG().nextFloat() < this.chancePerTickInPercent;
    }

    @Override
    public void startExecuting() {
        durationTicks = 200 + this.entity.getRNG().nextInt(500);
        jumpAndScream();
    }

    @Override
    public boolean continueExecuting() {
        return this.entity.isEntityAlive() && durationTicks > 0;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void updateTask() {
        if (this.entity.onGround && this.entity.motionY <= 0) {
            jumpAndScream();
        }
    }

    private void jumpAndScream() {
        this.entity.getJumpHelper().setJumping();
        this.entity.playSound(screamSound, 1.0F, 1.0F);
    }
}
