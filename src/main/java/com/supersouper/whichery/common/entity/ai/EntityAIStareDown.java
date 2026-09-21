package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIStareDown extends EntityAIBase {

    private final EntityLiving entity;
    private final float chancePerTickInPercent;
    private final int maxDurationTicks;

    private int durationTicks;

    public EntityAIStareDown(EntityLiving entity, float chancePerTickInPercent, int maxDurationTicks) {
        this.entity = entity;
        this.chancePerTickInPercent = chancePerTickInPercent;
        this.maxDurationTicks = maxDurationTicks;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getRNG().nextFloat() < this.chancePerTickInPercent;
    }

    @Override
    public boolean continueExecuting() {
        return this.entity.isEntityAlive()
            && this.durationTicks > 0;
    }

    @Override
    public void startExecuting() {
        int halfLookTimeTicks = this.maxDurationTicks / 2;
        this.durationTicks = halfLookTimeTicks + this.entity.getRNG().nextInt(halfLookTimeTicks);
    }

    @Override
    public void updateTask() {
        this.entity.getLookHelper().setLookPosition(
            this.entity.posX, this.entity.posY, this.entity.posZ, 10.0F, this.entity.getVerticalFaceSpeed()
        );
    }
}
