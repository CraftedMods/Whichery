package com.supersouper.whichery.common.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Similar to {@link net.minecraft.entity.ai.EntityAIWatchClosest}, but more configurable and not interruptible. Intended
 * for long, uninterrupted staring.
 */
public class EntityAICreepyStare extends EntityAIBase {

    private final EntityLiving watcherEntity;
    protected Entity watchedEntity;

    private final Class<? extends Entity> watchedEntityClass;

    private final float maxLookDistance;
    private final float chancePerTickInPercent;
    private final int maxLookTimeTicks;

    private int lookTimeTicks;

    public EntityAICreepyStare(EntityLiving watcherEntity,
                               Class<? extends Entity> watchedEntityClass,
                               float maxLookDistance,
                               float chancePerTickInPercent,
                               int maxLookTimeTicks,
                               boolean mutexWandering) {
        this.watcherEntity = watcherEntity;
        this.watchedEntityClass = watchedEntityClass;
        this.maxLookDistance = maxLookDistance;
        this.chancePerTickInPercent = chancePerTickInPercent;
        this.maxLookTimeTicks = maxLookTimeTicks;
        this.setMutexBits(mutexWandering ? 3 : 2);
    }

    @Override
    public boolean shouldExecute() {
        if (this.watcherEntity.getRNG().nextFloat() >= this.chancePerTickInPercent) {
            return false;
        } else {
            if (this.watcherEntity.getAttackTarget() != null) {
                this.watchedEntity = this.watcherEntity.getAttackTarget();
            }

            if (this.watchedEntityClass == EntityPlayer.class) {
                this.watchedEntity = this.watcherEntity.worldObj.getClosestPlayerToEntity(this.watcherEntity, this.maxLookDistance);
            } else {
                this.watchedEntity = this.watcherEntity.worldObj.findNearestEntityWithinAABB(this.watchedEntityClass, this.watcherEntity.boundingBox.expand(this.maxLookDistance, 3.0D, this.maxLookDistance), this.watcherEntity);
            }

            return this.watchedEntity != null;
        }
    }

    @Override
    public boolean continueExecuting() {
        return this.watchedEntity.isEntityAlive()
            && this.watcherEntity.getDistanceSqToEntity(this.watchedEntity) <= this.maxLookDistance * this.maxLookDistance
            && this.lookTimeTicks > 0;
    }

    @Override
    public void startExecuting() {
        int halfLookTimeTicks = this.maxLookTimeTicks / 2;
        this.lookTimeTicks = halfLookTimeTicks + this.watcherEntity.getRNG().nextInt(halfLookTimeTicks);
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void resetTask() {
        this.watchedEntity = null;
    }

    @Override
    public void updateTask() {
        this.watcherEntity.getLookHelper().setLookPosition(
            this.watchedEntity.posX, this.watchedEntity.posY + this.watchedEntity.getEyeHeight(), this.watchedEntity.posZ, 10.0F, this.watcherEntity.getVerticalFaceSpeed()
        );

        --this.lookTimeTicks;
    }
}
