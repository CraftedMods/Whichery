package com.supersouper.whichery.common.entity.ai;

import com.supersouper.whichery.common.util.EntityUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Objects;

public class EntityAICancelAttackWhenPlayerWatches extends EntityAIBase {

    private final EntityCreature entity;

    public EntityAICancelAttackWhenPlayerWatches(EntityCreature entity) {
        this.entity = Objects.requireNonNull(entity);

        setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        if (entity.getAttackTarget() != null && entity.getAttackTarget().isEntityAlive() && entity.ticksExisted % 5 == 0) {
            return !EntityUtils.canEntitiesFightWithoutNearbyPlayersWatching(entity, entity.getAttackTarget());
        }

        return false;
    }

    @Override
    public void startExecuting() {
        this.entity.setAttackTarget(null);
        this.entity.getNavigator().clearPathEntity();
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }
}
