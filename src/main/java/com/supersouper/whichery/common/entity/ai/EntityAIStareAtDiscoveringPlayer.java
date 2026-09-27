package com.supersouper.whichery.common.entity.ai;

import com.supersouper.whichery.common.entity.demon.IPossessedEntity;
import com.supersouper.whichery.common.entity.extendedproperties.DemonologyProperty;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class EntityAIStareAtDiscoveringPlayer<T extends EntityCreature & IPossessedEntity> extends EntityAIBase {

    private final T entity;
    private EntityPlayer discoveringPlayer;

    public EntityAIStareAtDiscoveringPlayer(T entity) {
        this.entity = entity;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.ticksExisted % 20 == 0) {
            AxisAlignedBB scanArea = this.entity.boundingBox
                .expand(16, 16, 16);

            for (EntityPlayer player : this.entity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, scanArea)) {
                DemonologyProperty property = DemonologyProperty.get(player);
                if (property != null && property.isCanSeeDemonsPossessingHosts() && isPlayerLookingAtEntity(player)) {
                    discoveringPlayer = player;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPlayerLookingAtEntity(EntityPlayer player) {
        Vec3 playerLookVec = player.getLookVec().normalize();
        Vec3 playerToTargetVec = Vec3.createVectorHelper(
            this.entity.posX - player.posX,
            this.entity.posY + this.entity.height / 2.0 - (player.posY + player.getEyeHeight()),
            this.entity.posZ - player.posZ
        );
        double playerToTargetDistance = playerToTargetVec.lengthVector();
        playerToTargetVec = playerToTargetVec.normalize();
        double d1 = playerLookVec.dotProduct(playerToTargetVec);
        return d1 > 1.0D - 0.025D / playerToTargetDistance && player.canEntityBeSeen(this.entity);
    }

    @Override
    public boolean continueExecuting() {
        if (discoveringPlayer.isEntityAlive()) {
            DemonologyProperty property = DemonologyProperty.get(discoveringPlayer);
            if (property != null && property.isCanSeeDemonsPossessingHosts()) {
                return this.entity.getDistanceSqToEntity(this.discoveringPlayer) <= 16 * 16;
            }
        }

        return false;
    }

    @Override
    public void resetTask() {
        this.discoveringPlayer = null;
    }

    @Override
    public void updateTask() {
        this.entity.getLookHelper()
            .setLookPosition(
                this.discoveringPlayer.posX,
                this.discoveringPlayer.posY + this.discoveringPlayer.getEyeHeight(),
                this.discoveringPlayer.posZ,
                10.0F,
                this.entity.getVerticalFaceSpeed());
    }
}
