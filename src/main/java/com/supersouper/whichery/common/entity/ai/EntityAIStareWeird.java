package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIStareWeird extends EntityAIBase {

    private final EntityLiving entity;
    private final float chancePerTickInPercent;
    private final int maxDurationTicks;

    private int durationTicks;
    private int yOffset;

    public EntityAIStareWeird(EntityLiving entity, float chancePerTickInPercent, int maxDurationTicks) {
        this.entity = entity;
        this.chancePerTickInPercent = chancePerTickInPercent;
        this.maxDurationTicks = maxDurationTicks;
        this.setMutexBits(0b11); // Exclude other wander (01) and look (10) tasks
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
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void startExecuting() {
        int halfLookTimeTicks = this.maxDurationTicks / 2;
        this.durationTicks = halfLookTimeTicks + this.entity.getRNG().nextInt(halfLookTimeTicks);
        this.yOffset = this.entity.getRNG().nextBoolean() ? 1 : 0; // Either stare up or down
    }

    @Override
    public void updateTask() {
        this.entity.getLookHelper().setLookPosition(
            this.entity.posX, this.entity.posY + this.yOffset, this.entity.posZ, 10.0F, this.entity.getVerticalFaceSpeed()
        );
    }
}
